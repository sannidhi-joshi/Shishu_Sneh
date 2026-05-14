package com.example.shishu_sneh.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "UserProfile",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val babyName: String,
    val gender: String,
    val dob: Long,
    val birthTime: String,
    val weight: Float,
    val height: Float,
    val bloodGroup: String,
    val abnormalities: String?,
    val allergies: String?
)