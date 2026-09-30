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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.ui.components.parseColorHex
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import com.example.ui.components.AdminSecurityModal
import com.example.ui.components.BackupRestoreModal
import com.example.ui.components.ProductsManagementModal
import com.example.ui.components.PurgeDemoDataDialog
import com.example.ui.components.StaffManagementModal
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AuditLogEntity
import com.example.data.ExpenseEntity
import com.example.data.ProductVariantDraft
import com.example.data.SaleEntity
import com.example.data.UserEntity
import com.example.ui.PosViewModel
import com.example.ui.components.formatInr
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeError
import com.example.ui.theme.EscapeErrorContainer
import com.example.ui.theme.EscapeInfo
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSuccess
import com.example.ui.theme.EscapeSuccessContainer
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary
import com.example.ui.theme.EscapeWarning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AdminMoreScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var activeModal by remember { mutableStateOf<String?>(null) }

    val pendingSales by viewModel.pendingSales.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val backupRecords by viewModel.backupRecords.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // GROUP 1: Management
        MoreSectionHeader("MANAGEMENT")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
        ) {
            Column {
                MoreMenuItem("Products & Variants", "Add and edit clothing catalog items", Icons.Default.Checkroom) { activeModal = "PRODUCTS" }
                MoreDivider()
                MoreMenuItem("Categories", "Manage clothing categories", Icons.Default.Category) { activeModal = "CATEGORIES" }
                MoreDivider()
                MoreMenuItem("Sizes & Colors", "Manage garment sizes and color palette", Icons.Default.FormatSize) { activeModal = "SIZES_COLORS" }
                MoreDivider()
                MoreMenuItem("Customers", "Customer profiles and purchase history", Icons.Default.People) { activeModal = "CUSTOMERS" }
                MoreDivider()
                MoreMenuItem("Staff Accounts & Passwords", "Admin change staff passwords, email & mobile", Icons.Default.Security) { activeModal = "STAFF" }
                MoreDivider()
                MoreMenuItem("Admin Profile & Security", "Admin change admin password, email & credentials", Icons.Default.AdminPanelSettings) { activeModal = "ADMIN_SECURITY" }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // GROUP 2: Finance
        MoreSectionHeader("FINANCE & AUDIT")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
        ) {
            Column {
                val pendingCount = pendingSales.size
                val pendingTotal = pendingSales.sumOf { it.pendingAmount }
                MoreMenuItem(
                    title = "Pending Payments",
                    subtitle = "$pendingCount due bills (${formatInr(pendingTotal)})",
                    icon = Icons.Default.PendingActions,
                    badge = if (pendingCount > 0) "$pendingCount DUE" else null
                ) { activeModal = "PENDING_PAYMENTS" }
                MoreDivider()
                MoreMenuItem("Shop Expenses", "Record electricity, maintenance, packaging & staff expenses", Icons.Default.CreditCard) { activeModal = "EXPENSES" }
                MoreDivider()
                MoreMenuItem("Returns & Exchanges Log", "Trace customer returns, refunds and item swaps", Icons.Default.AssignmentReturn) { activeModal = "RETURNS_EXCHANGES" }
                MoreDivider()
                MoreMenuItem("Daily Closing", "Review daily reconciliations and mark day closed", Icons.Default.Today) { activeModal = "DAILY_CLOSING" }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // GROUP 3: Data & System
        MoreSectionHeader("SYSTEM & DATA")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
        ) {
            Column {
                val lastBackup = backupRecords.firstOrNull()?.let {
                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(it.createdAt))
                } ?: "Never"
                MoreMenuItem("Backup & Restore (Supabase)", "Last snapshot: $lastBackup • Cloud synced", Icons.Default.Backup) { activeModal = "BACKUP_RESTORE" }
                MoreDivider()
                MoreMenuItem("Remove All Demo Data", "Purge test sales, bills, returns & expenses", Icons.Default.DeleteSweep) { activeModal = "PURGE_DEMO" }
                MoreDivider()
                MoreMenuItem("Audit Trail Log", "Immutable chronological record of all store events", Icons.Default.History) { activeModal = "AUDIT_LOG" }
                MoreDivider()
                MoreMenuItem("Shop Settings", "Address, thermal printer (58/80mm), return policy", Icons.Default.Settings) { activeModal = "SETTINGS" }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Role Switch / Logout button
        Button(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EscapeSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Switch Account / Logout", color = EscapeRed, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Interactive Modals
    when (activeModal) {
        "PENDING_PAYMENTS" -> PendingPaymentsModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "EXPENSES" -> ExpensesModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "DAILY_CLOSING" -> DailyClosingModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "BACKUP_RESTORE" -> BackupRestoreModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "PURGE_DEMO" -> PurgeDemoDataDialog(viewModel = viewModel, onDismiss = { activeModal = null })
        "AUDIT_LOG" -> AuditLogModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "SETTINGS" -> SettingsModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "PRODUCTS" -> ProductsManagementModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "CATEGORIES" -> CategoriesManagementModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "SIZES_COLORS" -> SizesColorsModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "CUSTOMERS" -> CustomersModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "STAFF" -> StaffManagementModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "ADMIN_SECURITY" -> AdminSecurityModal(viewModel = viewModel, onDismiss = { activeModal = null })
        "RETURNS_EXCHANGES" -> ReturnsExchangesModal(viewModel = viewModel, onDismiss = { activeModal = null })
    }
}

@Composable
private fun MoreSectionHeader(title: String) {
    Text(
        text = title,
        color = EscapeTextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun MoreMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EscapeSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = EscapeRed, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = EscapeTextTertiary, fontSize = 11.sp)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EscapeWarning.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(badge, color = EscapeWarning, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Open", tint = EscapeTextTertiary)
        }
    }
}

@Composable
private fun MoreDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EscapeBorder))
}

