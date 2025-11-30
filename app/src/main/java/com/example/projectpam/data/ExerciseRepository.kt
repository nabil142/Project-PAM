package com.example.projectpam.data

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ExerciseRepository {

    private val client = SupabaseClientProvider.client

    private const val LOGS_TABLE = "exercise_logs"
    private const val CALORIES_TABLE = "activity_calories"

    private const val DEMO_USER_ID = "demo-user-1"

    private suspend fun getCurrentUserId(): String {
        val current = client.auth.currentUserOrNull()
        return current?.id ?: DEMO_USER_ID
    }

    suspend fun loadExercises(): List<ExerciseUi> = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()

        val response = client.postgrest[LOGS_TABLE].select {
            filter { eq("user_id", userId) }
        }

        val rows = response.decodeList<ExerciseLogRow>()
        rows.map { it.toUi() }
    }

    suspend fun addExercise(
        activityType: String,
        durationMin: Int,
        timeDisplay: String
    ) = withContext(Dispatchers.IO) {

        val userId = getCurrentUserId()
        val calories = calculateCalories(activityType, durationMin)

        val body = ExerciseLogInsert(
            userId = userId,
            activityType = activityType,
            durationMin = durationMin,
            calories = calories,
            timeDisplay = timeDisplay
        )

        client.postgrest[LOGS_TABLE].insert(body)
    }

    suspend fun updateExercise(ex: ExerciseUi) = withContext(Dispatchers.IO) {

        val userId = getCurrentUserId()
        val newCalories = calculateCalories(ex.name, ex.durationMin)

        val body = mapOf(
            "activity_type" to ex.name,
            "duration_min" to ex.durationMin,
            "calories" to newCalories,
            "time_display" to ex.time
        )

        client.postgrest[LOGS_TABLE].update(body) {
            filter {
                eq("id", ex.id)
                eq("user_id", userId)
            }
        }
    }

    suspend fun deleteExercise(id: Int) = withContext(Dispatchers.IO) {
        val userId = getCurrentUserId()

        client.postgrest[LOGS_TABLE].delete {
            filter {
                eq("id", id)
                eq("user_id", userId)
            }
        }
    }

    private suspend fun calculateCalories(
        activityType: String,
        durationMin: Int
    ): Int = withContext(Dispatchers.IO) {

        val response = client.postgrest[CALORIES_TABLE].select {
            filter { eq("activity_type", activityType) }
        }

        val rows = response.decodeList<ActivityCaloriesRow>()
        val perMinute = rows.firstOrNull()?.kcalPerMin ?: 5

        durationMin * perMinute
    }
}
