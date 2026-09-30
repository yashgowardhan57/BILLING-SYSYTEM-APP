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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.PosViewModel
import com.example.ui.components.EscapeTopBar
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeError
import com.example.ui.theme.EscapeErrorContainer
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary

sealed class StaffTab(val title: String, val icon: ImageVector) {
    object Billing : StaffTab("Billing", Icons.Default.PointOfSale)
    object Products : StaffTab("Products", Icons.Default.Checkroom)
    object History : StaffTab("History", Icons.Default.History)
    object Profile : StaffTab("Profile", Icons.Default.Person)
}

@Composable
fun StaffShell(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = remember { listOf(StaffTab.Billing, StaffTab.Products, StaffTab.History, StaffTab.Profile) }
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showAdminLoginDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EscapeTopBar(
                title = "Escape Clothing",
                subtitle = "${currentUser?.fullName ?: "Staff"} • Butibori POS",
                connectionState = connectionState,
                onConnectionClick = { viewModel.toggleConnectionSimulation() },
                actions = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeSurfaceVariant)
                            .border(1.dp, EscapeBorder, RoundedCornerShape(8.dp))
                            .clickable { showAdminLoginDialog = true }
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Portal",
                            tint = EscapeRed,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Admin",
                            color = EscapeTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = EscapeSurfaceElevated,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = EscapeRed,
                            indicatorColor = EscapeRed,
                            unselectedIconColor = EscapeTextSecondary,
                            unselectedTextColor = EscapeTextSecondary
                        )
                    )
                }
            }
        },
        containerColor = EscapeSurface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> StaffBillingScreen(viewModel = viewModel)
                1 -> StaffProductsScreen(viewModel = viewModel)
                2 -> StaffHistoryScreen(viewModel = viewModel)
                3 -> StaffProfileReturnsScreen(viewModel = viewModel)
            }
        }
    }

    if (showAdminLoginDialog) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isVerifying by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showAdminLoginDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = EscapeSurface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EscapeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = "Admin", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Admin Access Verification", color = EscapeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Enter administrator credentials", color = EscapeTextSecondary, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Admin Email / Mobile") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = EscapeTextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_dialog_email"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password", tint = EscapeTextSecondary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_dialog_password"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(EscapeErrorContainer)
                                .padding(8.dp)
                        ) {
                            Text(errorMessage!!, color = EscapeError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showAdminLoginDialog = false },
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("Cancel", color = EscapeTextPrimary)
                        }
                        Button(
                            onClick = {
                                isVerifying = true
                                errorMessage = null
                                viewModel.loginAdmin(email, password) { success, err ->
                                    isVerifying = false
                                    if (success) {
                                        showAdminLoginDialog = false
                                    } else {
                                        errorMessage = err ?: "Invalid credentials"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            modifier = Modifier.weight(1f).height(44.dp).testTag("verify_admin_btn")
                        ) {
                            Text(if (isVerifying) "Verifying..." else "Enter Admin", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
