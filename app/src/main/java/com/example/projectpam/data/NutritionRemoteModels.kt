package com.example.projectpam.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NutritionMealRow(
    @SerialName("id")
    val id: Int,

    @SerialName("user_id")
    val userId: String,

    @SerialName("date_label")
    val dateLabel: String,

    @SerialName("meal_type")
    val mealType: String
)

@Serializable
data class NutritionMealInsert(
    @SerialName("user_id")
    val userId: String,

    @SerialName("date_label")
    val dateLabel: String,

    @SerialName("meal_type")
    val mealType: String
)

@Serializable
data class NutritionFoodRow(
    @SerialName("id")
    val id: Int,

    @SerialName("meal_id")
    val mealId: Int,

    @SerialName("name")
    val name: String,

    @SerialName("calories")
    val calories: Int
)

@Serializable
data class NutritionFoodInsert(
    @SerialName("meal_id")
    val mealId: Int,

    @SerialName("name")
    val name: String,

    @SerialName("calories")
    val calories: Int
)
