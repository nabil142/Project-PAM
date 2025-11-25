package com.example.projectpam.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class NutritionViewModel : ViewModel() {

    var meals = mutableStateOf(listOf<MealEntry>())
        private set

    fun addFoodToMeal(mealType: String, food: FoodItem) {
        val current = meals.value.toMutableList()
        val existing = current.find { it.mealType == mealType }

        if (existing != null) {
            val updated = existing.copy(
                foods = existing.foods + food
            )
            current[current.indexOf(existing)] = updated
        } else {
            current.add(
                MealEntry(mealType = mealType, foods = listOf(food))
            )
        }

        meals.value = current
    }

    fun removeFoodFromMeal(mealType: String, foodId: String) {
        val current = meals.value.toMutableList()
        val index = current.indexOfFirst { it.mealType == mealType }

        if (index != -1) {
            val meal = current[index]
            val updatedFoods = meal.foods.filter { it.id != foodId }

            if (updatedFoods.isEmpty()) {
                current.removeAt(index)
            } else {
                current[index] = meal.copy(foods = updatedFoods)
            }
        }

        meals.value = current
    }

    fun getTotalCalories(): Int {
        return meals.value.sumOf { it.totalCalories }
    }

    fun getMealCalories(mealType: String): Int {
        return meals.value.find { it.mealType == mealType }?.totalCalories ?: 0
    }
}
