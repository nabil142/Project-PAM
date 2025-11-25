package com.example.projectpam.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projectpam.R
import com.example.projectpam.data.ExerciseViewModel
import com.example.projectpam.data.NutritionViewModel   // ⬅️ IMPORT BARU

private enum class BottomTab { HOME, NUTRITION, HEALTH }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {

    // ==== VIEWMODEL ====
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val exerciseState = exerciseViewModel.uiState.value

    val nutritionViewModel: NutritionViewModel = viewModel()   // ⬅️ VM NUTRISI

    // 🔥 total kalori terbakar (dari exercise)
    val burnKcal = exerciseState.exercises.sumOf { it.calories }
    // 🍽 total kalori masuk (dari Supabase lewat NutritionViewModel)
    val eatenKcal = nutritionViewModel.getTotalCalories()

    val primaryGreen = Color(0xFF00C50D)
    val primaryOrange = Color(0xFFFFA935)
    val bgGreen = Color(0xFF91C788)
    val grayBar = Color(0xFFE6E6E6)

    val kcalGoal = 2181f
    val burnRatio = (burnKcal.toFloat() / kcalGoal).coerceIn(0f, 1f)
    val eatenRatio = (eatenKcal.toFloat() / kcalGoal).coerceIn(0f, 1f)

    var water by remember { mutableStateOf(0.7f) }
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var showDatePicker by remember { mutableStateOf(false) }
    // label tanggal & hari otomatis (sama seperti di NutritionScreen)
    val todayLabel = remember {
        val calendar = java.util.Calendar.getInstance()
        val dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val monthName = java.text.SimpleDateFormat(
            "MMMM",
            java.util.Locale.getDefault()
        ).format(calendar.time)
        val dayName = java.text.SimpleDateFormat(
            "EEEE",
            java.util.Locale.getDefault()
        ).format(calendar.time)

        "$dayOfMonth $monthName, $dayName"
    }

    var selectedDate by remember { mutableStateOf(todayLabel) }


    // 🔁 SETIAP TANGGAL GANTI → REFRESH DATA NUTRISI
    LaunchedEffect(selectedDate) {
        nutritionViewModel.refresh(selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showDatePicker = true }
                        ) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(selectedDate, fontSize = 16.sp)
                        }
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {

                NavigationBarItem(
                    selected = selectedTab == BottomTab.HOME,
                    onClick = { selectedTab = BottomTab.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen
                    )
                )

                NavigationBarItem(
                    selected = false, // atau biarin aja false, karena halaman ini cuma untuk HOME
                    onClick = { navController.navigate("nutrition") },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                    label = { Text("Nutrition") }
                )


                NavigationBarItem(
                    selected = selectedTab == BottomTab.HEALTH,
                    onClick = { selectedTab = BottomTab.HEALTH },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Health") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->

        when (selectedTab) {
            BottomTab.HEALTH -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    HomeScreen(
                        activities = exerciseState.exercises,
                        onAddExercise = { name, dur, time ->
                            exerciseViewModel.addExercise(name, dur, time)
                        },
                        onUpdateExercise = {
                            exerciseViewModel.updateExercise(it)
                        },
                        onDeleteExercise = {
                            exerciseViewModel.deleteExercise(it.id)
                        }
                    )
                }
            }

            BottomTab.NUTRITION -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(bgGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Catatan asupan harian bakal muncul di sini.",
                        color = Color.White
                    )
                }
            }

            BottomTab.HOME -> {
                // ============= HOME PAGE =============
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                    // HEADER CURVE
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(330.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.curve_header),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )

                        Column(
                            Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Spacer(Modifier.height(40.dp))

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 40.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painterResource(id = R.drawable.fire),
                                        contentDescription = null,
                                        tint = primaryOrange
                                    )
                                    Text(
                                        burnKcal.toString(),
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("burn", fontSize = 14.sp, color = Color.Gray)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painterResource(id = R.drawable.restaurant),
                                        contentDescription = null,
                                        tint = primaryGreen
                                    )
                                    Text(
                                        eatenKcal.toString(),            // ⬅️ SUDAH DINAMIS
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("eaten", fontSize = 14.sp, color = Color.Gray)
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            // BURN BAR
                            Box(
                                Modifier
                                    .width(166.dp)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(grayBar)
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(burnRatio)        // ⬅️ DINAMIS
                                        .clip(RoundedCornerShape(50))
                                        .background(primaryOrange)
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            // EATEN BAR
                            Box(
                                Modifier
                                    .width(166.dp)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(grayBar)
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(eatenRatio)       // ⬅️ DINAMIS
                                        .clip(RoundedCornerShape(50))
                                        .background(primaryGreen)
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            Text(
                                kcalGoal.toInt().toString(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Kcal Goal", fontSize = 14.sp, color = Color.Gray)
                        }
                    }

                    // BACKGROUND GREEN
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(bgGreen)
                    ) {

                        Column(Modifier.padding(16.dp)) {

                            // WATER CARD
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        Modifier.weight(1f)
                                    ) {
                                        Text("Water", fontWeight = FontWeight.Bold)
                                        Text(
                                            "${String.format("%.1f", water)} L",
                                            fontSize = 20.sp
                                        )
                                        Text(
                                            "Recommended 1.4 L",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Spacer(Modifier.height(10.dp))

                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(grayBar)
                                        ) {
                                            Box(
                                                Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(water / 1.4f)
                                                    .clip(RoundedCornerShape(50))
                                                    .background(primaryGreen)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.width(16.dp))

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                        Box(
                                            Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(primaryGreen)
                                                .clickable {
                                                    if (water < 1.4f) water += 0.1f
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }

                                        Spacer(Modifier.height(10.dp))

                                        Box(
                                            Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color.LightGray)
                                                .clickable {
                                                    if (water > 0f) water -= 0.1f
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            Text("Daily meals", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(20.dp)) {
                                    Text("Belum ada data makanan.")
                                    Text(
                                        "Fitur catatan asupan harian akan muncul di sini.",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DATE PICKER DIALOG
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = java.util.Calendar.getInstance()
                            calendar.timeInMillis = millis

                            val dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                            val monthName = when (calendar.get(java.util.Calendar.MONTH)) {
                                0 -> "January"
                                1 -> "February"
                                2 -> "March"
                                3 -> "April"
                                4 -> "May"
                                5 -> "June"
                                6 -> "July"
                                7 -> "August"
                                8 -> "September"
                                9 -> "October"
                                10 -> "November"
                                11 -> "December"
                                else -> ""
                            }
                            val dayName = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                                1 -> "Sunday"
                                2 -> "Monday"
                                3 -> "Tuesday"
                                4 -> "Wednesday"
                                5 -> "Thursday"
                                6 -> "Friday"
                                7 -> "Saturday"
                                else -> ""
                            }

                            selectedDate = "$dayOfMonth $monthName, $dayName"
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = primaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
