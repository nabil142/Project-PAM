package com.example.projectpam.data

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NutritionRepository {

    private val client = SupabaseClientProvider.client

    private const val MEALS_TABLE = "nutrition_meals"
    private const val FOODS_TABLE = "nutrition_foods"

    // sementara hard-code dulu, nanti bisa diganti pakai auth uid
    private const val DEMO_USER_ID = "demo-user-1"

    // ==== READ: load semua meal + foods untuk 1 tanggal ====

    suspend fun loadMealsForDate(
        selectedDateLabel: String
    ): List<MealEntry> = withContext(Dispatchers.IO) {

        // 1) Ambil semua meal row untuk user & tanggal ini
        val mealsResponse = client.postgrest[MEALS_TABLE].select {
            filter {
                eq("user_id", DEMO_USER_ID)
                eq("date_label", selectedDateLabel)
            }
        }

        // TODO: sesuaikan dengan nama data class di NutritionRemoteModels.kt
        val mealRows = mealsResponse.decodeList<NutritionMealRow>()

        if (mealRows.isEmpty()) {
            // belum ada apa-apa di tanggal ini
            return@withContext emptyList<MealEntry>()
        }

        // 2) Untuk setiap meal, ambil food-nya
        val result = mutableListOf<MealEntry>()

        for (meal in mealRows) {
            val foodsResponse = client.postgrest[FOODS_TABLE].select {
                filter { eq("meal_id", meal.id) }
            }

            // TODO: sesuaikan dengan nama data class di NutritionRemoteModels.kt
            val foodRows = foodsResponse.decodeList<NutritionFoodRow>()

            val foods = foodRows.map { row ->
                FoodItem(
                    id = row.id.toString(),   // DB Int → domain String
                    name = row.name,
                    calories = row.calories
                )
            }


            result += MealEntry(
                mealType = meal.mealType,
                foods = foods
            )
        }

        result
    }

    // ==== CREATE: tambah 1 food ke meal tertentu (buat meal kalau belum ada) ====

    suspend fun addFoodToMeal(
        mealType: String,
        food: FoodItem,
        selectedDateLabel: String
    ) = withContext(Dispatchers.IO) {

        // 1) Cari meal row yang sudah ada
        val mealResponse = client.postgrest[MEALS_TABLE].select {
            filter {
                eq("user_id", DEMO_USER_ID)
                eq("date_label", selectedDateLabel)
                eq("meal_type", mealType)
            }
        }

        val existingMeals = mealResponse.decodeList<NutritionMealRow>()

        // 2) Kalau belum ada -> insert dulu meal row
        val mealId: Int = if (existingMeals.isNotEmpty()) {
            existingMeals.first().id
        } else {
            // TODO: sesuaikan nama NutritionMealInsert kalau beda
            val insertBody = NutritionMealInsert(
                userId = DEMO_USER_ID,
                dateLabel = selectedDateLabel,
                mealType = mealType
            )

            val inserted = client.postgrest[MEALS_TABLE].insert(insertBody) {
                select()   // supaya balik data barunya
            }.decodeSingle<NutritionMealRow>()

            inserted.id
        }

        // 3) Insert 1 food ke tabel nutrition_foods
        // TODO: sesuaikan nama NutritionFoodInsert kalau beda
        val foodInsert = NutritionFoodInsert(
            mealId = mealId,
            name = food.name,
            calories = food.calories
        )

        client.postgrest[FOODS_TABLE].insert(foodInsert)
    }

    // ==== DELETE: hapus 1 food dari meal ====

    suspend fun removeFoodFromMeal(
        mealType: String,          // cuma buat konteks, nggak dipakai di query
        foodId: String,
        selectedDateLabel: String  // juga nggak dipakai di query delete, tapi match dengan VM
    ) = withContext(Dispatchers.IO) {
        // Kalau kolom id di nutrition_foods bertipe INT:
        client.postgrest[FOODS_TABLE].delete {
            filter {
                eq("id", foodId.toInt())
            }
        }

    }

    // ==== UPDATE: ubah nama / kalori food tertentu ====

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
                eq("id", foodId)
            }
        }
    }

    // ==== DELETE: hapus 1 meal beserta semua food-nya (optional) ====

    suspend fun deleteMealCompletely(
        mealType: String,
        selectedDateLabel: String
    ) = withContext(Dispatchers.IO) {

        // 1) Cari meal row
        val mealResponse = client.postgrest[MEALS_TABLE].select {
            filter {
                eq("user_id", DEMO_USER_ID)
                eq("date_label", selectedDateLabel)
                eq("meal_type", mealType)
            }
        }

        val meals = mealResponse.decodeList<NutritionMealRow>()
        if (meals.isEmpty()) return@withContext

        val mealId = meals.first().id

        // 2) Hapus semua food untuk meal ini
        client.postgrest[FOODS_TABLE].delete {
            filter { eq("meal_id", mealId) }
        }

        // 3) Hapus meal-nya
        client.postgrest[MEALS_TABLE].delete {
            filter { eq("id", mealId) }
        }
    }
}
