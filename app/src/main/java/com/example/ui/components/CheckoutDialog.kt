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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartItem
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeError
import com.example.ui.theme.EscapeErrorContainer
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSuccess
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary
import com.example.ui.theme.EscapeWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutDialog(
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onConfirm: (
        paymentMethod: String,
        paidAmountInput: Double,
        discountAmount: Double,
        customerName: String?,
        customerMobile: String?,
        notes: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val subtotal = remember(cartItems) { cartItems.sumOf { it.variant.sellingPrice * it.quantity } }
    var discountText by remember { mutableStateOf("") }
    val discountAmount = discountText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val totalAmount = (subtotal - discountAmount).coerceAtLeast(0.0)

    var paymentMethod by remember { mutableStateOf("CASH") } // CASH, ONLINE, PENDING
    var paidInputText by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerMobile by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val paidAmount by remember(paymentMethod, paidInputText, totalAmount) {
        derivedStateOf {
            if (paymentMethod == "PENDING") {
                paidInputText.toDoubleOrNull()?.coerceIn(0.0, totalAmount) ?: 0.0
            } else {
                totalAmount
            }
        }
    }
    val pendingAmount by remember(paymentMethod, paidAmount, totalAmount) {
        derivedStateOf {
            if (paymentMethod == "PENDING") (totalAmount - paidAmount).coerceAtLeast(0.0) else 0.0
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = EscapeSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "Checkout", tint = EscapeRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Checkout & Payment", color = EscapeTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EscapeTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bill Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Items in Bill (${cartItems.size} lines, ${cartItems.sumOf { it.quantity }.toInt()} pcs)", color = EscapeTextSecondary, fontSize = 13.sp)
                        Text(formatInr(subtotal), color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Discount (₹)", color = EscapeTextSecondary, fontSize = 13.sp)
                        OutlinedTextField(
                            value = discountText,
                            onValueChange = { discountText = it },
                            placeholder = { Text("0", color = EscapeTextTertiary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(110.dp).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EscapeRed,
                                unfocusedBorderColor = EscapeBorder,
                                focusedTextColor = EscapeTextPrimary,
                                unfocusedTextColor = EscapeTextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EscapeBorder))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("FINAL TOTAL", color = EscapeTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                        Text(formatInr(totalAmount), color = EscapeRed, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method Selection (CASH, ONLINE, PENDING)
            Text("Payment Method", color = EscapeTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethodOption(
                    title = "CASH",
                    icon = Icons.Default.Money,
                    isSelected = paymentMethod == "CASH",
                    onClick = {
                        paymentMethod = "CASH"
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodOption(
                    title = "ONLINE",
                    icon = Icons.Default.CreditCard,
                    isSelected = paymentMethod == "ONLINE",
                    onClick = {
                        paymentMethod = "ONLINE"
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodOption(
                    title = "PENDING",
                    icon = Icons.Default.PendingActions,
                    isSelected = paymentMethod == "PENDING",
                    onClick = {
                        paymentMethod = "PENDING"
                        if (paidInputText.isEmpty()) paidInputText = (totalAmount / 2).toInt().toString()
                        errorMessage = null
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Pending Payment breakdown section
            if (paymentMethod == "PENDING") {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EscapeWarning.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Pending Payment Details", color = EscapeWarning, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = paidInputText,
                            onValueChange = { paidInputText = it },
                            label = { Text("Initial Amount Paid (₹)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EscapeRed,
                                unfocusedBorderColor = EscapeBorder,
                                focusedTextColor = EscapeTextPrimary,
                                unfocusedTextColor = EscapeTextPrimary,
                                focusedLabelColor = EscapeRed
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Balance Due (Pending):", color = EscapeTextSecondary, fontSize = 13.sp)
                            Text(formatInr(pendingAmount), color = EscapeWarning, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Customer details
            val isPending = paymentMethod == "PENDING"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPending) "Customer Information (MANDATORY)" else "Customer Information (Optional)",
                    color = if (isPending) EscapeRed else EscapeTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerName,
                onValueChange = {
                    customerName = it
                    errorMessage = null
                },
                label = { Text(if (isPending) "Customer Name *" else "Customer Name (Optional)") },
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Name", tint = EscapeTextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("checkout_cust_name"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EscapeRed,
                    unfocusedBorderColor = EscapeBorder,
                    focusedTextColor = EscapeTextPrimary,
                    unfocusedTextColor = EscapeTextPrimary,
                    focusedLabelColor = EscapeRed
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = customerMobile,
                onValueChange = {
                    customerMobile = it
                    errorMessage = null
                },
                label = { Text(if (isPending) "Mobile Number *" else "Mobile Number (Optional)") },
                leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = "Mobile", tint = EscapeTextSecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().testTag("checkout_cust_mobile"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EscapeRed,
                    unfocusedBorderColor = EscapeBorder,
                    focusedTextColor = EscapeTextPrimary,
                    unfocusedTextColor = EscapeTextPrimary,
                    focusedLabelColor = EscapeRed
                )
            )

            // Optional note
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Bill Notes (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EscapeRed,
                    unfocusedBorderColor = EscapeBorder,
                    focusedTextColor = EscapeTextPrimary,
                    unfocusedTextColor = EscapeTextPrimary,
                    focusedLabelColor = EscapeRed
                )
            )

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

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm Button
            Button(
                onClick = {
                    if (isPending) {
                        if (customerName.trim().isBlank()) {
                            errorMessage = "Customer Name is required for Pending bills."
                            return@Button
                        }
                        if (customerMobile.trim().length < 10) {
                            errorMessage = "Valid 10-digit Mobile Number is required for Pending bills."
                            return@Button
                        }
                    }
                    onConfirm(
                        paymentMethod,
                        paidAmount,
                        discountAmount,
                        customerName.ifBlank { null },
                        customerMobile.ifBlank { null },
                        notes.ifBlank { null }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_save_bill_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Confirm & Save Bill • ${formatInr(totalAmount)}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) EscapeRed else EscapeSurface)
            .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else EscapeTextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else EscapeTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
