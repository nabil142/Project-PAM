package com.example.projectpam.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projectpam.data.FoodItem
import com.example.projectpam.data.MealEntry
import com.example.projectpam.data.NutritionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(navController: NavController) {

    val context = LocalContext.current as ComponentActivity
    // ✅ share NutritionViewModel dengan HomePage
    val viewModel: NutritionViewModel = viewModel(context)

    var currentScreen by remember { mutableStateOf("main") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var selectedDate by remember { mutableStateOf("2 May, Monday") }
    var showDatePicker by remember { mutableStateOf(false) }

    val primaryGreen = Color(0xFF00C50D)
    val primaryOrange = Color(0xFFFFA935)
    val lightYellow = Color(0xFFC8D936)

    when (currentScreen) {
        "main" -> NutritionMainScreen(
            viewModel = viewModel,
            selectedDate = selectedDate,
            onDateClick = { showDatePicker = true },
            onMealClick = { mealType ->
                selectedMealType = mealType
                currentScreen = "search"
            },
            onNavigateHome = { navController.navigate("home") },
            primaryGreen = primaryGreen
        )

        "search" -> NutritionSearchScreen(
            viewModel = viewModel,
            selectedMealType = selectedMealType,
            selectedDate = selectedDate,
            onBack = { currentScreen = "main" },
            onFoodClick = { food ->
                viewModel.addFoodToMeal(selectedMealType, food)
                // ✅ langsung ke meal list biar kelihatan efeknya
                currentScreen = "mealList"
            },
            onViewList = { currentScreen = "mealList" },
            primaryGreen = primaryGreen,
            lightYellow = lightYellow
        )

        "mealList" -> NutritionMealListScreen(
            viewModel = viewModel,
            selectedMealType = selectedMealType,
            selectedDate = selectedDate,
            onBack = { currentScreen = "search" },
            onAddMore = { currentScreen = "search" },
            onRemoveFood = { mealType, foodId ->
                viewModel.removeFoodFromMeal(mealType, foodId)
            },
            primaryGreen = primaryGreen
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                        val monthName =
                            SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
                        val dayName =
                            SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
                        selectedDate = "$dayOfMonth $monthName, $dayName"
                    }
                    showDatePicker = false
                }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionMainScreen(
    viewModel: NutritionViewModel,
    selectedDate: String,
    onDateClick: () -> Unit,
    onMealClick: (String) -> Unit,
    onNavigateHome: () -> Unit,
    primaryGreen: Color
) {
    val totalCalories = viewModel.getTotalCalories()
    val calorieGoal = 2181
    val progress = if (calorieGoal > 0) totalCalories.toFloat() / calorieGoal else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onDateClick)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
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
                    selected = false,
                    onClick = onNavigateHome,
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Restaurant, null) },
                    label = { Text("Nutrition") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Health") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // CIRCULAR PROGRESS
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxSize(),
                    color = primaryGreen,
                    strokeWidth = 16.dp,
                    trackColor = Color(0xFFE0E0E0)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        totalCalories.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "kcal eaten today",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // DAILY MEALS SECTION
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily meals", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
            }

            Spacer(Modifier.height(16.dp))

            // MEAL CARDS
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MealCard("Breakfast", "Recommended 447 Kcal", Color(0xFF4CAF50), viewModel, onMealClick)
                MealCard("Lunch", "Recommended 547 Kcal", Color(0xFFFFA726), viewModel, onMealClick)
                MealCard("Dinner", "Recommended 547 Kcal", Color(0xFF26A69A), viewModel, onMealClick)
                MealCard("Snack", "Recommended 547 Kcal", Color(0xFFAB47BC), viewModel, onMealClick)
            }
        }
    }
}

@Composable
fun MealCard(
    mealType: String,
    recommended: String,
    color: Color,
    viewModel: NutritionViewModel,
    onMealClick: (String) -> Unit
) {
    val meals = viewModel.meals.value
    val mealEntry = meals.find { it.mealType == mealType }
    val hasFood = mealEntry != null && mealEntry.foods.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMealClick(mealType) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    Text(mealType, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        if (hasFood) "${mealEntry!!.totalCalories} Kcal" else recommended,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C50D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionSearchScreen(
    viewModel: NutritionViewModel,
    selectedMealType: String,
    selectedDate: String,
    onBack: () -> Unit,
    onFoodClick: (FoodItem) -> Unit,
    onViewList: () -> Unit,
    primaryGreen: Color,
    lightYellow: Color
) {
    var searchQuery by remember { mutableStateOf("") }

    val allFoods = remember {
        listOf(
            FoodItem(name = "Tempe Goreng", calories = 75),
            FoodItem(name = "Telur Dadar", calories = 154),
            FoodItem(name = "Bubur Ayam", calories = 240),
            FoodItem(name = "Roti Gandum", calories = 70),
            FoodItem(name = "Nasi Putih", calories = 180),
            FoodItem(name = "Ayam Goreng", calories = 250),
            FoodItem(name = "Sayur Asem", calories = 50),
            FoodItem(name = "Pisang", calories = 89)
        )
    }

    val filteredFoods = if (searchQuery.isBlank()) {
        allFoods
    } else {
        allFoods.filter { it.name.contains(searchQuery, ignoreCase = true) }
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(selectedDate, fontSize = 16.sp)
                        }
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                selectedMealType,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                placeholder = { Text("Search food...") },
                trailingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onViewList,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(lightYellow),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Favourite List",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredFoods) { food ->
                    FoodItemCard(food) { onFoodClick(food) }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(food: FoodItem, onFoodClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("${food.calories} kcal", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C50D))
                    .clickable(onClick = onFoodClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionMealListScreen(
    viewModel: NutritionViewModel,
    selectedMealType: String,
    selectedDate: String,
    onBack: () -> Unit,
    onAddMore: () -> Unit,
    onRemoveFood: (String, String) -> Unit,
    primaryGreen: Color
) {
    val meals = viewModel.meals.value
    val mealEntry = meals.find { it.mealType == selectedMealType }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(selectedDate, fontSize = 16.sp)
                        }
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                "$selectedMealType List",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search...") },
                    trailingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    )
                )

                Box(
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFA726))
                        .clickable(onClick = onAddMore),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }

            Spacer(Modifier.height(24.dp))

            if (mealEntry != null && mealEntry.foods.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mealEntry.foods) { food ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        food.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${food.calories} kcal",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }

                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            onRemoveFood(selectedMealType, food.id)
                                        }
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Belum ada makanan ditambahkan",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
