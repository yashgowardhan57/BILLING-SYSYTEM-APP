package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserEntity
import com.example.ui.PosViewModel
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeError
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSuccess
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffManagementModal(
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val staffList by viewModel.allStaff.collectAsStateWithLifecycle()
    var showAddStaffDialog by remember { mutableStateOf(false) }
    var editingStaff by remember { mutableStateOf<UserEntity?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EscapeSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Staff Management",
                        color = EscapeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Change staff passwords, emails & mobile access",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { showAddStaffDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp).testTag("add_staff_modal_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Add Staff", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (staffList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No staff members registered. Tap '+ Add Staff' to create one.",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(staffList) { staff ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = staff.fullName,
                                            color = EscapeTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (staff.isActive) EscapeSuccess.copy(alpha = 0.2f) else EscapeError.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (staff.isActive) "ACTIVE" else "INACTIVE",
                                                color = if (staff.isActive) EscapeSuccess else EscapeError,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "Mobile: ${staff.mobile ?: "N/A"} • PIN: ${staff.passwordHash}",
                                        color = EscapeTextSecondary,
                                        fontSize = 12.sp
                                    )
                                    if (!staff.email.isNullOrBlank()) {
                                        Text(
                                            text = "Email: ${staff.email}",
                                            color = EscapeTextTertiary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { editingStaff = staff },
                                    colors = ButtonDefaults.buttonColors(containerColor = EscapeSurfaceVariant),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("edit_staff_${staff.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = EscapeTextPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Staff", color = EscapeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ADD STAFF DIALOG
    if (showAddStaffDialog) {
        AddStaffDialog(
            onDismiss = { showAddStaffDialog = false },
            onSave = { name, email, mobile, pin ->
                viewModel.createStaff(name, email, mobile, pin) { success, _ ->
                    if (success) {
                        showAddStaffDialog = false
                    }
                }
            }
        )
    }

    // EDIT STAFF DIALOG (Admin change staff password and email)
    editingStaff?.let { staff ->
        EditStaffDialog(
            staff = staff,
            onDismiss = { editingStaff = null },
            onSave = { updatedName, updatedEmail, updatedMobile, updatedPin, isActive ->
                viewModel.updateStaff(
                    staffId = staff.id,
                    fullName = updatedName,
                    email = updatedEmail,
                    mobile = updatedMobile,
                    pin = updatedPin,
                    isActive = isActive
                ) { success, _ ->
                    if (success) {
                        editingStaff = null
                    }
                }
            }
        )
    }
}

@Composable
private fun AddStaffDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, email: String?, mobile: String, pin: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("1234") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = EscapeSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Staff Operator",
                    color = EscapeTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Create new POS billing account with email & PIN",
                    color = EscapeTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorText = null },
                    label = { Text("Staff Full Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EscapeTextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_staff_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorText = null },
                    label = { Text("Staff Email Address") },
                    placeholder = { Text("e.g. staff@escapeclothing.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EscapeTextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_staff_email_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it; errorText = null },
                    label = { Text("Mobile Number (Login ID) *") },
                    placeholder = { Text("10 digit mobile") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EscapeTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_staff_mobile_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it; errorText = null },
                    label = { Text("Password / PIN *") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EscapeTextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_staff_pin_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorText!!, color = EscapeError, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = EscapeTextPrimary)
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorText = "Please enter staff name"
                            } else if (mobile.isBlank()) {
                                errorText = "Please enter staff mobile number"
                            } else if (pin.isBlank()) {
                                errorText = "Please enter a PIN / Password"
                            } else {
                                onSave(name.trim(), email.trim().ifBlank { null }, mobile.trim(), pin.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        modifier = Modifier.weight(1f).testTag("save_new_staff_btn")
                    ) {
                        Text("Create Staff", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditStaffDialog(
    staff: UserEntity,
    onDismiss: () -> Unit,
    onSave: (fullName: String, email: String, mobile: String, pin: String, isActive: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(staff.fullName) }
    var email by remember { mutableStateOf(staff.email ?: "") }
    var mobile by remember { mutableStateOf(staff.mobile ?: "") }
    var pin by remember { mutableStateOf(staff.passwordHash) }
    var isActive by remember { mutableStateOf(staff.isActive) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = EscapeSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Edit Staff Account",
                            color = EscapeTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Admin can change staff password & email",
                            color = EscapeTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EscapeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorText = null },
                    label = { Text("Staff Full Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EscapeTextSecondary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_staff_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ADMIN CHANGE STAFF EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorText = null },
                    label = { Text("Staff Email Address") },
                    placeholder = { Text("e.g. staff@escapeclothing.com") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EscapeRed) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_staff_email_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ADMIN CHANGE STAFF MOBILE
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it; errorText = null },
                    label = { Text("Mobile Number (Login ID) *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EscapeTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_staff_mobile_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ADMIN CHANGE STAFF PASSWORD / PIN
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it; errorText = null },
                    label = { Text("Staff Password / PIN *") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EscapeRed) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_staff_pin_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EscapeSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Account Status", color = EscapeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(if (isActive) "Active (can login)" else "Disabled (login locked)", color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = EscapeRed, checkedTrackColor = EscapeRed.copy(alpha = 0.5f))
                    )
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorText!!, color = EscapeError, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = EscapeTextPrimary)
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorText = "Please enter staff name"
                            } else if (mobile.isBlank()) {
                                errorText = "Please enter mobile number"
                            } else if (pin.isBlank()) {
                                errorText = "Please enter staff password / PIN"
                            } else {
                                onSave(name.trim(), email.trim(), mobile.trim(), pin.trim(), isActive)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        modifier = Modifier.weight(1f).testTag("save_edited_staff_btn")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ADMIN CHANGE ADMIN PASSWORD AND EMAIL MODAL
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSecurityModal(
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var fullName by remember { mutableStateOf(currentUser?.fullName ?: "Store Manager (Admin)") }
    var email by remember { mutableStateOf(currentUser?.email ?: "admin@escapeclothing.com") }
    var mobile by remember { mutableStateOf(currentUser?.mobile ?: "9999900000") }
    var currentPass by remember { mutableStateOf("admin123") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EscapeSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Admin Profile & Security",
                            color = EscapeTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Change Admin password, email & credentials",
                            color = EscapeTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current info banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EscapeSurfaceVariant)
                    .border(1.dp, EscapeBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Current Administrator Account",
                        color = EscapeTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentUser?.fullName ?: "Admin"} • Role: ADMIN",
                        color = EscapeTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Email: ${currentUser?.email ?: "admin@escapeclothing.com"} • Mobile: ${currentUser?.mobile ?: "9999900000"}",
                        color = EscapeTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("ADMIN ACCOUNT DETAILS", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMessage = null },
                label = { Text("Admin Full Name *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EscapeTextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("admin_fullname_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ADMIN CHANGE ADMIN EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                label = { Text("Admin Email Address *") },
                placeholder = { Text("admin@escapeclothing.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EscapeRed) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("admin_email_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ADMIN CHANGE ADMIN MOBILE
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it; errorMessage = null },
                label = { Text("Admin Mobile Number *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EscapeTextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("admin_mobile_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("SECURITY / PASSWORD CHANGE", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = currentPass,
                onValueChange = { currentPass = it; errorMessage = null },
                label = { Text("Current Password * (to confirm identity)") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EscapeTextSecondary) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("admin_current_password_input"),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ADMIN CHANGE ADMIN PASSWORD
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it; errorMessage = null },
                label = { Text("New Admin Password (optional, leave blank to keep)") },
                placeholder = { Text("Enter new password") },
                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = EscapeRed) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("admin_new_password_input"),
                shape = RoundedCornerShape(8.dp)
            )

            if (newPass.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it; errorMessage = null },
                    label = { Text("Confirm New Password *") },
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = EscapeRed) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_confirm_password_input"),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(EscapeError.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Text(errorMessage!!, color = EscapeError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = EscapeTextPrimary)
                }

                Button(
                    onClick = {
                        if (email.isBlank() || !email.contains("@")) {
                            errorMessage = "Please enter a valid Admin email address."
                            return@Button
                        }
                        if (currentPass.isBlank()) {
                            errorMessage = "Please enter your current password to authorize changes."
                            return@Button
                        }
                        if (newPass.isNotBlank()) {
                            if (newPass.length < 4) {
                                errorMessage = "New password must be at least 4 characters."
                                return@Button
                            }
                            if (newPass != confirmPass) {
                                errorMessage = "New password and Confirm Password do not match."
                                return@Button
                            }
                        }

                        isSaving = true
                        errorMessage = null
                        viewModel.changeAdminCredentials(
                            email = email.trim(),
                            fullName = fullName.trim(),
                            mobile = mobile.trim(),
                            currentPass = currentPass.trim(),
                            newPass = if (newPass.isNotBlank()) newPass.trim() else null
                        ) { success, err ->
                            isSaving = false
                            if (success) {
                                onDismiss()
                            } else {
                                errorMessage = err ?: "Failed to update admin credentials."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    modifier = Modifier.weight(1f).testTag("save_admin_credentials_btn")
                ) {
                    Text(if (isSaving) "Saving..." else "Save Changes", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
