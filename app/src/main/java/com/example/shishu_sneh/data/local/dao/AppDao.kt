package com.example.shishu_sneh.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shishu_sneh.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    suspend fun insertUser(user: UserProfile)

    @Query("SELECT * FROM UserProfile WHERE LOWER(TRIM(username)) = LOWER(TRIM(:username)) LIMIT 1")
    suspend fun getUser(username: String): UserProfile?

    @Query("SELECT * FROM UserProfile LIMIT 1")
    fun getAnyUser(): kotlinx.coroutines.flow.Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBabyProfile(profile: BabyProfile)

    @Query("SELECT * FROM baby_profile LIMIT 1")
    fun getBabyProfile(): Flow<BabyProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowthRecord(record: GrowthRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowth(record: GrowthRecord)

    @Query("SELECT * FROM growth_record ORDER BY date DESC")
    fun getAllGrowthRecords(): Flow<List<GrowthRecord>>

    @Query("SELECT * FROM growth_record ORDER BY date ASC")
    fun getAllGrowthRecordsAsc(): Flow<List<GrowthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccination(vaccination: Vaccination): Long

    @Query("SELECT * FROM vaccination ORDER BY dueDate ASC")
    fun getAllVaccinations(): Flow<List<Vaccination>>

    @Query("SELECT * FROM vaccination WHERE id = :id LIMIT 1")
    suspend fun getVaccinationById(id: Int): Vaccination?

    @Query("UPDATE vaccination SET completed = :completed WHERE id = :id")
    suspend fun updateVaccinationStatus(id: Int, completed: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone)

    @Query("SELECT * FROM milestone")
    fun getAllMilestones(): Flow<List<Milestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLog)

    @Query("DELETE FROM daily_log WHERE date >= :dayStart")
    suspend fun deleteDailyLogsFromDayStart(dayStart: Long)

    @Query("SELECT * FROM daily_log ORDER BY date ASC")
    fun getAllDailyLogs(): Flow<List<DailyLog>>
}