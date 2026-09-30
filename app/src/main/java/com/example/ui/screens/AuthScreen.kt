package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PosViewModel
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeError
import com.example.ui.theme.EscapeErrorContainer
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary

@Composable
fun AuthScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var isAdminLogin by remember { mutableStateOf(false) }

    // Staff fields
    var staffMobile by remember { mutableStateOf("") }
    var staffPin by remember { mutableStateOf("") }

    // Admin fields
    var adminEmail by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Brand Logo Emblem
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(EscapeRed),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "ESCAPE CLOTHING",
            color = EscapeTextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )

        Text(
            text = "City Center 2, Butibori • 441122",
            color = EscapeTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Role Toggle Tabs (Staff vs Admin)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EscapeSurfaceElevated)
                .border(1.dp, EscapeBorder, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isAdminLogin) EscapeRed else Color.Transparent)
                    .clickable {
                        isAdminLogin = false
                        errorMessage = null
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Staff POS Login",
                    color = if (!isAdminLogin) Color.White else EscapeTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isAdminLogin) EscapeRed else Color.Transparent)
                    .clickable {
                        isAdminLogin = true
                        errorMessage = null
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Admin Portal",
                    color = if (isAdminLogin) Color.White else EscapeTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (!isAdminLogin) {
                    // STAFF LOGIN: Mobile + PIN
                    Text("Staff Terminal Login", color = EscapeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Fast retail billing access", color = EscapeTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = staffMobile,
                        onValueChange = {
                            staffMobile = it
                            errorMessage = null
                        },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = EscapeTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("staff_login_mobile"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary,
                            focusedLabelColor = EscapeRed
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = staffPin,
                        onValueChange = {
                            staffPin = it
                            errorMessage = null
                        },
                        label = { Text("PIN / Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "PIN", tint = EscapeTextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password",
                                    tint = EscapeTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("staff_login_pin"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary,
                            focusedLabelColor = EscapeRed
                        )
                    )
                } else {
                    // ADMIN LOGIN: Email + Password
                    Text("Admin Login", color = EscapeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Reports, inventory, expenses & settings", color = EscapeTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = adminEmail,
                        onValueChange = {
                            adminEmail = it
                            errorMessage = null
                        },
                        label = { Text("Admin Email") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = EscapeTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_login_email"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary,
                            focusedLabelColor = EscapeRed
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = {
                            adminPassword = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password", tint = EscapeTextSecondary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password",
                                    tint = EscapeTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_login_password"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary,
                            focusedLabelColor = EscapeRed
                        )
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeErrorContainer)
                            .padding(10.dp)
                    ) {
                        Text(text = errorMessage!!, color = EscapeError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        if (!isAdminLogin) {
                            viewModel.loginStaff(staffMobile, staffPin) { success, err ->
                                isLoading = false
                                if (!success) errorMessage = err
                            }
                        } else {
                            viewModel.loginAdmin(adminEmail, adminPassword) { success, err ->
                                isLoading = false
                                if (!success) errorMessage = err
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_submit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isLoading) "Verifying..." else if (!isAdminLogin) "Login to POS" else "Login to Admin Portal",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
