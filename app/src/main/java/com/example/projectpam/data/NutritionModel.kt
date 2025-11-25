package com.example.projectpam.data

import java.util.UUID

data class FoodItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val calories: Int
)

data class MealEntry(
    val mealType: String,
    val foods: List<FoodItem>
) {
    val totalCalories: Int get() = foods.sumOf { it.calories }
}
