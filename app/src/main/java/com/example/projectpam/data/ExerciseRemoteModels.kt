package com.example.projectpam.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseLogRow(
    val id: Long,
    @SerialName("user_id")       val userId: String,
    @SerialName("activity_type") val activityType: String,
    @SerialName("duration_min")  val durationMin: Int,
    val calories: Int,
    @SerialName("time_display")  val timeDisplay: String
)

@Serializable
data class ExerciseLogInsert(
    @SerialName("user_id")       val userId: String,
    @SerialName("activity_type") val activityType: String,
    @SerialName("duration_min")  val durationMin: Int,
    val calories: Int,
    @SerialName("time_display")  val timeDisplay: String
)

@Serializable
data class ActivityCaloriesRow(
    @SerialName("activity_type") val activityType: String,
    @SerialName("kcal_per_min")  val kcalPerMin: Int
)

fun ExerciseLogRow.toUi() = ExerciseUi(
    id = id.toInt(),
    name = activityType,
    durationMin = durationMin,
    calories = calories,
    time = timeDisplay
)
