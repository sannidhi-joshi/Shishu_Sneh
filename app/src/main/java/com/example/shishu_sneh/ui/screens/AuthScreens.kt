package com.example.shishu_sneh.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shishu_sneh.data.local.entity.UserProfile
import com.example.shishu_sneh.ui.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE4E1),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("👶", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "ShishuSneh",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A4A4A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Caring for your little one ❤️",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A6E7A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Username") },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Password") },
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    if (errorMessage.isNotBlank()) {
                        Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            errorMessage = when {
                                username.isBlank() || password.isBlank() -> "Please fill in both username and password."
                                else -> {
                                    when (authViewModel.login(username, password)) {
                                        AuthViewModel.LoginResult.Success -> {
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                            ""
                                        }
                                        AuthViewModel.LoginResult.UserNotFound -> "User not found. Please register first."
                                        AuthViewModel.LoginResult.WrongPassword -> "Incorrect password. Please try again."
                                        AuthViewModel.LoginResult.DatabaseError -> "Database error. Please reinstall app or clear app data."
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Login")
                    }

                    TextButton(
                        onClick = {
                            errorMessage = "Password recovery is not configured in this local Room demo."
                        }
                    ) {
                        Text("Forgot Password?")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("New here? Register")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel) {
    var babyName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var dobText by remember { mutableStateOf("") }
    var birthTime by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var hasAbnormalities by remember { mutableStateOf(false) }
    var abnormalities by remember { mutableStateOf("") }
    var hasAllergies by remember { mutableStateOf(false) }
    var allergies by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var bloodGroupExpanded by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = babyName,
            onValueChange = {
                babyName = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Baby Name") }
        )

        Text("Gender", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            listOf("Male", "Female", "Other").forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == option,
                        onClick = { gender = option }
                    )
                    Text(option)
                }
            }
        }

        OutlinedTextField(
            value = dobText,
            onValueChange = {
                dobText = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Date of Birth (yyyy-MM-dd)") }
        )

        OutlinedTextField(
            value = birthTime,
            onValueChange = {
                birthTime = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Time of Birth") }
        )

        OutlinedTextField(
            value = weightText,
            onValueChange = {
                weightText = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Weight") }
        )

        OutlinedTextField(
            value = heightText,
            onValueChange = {
                heightText = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Height") }
        )

        ExposedDropdownMenuBox(
            expanded = bloodGroupExpanded,
            onExpandedChange = { bloodGroupExpanded = !bloodGroupExpanded }
        ) {
            OutlinedTextField(
                value = bloodGroup,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Blood Group") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupExpanded) }
            )
            DropdownMenu(
                expanded = bloodGroupExpanded,
                onDismissRequest = { bloodGroupExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                bloodGroups.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            bloodGroup = option
                            bloodGroupExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Username") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = ""
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = hasAbnormalities, onCheckedChange = { hasAbnormalities = it })
            Text("Any abnormalities?")
        }
        if (hasAbnormalities) {
            OutlinedTextField(
                value = abnormalities,
                onValueChange = { abnormalities = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Describe abnormalities") }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = hasAllergies, onCheckedChange = { hasAllergies = it })
            Text("Any allergies?")
        }
        if (hasAllergies) {
            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Describe allergies") }
            )
        }

        if (errorMessage.isNotBlank()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val parsedDob = parseDateMillis(dobText)
                val parsedWeight = weightText.toFloatOrNull()
                val parsedHeight = heightText.toFloatOrNull()
                val selectedBloodGroup = bloodGroup.trim()
                val abnormalitiesText = if (hasAbnormalities) abnormalities.trim() else null
                val allergiesText = if (hasAllergies) allergies.trim() else null

                errorMessage = when {
                    babyName.isBlank() || dobText.isBlank() || birthTime.isBlank() || weightText.isBlank() || heightText.isBlank() || selectedBloodGroup.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                        "Please fill in all required fields."
                    }
                    password != confirmPassword -> {
                        "Password and confirm password do not match."
                    }
                    parsedDob == null -> {
                        "Please enter DOB in yyyy-MM-dd format."
                    }
                    parsedWeight == null || parsedHeight == null -> {
                        "Please enter valid numeric values for weight and height."
                    }
                    hasAbnormalities && abnormalitiesText.isNullOrBlank() -> {
                        "Please describe the abnormalities."
                    }
                    hasAllergies && allergiesText.isNullOrBlank() -> {
                        "Please describe the allergies."
                    }
                    else -> {
                        when (
                            authViewModel.registerUser(
                            UserProfile(
                                username = username.trim(),
                                password = password,
                                babyName = babyName.trim(),
                                gender = gender,
                                dob = parsedDob,
                                birthTime = birthTime.trim(),
                                weight = parsedWeight,
                                height = parsedHeight,
                                bloodGroup = selectedBloodGroup,
                                abnormalities = abnormalitiesText,
                                allergies = allergiesText
                            )
                        )
                        ) {
                            AuthViewModel.RegisterResult.Success -> {
                                navController.popBackStack()
                                ""
                            }
                            AuthViewModel.RegisterResult.UsernameExists -> "Username already exists. Please choose another username."
                            AuthViewModel.RegisterResult.DatabaseError -> "Database error. Please reinstall app or clear app data."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back to Login")
        }
    }
}

private fun parseDateMillis(dateText: String): Long? {
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateText.trim())?.time
    }.getOrNull()
}