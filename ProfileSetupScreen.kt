package com.bachatcom.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileSetupScreen(
    onSaveProfile: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var mobileError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var cityError by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    val green = Color(0xFF08784D)
    val red = MaterialTheme.colorScheme.error

    fun validateForm(): Boolean {
        nameError = when {
            name.isBlank() -> "Please enter your full name."
            name.trim().length < 2 -> "Name must contain at least 2 characters."
            else -> ""
        }

        mobileError = when {
            mobile.isBlank() -> "Please enter your mobile number."
            mobile.length != 10 -> "Mobile number must contain exactly 10 digits."
            mobile.firstOrNull() !in '6'..'9' ->
                "Mobile number must start with 6, 7, 8 or 9."
            else -> ""
        }

        emailError = when {
            email.isBlank() -> "Please enter your email address."
            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email.trim()).matches() ->
                "Invalid email. Example: name@gmail.com"
            else -> ""
        }

        cityError = when {
            city.isBlank() -> "Please enter your city."
            city.trim().length < 2 -> "Please enter a valid city name."
            else -> ""
        }

        return nameError.isEmpty() &&
                mobileError.isEmpty() &&
                emailError.isEmpty() &&
                cityError.isEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF8))
            .verticalScroll(rememberScrollState())
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Welcome to BachatCom!",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = green
        )

        Text(
            text = "Please complete your profile to continue.",
            color = Color.Gray,
            fontSize = 15.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = ""
                        showError = false
                    },
                    label = { Text("Full Name *") },
                    isError = nameError.isNotEmpty(),
                    supportingText = {
                        if (nameError.isNotEmpty()) {
                            Text(nameError, color = red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = mobile,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() } && it.length <= 10) {
                            mobile = it
                            mobileError = ""
                            showError = false
                        }
                    },
                    label = { Text("10-digit Mobile Number *") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    isError = mobileError.isNotEmpty(),
                    supportingText = {
                        if (mobileError.isNotEmpty()) {
                            Text(mobileError, color = red)
                        } else {
                            Text("${mobile.length}/10 digits")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = ""
                        showError = false
                    },
                    label = { Text("Email Address *") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    isError = emailError.isNotEmpty(),
                    supportingText = {
                        if (emailError.isNotEmpty()) {
                            Text(emailError, color = red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = {
                        city = it
                        cityError = ""
                        showError = false
                    },
                    label = { Text("City *") },
                    isError = cityError.isNotEmpty(),
                    supportingText = {
                        if (cityError.isNotEmpty()) {
                            Text(cityError, color = red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (showError) {
                    val errors = listOfNotNull(
                        nameError.takeIf { it.isNotEmpty() },
                        mobileError.takeIf { it.isNotEmpty() },
                        emailError.takeIf { it.isNotEmpty() },
                        cityError.takeIf { it.isNotEmpty() }
                    )

                    AlertDialog(
                        onDismissRequest = { showError = false },
                        title = {
                            Text(
                                "Please correct these details",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                errors.forEach { error ->
                                    Text(
                                        text = "• $error",
                                        color = red
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = { showError = false }
                            ) {
                                Text("OK", color = green)
                            }
                        }
                    )
                }

                Button(
                    onClick = {
                        if (validateForm()) {
                            showError = false
                            onSaveProfile(
                                name.trim(),
                                mobile,
                                email.trim(),
                                city.trim()
                            )
                        } else {
                            showError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = green
                    )
                ) {
                    Text(
                        "Save Profile & Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "* All fields are required",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}
