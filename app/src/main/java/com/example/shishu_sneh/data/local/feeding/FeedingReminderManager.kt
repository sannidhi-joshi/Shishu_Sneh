package com.example.shishu_sneh.data.local.feeding

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.shishu_sneh.workers.FeedingReminderWorker
import java.util.concurrent.TimeUnit

class FeedingReminderManager(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleDailyReminders(ageInMonths: Int) {
        val plan = when {
            ageInMonths <= 5 -> listOf(
                Pair("Feeding time", 2L),
                Pair("Feeding time", 4L),
                Pair("Feeding time", 6L),
                Pair("Feeding time", 8L)
            )
            ageInMonths in 6..12 -> listOf(
                Pair("Breakfast", 2L),
                Pair("Lunch", 6L),
                Pair("Snack", 10L),
                Pair("Dinner", 14L)
            )
            else -> listOf(
                Pair("Breakfast", 2L),
                Pair("Lunch", 6L),
                Pair("Dinner", 10L)
            )
        }

        plan.forEachIndexed { index, item ->
            val request = OneTimeWorkRequestBuilder<FeedingReminderWorker>()
                .setInitialDelay(item.second, TimeUnit.HOURS)
                .setInputData(
                    workDataOf(
                        FeedingReminderWorker.KEY_TITLE to item.first,
                        FeedingReminderWorker.KEY_MESSAGE to when {
                            ageInMonths <= 5 -> "Offer breast milk now"
                            ageInMonths in 6..12 -> "Offer a planned feeding for today"
                            else -> "Serve the next family meal"
                        }
                    )
                )
                .build()

            workManager.enqueueUniqueWork(
                "feeding_reminder_$index",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
