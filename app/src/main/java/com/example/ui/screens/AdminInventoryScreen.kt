package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.InventoryMovementEntity
import com.example.data.ProductVariantEntity
import com.example.ui.PosViewModel
import com.example.ui.components.StockBadge
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
import com.example.ui.theme.EscapeWarning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInventoryScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val variants by viewModel.allVariants.collectAsStateWithLifecycle()
    val movements by viewModel.allMovements.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var stockFilter by remember { mutableStateOf("ALL") } // ALL, LOW, OUT

    var adjustingVariant by remember { mutableStateOf<ProductVariantEntity?>(null) }
    var viewMovementVariant by remember { mutableStateOf<ProductVariantEntity?>(null) }

    val filtered = remember(variants, searchQuery, stockFilter) {
        variants.filter { v ->
            val matchStock = when (stockFilter) {
                "LOW" -> v.stockQuantity in 0.001..v.reorderLevel
                "OUT" -> v.stockQuantity <= 0
                else -> true
            }
            val q = searchQuery.trim().lowercase()
            val matchQuery = q.isEmpty() ||
                v.productName.lowercase().contains(q) ||
                v.sku.lowercase().contains(q) ||
                v.sizeName.lowercase().contains(q) ||
                v.colorName.lowercase().contains(q)
            matchStock && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
    ) {
        // Search & Filter header
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter inventory by SKU, name or size...", color = EscapeTextTertiary, fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = EscapeTextSecondary) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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

        // Filter chips: ALL, LOW STOCK, OUT OF STOCK
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                InventoryStatusChip(
                    label = "All Variants (${variants.size})",
                    isSelected = stockFilter == "ALL",
                    onClick = { stockFilter = "ALL" }
                )
            }
            item {
                val lowCount = variants.count { it.stockQuantity in 0.001..it.reorderLevel }
                InventoryStatusChip(
                    label = "Low Stock ($lowCount)",
                    isSelected = stockFilter == "LOW",
                    onClick = { stockFilter = "LOW" },
                    accentColor = EscapeWarning
                )
            }
            item {
                val outCount = variants.count { it.stockQuantity <= 0 }
                InventoryStatusChip(
                    label = "Out of Stock ($outCount)",
                    isSelected = stockFilter == "OUT",
                    onClick = { stockFilter = "OUT" },
                    accentColor = EscapeError
                )
            }
        }

        // Inventory table / list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { variant ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = variant.productName,
                                    color = EscapeTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "SKU: ${variant.sku} • Size: ${variant.sizeName} • ${variant.colorName}",
                                    color = EscapeTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            StockBadge(quantity = variant.stockQuantity, reorderLevel = variant.reorderLevel)
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
                                Text("Cost: ${formatInr(variant.costPrice)} • Sell: ${formatInr(variant.sellingPrice)}", color = EscapeTextTertiary, fontSize = 11.sp)
                                Text("Reorder threshold: ${variant.reorderLevel.toInt()} units", color = EscapeTextSecondary, fontSize = 11.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewMovementVariant = variant },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EscapeTextPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                                ) {
                                    Icon(imageVector = Icons.Default.History, contentDescription = "History", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Log", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { adjustingVariant = variant },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp).testTag("adjust_stock_btn_${variant.sku}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed)
                                ) {
                                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Adjust", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Adjust", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Stock Adjustment Dialog
    adjustingVariant?.let { variant ->
        StockAdjustmentDialog(
            variant = variant,
            onDismiss = { adjustingVariant = null },
            onConfirm = { delta, reason, notes ->
                viewModel.adjustInventory(
                    variantId = variant.id,
                    delta = delta,
                    reason = reason,
                    notes = notes,
                    onComplete = { success, msg ->
                        if (success) adjustingVariant = null
                    }
                )
            }
        )
    }

    // Movement History Sheet
    viewMovementVariant?.let { variant ->
        val variantMovements = movements.filter { it.variantId == variant.id }
        ModalBottomSheet(
            onDismissRequest = { viewMovementVariant = null },
            containerColor = EscapeSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Inventory History: ${variant.sku}",
                    color = EscapeTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${variant.productName} [${variant.sizeName}/${variant.colorName}] • Current Stock: ${variant.stockQuantity.toInt()}",
                    color = EscapeTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (variantMovements.isEmpty()) {
                    Text("No movement history found for this variant.", color = EscapeTextTertiary, fontSize = 13.sp)
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(variantMovements) { m ->
                            val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(m.createdAt))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EscapeSurface)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(m.reason, color = EscapeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    Text("${m.movementType} • By ${m.userName} • $dateStr", color = EscapeTextSecondary, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    val deltaStr = if (m.quantityDelta > 0) "+${m.quantityDelta.toInt()}" else "${m.quantityDelta.toInt()}"
                                    Text(
                                        text = deltaStr,
                                        color = if (m.quantityDelta > 0) EscapeSuccess else EscapeError,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("${m.previousQuantity.toInt()} → ${m.newQuantity.toInt()}", color = EscapeTextTertiary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun StockAdjustmentDialog(
    variant: ProductVariantEntity,
    onDismiss: () -> Unit,
    onConfirm: (delta: Double, reason: String, notes: String?) -> Unit
) {
    var deltaInput by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("PURCHASE") } // PURCHASE, DAMAGE, CORRECTION, AUDIT
    var notes by remember { mutableStateOf("") }

    val reasons = listOf("PURCHASE", "DAMAGE", "CORRECTION", "STOCK_TAKE")

    val delta = deltaInput.toDoubleOrNull() ?: 0.0
    val newStock = (variant.stockQuantity + delta).coerceAtLeast(0.0)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            color = EscapeSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Adjust Inventory", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${variant.productName} • ${variant.sku}", color = EscapeTextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(14.dp))

                // Before -> After Box (MANDATORY Trust pattern from design doc)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Current Stock", color = EscapeTextSecondary, fontSize = 11.sp)
                            Text("${variant.stockQuantity.toInt()}", color = EscapeTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("➔", color = EscapeRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("New Stock", color = EscapeTextSecondary, fontSize = 11.sp)
                            Text("${newStock.toInt()}", color = if (delta >= 0) EscapeSuccess else EscapeError, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Adjustment Delta field
                OutlinedTextField(
                    value = deltaInput,
                    onValueChange = { deltaInput = it },
                    label = { Text("Quantity Change (+ or -)") },
                    placeholder = { Text("e.g. 5 or -2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("adjustment_delta_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EscapeRed,
                        unfocusedBorderColor = EscapeBorder,
                        focusedTextColor = EscapeTextPrimary,
                        unfocusedTextColor = EscapeTextPrimary,
                        focusedLabelColor = EscapeRed
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Reason for Adjustment", color = EscapeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    reasons.forEach { r ->
                        val isSel = selectedReason == r
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) EscapeRed else EscapeSurfaceElevated)
                                .border(1.dp, if (isSel) EscapeRed else EscapeBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedReason = r }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(r, color = if (isSel) Color.White else EscapeTextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
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

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EscapeTextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (delta != 0.0) {
                                onConfirm(delta, selectedReason, notes.ifBlank { null })
                            }
                        },
                        enabled = delta != 0.0,
                        modifier = Modifier.weight(1f).height(46.dp).testTag("confirm_adjustment_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryStatusChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = EscapeRed
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) accentColor else EscapeSurfaceElevated)
            .border(1.dp, if (isSelected) accentColor else EscapeBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else EscapeTextPrimary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
