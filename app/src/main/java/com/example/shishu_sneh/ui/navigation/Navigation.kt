package com.example.shishu_sneh.ui.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shishu_sneh.data.local.database.AppDatabase
import com.example.shishu_sneh.data.local.entity.BabyProfile
import com.example.shishu_sneh.data.repository.AppRepository
import com.example.shishu_sneh.ui.screens.*
import com.example.shishu_sneh.ui.viewmodel.AuthViewModel
import com.example.shishu_sneh.ui.viewmodel.FeedingGuideViewModel
import com.example.shishu_sneh.ui.viewmodel.GrowthViewModel
import com.example.shishu_sneh.ui.viewmodel.VaccinationViewModel
import com.example.shishu_sneh.ui.viewmodel.InsightsViewModel
import com.example.shishu_sneh.ui.viewmodel.CustomMilestoneViewModel
import com.example.shishu_sneh.ui.viewmodel.DailyLogViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Provide repository and ViewModel with factory
    val database = AppDatabase.getDatabase(context)
    val repository = AppRepository(database.appDao())

    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(GrowthViewModel::class.java) -> {
                    @Suppress("UNCHECKED_CAST")
                    GrowthViewModel(repository) as T
                }
                modelClass.isAssignableFrom(VaccinationViewModel::class.java) -> {
                    @Suppress("UNCHECKED_CAST")
                    VaccinationViewModel(context.applicationContext as Application, repository) as T
                }
                modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                    @Suppress("UNCHECKED_CAST")
                    AuthViewModel(repository) as T
                }
                modelClass.isAssignableFrom(FeedingGuideViewModel::class.java) -> {
                    @Suppress("UNCHECKED_CAST")
                    FeedingGuideViewModel(context.applicationContext as Application, repository) as T
                }
                modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                    @Suppress("UNCHECKED_CAST")
                    InsightsViewModel(repository) as T
                }
                modelClass.isAssignableFrom(CustomMilestoneViewModel::class.java) -> {
                    @Suppress("UNCHECKED_CAST")
                    CustomMilestoneViewModel(database.customMilestoneDao()) as T
                }
                modelClass.isAssignableFrom(DailyLogViewModel::class.java) -> {
                    @Suppress("UNCHECKED_CAST")
                    DailyLogViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val authViewModel: AuthViewModel = viewModel<AuthViewModel>(factory = factory)
            LoginScreen(navController, authViewModel)
        }
        composable("register") {
            val authViewModel: AuthViewModel = viewModel<AuthViewModel>(factory = factory)
            RegisterScreen(navController, authViewModel)
        }
        composable("onboarding") {
            OnboardingScreen(navController, onGetStarted = {
                // Create default baby profile with current date as DOB
                val now = System.currentTimeMillis()
                val profile = BabyProfile(name = "Baby", dateOfBirth = now)
                scope.launch {
                    repository.insertBabyProfile(profile)
                }
            })
        }
        composable("home") {
            val growthViewModel: GrowthViewModel = viewModel<GrowthViewModel>(factory = factory)
            val vaccinationViewModel: VaccinationViewModel = viewModel<VaccinationViewModel>(factory = factory)

            val babyProfile by repository.babyProfile.collectAsState(initial = null)
            LaunchedEffect(babyProfile?.dateOfBirth) {
                babyProfile?.let {
                    vaccinationViewModel.generateVaccinationSchedule(it.dateOfBirth)
                }
            }

            HomeScreen(navController, growthViewModel, vaccinationViewModel)
        }
        composable("growth_tracker") {
            val growthViewModel: GrowthViewModel = viewModel<GrowthViewModel>(factory = factory)
            GrowthTrackerScreen(navController, growthViewModel)
        }
        composable("vaccination") {
            val vaccinationViewModel: VaccinationViewModel = viewModel<VaccinationViewModel>(factory = factory)
            VaccinationScreen(navController, vaccinationViewModel)
        }
        composable("feeding") {
            val feedingGuideViewModel: FeedingGuideViewModel = viewModel<FeedingGuideViewModel>(factory = factory)
            FeedingGuideScreen(navController, feedingGuideViewModel)
        }
        composable("feeding_guide") {
            val feedingGuideViewModel: FeedingGuideViewModel = viewModel<FeedingGuideViewModel>(factory = factory)
            FeedingGuideScreen(navController, feedingGuideViewModel)
        }
        composable("daily_log") {
            val dailyLogViewModel: DailyLogViewModel = viewModel<DailyLogViewModel>(factory = factory)
            DailyLogScreen(navController, dailyLogViewModel)
        }
        composable("insights") {
            val insightsViewModel: InsightsViewModel = viewModel<InsightsViewModel>(factory = factory)
            InsightsScreen(navController, insightsViewModel)
        }
        composable("milestones") {
            val growthViewModel: GrowthViewModel = viewModel<GrowthViewModel>(factory = factory)
            val customMilestoneViewModel: CustomMilestoneViewModel = viewModel<CustomMilestoneViewModel>(factory = factory)
            MilestonesScreen(navController, growthViewModel, customMilestoneViewModel)
        }
        composable("chatbot") { ChatbotScreen(navController) }
    }
}


