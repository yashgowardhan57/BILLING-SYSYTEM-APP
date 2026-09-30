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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.CategoryEntity
import com.example.data.ColorEntity
import com.example.data.ProductEntity
import com.example.data.ProductVariantDraft
import com.example.data.ProductVariantEntity
import com.example.data.SizeEntity
import com.example.ui.PosViewModel
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsManagementModal(
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val sizes by viewModel.sizes.collectAsStateWithLifecycle()
    val colors by viewModel.colors.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }
    var filterCategoryId by remember { mutableStateOf<String?>(null) }
    var showCategoryManagementInline by remember { mutableStateOf(false) }
    var newCatNameInput by remember { mutableStateOf("") }

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
                        text = "Products & Catalog",
                        color = EscapeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${products.size} clothing items in catalog",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("add_product_modal_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Bar & Quick Category Management
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CATEGORIES (${categories.size})", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                TextButton(
                    onClick = { showCategoryManagementInline = !showCategoryManagementInline },
                    modifier = Modifier.height(26.dp)
                ) {
                    Icon(
                        imageVector = if (showCategoryManagementInline) Icons.Default.Close else Icons.Default.Tune,
                        contentDescription = "Manage Categories",
                        tint = EscapeRed,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (showCategoryManagementInline) "Close Category Tools" else "+ Add / Manage Categories",
                        color = EscapeRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (showCategoryManagementInline) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Add or Remove Categories", color = EscapeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newCatNameInput,
                                onValueChange = { newCatNameInput = it },
                                placeholder = { Text("New Category name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (newCatNameInput.isNotBlank()) {
                                        viewModel.addCategory(newCatNameInput.trim())
                                        newCatNameInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Add", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap ✕ on any category to delete:", color = EscapeTextTertiary, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EscapeSurfaceVariant)
                                        .border(0.5.dp, EscapeBorder, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(cat.name, color = EscapeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete ${cat.name}",
                                        tint = EscapeError,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable {
                                                viewModel.deleteCategory(cat.id)
                                                if (filterCategoryId == cat.id) filterCategoryId = null
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterCategoryId == null,
                        onClick = { filterCategoryId = null },
                        label = { Text("All (${products.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EscapeRed,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
                items(categories) { cat ->
                    val count = products.count { it.categoryId == cat.id }
                    FilterChip(
                        selected = filterCategoryId == cat.id,
                        onClick = { filterCategoryId = if (filterCategoryId == cat.id) null else cat.id },
                        label = { Text("${cat.name} ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EscapeRed,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        )
                    )
                }
            }

            val displayedProducts = if (filterCategoryId == null) products else products.filter { it.categoryId == filterCategoryId }

            if (displayedProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (products.isEmpty()) "No products yet. Tap '+ Add Product' to create clothing items." else "No products in this category.",
                        color = EscapeTextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedProducts) { prod ->
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
                                    Text(
                                        text = prod.name,
                                        color = EscapeTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Category: ${prod.categoryName}",
                                        color = EscapeTextSecondary,
                                        fontSize = 12.sp
                                    )
                                    if (!prod.description.isNullOrBlank()) {
                                        Text(
                                            text = prod.description,
                                            color = EscapeTextTertiary,
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = { editingProduct = prod },
                                        colors = ButtonDefaults.buttonColors(containerColor = EscapeSurfaceVariant),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("edit_product_${prod.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Product",
                                            tint = EscapeTextPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit", color = EscapeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { productToDelete = prod },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = EscapeError,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ADD PRODUCT DIALOG
    if (showAddDialog) {
        AddProductDialog(
            categories = categories,
            allSizes = sizes,
            allColors = colors,
            viewModel = viewModel,
            onDismiss = { showAddDialog = false },
            onSave = { name, desc, cat, buyPrice, sellPrice, selectedSizes, selectedColors, initialStock ->
                val drafts = mutableListOf<ProductVariantDraft>()
                val cleanPrefix = name.filter { it.isLetterOrDigit() }.take(4).uppercase().ifBlank { "PROD" }

                selectedSizes.forEach { sz ->
                    selectedColors.forEach { col ->
                        val sku = "$cleanPrefix-${col.name.take(3).uppercase()}-${sz.name.uppercase()}"
                        val barcode = "890${System.currentTimeMillis() % 100000000}${drafts.size}"
                        drafts.add(
                            ProductVariantDraft(
                                sizeId = sz.id,
                                sizeName = sz.name,
                                colorId = col.id,
                                colorName = col.name,
                                sku = sku,
                                barcode = barcode,
                                costPrice = buyPrice,
                                sellingPrice = sellPrice,
                                stockQuantity = initialStock,
                                reorderLevel = 5.0
                            )
                        )
                    }
                }

                coroutineScope.launch {
                    viewModel.repository.createProductWithVariants(
                        name = name,
                        description = desc,
                        categoryId = cat.id,
                        categoryName = cat.name,
                        variants = drafts
                    )
                    viewModel.showMessage("Added '$name' with ${drafts.size} variants (${selectedSizes.size} sizes × ${selectedColors.size} colors)")
                }
                showAddDialog = false
            }
        )
    }

    // EDIT PRODUCT DIALOG (for fixing mistakes)
    editingProduct?.let { product ->
        EditProductDialog(
            product = product,
            categories = categories,
            allSizes = sizes,
            allColors = colors,
            viewModel = viewModel,
            onDismiss = { editingProduct = null },
            onSaved = {
                editingProduct = null
            }
        )
    }

    // DELETE PRODUCT CONFIRMATION
    productToDelete?.let { prod ->
        Dialog(onDismissRequest = { productToDelete = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = EscapeSurface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Delete Product",
                        color = EscapeError,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Are you sure you want to delete '${prod.name}' and all associated size/color variants? This action cannot be undone.",
                        color = EscapeTextPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { productToDelete = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = EscapeTextPrimary)
                        }
                        Button(
                            onClick = {
                                viewModel.deleteProduct(prod.id) { _, _ -> }
                                productToDelete = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeError),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun AddProductDialog(
    categories: List<CategoryEntity>,
    allSizes: List<SizeEntity>,
    allColors: List<ColorEntity>,
    viewModel: PosViewModel,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        desc: String?,
        cat: CategoryEntity,
        buyPrice: Double,
        sellPrice: Double,
        selectedSizes: List<SizeEntity>,
        selectedColors: List<ColorEntity>,
        initialStock: Double
    ) -> Unit
) {
    var prodName by remember { mutableStateOf("") }
    var prodDesc by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(categories.firstOrNull()) }
    var buyPriceText by remember { mutableStateOf("350") }
    var sellPriceText by remember { mutableStateOf("699") }
    var initialStockText by remember { mutableStateOf("10") }

    val selectedSizes = remember { mutableStateListOf<SizeEntity>() }
    val selectedColors = remember { mutableStateListOf<ColorEntity>() }

    // Inline Size & Color addition states
    var showInlineAddSize by remember { mutableStateOf(false) }
    var newCustomSizeName by remember { mutableStateOf("") }
    var showInlineAddColor by remember { mutableStateOf(false) }
    var newCustomColorName by remember { mutableStateOf("") }
    var newCustomColorHex by remember { mutableStateOf("#800000") }
    var showInlineAddCategory by remember { mutableStateOf(false) }
    var newCategoryNameInput by remember { mutableStateOf("") }

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

    // Pre-select popular sizes and colors by default
    LaunchedEffect(allSizes, allColors) {
        if (selectedSizes.isEmpty() && allSizes.isNotEmpty()) {
            val common = allSizes.filter { it.name in listOf("S", "M", "L", "XL") }
            if (common.isNotEmpty()) selectedSizes.addAll(common)
            else selectedSizes.addAll(allSizes.take(3))
        }
        if (selectedColors.isEmpty() && allColors.isNotEmpty()) {
            val common = allColors.filter { it.name in listOf("Black", "White", "Navy") }
            if (common.isNotEmpty()) selectedColors.addAll(common)
            else selectedColors.addAll(allColors.take(2))
        }
    }

    var catDropdownExpanded by remember { mutableStateOf(false) }

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
                    Text(
                        text = "Add Clothing Product",
                        color = EscapeTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EscapeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = prodName,
                    onValueChange = { prodName = it },
                    label = { Text("Product Name *") },
                    placeholder = { Text("e.g. Slim Polo T-Shirt") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_product_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = prodDesc,
                    onValueChange = { prodDesc = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. 100% Cotton, Casual wear") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category selector header with Add Category button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category: ${selectedCat?.name ?: "None"}",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = { showInlineAddCategory = !showInlineAddCategory },
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category", modifier = Modifier.size(12.dp), tint = EscapeRed)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+ Add Category", color = EscapeRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (showInlineAddCategory) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCategoryNameInput,
                            onValueChange = { newCategoryNameInput = it },
                            placeholder = { Text("e.g. Ethnic Wear, Activewear") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (newCategoryNameInput.isNotBlank()) {
                                    viewModel.addCategory(newCategoryNameInput.trim()) { created ->
                                        if (created != null) {
                                            selectedCat = created
                                        }
                                        newCategoryNameInput = ""
                                        showInlineAddCategory = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Category dropdown selector
                ExposedDropdownMenuBox(
                    expanded = catDropdownExpanded,
                    onExpandedChange = { catDropdownExpanded = !catDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCat?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = catDropdownExpanded,
                        onDismissRequest = { catDropdownExpanded = false },
                        modifier = Modifier.background(EscapeSurfaceElevated)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name, color = EscapeTextPrimary) },
                                onClick = {
                                    selectedCat = cat
                                    catDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Buy Price & Sell Price row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = buyPriceText,
                        onValueChange = { buyPriceText = it },
                        label = { Text("Buy Price (₹) *") },
                        placeholder = { Text("350") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_product_buy_price"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = sellPriceText,
                        onValueChange = { sellPriceText = it },
                        label = { Text("Sell Price (₹) *") },
                        placeholder = { Text("699") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_product_sell_price"),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = initialStockText,
                    onValueChange = { initialStockText = it },
                    label = { Text("Initial Stock per Variant") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // SIZES SELECTION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sizes (${selectedSizes.size} selected):",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { showInlineAddSize = !showInlineAddSize },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp).testTag("add_size_button_toggle")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Size", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("+ Add Size", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedSizes.size == allSizes.size) "Deselect All" else "Select All",
                            color = EscapeTextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                if (selectedSizes.size == allSizes.size) selectedSizes.clear()
                                else {
                                    selectedSizes.clear()
                                    selectedSizes.addAll(allSizes)
                                }
                            }
                        )
                    }
                }

                // INLINE ADD CUSTOM SIZE PANEL
                if (showInlineAddSize) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Add Custom Size to Catalog:", color = EscapeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = newCustomSizeName,
                                    onValueChange = { newCustomSizeName = it },
                                    placeholder = { Text("e.g. 38, 40, XXL, Free Size") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("new_size_input"),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newCustomSizeName.isNotBlank()) {
                                            viewModel.addSize(newCustomSizeName.trim()) { created ->
                                                if (created != null) {
                                                    selectedSizes.add(created)
                                                }
                                                newCustomSizeName = ""
                                                showInlineAddSize = false
                                            }
                                        }
                                    },
                                    enabled = newCustomSizeName.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("save_new_size_btn")
                                ) {
                                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Quick Add Suggestions:", color = EscapeTextTertiary, fontSize = 10.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                listOf("28", "30", "32", "34", "36", "38", "40", "42", "XXL", "3XL", "Free Size")
                                    .filter { suggestion -> allSizes.none { it.name.equals(suggestion, ignoreCase = true) } }
                                    .take(7)
                                    .forEach { suggestion ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(EscapeSurfaceVariant)
                                                .clickable { newCustomSizeName = suggestion }
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text(suggestion, color = EscapeTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    allSizes.forEach { sz ->
                        val isSelected = selectedSizes.any { it.id == sz.id }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) EscapeRed else EscapeSurfaceVariant)
                                .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    if (isSelected) selectedSizes.removeAll { it.id == sz.id }
                                    else selectedSizes.add(sz)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = sz.name,
                                color = if (isSelected) Color.White else EscapeTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // COLORS SELECTION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Colors (${selectedColors.size} selected):",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { showInlineAddColor = !showInlineAddColor },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp).testTag("add_color_button_toggle")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Color", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("+ Add Color", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedColors.size == allColors.size) "Deselect All" else "Select All",
                            color = EscapeTextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                if (selectedColors.size == allColors.size) selectedColors.clear()
                                else {
                                    selectedColors.clear()
                                    selectedColors.addAll(allColors)
                                }
                            }
                        )
                    }
                }

                // INLINE ADD CUSTOM COLOR PANEL
                if (showInlineAddColor) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Add Custom Color to Palette:", color = EscapeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = newCustomColorName,
                                    onValueChange = { newCustomColorName = it },
                                    placeholder = { Text("e.g. Maroon, Lavender, Olive") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("new_color_input"),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newCustomColorName.isNotBlank()) {
                                            viewModel.addColor(newCustomColorName.trim(), newCustomColorHex.trim()) { created ->
                                                if (created != null) {
                                                    selectedColors.add(created)
                                                }
                                                newCustomColorName = ""
                                                showInlineAddColor = false
                                            }
                                        }
                                    },
                                    enabled = newCustomColorName.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("save_new_color_btn")
                                ) {
                                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Select Palette Swatch:", color = EscapeTextTertiary, fontSize = 10.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                presetColors.forEach { (presetName, hex) ->
                                    val isCurrent = newCustomColorHex.equals(hex, ignoreCase = true)
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isCurrent) EscapeRed.copy(alpha = 0.2f) else EscapeSurfaceVariant)
                                            .border(1.dp, if (isCurrent) EscapeRed else EscapeBorder, RoundedCornerShape(4.dp))
                                            .clickable {
                                                newCustomColorName = presetName
                                                newCustomColorHex = hex
                                            }
                                            .padding(horizontal = 5.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(parseColorHex(hex))
                                                .border(0.5.dp, Color.Gray, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(presetName, color = EscapeTextPrimary, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    allColors.forEach { col ->
                        val isSelected = selectedColors.any { it.id == col.id }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) EscapeRed.copy(alpha = 0.2f) else EscapeSurfaceVariant)
                                .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    if (isSelected) selectedColors.removeAll { it.id == col.id }
                                    else selectedColors.add(col)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val dotColor = parseColorHex(col.hexCode)
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                                    .border(0.5.dp, Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = col.name,
                                color = if (isSelected) EscapeRed else EscapeTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SUMMARY CALCULATION
                val totalVariants = selectedSizes.size * selectedColors.size
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
                            text = "Summary: $totalVariants Variants will be created",
                            color = EscapeTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedSizes.size} sizes × ${selectedColors.size} colors • ${initialStockText.toDoubleOrNull() ?: 10.0} units each",
                            color = EscapeTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                            val cat = selectedCat ?: categories.firstOrNull()
                            if (prodName.isNotBlank() && cat != null && selectedSizes.isNotEmpty() && selectedColors.isNotEmpty()) {
                                val buy = buyPriceText.toDoubleOrNull() ?: 350.0
                                val sell = sellPriceText.toDoubleOrNull() ?: 699.0
                                val stock = initialStockText.toDoubleOrNull() ?: 10.0
                                onSave(
                                    prodName.trim(),
                                    prodDesc.trim().ifBlank { null },
                                    cat,
                                    buy,
                                    sell,
                                    selectedSizes.toList(),
                                    selectedColors.toList(),
                                    stock
                                )
                            }
                        },
                        enabled = prodName.isNotBlank() && selectedSizes.isNotEmpty() && selectedColors.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_new_product_btn")
                    ) {
                        Text("Create Product", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EditProductDialog(
    product: ProductEntity,
    categories: List<CategoryEntity>,
    allSizes: List<SizeEntity>,
    allColors: List<ColorEntity>,
    viewModel: PosViewModel,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var desc by remember { mutableStateOf(product.description ?: "") }
    var selectedCat by remember { mutableStateOf(categories.find { it.id == product.categoryId } ?: categories.firstOrNull()) }
    var catDropdownExpanded by remember { mutableStateOf(false) }

    // Quick price updates for all variants
    var bulkCostPrice by remember { mutableStateOf("") }
    var bulkSellPrice by remember { mutableStateOf("") }
    var applyBulkPrices by remember { mutableStateOf(false) }

    // Variants list state
    val existingVariants = remember { mutableStateListOf<ProductVariantEntity>() }
    val deletedVariantIds = remember { mutableStateListOf<String>() }
    val newDrafts = remember { mutableStateListOf<ProductVariantDraft>() }
    var isLoadingVariants by remember { mutableStateOf(true) }

    LaunchedEffect(product.id) {
        viewModel.loadVariantsForProduct(product.id) { list ->
            existingVariants.clear()
            existingVariants.addAll(list)
            if (list.isNotEmpty()) {
                bulkCostPrice = list.first().costPrice.toInt().toString()
                bulkSellPrice = list.first().sellingPrice.toInt().toString()
            }
            isLoadingVariants = false
        }
    }

    // Modal to add an extra variant
    var showAddVariantRow by remember { mutableStateOf(false) }
    var newVarSize by remember { mutableStateOf(allSizes.firstOrNull()) }
    var newVarColor by remember { mutableStateOf(allColors.firstOrNull()) }
    var newVarCost by remember { mutableStateOf("350") }
    var newVarSell by remember { mutableStateOf("699") }
    var newVarStock by remember { mutableStateOf("10") }
    var showAddSizeInEdit by remember { mutableStateOf(false) }
    var newSizeNameInEdit by remember { mutableStateOf("") }
    var showAddColorInEdit by remember { mutableStateOf(false) }
    var newColorNameInEdit by remember { mutableStateOf("") }
    var newColorHexInEdit by remember { mutableStateOf("#800000") }
    var showInlineAddCategoryInEdit by remember { mutableStateOf(false) }
    var newCategoryNameInEdit by remember { mutableStateOf("") }

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
                            text = "Edit Product & Variants",
                            color = EscapeTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Correct product details, prices, or variants",
                            color = EscapeTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EscapeTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_product_name_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("CATEGORY", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    TextButton(
                        onClick = { showInlineAddCategoryInEdit = !showInlineAddCategoryInEdit },
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Category", modifier = Modifier.size(12.dp), tint = EscapeRed)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+ Add Category", color = EscapeRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (showInlineAddCategoryInEdit) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCategoryNameInEdit,
                            onValueChange = { newCategoryNameInEdit = it },
                            placeholder = { Text("e.g. Ethnic Wear, Activewear") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (newCategoryNameInEdit.isNotBlank()) {
                                    viewModel.addCategory(newCategoryNameInEdit.trim()) { created ->
                                        if (created != null) {
                                            selectedCat = created
                                        }
                                        newCategoryNameInEdit = ""
                                        showInlineAddCategoryInEdit = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = catDropdownExpanded,
                    onExpandedChange = { catDropdownExpanded = !catDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCat?.name ?: "Select Category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = catDropdownExpanded,
                        onDismissRequest = { catDropdownExpanded = false },
                        modifier = Modifier.background(EscapeSurfaceElevated)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name, color = EscapeTextPrimary) },
                                onClick = {
                                    selectedCat = cat
                                    catDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bulk Price Update Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EscapeSurfaceVariant)
                        .border(1.dp, EscapeBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Batch Price Correction",
                            color = EscapeTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = bulkCostPrice,
                                onValueChange = { bulkCostPrice = it },
                                label = { Text("Buy Price (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            OutlinedTextField(
                                value = bulkSellPrice,
                                onValueChange = { bulkSellPrice = it },
                                label = { Text("Sell Price (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { applyBulkPrices = !applyBulkPrices }
                        ) {
                            Checkbox(
                                checked = applyBulkPrices,
                                onCheckedChange = { applyBulkPrices = it },
                                colors = CheckboxDefaults.colors(checkedColor = EscapeRed)
                            )
                            Text(
                                text = "Apply this Buy & Sell price to all variants",
                                color = EscapeTextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // VARIANTS LIST (Edit/Delete individual variants)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Variants (${existingVariants.size + newDrafts.size}):",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { showAddVariantRow = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+ Add Variant", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (isLoadingVariants) {
                    Text("Loading variants...", color = EscapeTextSecondary, fontSize = 12.sp)
                } else {
                    existingVariants.forEachIndexed { index, v ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, EscapeBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${v.sizeName} / ${v.colorName} • SKU: ${v.sku}",
                                        color = EscapeTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Stock: ${v.stockQuantity.toInt()} • Buy: ₹${v.costPrice.toInt()} • Sell: ₹${v.sellingPrice.toInt()}",
                                        color = EscapeTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        deletedVariantIds.add(v.id)
                                        existingVariants.removeAt(index)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete variant",
                                        tint = EscapeError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Newly drafted variants
                    newDrafts.forEachIndexed { index, draft ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = EscapeRed.copy(alpha = 0.1f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeRed)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "[NEW] ${draft.sizeName} / ${draft.colorName} • SKU: ${draft.sku}",
                                        color = EscapeRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Stock: ${draft.stockQuantity.toInt()} • Buy: ₹${draft.costPrice.toInt()} • Sell: ₹${draft.sellingPrice.toInt()}",
                                        color = EscapeTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                IconButton(
                                    onClick = { newDrafts.removeAt(index) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = EscapeError, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Add single variant sheet if opened
                if (showAddVariantRow) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("Add Missing Variant", color = EscapeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            // Size picker
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Size (${newVarSize?.name ?: "None"}):", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                TextButton(
                                    onClick = { showAddSizeInEdit = !showAddSizeInEdit },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text("+ New Size", color = EscapeRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (showAddSizeInEdit) {
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = newSizeNameInEdit,
                                        onValueChange = { newSizeNameInEdit = it },
                                        placeholder = { Text("e.g. 42, XXL") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = {
                                            if (newSizeNameInEdit.isNotBlank()) {
                                                viewModel.addSize(newSizeNameInEdit.trim()) { created ->
                                                    if (created != null) {
                                                        newVarSize = created
                                                    }
                                                    newSizeNameInEdit = ""
                                                    showAddSizeInEdit = false
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Add", fontSize = 11.sp)
                                    }
                                }
                            }
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                allSizes.forEach { sz ->
                                    val isSel = newVarSize?.id == sz.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSel) EscapeRed else EscapeSurfaceElevated)
                                            .border(1.dp, if (isSel) EscapeRed else EscapeBorder, RoundedCornerShape(4.dp))
                                            .clickable { newVarSize = sz }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(sz.name, color = if (isSel) Color.White else EscapeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Color picker
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Color (${newVarColor?.name ?: "None"}):", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                TextButton(
                                    onClick = { showAddColorInEdit = !showAddColorInEdit },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text("+ New Color", color = EscapeRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (showAddColorInEdit) {
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = newColorNameInEdit,
                                        onValueChange = { newColorNameInEdit = it },
                                        placeholder = { Text("e.g. Maroon, Olive") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = {
                                            if (newColorNameInEdit.isNotBlank()) {
                                                viewModel.addColor(newColorNameInEdit.trim(), newColorHexInEdit) { created ->
                                                    if (created != null) {
                                                        newVarColor = created
                                                    }
                                                    newColorNameInEdit = ""
                                                    showAddColorInEdit = false
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Add", fontSize = 11.sp)
                                    }
                                }
                            }
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                allColors.forEach { col ->
                                    val isSel = newVarColor?.id == col.id
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSel) EscapeRed.copy(alpha = 0.2f) else EscapeSurfaceElevated)
                                            .border(1.dp, if (isSel) EscapeRed else EscapeBorder, RoundedCornerShape(4.dp))
                                            .clickable { newVarColor = col }
                                            .padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(parseColorHex(col.hexCode))
                                                .border(0.5.dp, Color.Gray, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(col.name, color = if (isSel) EscapeRed else EscapeTextPrimary, fontSize = 11.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedTextField(
                                    value = newVarStock,
                                    onValueChange = { newVarStock = it },
                                    label = { Text("Stock") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = newVarSell,
                                    onValueChange = { newVarSell = it },
                                    label = { Text("Sell (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { showAddVariantRow = false }) {
                                    Text("Cancel", color = EscapeTextSecondary)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        val s = newVarSize ?: allSizes.firstOrNull()
                                        val c = newVarColor ?: allColors.firstOrNull()
                                        if (s != null && c != null) {
                                            val cleanPrefix = name.filter { it.isLetterOrDigit() }.take(4).uppercase().ifBlank { "PROD" }
                                            val sku = "$cleanPrefix-${c.name.take(3).uppercase()}-${s.name.uppercase()}-${System.currentTimeMillis()%1000}"
                                            newDrafts.add(
                                                ProductVariantDraft(
                                                    sizeId = s.id,
                                                    sizeName = s.name,
                                                    colorId = c.id,
                                                    colorName = c.name,
                                                    sku = sku,
                                                    barcode = "890${System.currentTimeMillis()%100000000}",
                                                    costPrice = newVarCost.toDoubleOrNull() ?: 350.0,
                                                    sellingPrice = newVarSell.toDoubleOrNull() ?: 699.0,
                                                    stockQuantity = newVarStock.toDoubleOrNull() ?: 10.0
                                                )
                                            )
                                            showAddVariantRow = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed)
                                ) {
                                    Text("Add to List")
                                }
                            }
                        }
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
                            val cat = selectedCat ?: categories.firstOrNull()
                            if (name.isNotBlank() && cat != null) {
                                val bulkCost = bulkCostPrice.toDoubleOrNull()
                                val bulkSell = bulkSellPrice.toDoubleOrNull()

                                val updatedList = existingVariants.map { v ->
                                    if (applyBulkPrices && bulkSell != null && bulkCost != null) {
                                        v.copy(sellingPrice = bulkSell, costPrice = bulkCost)
                                    } else {
                                        v
                                    }
                                }

                                viewModel.updateProductAndVariants(
                                    productId = product.id,
                                    name = name.trim(),
                                    description = desc.trim().ifBlank { null },
                                    categoryId = cat.id,
                                    categoryName = cat.name,
                                    updatedVariants = updatedList,
                                    newVariants = newDrafts.toList(),
                                    deletedVariantIds = deletedVariantIds.toList()
                                ) { success, _ ->
                                    if (success) {
                                        onSaved()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_edited_product_btn")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun parseColorHex(hex: String?): Color {
    if (hex.isNullOrBlank()) return Color.Gray
    return try {
        val clean = hex.removePrefix("#")
        Color(android.graphics.Color.parseColor("#$clean"))
    } catch (e: Exception) {
        Color.Gray
    }
}
