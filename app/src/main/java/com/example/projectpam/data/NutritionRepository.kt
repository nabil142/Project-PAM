package com.example.projectpam.data

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object NutritionRepository {

    private val client = SupabaseClientProvider.client

    private const val MEALS_TABLE = "nutrition_meals"
    private const val FOODS_TABLE = "nutrition_foods"

    private const val DEMO_USER_ID = "demo-user-1"

    private suspend fun getCurrentUserId(): String {
        val current = client.auth.currentUserOrNull()
        return current?.id ?: DEMO_USER_ID
    }


    suspend fun loadMealsForDate(
        selectedDateLabel: String
    ): List<MealEntry> = withContext(Dispatchers.IO) {

        val userId = getCurrentUserId()

        val mealsResponse = client.postgrest[MEALS_TABLE].select {
            filter {
                eq("user_id", userId)
                eq("date_label", selectedDateLabel)
            }
        }

        val mealRows = mealsResponse.decodeList<NutritionMealRow>()

        if (mealRows.isEmpty()) {
            return@withContext emptyList<MealEntry>()
        }

        val result = mutableListOf<MealEntry>()

        for (meal in mealRows) {
            val foodsResponse = client.postgrest[FOODS_TABLE].select {
                filter { eq("meal_id", meal.id) }
            }

            val foodRows = foodsResponse.decodeList<NutritionFoodRow>()

            val foods = foodRows.map { row ->
                FoodItem(
                    id = row.id.toString(),
                    name = row.name,
                    calories = row.calories
                )
            }

            result += MealEntry(
                mealType = meal.mealType,
                foods = foods
            )
        }

        return@withContext result
    }

    suspend fun addFoodToMeal(
        mealType: String,
        food: FoodItem,
        selectedDateLabel: String
    ) = withContext(Dispatchers.IO) {

        val userId = getCurrentUserId()

        val mealResponse = client.postgrest[MEALS_TABLE].select {
            filter {
                eq("user_id", userId)
                eq("date_label", selectedDateLabel)
                eq("meal_type", mealType)
            }
        }

        val existingMeals = mealResponse.decodeList<NutritionMealRow>()

        val mealId: Int = if (existingMeals.isNotEmpty()) {
            existingMeals.first().id
        } else {
            val insertBody = NutritionMealInsert(
                userId = userId,
                dateLabel = selectedDateLabel,
                mealType = mealType
            )

            val inserted = client.postgrest[MEALS_TABLE].insert(insertBody) {
                select()
            }.decodeSingle<NutritionMealRow>()

            inserted.id
        }

        val foodInsert = NutritionFoodInsert(
            mealId = mealId,
            name = food.name,
            calories = food.calories
        )

        client.postgrest[FOODS_TABLE].insert(foodInsert)
    }

    suspend fun updateFoodInMeal(
        foodId: String,
        newName: String,
        newCalories: Int
    ) = withContext(Dispatchers.IO) {

        val body = mapOf(
            "name" to newName,
            "calories" to newCalories
        )

        client.postgrest[FOODS_TABLE].update(body) {
            filter {
                eq("id", foodId.toInt())
            }
        }
    }

    suspend fun removeFoodFromMeal(
        mealType: String,
        foodId: String,
        selectedDateLabel: String
    ) = withContext(Dispatchers.IO) {

        client.postgrest[FOODS_TABLE].delete {
            filter {
                eq("id", foodId.toInt())
            }
        }
    }
    suspend fun deleteMealCompletely(
        mealType: String,
        selectedDateLabel: String
    ) = withContext(Dispatchers.IO) {

        val userId = getCurrentUserId()

        val mealResponse = client.postgrest[MEALS_TABLE].select {
            filter {
                eq("user_id", userId)
                eq("date_label", selectedDateLabel)
                eq("meal_type", mealType)
            }
        }

        val meals = mealResponse.decodeList<NutritionMealRow>()
        if (meals.isEmpty()) return@withContext

        val mealId = meals.first().id

        client.postgrest[FOODS_TABLE].delete {
            filter { eq("meal_id", mealId) }
        }

        client.postgrest[MEALS_TABLE].delete {
            filter { eq("id", mealId) }
        }
    }
}
