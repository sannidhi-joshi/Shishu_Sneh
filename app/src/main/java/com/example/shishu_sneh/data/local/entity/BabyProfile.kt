package com.example.shishu_sneh.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby_profile")
data class BabyProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dateOfBirth: Long
)