// -------------------------------------------------------------
// MODALS
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PendingPaymentsModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val pendingSales by viewModel.pendingSales.collectAsStateWithLifecycle()
    var collectingSale by remember { mutableStateOf<SaleEntity?>(null) }
    var collectAmount by remember { mutableStateOf("") }
    var collectMethod by remember { mutableStateOf("CASH") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Pending Payments Collection", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Customer receivables requiring collection", color = EscapeTextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(14.dp))

            if (pendingSales.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No pending payments outstanding! All customer accounts cleared.", color = EscapeSuccess, fontSize = 13.sp)
                }
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(pendingSales) { sale ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(sale.customerName ?: "Customer", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Mobile: ${sale.customerMobile ?: ""} • Invoice: ${sale.invoiceNumber}", color = EscapeTextSecondary, fontSize = 11.sp)
                                    Text("Due: ${formatInr(sale.pendingAmount)} (Paid: ${formatInr(sale.paidAmount)} / ${formatInr(sale.totalAmount)})", color = EscapeWarning, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        collectingSale = sale
                                        collectAmount = sale.pendingAmount.toInt().toString()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp).testTag("collect_btn_${sale.invoiceNumber}")
                                ) {
                                    Text("Collect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    collectingSale?.let { sale ->
        Dialog(onDismissRequest = { collectingSale = null }) {
            Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), color = EscapeSurface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Record Payment Collection", color = EscapeTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Customer: ${sale.customerName} • Due: ${formatInr(sale.pendingAmount)}", color = EscapeWarning, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = collectAmount,
                        onValueChange = { collectAmount = it },
                        label = { Text("Amount Collected (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Payment Method", color = EscapeTextSecondary, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = collectMethod == "CASH", onClick = { collectMethod = "CASH" }, colors = RadioButtonDefaults.colors(selectedColor = EscapeRed))
                            Text("Cash", color = EscapeTextPrimary, fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = collectMethod == "ONLINE", onClick = { collectMethod = "ONLINE" }, colors = RadioButtonDefaults.colors(selectedColor = EscapeRed))
                            Text("Online (UPI/Bank)", color = EscapeTextPrimary, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { collectingSale = null }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = EscapeTextPrimary)
                        }
                        Button(
                            onClick = {
                                val amt = collectAmount.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    viewModel.collectPendingPayment(sale.id, amt, collectMethod, "Admin collection") { success, _ ->
                                        if (success) collectingSale = null
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            modifier = Modifier.weight(1f).testTag("confirm_collection_btn")
                        ) {
                            Text("Confirm", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpensesModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val expenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val categories by viewModel.expenseCategories.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var newAmount by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var newCat by remember { mutableStateOf(categories.firstOrNull()?.name ?: "Packaging") }
    var newMethod by remember { mutableStateOf("CASH") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Store Expenses", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Packaging, electricity, repairs & transport", color = EscapeTextSecondary, fontSize = 12.sp)
                }
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp).testTag("add_expense_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 380.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(expenses) { exp ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exp.categoryName, color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("${exp.expenseDate} • ${exp.description}", color = EscapeTextSecondary, fontSize = 11.sp)
                                Text("Mode: ${exp.paymentMethod} • Recorded by ${exp.createdByName}", color = EscapeTextTertiary, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatInr(exp.amount), color = EscapeRed, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                if (exp.status == "ACTIVE") {
                                    OutlinedButton(
                                        onClick = { viewModel.voidExpense(exp.id, "Voided by admin") },
                                        modifier = Modifier.height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Void", color = EscapeError, fontSize = 10.sp)
                                    }
                                } else {
                                    Text("VOID", color = EscapeError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), color = EscapeSurface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Add Store Expense", color = EscapeTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newAmount,
                        onValueChange = { newAmount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("expense_amount_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Description") },
                        placeholder = { Text("e.g. Carry bags, Shop bulb replacement") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EscapeRed,
                            unfocusedBorderColor = EscapeBorder,
                            focusedTextColor = EscapeTextPrimary,
                            unfocusedTextColor = EscapeTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Category: $newCat", color = EscapeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Packaging", "Electricity", "Repairing", "Transport").forEach { c ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (newCat == c) EscapeRed else EscapeSurfaceVariant)
                                    .clickable { newCat = c }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(c, color = if (newCat == c) Color.White else EscapeTextPrimary, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showAddDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = EscapeTextPrimary)
                        }
                        Button(
                            onClick = {
                                val amt = newAmount.toDoubleOrNull() ?: 0.0
                                if (amt > 0 && newDesc.isNotBlank()) {
                                    viewModel.addExpense(newCat, amt, newDesc, newMethod, null) { success, _ ->
                                        if (success) showAddDialog = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            modifier = Modifier.weight(1f).testTag("confirm_add_expense_btn")
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyClosingModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val today = remember { dateFormat.format(Date()) }
    val closings by viewModel.allDailyClosings.collectAsStateWithLifecycle()
    val todayClosing = closings.find { it.businessDate == today }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Daily Store Closing", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Date: $today • Butibori Store", color = EscapeTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            if (todayClosing?.status == "CLOSED") {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(EscapeSuccessContainer).padding(12.dp)
                ) {
                    Column {
                        Text("Day $today is officially CLOSED", color = EscapeSuccess, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Bills: ${todayClosing.billCount} • Gross Revenue: ${formatInr(todayClosing.grossRevenue)} • Net Profit: ${formatInr(todayClosing.netOperatingProfit)}", color = EscapeTextPrimary, fontSize = 12.sp)
                    }
                }
            } else {
                Text(
                    text = "Review all sales, cash, online, pending balances and expenses before closing. Closing creates an immutable daily summary record.",
                    color = EscapeTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.executeDailyClosing(today) { success, _ ->
                            if (success) onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("mark_day_closed_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Mark Business Day Closed", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuditLogModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val logs by viewModel.auditLogs.collectAsStateWithLifecycle()

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Immutable Audit Trail", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Complete traceability for sales, returns, adjustments & finance", color = EscapeTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(logs) { log ->
                    val dateFormatted = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()).format(Date(log.createdAt))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(log.action, color = EscapeRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(dateFormatted, color = EscapeTextTertiary, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(log.details, color = EscapeTextPrimary, fontSize = 12.sp)
                            Text("By ${log.userName} (${log.role})", color = EscapeTextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var shopName by remember { mutableStateOf(settings?.businessName ?: "Escape Clothing") }
    var addr1 by remember { mutableStateOf(settings?.addressLine1 ?: "City Center 2, Butibori") }
    var addr2 by remember { mutableStateOf(settings?.addressLine2 ?: "441122, Maharashtra, India") }
    var phone by remember { mutableStateOf(settings?.phone ?: "+91 98765 43210") }
    var returnPolicy by remember { mutableStateOf(settings?.returnPolicy ?: "Returns & exchanges accepted within 7 days.") }
    var paperWidth by remember { mutableStateOf(settings?.printerPaperWidth ?: 80) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Store Settings", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Escape Clothing • Configuration", color = EscapeTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = addr1,
                onValueChange = { addr1 = it },
                label = { Text("Address Line 1") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = addr2,
                onValueChange = { addr2 = it },
                label = { Text("Address Line 2 (City / Pincode)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Store Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("Thermal Printer Width", color = EscapeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                listOf(58 to "58 mm", 80 to "80 mm (Default)").forEach { (w, label) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (paperWidth == w) EscapeRed else EscapeSurfaceVariant)
                            .clickable { paperWidth = w }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(label, color = if (paperWidth == w) Color.White else EscapeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = returnPolicy,
                onValueChange = { returnPolicy = it },
                label = { Text("Return Policy Text") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateShopDetails(shopName, addr1, addr2, phone, returnPolicy)
                    viewModel.updatePrinterPaperWidth(paperWidth)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save Settings", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CategoriesManagementModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var newCatName by remember { mutableStateOf("") }
    var newCatDesc by remember { mutableStateOf("") }

    val quickSuggestions = remember {
        listOf("Ethnic Wear", "Activewear", "Footwear", "Accessories", "Winter Wear", "Party Wear", "Nightwear", "Formal Wear", "Loungewear")
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
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
                Column {
                    Text("Clothing Categories", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Add or remove product categories for Escape Clothing", color = EscapeTextSecondary, fontSize = 12.sp)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EscapeTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Add Category Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Add New Category", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        placeholder = { Text("Category name (e.g. Ethnic Wear)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_category_input"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCatDesc,
                        onValueChange = { newCatDesc = it },
                        placeholder = { Text("Description (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (newCatName.isNotBlank()) {
                                viewModel.addCategory(newCatName.trim(), newCatDesc.trim().ifBlank { null })
                                newCatName = ""
                                newCatDesc = ""
                            }
                        },
                        enabled = newCatName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("add_category_confirm_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Category", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Quick Suggestions:", color = EscapeTextTertiary, fontSize = 10.sp)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        quickSuggestions
                            .filter { suggestion -> categories.none { it.name.equals(suggestion, ignoreCase = true) } }
                            .take(6)
                            .forEach { suggestion ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EscapeSurfaceVariant)
                                        .clickable { newCatName = suggestion }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(suggestion, color = EscapeTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Current Categories (${categories.size}):", color = EscapeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            if (categories.isEmpty()) {
                Text("No categories yet. Add one above.", color = EscapeTextTertiary, fontSize = 12.sp)
            } else {
                categories.forEach { cat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, EscapeBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cat.name, color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                if (!cat.description.isNullOrBlank()) {
                                    Text(cat.description, color = EscapeTextSecondary, fontSize = 11.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(EscapeSuccess.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(cat.status, color = EscapeSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.deleteCategory(cat.id) },
                                    modifier = Modifier.size(28.dp).testTag("delete_category_${cat.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove category ${cat.name}",
                                        tint = EscapeError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SizesColorsModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val sizes by viewModel.sizes.collectAsStateWithLifecycle()
    val colors by viewModel.colors.collectAsStateWithLifecycle()

    var newSizeName by remember { mutableStateOf("") }
    var newColorName by remember { mutableStateOf("") }
    var newColorHex by remember { mutableStateOf("#800000") }

    val presetColors = remember {
        listOf(
            "Black" to "#000000",
            "White" to "#FFFFFF",
            "Navy" to "#000080",
            "Red" to "#D32F2F",
            "Maroon" to "#800000",
            "Olive" to "#556B2F",
            "Sky Blue" to "#87CEEB",
            "Lavender" to "#BA68C8",
            "Beige" to "#D7CCC8",
            "Pink" to "#F06292",
            "Grey" to "#757575",
            "Yellow" to "#FBC02D",
            "Green" to "#388E3C",
            "Brown" to "#6D4C41",
            "Teal" to "#00897B",
            "Purple" to "#7B1FA2"
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp).verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Garment Sizes & Color Palette", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EscapeTextSecondary)
                }
            }
            Text("Admin can add or remove sizes and colors for clothing products", color = EscapeTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== SECTION 1: SIZES ====================
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Add New Garment Size", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newSizeName,
                            onValueChange = { newSizeName = it },
                            placeholder = { Text("e.g. 38, 40, 42, XXL, Free Size") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("admin_more_new_size_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newSizeName.isNotBlank()) {
                                    viewModel.addSize(newSizeName.trim())
                                    newSizeName = ""
                                }
                            },
                            enabled = newSizeName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_more_add_size_btn")
                        ) {
                            Text("Add Size", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Current Sizes (${sizes.size}):", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        sizes.forEach { sz ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EscapeSurfaceVariant)
                                    .border(1.dp, EscapeBorder, RoundedCornerShape(6.dp))
                                    .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(sz.name, color = EscapeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { viewModel.deleteSize(sz.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete ${sz.name}", tint = EscapeError, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== SECTION 2: COLORS ====================
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Add New Fabric Color", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newColorName,
                            onValueChange = { newColorName = it },
                            placeholder = { Text("e.g. Maroon, Lavender, Olive") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("admin_more_new_color_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newColorName.isNotBlank()) {
                                    viewModel.addColor(newColorName.trim(), newColorHex.trim())
                                    newColorName = ""
                                }
                            },
                            enabled = newColorName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_more_add_color_btn")
                        ) {
                            Text("Add Color", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select Palette Swatch:", color = EscapeTextTertiary, fontSize = 10.sp)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        presetColors.forEach { (presetName, hex) ->
                            val isSel = newColorHex.equals(hex, ignoreCase = true)
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) EscapeRed.copy(alpha = 0.2f) else EscapeSurfaceVariant)
                                    .border(1.dp, if (isSel) EscapeRed else EscapeBorder, RoundedCornerShape(4.dp))
                                    .clickable {
                                        newColorName = presetName
                                        newColorHex = hex
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(parseColorHex(hex))
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(presetName, color = EscapeTextPrimary, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Current Colors (${colors.size}):", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        colors.forEach { c ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EscapeSurfaceVariant)
                                    .border(1.dp, EscapeBorder, RoundedCornerShape(6.dp))
                                    .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(parseColorHex(c.hexCode))
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(c.name, color = EscapeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { viewModel.deleteColor(c.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete ${c.name}", tint = EscapeError, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomersModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val customers by viewModel.allCustomers.collectAsStateWithLifecycle()

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Customers Directory", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("${customers.size} registered customer records", color = EscapeTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 350.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(customers) { c ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = EscapeSurface)) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(c.name, color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Mobile: ${c.mobile}", color = EscapeTextSecondary, fontSize = 12.sp)
                                if (!c.notes.isNullOrBlank()) {
                                    Text("Note: ${c.notes}", color = EscapeTextTertiary, fontSize = 11.sp)
                                }
                            }
                            Text(if (c.isActive) "ACTIVE" else "INACTIVE", color = EscapeSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReturnsExchangesModal(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val returns by viewModel.allReturns.collectAsStateWithLifecycle()
    val exchanges by viewModel.allExchanges.collectAsStateWithLifecycle()

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = EscapeSurfaceElevated) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Returns & Exchanges Ledger", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("History of customer returns and swaps", color = EscapeTextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 380.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(returns) { ret ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = EscapeSurface)) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(ret.returnNumber, color = EscapeRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("For Bill: ${ret.originalInvoiceNumber} • By ${ret.processedByName}", color = EscapeTextSecondary, fontSize = 11.sp)
                                Text("Reason: ${ret.reason}", color = EscapeTextTertiary, fontSize = 10.sp)
                            }
                            Text("Refund: ${formatInr(ret.refundAmount)}", color = EscapeError, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                items(exchanges) { ex ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = EscapeSurface)) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(ex.exchangeNumber, color = EscapeInfo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("For Bill: ${ex.originalInvoiceNumber}", color = EscapeTextSecondary, fontSize = 11.sp)
                            }
                            val diffText = if (ex.customerDifference > 0) "+${formatInr(ex.customerDifference)}" else "Even"
                            Text("Diff: $diffText", color = EscapeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
