package com.example.shishu_sneh.data.local.feeding

data class MealPlan(
    val breakfast: String,
    val lunch: String,
    val snack: String,
    val dinner: String
)

data class NutritionInfo(
    val caloriesEstimate: String,
    val hydrationTip: String,
    val proteinTip: String
)
