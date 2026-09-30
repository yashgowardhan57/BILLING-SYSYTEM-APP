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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ReturnItemRequest
import com.example.data.SaleEntity
import com.example.data.SaleItemEntity
import com.example.ui.PosViewModel
import com.example.ui.components.formatInr
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
import kotlinx.coroutines.launch

@Composable
fun StaffProfileReturnsScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allSales by viewModel.allSales.collectAsStateWithLifecycle()
    val allVariants by viewModel.allVariants.collectAsStateWithLifecycle()

    var invoiceSearchQuery by remember { mutableStateOf("") }
    var selectedSale by remember { mutableStateOf<SaleEntity?>(null) }
    var saleItems by remember { mutableStateOf<List<SaleItemEntity>>(emptyList()) }

    var isExchangeMode by remember { mutableStateOf(false) }
    var selectedItemId by remember { mutableStateOf<String?>(null) }
    var returnQty by remember { mutableDoubleStateOf(1.0) }
    var refundMethod by remember { mutableStateOf("CASH") } // CASH, ONLINE
    var returnReason by remember { mutableStateOf("WRONG_SIZE") }
    var replacementVariantId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Staff Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(EscapeRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Staff", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser?.fullName ?: "Staff Member",
                        color = EscapeTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Role: ${currentUser?.role ?: "STAFF"} • Mobile: ${currentUser?.mobile ?: ""}",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Store: City Center 2, Butibori (441122)",
                        color = EscapeTextTertiary,
                        fontSize = 11.sp
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EscapeRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EscapeRed)
                ) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Exit", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Return / Exchange Section
        Text(
            text = "RETURNS & EXCHANGES",
            color = EscapeTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find original invoice to process item returns, refund cash/online, or exchange for a replacement product.",
            color = EscapeTextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Invoice Lookup
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = invoiceSearchQuery,
                onValueChange = { invoiceSearchQuery = it },
                placeholder = { Text("Enter Invoice # (e.g. ESC-2026-001001)", fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EscapeRed,
                    unfocusedBorderColor = EscapeBorder,
                    focusedTextColor = EscapeTextPrimary,
                    unfocusedTextColor = EscapeTextPrimary,
                    focusedContainerColor = EscapeSurfaceElevated,
                    unfocusedContainerColor = EscapeSurfaceElevated
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val q = invoiceSearchQuery.trim()
                    val found = allSales.find { it.invoiceNumber.equals(q, ignoreCase = true) }
                    if (found != null) {
                        selectedSale = found
                        // Load items
                        coroutineScope.launch {
                            saleItems = viewModel.db.saleItemDao().getItemsForSaleSync(found.id)
                            selectedItemId = saleItems.firstOrNull()?.id
                        }
                        viewModel.showMessage("Invoice loaded: ${found.invoiceNumber}")
                    } else {
                        viewModel.showMessage("No invoice matching '$q' found")
                    }
                },
                modifier = Modifier.height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Lookup")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Found Sale & Return/Exchange Box
        selectedSale?.let { sale ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(sale.invoiceNumber, color = EscapeRed, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Date: ${sale.businessDate} • Total: ${formatInr(sale.totalAmount)}", color = EscapeTextSecondary, fontSize = 12.sp)
                            if (!sale.customerName.isNullOrBlank()) {
                                Text("Customer: ${sale.customerName} (${sale.customerMobile ?: ""})", color = EscapeTextPrimary, fontSize = 12.sp)
                            }
                        }
                        Text(sale.status, color = EscapeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EscapeBorder))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Item to Return / Exchange:", color = EscapeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    saleItems.forEach { item ->
                        val availableReturn = item.quantity - item.returnedQuantity
                        val isSelected = selectedItemId == item.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EscapeSurfaceVariant else EscapeSurface)
                                .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(8.dp))
                                .clickable(enabled = availableReturn > 0) {
                                    selectedItemId = item.id
                                    returnQty = 1.0
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.productName} [${item.size}/${item.color}]",
                                    color = if (availableReturn > 0) EscapeTextPrimary else EscapeTextTertiary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Purchased: ${item.quantity.toInt()} • Returned: ${item.returnedQuantity.toInt()} • Rate: ${formatInr(item.unitPrice)}",
                                    color = EscapeTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            if (availableReturn > 0) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedItemId = item.id
                                        returnQty = 1.0
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = EscapeRed)
                                )
                            } else {
                                Text("Fully Returned", color = EscapeError, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    selectedItemId?.let { sId ->
                        val activeItem = saleItems.find { it.id == sId }
                        if (activeItem != null) {
                            Spacer(modifier = Modifier.height(12.dp))

                            // Mode toggle: Return with Refund vs Exchange
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { isExchangeMode = false },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (!isExchangeMode) EscapeRed else Color.Transparent,
                                        contentColor = if (!isExchangeMode) Color.White else EscapeTextPrimary
                                    )
                                ) {
                                    Text("Refund Return", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { isExchangeMode = true },
                                    modifier = Modifier.weight(1f).height(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isExchangeMode) EscapeRed else Color.Transparent,
                                        contentColor = if (isExchangeMode) Color.White else EscapeTextPrimary
                                    )
                                ) {
                                    Text("Exchange Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!isExchangeMode) {
                                // REFUND FLOW
                                Text("Refund Method:", color = EscapeTextSecondary, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = refundMethod == "CASH", onClick = { refundMethod = "CASH" }, colors = RadioButtonDefaults.colors(selectedColor = EscapeRed))
                                        Text("Cash", color = EscapeTextPrimary, fontSize = 13.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = refundMethod == "ONLINE", onClick = { refundMethod = "ONLINE" }, colors = RadioButtonDefaults.colors(selectedColor = EscapeRed))
                                        Text("Online", color = EscapeTextPrimary, fontSize = 13.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Refund Amount:", color = EscapeTextSecondary, fontSize = 13.sp)
                                    Text(formatInr(activeItem.unitPrice * returnQty), color = EscapeRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        viewModel.processReturn(
                                            originalSaleId = sale.id,
                                            returnItems = listOf(ReturnItemRequest(activeItem.id, returnQty)),
                                            refundMethod = refundMethod,
                                            reason = returnReason,
                                            notes = null,
                                            onComplete = { success, msg ->
                                                if (success) {
                                                    selectedSale = null
                                                }
                                            }
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Confirm Return & Refund • ${formatInr(activeItem.unitPrice * returnQty)}", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                // EXCHANGE FLOW
                                Text("Select Replacement Product:", color = EscapeTextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(6.dp))

                                allVariants.take(4).forEach { replVar ->
                                    val isReplSelected = replacementVariantId == replVar.id
                                    val diff = replVar.sellingPrice - activeItem.unitPrice
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isReplSelected) EscapeSurfaceVariant else EscapeSurface)
                                            .border(1.dp, if (isReplSelected) EscapeRed else EscapeBorder, RoundedCornerShape(8.dp))
                                            .clickable { replacementVariantId = replVar.id }
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("${replVar.productName} [${replVar.sizeName}/${replVar.colorName}]", color = EscapeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = if (diff > 0) "Customer pays +${formatInr(diff)}"
                                                else if (diff < 0) "Shop refunds ${formatInr(-diff)}"
                                                else "Equal value (₹0)",
                                                color = if (diff > 0) EscapeRed else if (diff < 0) EscapeSuccess else EscapeTextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        RadioButton(selected = isReplSelected, onClick = { replacementVariantId = replVar.id }, colors = RadioButtonDefaults.colors(selectedColor = EscapeRed))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                replacementVariantId?.let { rId ->
                                    val repl = allVariants.find { it.id == rId }
                                    if (repl != null) {
                                        val diff = repl.sellingPrice - activeItem.unitPrice
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                viewModel.processExchange(
                                                    originalSaleId = sale.id,
                                                    returnedSaleItemId = activeItem.id,
                                                    returnedQuantity = 1.0,
                                                    replacementVariantId = repl.id,
                                                    replacementQuantity = 1.0,
                                                    notes = "Store exchange",
                                                    onComplete = { success, msg ->
                                                        if (success) {
                                                            selectedSale = null
                                                        }
                                                    }
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth().height(48.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = if (diff > 0) "Confirm Exchange (Customer pays ${formatInr(diff)})"
                                                else if (diff < 0) "Confirm Exchange (Refund ${formatInr(-diff)})"
                                                else "Confirm Exchange (₹0)",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
