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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CartItem
import com.example.data.ProductEntity
import com.example.data.ProductVariantEntity
import com.example.ui.PosViewModel
import com.example.ui.components.CheckoutDialog
import com.example.ui.components.QuantityStepper
import com.example.ui.components.StockBadge
import com.example.ui.components.ThermalReceiptDialog
import com.example.ui.components.VariantSelectorSheet
import com.example.ui.components.formatInr
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary

@Composable
fun StaffBillingScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val searchQuery by viewModel.billingSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val variants by viewModel.filteredBillingVariants.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val activeReceipt by viewModel.activeReceiptModal.collectAsStateWithLifecycle()

    var showCheckout by remember { mutableStateOf(false) }
    var selectedProductForVariants by remember { mutableStateOf<Pair<ProductEntity, List<ProductVariantEntity>>?>(null) }
    var isCartExpanded by remember { mutableStateOf(false) }

    val subtotal = remember(cart) { cart.sumOf { it.variant.sellingPrice * it.quantity } }
    val totalUnits = remember(cart) { cart.sumOf { it.quantity }.toInt() }

    Box(modifier = modifier.fillMaxSize().background(EscapeSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Area with Barcode quick scanner icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setBillingSearchQuery(it) },
                    placeholder = { Text("Search product, SKU or barcode...", color = EscapeTextTertiary, fontSize = 13.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = EscapeTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setBillingSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = EscapeTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("billing_search_input"),
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

                Spacer(modifier = Modifier.width(8.dp))

                // Barcode simulation scanner button
                IconButton(
                    onClick = {
                        // Quick scan simulation: pick first variant with barcode
                        val firstWithBarcode = variants.firstOrNull { it.barcode != null }
                        if (firstWithBarcode != null) {
                            viewModel.addToCart(firstWithBarcode)
                        } else {
                            viewModel.showMessage("Barcode scanned: No matching item found")
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EscapeSurfaceElevated)
                        .border(1.dp, EscapeBorder, RoundedCornerShape(12.dp))
                        .testTag("barcode_scan_btn")
                ) {
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode", tint = EscapeRed)
                }
            }

            // Quick Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        name = "All Items",
                        isSelected = selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) }
                    )
                }
                items(categories) { cat ->
                    CategoryChip(
                        name = cat.name,
                        isSelected = selectedCategory == cat.id,
                        onClick = { viewModel.selectCategory(cat.id) }
                    )
                }
            }

            // Main Product Variant Results List
            if (variants.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No products found", color = EscapeTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try searching for another SKU, name or size", color = EscapeTextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = if (cart.isNotEmpty()) 120.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(variants, key = { it.id }) { variant ->
                        VariantBillingCard(
                            variant = variant,
                            onAddToCart = { viewModel.addToCart(variant) },
                            onSelectProduct = {
                                val prod = products.find { it.id == variant.productId }
                                if (prod != null) {
                                    val prodVariants = variants.filter { it.productId == prod.id }
                                    selectedProductForVariants = Pair(prod, prodVariants)
                                } else {
                                    viewModel.addToCart(variant)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Expandable Cart Section or Sticky Checkout bar
        if (cart.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = EscapeSurfaceElevated,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Cart toggle header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCartExpanded = !isCartExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = EscapeRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cart ($totalUnits items)",
                                color = EscapeTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formatInr(subtotal),
                                color = EscapeRed,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isCartExpanded) "Hide ▲" else "View Details ▼",
                                color = EscapeTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Expanded Cart list
                    if (isCartExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EscapeBorder))
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp)
                        ) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(cart, key = { it.variant.id }) { item ->
                                    CartItemRow(
                                        item = item,
                                        onQuantityChange = { viewModel.updateCartQuantity(item.variant.id, it) },
                                        onRemove = { viewModel.removeFromCart(item.variant.id) }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { viewModel.clearCart() }) {
                                Text("Clear Cart", color = EscapeTextTertiary, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Large Checkout Button
                    Button(
                        onClick = { showCheckout = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("billing_checkout_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Checkout (${totalUnits} pcs)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = formatInr(subtotal),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Product Variant Selector Bottom Sheet
    selectedProductForVariants?.let { (product, vList) ->
        VariantSelectorSheet(
            product = product,
            variants = vList,
            onDismiss = { selectedProductForVariants = null },
            onAddToCart = { v, qty ->
                viewModel.addToCart(v, qty)
            }
        )
    }

    // Checkout Sheet
    if (showCheckout) {
        CheckoutDialog(
            cartItems = cart,
            onDismiss = { showCheckout = false },
            onConfirm = { paymentMethod, paidAmount, discount, name, mobile, notes ->
                viewModel.completeCheckout(
                    paymentMethod = paymentMethod,
                    paidAmountInput = paidAmount,
                    discountAmount = discount,
                    customerName = name,
                    customerMobile = mobile,
                    notes = notes,
                    onSuccess = {
                        showCheckout = false
                    },
                    onError = { err ->
                        viewModel.showMessage(err)
                    }
                )
            }
        )
    }

    // Thermal Receipt Preview Modal
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
private fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) EscapeRed else EscapeSurfaceElevated)
            .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.White else EscapeTextPrimary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun VariantBillingCard(
    variant: ProductVariantEntity,
    onAddToCart: () -> Unit,
    onSelectProduct: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectProduct() },
        colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
        shape = RoundedCornerShape(12.dp),
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
                Text(
                    text = variant.productName,
                    color = EscapeTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Size: ${variant.sizeName} • ${variant.colorName}",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = variant.sku,
                        color = EscapeTextTertiary,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatInr(variant.sellingPrice),
                        color = EscapeRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    StockBadge(quantity = variant.stockQuantity, reorderLevel = variant.reorderLevel)
                }
            }

            IconButton(
                onClick = onAddToCart,
                enabled = variant.stockQuantity > 0,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (variant.stockQuantity > 0) EscapeRed else EscapeSurfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = if (variant.stockQuantity > 0) Color.White else EscapeTextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Double) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(EscapeSurface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${item.variant.productName} [${item.variant.sizeName}/${item.variant.colorName}]",
                color = EscapeTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${formatInr(item.variant.sellingPrice)} × ${item.quantity.toInt()} = ${formatInr(item.variant.sellingPrice * item.quantity)}",
                color = EscapeRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            QuantityStepper(
                quantity = item.quantity,
                maxStock = item.variant.stockQuantity,
                onQuantityChange = onQuantityChange
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = EscapeTextTertiary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
