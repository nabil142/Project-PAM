package com.example.projectpam.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projectpam.data.FoodItem
import com.example.projectpam.data.MealEntry
import com.example.projectpam.data.NutritionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(navController: NavController) {

    val viewModel: NutritionViewModel = viewModel()

    var currentScreen by remember { mutableStateOf("main") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }

    // label tanggal & hari otomatis (hari ini)
    val todayLabel = remember {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        "$dayOfMonth $monthName, $dayName"
    }
    var selectedDate by remember { mutableStateOf(todayLabel) }

    val primaryGreen = Color(0xFF00C50D)
    val primaryOrange = Color(0xFFFFA935)
    val lightYellow = Color(0xFFC8D936)

    // pertama kali load data untuk hari ini
    LaunchedEffect(Unit) {
        viewModel.refresh(selectedDate)
    }

    when (currentScreen) {
        "main" -> NutritionMainScreen(
            navController = navController,
            viewModel = viewModel,
            selectedDate = selectedDate,
            onMealClick = { mealType ->
                selectedMealType = mealType
                currentScreen = "search"
            },
            primaryGreen = primaryGreen
        )

        "search" -> NutritionSearchScreen(
            viewModel = viewModel,
            selectedMealType = selectedMealType,
            selectedDate = selectedDate,
            onBack = { currentScreen = "main" },
            onFoodClick = { food ->
                viewModel.addFoodToMeal(selectedMealType, food)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionMainScreen(
    navController: NavController,
    viewModel: NutritionViewModel,
    selectedDate: String,
    onMealClick: (String) -> Unit,
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                selectedDate,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                colors = topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                // Home
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )
                // Nutrition (current)
                NavigationBarItem(
                    selected = true,
                    onClick = { /* already here */ },
                    icon = { Icon(Icons.Default.Restaurant, null) },
                    label = { Text("Nutrition") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen
                    )
                )
                // Health → sementara balik ke Home (tab Health ada di HomePage)
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("home") },
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("Health") }
                )
                // Profile
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
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
                    strokeWidth = 14.dp,
                    color = primaryGreen,
                    trackColor = Color(0xFFE9E9E9),
                    modifier = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$totalCalories",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "kcal eaten today",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Daily meals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(Modifier.height(16.dp))

            MealCard(
                mealType = "Breakfast",
                recommended = "${viewModel.getMealCalories("Breakfast")} Kcal",
                color = Color(0xFF66BB6A),
                viewModel = viewModel,
                onMealClick = onMealClick
            )
            MealCard(
                mealType = "Lunch",
                recommended = "Recommended 547 Kcal",
                color = Color(0xFFFFA726),
                viewModel = viewModel,
                onMealClick = onMealClick
            )
            MealCard(
                mealType = "Dinner",
                recommended = "Recommended 547 Kcal",
                color = Color(0xFF26A69A),
                viewModel = viewModel,
                onMealClick = onMealClick
            )
            MealCard(
                mealType = "Snack",
                recommended = "Recommended 547 Kcal",
                color = Color(0xFFAB47BC),
                viewModel = viewModel,
                onMealClick = onMealClick
            )

            Spacer(Modifier.height(32.dp))
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
    val calories = viewModel.getMealCalories(mealType)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable { onMealClick(mealType) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(mealType, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (calories > 0) "$calories Kcal" else recommended,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = color
                )
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

    // contoh list makanan lokal (bisa diganti dari Supabase kalau mau)
    val allFoods = listOf(
        FoodItem(id = "nasigoreng", name = "Nasi Goreng", calories = 250),
        FoodItem(id = "ayamgoreng", name = "Ayam Goreng", calories = 200),
        FoodItem(id = "tahugoreng", name = "Tahu Goreng", calories = 80),
        FoodItem(id = "telur", name = "Telur Rebus", calories = 70),
        FoodItem(id = "tempe", name = "Tempe Goreng", calories = 90),
        FoodItem(id = "sayur", name = "Sayur Asem", calories = 50),
        FoodItem(id = "pisang", name = "Pisang", calories = 89)
    )

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(onClick = onBack)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Add Food",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                colors = topAppBarColors(containerColor = Color.White)
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

            Spacer(Modifier.height(4.dp))

            Text(
                selectedDate,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                placeholder = { Text("Search food") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recommended",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewList) {
                    Text("View list")
                }
            }

            Spacer(Modifier.height(8.dp))

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
fun FoodItemCard(
    food: FoodItem,
    onFoodClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onFoodClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon kuning di kiri
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3CD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = Color(0xFFFFA935),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // teks ambil sisa space
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${food.calories} kcal", fontSize = 12.sp, color = Color.Gray)
            }

            // plus hijau di ujung kanan
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C50D))
                    .clickable(onClick = onFoodClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
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
    val meals by remember { derivedStateOf { viewModel.meals } }

    val mealEntry = meals.find { it.mealType == selectedMealType } ?: MealEntry(
        mealType = selectedMealType,
        foods = emptyList()
    )

    val totalCalories = mealEntry.foods.sumOf { it.calories }

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
                            modifier = Modifier.clickable(onClick = onBack)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                selectedMealType,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                },
                colors = topAppBarColors(containerColor = Color.White)
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
                selectedDate,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Total $totalCalories kcal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mealEntry.foods) { food ->
                    FoodRowItem(
                        food = food,
                        onEdit = {
                            // nanti bisa diisi dialog edit
                        },
                        onDelete = {
                            onRemoveFood(selectedMealType, food.id)
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onAddMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add more food")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun FoodRowItem(
    food: FoodItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(food.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text("${food.calories} kcal", fontSize = 12.sp, color = Color.Gray)
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}

