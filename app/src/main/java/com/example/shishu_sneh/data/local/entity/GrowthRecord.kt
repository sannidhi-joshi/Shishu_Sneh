package com.example.shishu_sneh.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "growth_record")
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weight: Float,
    val height: Float,
    val date: Long
)