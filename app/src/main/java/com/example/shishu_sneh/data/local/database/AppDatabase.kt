package com.example.shishu_sneh.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shishu_sneh.data.local.entity.*
import com.example.shishu_sneh.data.local.dao.AppDao
import com.example.shishu_sneh.data.local.dao.CustomMilestoneDao

@Database(
    entities = [
        BabyProfile::class,
        GrowthRecord::class,
        Vaccination::class,
        Milestone::class,
        UserProfile::class,
        DailyLog::class,
        CustomMilestone::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun customMilestoneDao(): CustomMilestoneDao

    companion object {
        private const val DB_NAME = "shishu_sneh_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val appContext = context.applicationContext

                    val instance = try {
                        buildDatabase(appContext).also { db ->
                            // Force open early so corruption/migration failures are handled here.
                            db.openHelper.writableDatabase
                        }
                    } catch (_: Exception) {
                        appContext.deleteDatabase(DB_NAME)
                        buildDatabase(appContext).also { db ->
                            db.openHelper.writableDatabase
                        }
                    }

                    INSTANCE = instance
                    instance
                }
            }
        }
    }
}