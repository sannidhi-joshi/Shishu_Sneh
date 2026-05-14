package com.example.shishu_sneh.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milestone")
data class Milestone(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val milestoneName: String,
    val achieved: Boolean
)