package com.example.shishu_sneh.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.shishu_sneh.data.local.database.AppDatabase
import kotlinx.coroutines.runBlocking

class VaccineReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val vaccineId = inputData.getInt(KEY_ID, -1)
        val vaccineName = inputData.getString(KEY_NAME) ?: "Vaccination"
        val windowLabel = inputData.getString(KEY_WINDOW) ?: "Today"

        if (vaccineId == -1) {
            return Result.success()
        }

        val vaccine = runBlocking {
            try {
                AppDatabase.getDatabase(applicationContext)
                    .appDao()
                    .getVaccinationById(vaccineId)
            } catch (e: Exception) {
                null
            }
        }

        if (vaccine?.completed == true) {
            return Result.success()
        }

        showNotification(vaccineName, windowLabel)
        return Result.success()
    }

    private fun showNotification(vaccineName: String, windowLabel: String) {
        val channelId = "vaccine_channel"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vaccination Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Vaccination Reminder")
            .setContentText("$windowLabel: $vaccineName")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val KEY_NAME = "name"
        const val KEY_ID = "id"
        const val KEY_WINDOW = "window"
    }
}
