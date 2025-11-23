package com.example.projectpam.data

import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ExerciseRepository {

    private val client = SupabaseClientProvider.client

    private const val LOGS_TABLE = "exercise_logs"
    private const val CALORIES_TABLE = "activity_calories"

    // sementara: user dummy (nanti bisa diganti auth uid)
    private const val DEMO_USER_ID = "demo-user-1"

    // ========= LOAD SEMUA EXERCISE (user ini) =========
    suspend fun loadExercises(): List<ExerciseUi> = withContext(Dispatchers.IO) {
        val response = client.postgrest[LOGS_TABLE].select {
            filter { eq("user_id", DEMO_USER_ID) }
        }

        val rows = response.decodeList<ExerciseLogRow>()
        rows.map { it.toUi() }
    }

    // ========= ADD (insert 1 log) =========
    suspend fun addExercise(
        activityType: String,
        durationMin: Int,
        timeDisplay: String
    ) = withContext(Dispatchers.IO) {

        val calories = calculateCalories(activityType, durationMin)

        val body = ExerciseLogInsert(
            userId = DEMO_USER_ID,
            activityType = activityType,
            durationMin = durationMin,
            calories = calories,
            timeDisplay = timeDisplay
        )

        client.postgrest[LOGS_TABLE].insert(body)
    }

    // ========= UPDATE =========
    suspend fun updateExercise(ex: ExerciseUi) = withContext(Dispatchers.IO) {

        val newCalories = calculateCalories(ex.name, ex.durationMin)

        // pakai Map supaya tidak perlu DSL `set`
        val body = mapOf(
            "activity_type" to ex.name,
            "duration_min" to ex.durationMin,
            "calories" to newCalories,
            "time_display" to ex.time
        )

        client.postgrest[LOGS_TABLE].update(body) {
            filter {
                eq("id", ex.id)
                eq("user_id", DEMO_USER_ID)
            }
        }
    }

    // ========= DELETE =========
    suspend fun deleteExercise(id: Int) = withContext(Dispatchers.IO) {
        client.postgrest[LOGS_TABLE].delete {
            filter {
                eq("id", id)
                eq("user_id", DEMO_USER_ID)
            }
        }
    }

    // ========= HITUNG KALORI dari tabel activity_calories =========
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
