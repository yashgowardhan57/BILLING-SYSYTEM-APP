package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SaleEntity
import com.example.ui.PosViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.components.ThermalReceiptDialog
import com.example.ui.components.formatInr
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeInfo
import com.example.ui.theme.EscapeInfoContainer
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StaffHistoryScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val sales by viewModel.staff7DaysSales.collectAsStateWithLifecycle()
    var searchInvoice by remember { mutableStateOf("") }
    val activeReceipt by viewModel.activeReceiptModal.collectAsStateWithLifecycle()

    val filtered = remember(sales, searchInvoice) {
        val q = searchInvoice.trim().lowercase()
        if (q.isEmpty()) sales
        else sales.filter {
            it.invoiceNumber.lowercase().contains(q) ||
                (it.customerName?.lowercase()?.contains(q) == true) ||
                (it.customerMobile?.contains(q) == true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
    ) {
        // Staff 7-day restriction info notice banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EscapeInfoContainer)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = EscapeInfo, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Staff Access: Displaying recent 7 days of store billing history.",
                    color = EscapeInfo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchInvoice,
            onValueChange = { searchInvoice = it },
            placeholder = { Text("Filter by invoice # or customer...", color = EscapeTextTertiary, fontSize = 13.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EscapeRed,
                unfocusedBorderColor = EscapeBorder,
                focusedTextColor = EscapeTextPrimary,
                unfocusedTextColor = EscapeTextPrimary,
                focusedContainerColor = EscapeSurfaceElevated,
                unfocusedContainerColor = EscapeSurfaceElevated
            )
        )

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = "Empty", tint = EscapeTextTertiary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No billing history found", color = EscapeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Bills created in the last 7 days will appear here", color = EscapeTextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { sale ->
                    StaffSaleCard(
                        sale = sale,
                        onClick = { viewModel.openReceiptModal(sale) }
                    )
                }
            }
        }
    }

    activeReceipt?.let { receiptData ->
        ThermalReceiptDialog(
            data = receiptData,
            onDismiss = { viewModel.closeReceiptModal() },
            onNewBill = {
                viewModel.closeReceiptModal()
                viewModel.clearCart()
            }
        )
    }
}

@Composable
private fun StaffSaleCard(
    sale: SaleEntity,
    onClick: () -> Unit
) {
    val dateFormatted = remember(sale.createdAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(sale.createdAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("sale_card_${sale.invoiceNumber}"),
        colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = sale.invoiceNumber,
                        color = EscapeRed,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dateFormatted,
                        color = EscapeTextSecondary,
                        fontSize = 11.sp
                    )
                }
                StatusBadge(status = sale.paymentStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EscapeBorder))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (!sale.customerName.isNullOrBlank()) {
                        Text(
                            text = "Customer: ${sale.customerName} (${sale.customerMobile ?: ""})",
                            color = EscapeTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Walk-in Customer",
                            color = EscapeTextTertiary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "Mode: ${sale.paymentMethod} • Billed by: ${sale.staffName}",
                        color = EscapeTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatInr(sale.totalAmount),
                        color = EscapeTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (sale.pendingAmount > 0) {
                        Text(
                            text = "Due: ${formatInr(sale.pendingAmount)}",
                            color = Color(0xFFF2B84B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
