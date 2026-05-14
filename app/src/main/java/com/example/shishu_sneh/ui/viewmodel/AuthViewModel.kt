package com.example.shishu_sneh.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shishu_sneh.data.local.entity.GrowthRecord
import com.example.shishu_sneh.data.local.entity.BabyProfile
import com.example.shishu_sneh.data.local.entity.UserProfile
import com.example.shishu_sneh.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AuthViewModel(private val repository: AppRepository) : ViewModel() {

    sealed class RegisterResult {
        data object Success : RegisterResult()
        data object UsernameExists : RegisterResult()
        data object DatabaseError : RegisterResult()
    }

    sealed class LoginResult {
        data object Success : LoginResult()
        data object UserNotFound : LoginResult()
        data object WrongPassword : LoginResult()
        data object DatabaseError : LoginResult()
    }

    fun registerUser(user: UserProfile): RegisterResult = runCatching {
        runBlocking {
            val existing = withContext(Dispatchers.IO) {
                repository.getUser(user.username.trim())
            }

            if (existing != null) {
                return@runBlocking RegisterResult.UsernameExists
            }

            withContext(Dispatchers.IO) {
                repository.insertUser(user)
                repository.insertBabyProfile(
                    BabyProfile(
                        name = user.babyName,
                        dateOfBirth = user.dob
                    )
                )

                if (repository.getGrowthRecords().isEmpty()) {
                    repository.insertGrowth(
                        GrowthRecord(
                            weight = user.weight,
                            height = user.height,
                            date = System.currentTimeMillis()
                        )
                    )
                }
            }

            RegisterResult.Success
        }
    }.getOrDefault(RegisterResult.DatabaseError)

    fun login(username: String, password: String): LoginResult = runCatching {
        runBlocking {
            val user = withContext(Dispatchers.IO) {
                repository.getUser(username.trim())
            }

            when {
                user == null -> LoginResult.UserNotFound
                user.password != password -> LoginResult.WrongPassword
                else -> LoginResult.Success
            }
        }
    }.getOrDefault(LoginResult.DatabaseError)
}