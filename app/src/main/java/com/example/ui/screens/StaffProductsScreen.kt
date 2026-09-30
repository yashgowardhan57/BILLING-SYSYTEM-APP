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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.PosViewModel
import com.example.ui.components.StockBadge
import com.example.ui.components.formatInr
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary

@Composable
fun StaffProductsScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf<String?>(null) }

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val allVariants by viewModel.allVariants.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()

    val prodCategoryMap = remember(products) { products.associate { it.id to it.categoryId } }

    val filtered = remember(allVariants, searchQuery, selectedCatId, prodCategoryMap) {
        allVariants.filter { v ->
            val matchCategory = selectedCatId == null || prodCategoryMap[v.productId] == selectedCatId
            val q = searchQuery.trim().lowercase()
            val matchQuery = q.isEmpty() ||
                v.productName.lowercase().contains(q) ||
                v.sku.lowercase().contains(q) ||
                v.sizeName.lowercase().contains(q) ||
                v.colorName.lowercase().contains(q) ||
                (v.barcode?.lowercase()?.contains(q) == true)
            matchCategory && matchQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
    ) {
        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search catalog by name, SKU or size...", color = EscapeTextTertiary, fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = EscapeTextSecondary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = EscapeTextSecondary)
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
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

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CatalogFilterChip(
                    label = "All (${allVariants.size})",
                    isSelected = selectedCatId == null,
                    onClick = { selectedCatId = null }
                )
            }
            items(categories) { cat ->
                CatalogFilterChip(
                    label = cat.name,
                    isSelected = selectedCatId == cat.id,
                    onClick = { selectedCatId = cat.id }
                )
            }
        }

        // List
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
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
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Size: ${variant.sizeName} • Color: ${variant.colorName}",
                                color = EscapeTextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "SKU: ${variant.sku}" + (variant.barcode?.let { " • Barcode: $it" } ?: ""),
                                color = EscapeTextTertiary,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatInr(variant.sellingPrice),
                                color = EscapeRed,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            StockBadge(quantity = variant.stockQuantity, reorderLevel = variant.reorderLevel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) EscapeRed else EscapeSurfaceElevated)
            .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(20.dp))
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
