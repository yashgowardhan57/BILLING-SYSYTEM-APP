package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductEntity
import com.example.data.ProductVariantEntity
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VariantSelectorSheet(
    product: ProductEntity,
    variants: List<ProductVariantEntity>,
    onDismiss: () -> Unit,
    onAddToCart: (ProductVariantEntity, Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val availableSizes = remember(variants) { variants.map { it.sizeName }.distinct() }
    val availableColors = remember(variants) { variants.map { it.colorName }.distinct() }

    var selectedSize by remember { mutableStateOf(availableSizes.firstOrNull() ?: "") }
    var selectedColor by remember { mutableStateOf(availableColors.firstOrNull() ?: "") }
    var quantity by remember { mutableDoubleStateOf(1.0) }

    // Find matching variant
    val matchingVariant by remember(selectedSize, selectedColor, variants) {
        derivedStateOf {
            variants.firstOrNull { it.sizeName == selectedSize && it.colorName == selectedColor }
                ?: variants.firstOrNull { it.sizeName == selectedSize }
                ?: variants.firstOrNull()
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
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        color = EscapeTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.categoryName,
                        color = EscapeTextSecondary,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EscapeTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Size Selection
            Text("Select Size", color = EscapeTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableSizes.forEach { sz ->
                    val isSelected = sz == selectedSize
                    val isAvailable = variants.any { it.sizeName == sz && it.stockQuantity > 0 }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) EscapeRed else EscapeSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) EscapeRed else EscapeBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedSize = sz
                                quantity = 1.0
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sz,
                            color = if (isSelected) Color.White else if (isAvailable) EscapeTextPrimary else EscapeTextTertiary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color Selection
            Text("Select Color", color = EscapeTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableColors.forEach { col ->
                    val isSelected = col == selectedColor
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) EscapeRed else EscapeSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) EscapeRed else EscapeBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedColor = col
                                quantity = 1.0
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = col,
                            color = if (isSelected) Color.White else EscapeTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Variant Detail Card (Price, Stock, SKU)
            matchingVariant?.let { variant ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selling Price",
                                color = EscapeTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = formatInr(variant.sellingPrice),
                                color = EscapeRed,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "SKU: ${variant.sku}",
                                color = EscapeTextTertiary,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            StockBadge(variant.stockQuantity, variant.reorderLevel)
                            Spacer(modifier = Modifier.height(8.dp))
                            QuantityStepper(
                                quantity = quantity,
                                maxStock = variant.stockQuantity,
                                onQuantityChange = { quantity = it }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Large Add to Cart button
                val canAdd = variant.stockQuantity > 0 && quantity > 0
                Button(
                    onClick = {
                        if (canAdd) {
                            onAddToCart(variant, quantity)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("add_variant_to_cart_btn"),
                    enabled = canAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Add", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (variant.stockQuantity <= 0) "Out of Stock" else "Add to Cart • ${formatInr(variant.sellingPrice * quantity)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } ?: run {
                Text(
                    text = "No matching variant available for this combination.",
                    color = EscapeTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
