package com.example.shishu_sneh.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shishu_sneh.data.local.entity.Vaccination
import com.example.shishu_sneh.data.repository.AppRepository
import com.example.shishu_sneh.workers.VaccineReminderWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class VaccinationViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    val vaccinationList: StateFlow<List<Vaccination>> =
        repository.allVaccinations
            .catch { emit(emptyList()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val vaccinations: StateFlow<List<Vaccination>> = vaccinationList

    private val workManager = WorkManager.getInstance(getApplication())

    init {
        viewModelScope.launch {
            try {
                vaccinationList.collectLatest { vaccinations ->
                    vaccinations.forEach { vaccination ->
                        try {
                            scheduleVaccinationReminder(vaccination)
                        } catch (inner: Exception) {
                            // swallow to avoid bringing down the app
                        }
                    }
                }
            } catch (e: Exception) {
                // if collecting or scheduling fails, don't crash app
            }
        }
    }

    fun generateVaccinationSchedule(dob: Long) {
        viewModelScope.launch {
            // Check if vaccinations already exist
            val existing = vaccinationList.value
            if (existing.isNotEmpty()) {
                return@launch
            }

            val vaccinations = listOf(
                Vaccination(name = "BCG", dueDate = dob + days(0), completed = false),
                Vaccination(name = "Hepatitis B", dueDate = dob + days(1), completed = false),
                Vaccination(name = "DPT 1", dueDate = dob + days(42), completed = false),
                Vaccination(name = "DPT 2", dueDate = dob + days(70), completed = false),
                Vaccination(name = "DPT 3", dueDate = dob + days(98), completed = false),
                Vaccination(name = "Measles", dueDate = dob + days(270), completed = false),
                Vaccination(name = "Polio 1", dueDate = dob + days(42), completed = false),
                Vaccination(name = "Polio 2", dueDate = dob + days(70), completed = false),
                Vaccination(name = "Polio 3", dueDate = dob + days(98), completed = false),
                Vaccination(name = "Rubella", dueDate = dob + days(270), completed = false)
            )

            val savedVaccinations = vaccinations.map { vaccination ->
                val insertedId = repository.insertVaccination(vaccination)
                vaccination.copy(id = insertedId.toInt())
            }

            scheduleAllVaccinationReminders(savedVaccinations)
        }
    }

    fun scheduleAllVaccinationReminders(vaccinations: List<Vaccination>) {
        vaccinations.forEach { vaccination ->
            scheduleVaccinationReminder(vaccination)
        }
    }

    fun updateVaccinationStatus(id: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.updateVaccinationStatus(id, completed)
        }
    }

    fun scheduleVaccinationReminder(vaccination: Vaccination) {
        if (vaccination.completed) {
            workManager.cancelUniqueWork("vaccine_${vaccination.id}_1day")
            workManager.cancelUniqueWork("vaccine_${vaccination.id}_today")
            return
        }

        enqueueReminder(
            vaccination = vaccination,
            uniqueWorkName = "vaccine_${vaccination.id}_1day",
            delay = vaccination.dueDate - System.currentTimeMillis() - days(1),
            windowLabel = "Tomorrow"
        )

        enqueueReminder(
            vaccination = vaccination,
            uniqueWorkName = "vaccine_${vaccination.id}_today",
            delay = vaccination.dueDate - System.currentTimeMillis(),
            windowLabel = "Today"
        )
    }

    private fun enqueueReminder(
        vaccination: Vaccination,
        uniqueWorkName: String,
        delay: Long,
        windowLabel: String
    ) {
        if (delay < 0) {
            return
        }

        val data = workDataOf(
            VaccineReminderWorker.KEY_NAME to vaccination.name,
            VaccineReminderWorker.KEY_ID to vaccination.id,
            VaccineReminderWorker.KEY_WINDOW to windowLabel
        )

        val request = OneTimeWorkRequestBuilder<VaccineReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun getDaysRemaining(dueDate: Long): String {
        val diff = dueDate - System.currentTimeMillis()
        val days = diff / (1000 * 60 * 60 * 24)

        return when {
            days > 0 -> "Due in $days days"
            days == 0L -> "Due today"
            else -> "Overdue"
        }
    }

    private fun days(n: Int): Long {
        return n * 24L * 60 * 60 * 1000
    }
}
