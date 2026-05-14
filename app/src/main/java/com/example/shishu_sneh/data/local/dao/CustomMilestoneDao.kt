package com.example.shishu_sneh.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.shishu_sneh.data.local.entity.CustomMilestone

@Dao
interface CustomMilestoneDao {

    @Insert
    suspend fun insertMilestone(milestone: CustomMilestone)

    @Query("SELECT * FROM CustomMilestone ORDER BY date DESC")
    suspend fun getAllMilestones(): List<CustomMilestone>
}
