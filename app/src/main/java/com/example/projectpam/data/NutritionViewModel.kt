package com.example.projectpam.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class NutritionUiState(
    val isLoading: Boolean = false,
    val meals: List<MealEntry> = emptyList(),
    val error: String? = null
)

class NutritionViewModel : ViewModel() {

    var uiState = mutableStateOf(NutritionUiState())
        private set

    private val repo = NutritionRepository

    val meals get() = uiState.value.meals

    private var currentDateLabel: String = "2 May, Monday"

    private var editingFoodTarget: Pair<String, String>? = null
    fun refresh(dateLabel: String) {
        currentDateLabel = dateLabel

        viewModelScope.launch {
            uiState.value = uiState.value.copy(isLoading = true, error = null)

            try {
                val list = repo.loadMealsForDate(dateLabel)
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    meals = list
                )
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun addFoodToMeal(mealType: String, food: FoodItem) {
        viewModelScope.launch {
            try {
                repo.addFoodToMeal(
                    mealType = mealType,
                    food = food,
                    selectedDateLabel = currentDateLabel
                )
                refresh(currentDateLabel)
            } catch (_: Exception) {
            }
        }
    }


    fun onFoodSelected(
        mealType: String,
        selectedDateLabel: String,
        food: FoodItem
    ) {
        viewModelScope.launch {
            try {
                val target = editingFoodTarget

                if (target != null && target.first == mealType) {

                    repo.updateFoodInMeal(
                        foodId = target.second,
                        newName = food.name,
                        newCalories = food.calories
                    )
                    editingFoodTarget = null
                } else {

                    repo.addFoodToMeal(
                        mealType = mealType,
                        food = food,
                        selectedDateLabel = selectedDateLabel
                    )
                }

                refresh(selectedDateLabel)
            } catch (_: Exception) {
            }
        }
    }


    fun removeFoodFromMeal(mealType: String, foodId: String) {
        viewModelScope.launch {
            try {
                repo.removeFoodFromMeal(
                    mealType = mealType,
                    foodId = foodId,
                    selectedDateLabel = currentDateLabel
                )
                refresh(currentDateLabel)
            } catch (_: Exception) {
            }
        }
    }


    fun updateFood(mealType: String, updated: FoodItem) {
        viewModelScope.launch {
            try {
                repo.updateFoodInMeal(
                    foodId = updated.id,
                    newName = updated.name,
                    newCalories = updated.calories
                )
                refresh(currentDateLabel)
            } catch (_: Exception) {
            }
        }
    }

    fun getTotalCalories(): Int =
        meals.sumOf { meal -> meal.foods.sumOf { it.calories } }

    fun getMealCalories(mealType: String): Int =
        meals.find { it.mealType == mealType }
            ?.foods
            ?.sumOf { it.calories }
            ?: 0


    fun startEditFood(mealType: String, foodId: String) {
        editingFoodTarget = mealType to foodId
    }

    fun clearEditingFood() {
        editingFoodTarget = null
    }
}
