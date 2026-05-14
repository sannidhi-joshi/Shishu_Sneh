package com.example.shishu_sneh.data.local

object WhoGrowthData {

    fun getWeightData(gender: String): List<Pair<Float, Float>> {
        return if (gender == "Male") {
            listOf(
                Pair(0f, 3.3f), Pair(1f, 4.5f), Pair(2f, 5.6f), Pair(3f, 6.4f),
                Pair(4f, 7.0f), Pair(5f, 7.5f), Pair(6f, 7.9f), Pair(7f, 8.3f),
                Pair(8f, 8.6f), Pair(9f, 8.9f), Pair(10f, 9.2f), Pair(11f, 9.4f),
                Pair(12f, 9.6f), Pair(13f, 9.9f), Pair(14f, 10.1f), Pair(15f, 10.3f),
                Pair(16f, 10.5f), Pair(17f, 10.7f), Pair(18f, 10.9f), Pair(19f, 11.0f),
                Pair(20f, 11.2f), Pair(21f, 11.4f), Pair(22f, 11.6f), Pair(23f, 11.7f),
                Pair(24f, 11.9f)
            )
        } else {
            listOf(
                Pair(0f, 3.2f), Pair(1f, 4.2f), Pair(2f, 5.1f), Pair(3f, 5.8f),
                Pair(4f, 6.4f), Pair(5f, 6.9f), Pair(6f, 7.3f), Pair(7f, 7.6f),
                Pair(8f, 7.9f), Pair(9f, 8.2f), Pair(10f, 8.4f), Pair(11f, 8.6f),
                Pair(12f, 8.9f), Pair(13f, 9.1f), Pair(14f, 9.3f), Pair(15f, 9.5f),
                Pair(16f, 9.7f), Pair(17f, 9.9f), Pair(18f, 10.1f), Pair(19f, 10.3f),
                Pair(20f, 10.5f), Pair(21f, 10.6f), Pair(22f, 10.8f), Pair(23f, 11.0f),
                Pair(24f, 11.2f)
            )
        }
    }
}
