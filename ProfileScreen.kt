package com.bachatcom.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ProfileGreen = Color(0xFF08784D)
private val ProfileDarkGreen = Color(0xFF075638)
private val ProfileLightGreen = Color(0xFFEAF7EF)

@Composable
fun ProfileScreen(
    onOptionClick: (String) -> Unit,
    userName: String,
    userMobile: String,
    userEmail: String,
    userCity: String,
    onSaveProfile: (String, String, String, String) -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAF8))
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(ProfileDarkGreen, ProfileGreen)
                    )
                )
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Profile",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 55.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName.ifBlank { "BachatCom User" },
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = userEmail,
                color = Color(0xFFD7F3E3),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showEditDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("✏️  Edit Profile")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Personal Details",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ProfileDarkGreen
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Full Name", userName)
                    HorizontalDivider(color = ProfileLightGreen)
                    DetailRow("Mobile Number", userMobile)
                    HorizontalDivider(color = ProfileLightGreen)
                    DetailRow("Email Address", userEmail)
                    HorizontalDivider(color = ProfileLightGreen)
                    DetailRow("City", userCity)
                }
            }

            Text(
                text = "My Account",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = ProfileDarkGreen
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileOption(
                        icon = "🛍️",
                        title = "My Shopping",
                        subtitle = "View your shopping list",
                        onClick = { onOptionClick("My Shopping") }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        color = ProfileLightGreen
                    )

                    ProfileOption(
                        icon = "💚",
                        title = "My Savings",
                        subtitle = "Check your saved money",
                        onClick = { onOptionClick("My Savings") }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        color = ProfileLightGreen
                    )

                    ProfileOption(
                        icon = "⚙️",
                        title = "Settings",
                        subtitle = "Manage app preferences",
                        onClick = { onOptionClick("Settings") }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ProfileLightGreen
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💰", fontSize = 30.sp)
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Shop Smart, Save More!",
                            fontWeight = FontWeight.Bold,
                            color = ProfileDarkGreen
                        )
                        Text(
                            text = "Compare prices and make better choices.",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Delete Account
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    "🗑️  Delete My Account",
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Deleting your account will remove your saved app data.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        EditProfileDialog(
            name = userName,
            mobile = userMobile,
            email = userEmail,
            city = userCity,
            onDismiss = { showEditDialog = false },
            onSave = { name, phone, mail, town ->
                onSaveProfile(name, phone, mail, town)
                showEditDialog = false
            }
        )
    }

    // Delete Account Confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Account?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete your account? " +
                            "Your saved profile and app data may be permanently " +
                            "removed. This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    }
                ) {
                    Text(
                        "Yes, Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel", color = ProfileGreen)
                }
            }
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.ifBlank { "Not provided" },
            color = Color(0xFF24352D),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EditProfileDialog(
    name: String,
    mobile: String,
    email: String,
    city: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var editedName by remember(name) { mutableStateOf(name) }
    var editedMobile by remember(mobile) { mutableStateOf(mobile) }
    var editedEmail by remember(email) { mutableStateOf(email) }
    var editedCity by remember(city) { mutableStateOf(city) }

    var nameError by remember { mutableStateOf("") }
    var mobileError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var cityError by remember { mutableStateOf("") }

    val red = MaterialTheme.colorScheme.error

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = {
                        editedName = it
                        nameError = ""
                    },
                    label = { Text("Full Name *") },
                    isError = nameError.isNotEmpty(),
                    supportingText = {
                        if (nameError.isNotEmpty()) {
                            Text(nameError, color = red)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedMobile,
                    onValueChange = {
                        if (it.all(Char::isDigit) && it.length <= 10) {
                            editedMobile = it
                            mobileError = ""
                        }
                    },
                    label = { Text("Mobile Number *") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    isError = mobileError.isNotEmpty(),
                    supportingText = {
                        if (mobileError.isNotEmpty()) {
                            Text(mobileError, color = red)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = {
                        editedEmail = it
                        emailError = ""
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
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = editedCity,
                    onValueChange = {
                        editedCity = it
                        cityError = ""
                    },
                    label = { Text("City *") },
                    isError = cityError.isNotEmpty(),
                    supportingText = {
                        if (cityError.isNotEmpty()) {
                            Text(cityError, color = red)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    nameError = when {
                        editedName.isBlank() ->
                            "Please enter your name."
                        editedName.trim().length < 2 ->
                            "Name must contain at least 2 characters."
                        else -> ""
                    }

                    mobileError = when {
                        editedMobile.length != 10 ->
                            "Mobile number must have 10 digits."
                        editedMobile.firstOrNull() !in '6'..'9' ->
                            "Number must start with 6, 7, 8 or 9."
                        else -> ""
                    }

                    emailError = when {
                        editedEmail.isBlank() ->
                            "Please enter your email."
                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(editedEmail.trim()).matches() ->
                            "Please enter a valid email."
                        else -> ""
                    }

                    cityError = when {
                        editedCity.isBlank() ->
                            "Please enter your city."
                        editedCity.trim().length < 2 ->
                            "Please enter a valid city."
                        else -> ""
                    }

                    if (
                        nameError.isEmpty() &&
                        mobileError.isEmpty() &&
                        emailError.isEmpty() &&
                        cityError.isEmpty()
                    ) {
                        onSave(
                            editedName.trim(),
                            editedMobile,
                            editedEmail.trim(),
                            editedCity.trim()
                        )
                    }
                }
            ) {
                Text("Save", color = ProfileGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ProfileOption(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    ProfileLightGreen,
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 23.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF24352D)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Text(
            text = "›",
            fontSize = 27.sp,
            color = ProfileGreen
        )
    }
}
