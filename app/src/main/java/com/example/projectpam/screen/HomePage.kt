package com.example.projectpam.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.projectpam.data.NutritionViewModel

private enum class BottomTab { HOME, HEALTH }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {

    val exerciseVM: ExerciseViewModel = viewModel()
    val nutritionVM: NutritionViewModel = viewModel()

    val exerciseState = exerciseVM.uiState.value

    val burnKcal = exerciseState.exercises.sumOf { it.calories }
    val eatenKcal = nutritionVM.getTotalCalories()

    val primaryGreen = Color(0xFF00C50D)
    val primaryOrange = Color(0xFFFFA935)
    val bgGreen = Color(0xFF91C788)
    val grayBar = Color(0xFFE6E6E6)

    var water by remember { mutableStateOf(0.8f) }
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var showDatePicker by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf("2 May, Monday") }

    val calorieGoal = 2181

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val formatter =
                                java.time.format.DateTimeFormatter.ofPattern("d MMM, EEEE")
                            val localDate =
                                java.time.Instant.ofEpochMilli(millis)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()

                            date = localDate.format(formatter)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                            Icon(Icons.Default.CalendarToday, null, tint = Color.Black)
                            Spacer(Modifier.width(12.dp))
                            Text(date, fontSize = 16.sp)
                        }
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.HOME,
                    onClick = { selectedTab = BottomTab.HOME },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("nutrition") },
                    icon = { Icon(Icons.Default.Restaurant, null) },
                    label = { Text("Nutrition") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.HEALTH,
                    onClick = { selectedTab = BottomTab.HEALTH },
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Health") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val formatter = java.time.format.DateTimeFormatter.ofPattern("d MMM, EEEE")
                                val localDate =
                                    java.time.Instant.ofEpochMilli(millis)
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate()

                                date = localDate.format(formatter)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        when (selectedTab) {

            BottomTab.HEALTH -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    HomeScreen(
                        activities = exerciseState.exercises,
                        onAddExercise = { n, d, t -> exerciseVM.addExercise(n, d, t) },
                        onUpdateExercise = { exerciseVM.updateExercise(it) },
                        onDeleteExercise = { exerciseVM.deleteExercise(it.id) }
                    )
                }
            }

            BottomTab.HOME -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    item {

                        // ===============================
                        // HEADER
                        // ===============================
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(330.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.curve_header),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds
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
                                            null,
                                            tint = primaryOrange
                                        )
                                        Text(burnKcal.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold)
                                        Text("burn", fontSize = 14.sp, color = Color.Gray)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            painterResource(id = R.drawable.restaurant),
                                            null,
                                            tint = primaryGreen
                                        )
                                        Text(eatenKcal.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold)
                                        Text("eaten", fontSize = 14.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(Modifier.height(20.dp))

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
                                            .fillMaxWidth((burnKcal.toFloat() / calorieGoal).coerceIn(0f, 1f))
                                            .background(primaryOrange, RoundedCornerShape(50))
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

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
                                            .fillMaxWidth((eatenKcal.toFloat() / calorieGoal).coerceIn(0f, 1f))
                                            .background(primaryGreen, RoundedCornerShape(50))
                                    )
                                }

                                Spacer(Modifier.height(20.dp))

                                Text(calorieGoal.toString(), fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                Text("Kcal Goal", fontSize = 14.sp, color = Color.Gray)
                            }
                        }

                        // =======================================
                        // GREEN BACKGROUND SECTION
                        // =======================================
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(bgGreen)
                        ) {

                            Column(Modifier.padding(16.dp)) {

                                // =====================
                                // WATER CARD
                                // =====================
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(Color.White),
                                    elevation = CardDefaults.cardElevation(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.padding(20.dp)) {

                                        Text(
                                            "Water ${String.format("%.1f", water)}L (${((water / 1.4f) * 100).toInt()}%)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                        Text("Recommended until now 1.4L", fontSize = 12.sp, color = Color.Gray)

                                        Spacer(Modifier.height(12.dp))

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
                                                    .fillMaxWidth((water / 1.4f).coerceIn(0f, 1f))
                                                    .background(primaryGreen)
                                            )
                                        }

                                        Spacer(Modifier.height(16.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            IconButton(
                                                onClick = { if (water > 0f) water -= 0.1f },
                                                modifier = Modifier
                                                    .size(45.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFDCEBDD))
                                            ) {
                                                Icon(Icons.Default.Remove, null)
                                            }

                                            Text(
                                                "${String.format("%.1f", water)} L",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            IconButton(
                                                onClick = { if (water < 1.4f) water += 0.1f },
                                                modifier = Modifier
                                                    .size(45.dp)
                                                    .clip(CircleShape)
                                                    .background(primaryGreen)
                                            ) {
                                                Icon(Icons.Default.Add, null, tint = Color.White)
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(20.dp))

                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Daily meals", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.Edit, null)
                                }

                                Spacer(Modifier.height(14.dp))

                                MealCard(
                                    title = "Breakfast",
                                    recommended = "Recommended 447 Kcal",
                                    color = Color(0xFF4CAF50),
                                    calories = nutritionVM.getMealCalories("Breakfast"),
                                    onClick = { navController.navigate("nutrition") }
                                )

                                Spacer(Modifier.height(12.dp))

                                MealCard(
                                    title = "Lunch",
                                    recommended = "Recommended 547 Kcal",
                                    color = Color(0xFFFFA726),
                                    calories = nutritionVM.getMealCalories("Lunch"),
                                    onClick = { navController.navigate("nutrition") }
                                )

                                Spacer(Modifier.height(12.dp))

                                MealCard(
                                    title = "Dinner",
                                    recommended = "Recommended 547 Kcal",
                                    color = Color(0xFF26A69A),
                                    calories = nutritionVM.getMealCalories("Dinner"),
                                    onClick = { navController.navigate("nutrition") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MealCard(
    title: String,
    recommended: String,
    color: Color,
    calories: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .background(color, RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.width(12.dp))

                Column {
                    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (calories > 0) "$calories Kcal" else recommended,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C50D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    }
}
