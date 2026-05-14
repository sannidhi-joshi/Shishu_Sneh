package com.example.shishu_sneh.data.repository

import com.example.shishu_sneh.data.local.dao.AppDao
import com.example.shishu_sneh.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(private val appDao: AppDao) {

    suspend fun insertUser(user: UserProfile) {
        appDao.insertUser(user)
    }

    suspend fun getUser(username: String): UserProfile? {
        return appDao.getUser(username)
    }

    val babyProfile: Flow<BabyProfile?> = appDao.getBabyProfile()
    val allGrowthRecords: Flow<List<GrowthRecord>> = appDao.getAllGrowthRecords()
    val allGrowthRecordsAsc: Flow<List<GrowthRecord>> = appDao.getAllGrowthRecordsAsc()
    val anyUser: Flow<UserProfile?> = appDao.getAnyUser()
    val allVaccinations: Flow<List<Vaccination>> = appDao.getAllVaccinations()
    val allMilestones: Flow<List<Milestone>> = appDao.getAllMilestones()
    val allDailyLogs: Flow<List<com.example.shishu_sneh.data.local.entity.DailyLog>> = appDao.getAllDailyLogs()

    suspend fun insertDailyLog(log: com.example.shishu_sneh.data.local.entity.DailyLog) {
        appDao.insertDailyLog(log)
    }

    suspend fun replaceTodayDailyLog(log: com.example.shishu_sneh.data.local.entity.DailyLog, dayStart: Long) {
        appDao.deleteDailyLogsFromDayStart(dayStart)
        appDao.insertDailyLog(log)
    }

    suspend fun insertBabyProfile(profile: BabyProfile) {
        appDao.insertBabyProfile(profile)
    }

    suspend fun insertGrowthRecord(record: GrowthRecord) {
        appDao.insertGrowthRecord(record)
    }

    suspend fun insertGrowth(record: GrowthRecord) {
        appDao.insertGrowth(record)
    }

    suspend fun getGrowthRecords(): List<GrowthRecord> {
        return appDao.getAllGrowthRecordsAsc().first()
    }

    suspend fun insertVaccination(vaccination: Vaccination): Long {
        return appDao.insertVaccination(vaccination)
    }

    suspend fun getVaccinationById(id: Int): Vaccination? {
        return appDao.getVaccinationById(id)
    }

    suspend fun updateVaccinationStatus(id: Int, completed: Boolean) {
        appDao.updateVaccinationStatus(id, completed)
    }

    suspend fun insertMilestone(milestone: Milestone) {
        appDao.insertMilestone(milestone)
    }
}