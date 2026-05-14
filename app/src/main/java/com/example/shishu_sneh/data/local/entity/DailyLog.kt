package com.example.shishu_sneh.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_log")
data class DailyLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val feeding: String,
    val sleepHours: Float,
    val diaperCount: Int,
    val notes: String
)
