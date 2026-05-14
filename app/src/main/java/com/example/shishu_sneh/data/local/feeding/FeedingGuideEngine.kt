package com.example.shishu_sneh.data.local.feeding

import java.util.Calendar
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

data class FeedingGuide(
    val stage: String,
    val tips: List<String>
)

object FeedingGuideEngine {

    fun getAgeInMonths(dob: Long): Int {
        val birthDate = Instant.ofEpochMilli(dob)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val currentDate = LocalDate.now()

        val period = Period.between(birthDate, currentDate)

        return period.years * 12 + period.months
    }

    fun getFeedingGuide(age: Int): FeedingGuide {
        return when (age) {
            in 0..5 -> FeedingGuide(
                stage = "0–5 months",
                tips = listOf(
                    "Exclusive breastfeeding",
                    "Feed every 2–3 hours",
                    "No water or solids"
                )
            )

            in 6..8 -> FeedingGuide(
                stage = "6–8 months",
                tips = listOf(
                    "Start semi-solid foods",
                    "Mashed fruits, rice, dal",
                    "Continue breastfeeding"
                )
            )

            in 9..12 -> FeedingGuide(
                stage = "9–12 months",
                tips = listOf(
                    "Soft solid foods",
                    "Finger foods",
                    "3 meals + 2 snacks"
                )
            )

            else -> FeedingGuide(
                stage = "1+ year",
                tips = listOf("Consult pediatrician for guidance")
            )
        }
    }

    fun getAgeGroup(age: Int): String {
        return when (age) {
            in 0..5 -> "0-5"
            in 6..8 -> "6-8"
            in 9..12 -> "9-12"
            else -> "1plus"
        }
    }

    fun getMealPlan(age: Int): MealPlan {
        return when (getAgeGroup(age)) {
            "0-5" -> MealPlan(
                breakfast = "Exclusive breastfeeding",
                lunch = "Exclusive breastfeeding",
                snack = "Exclusive breastfeeding",
                dinner = "Exclusive breastfeeding"
            )
            "6-8" -> MealPlan(
                breakfast = "Mashed banana",
                lunch = "Rice + dal",
                snack = "Fruit puree",
                dinner = "Khichdi"
            )
            "9-12" -> MealPlan(
                breakfast = "Soft oats porridge",
                lunch = "Vegetable rice with dal",
                snack = "Fruit pieces or yogurt",
                dinner = "Soft khichdi with vegetables"
            )
            else -> MealPlan(
                breakfast = "Milk + soft breakfast",
                lunch = "Balanced home meal",
                snack = "Fresh fruit",
                dinner = "Family meal with soft textures"
            )
        }
    }

    fun getNutritionInfo(age: Int): NutritionInfo {
        return when (getAgeGroup(age)) {
            "0-5" -> NutritionInfo(
                caloriesEstimate = "Breast milk is sufficient for growth",
                hydrationTip = "No water needed unless advised by a doctor",
                proteinTip = "Breast milk provides the needed nutrition"
            )
            "6-8" -> NutritionInfo(
                caloriesEstimate = "Moderate growth support with semi-solid meals",
                hydrationTip = "Offer small sips of water after meals",
                proteinTip = "Include dal, lentils, and mashed legumes"
            )
            "9-12" -> NutritionInfo(
                caloriesEstimate = "Balanced energy from milk and solids",
                hydrationTip = "Keep offering water through the day",
                proteinTip = "Add dal, paneer, curd, and soft pulses"
            )
            else -> NutritionInfo(
                caloriesEstimate = "Balanced diet with milk, grains, fruits, and vegetables",
                hydrationTip = "Encourage regular water intake",
                proteinTip = "Include lentils, milk products, nuts powder, and legumes"
            )
        }
    }

    fun getSmartSuggestions(age: Int, seed: Long = System.currentTimeMillis() / 86_400_000L): List<String> {
        val suggestions = when (getAgeGroup(age)) {
            "0-5" -> listOf(
                "Breastfeeding",
                "Skin-to-skin feeding",
                "Night feeding support"
            )
            "6-8" -> listOf(
                "Banana puree",
                "Rice porridge",
                "Soft mashed vegetables",
                "Dal puree",
                "Apple puree"
            )
            "9-12" -> listOf(
                "Rice porridge",
                "Soft vegetables",
                "Mashed fruit",
                "Khichdi",
                "Curd"
            )
            else -> listOf(
                "Soft chapati pieces",
                "Cooked vegetables",
                "Fruit slices",
                "Dal rice",
                "Yogurt"
            )
        }

        val startIndex = (seed % suggestions.size).toInt()
        return listOf(
            suggestions[startIndex % suggestions.size],
            suggestions[(startIndex + 1) % suggestions.size],
            suggestions[(startIndex + 2) % suggestions.size]
        ).distinct()
    }
}

