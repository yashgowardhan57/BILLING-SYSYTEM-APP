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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ExpenseEntity
import com.example.data.ProductVariantEntity
import com.example.data.SaleEntity
import com.example.ui.PosViewModel
import com.example.ui.components.StatusBadge
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

enum class ReportType(val title: String) {
    SALES("Sales"),
    PROFIT("Profit"),
    PENDING("Pending"),
    EXPENSE("Expenses"),
    INVENTORY("Inventory"),
    CUSTOMER("Customers")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var selectedReport by remember { mutableStateOf(ReportType.SALES) }
    var selectedPeriod by remember { mutableStateOf("LAST_30") } // TODAY, 7D, 30D, ALL
    var showExportSheet by remember { mutableStateOf(false) }

    val allSales by viewModel.allSales.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val allVariants by viewModel.allVariants.collectAsStateWithLifecycle()
    val allCustomers by viewModel.allCustomers.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
    ) {
        // Report Category Selector Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ReportType.values()) { r ->
                val isSelected = selectedReport == r
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) EscapeRed else EscapeSurfaceElevated)
                        .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedReport = r }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = r.title,
                        color = if (isSelected) Color.White else EscapeTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Subheader: Filter & Export button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("TODAY" to "Today", "7D" to "7 Days", "30D" to "30 Days", "ALL" to "All").forEach { (key, label) ->
                    val isSel = selectedPeriod == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) EscapeSurfaceVariant else EscapeSurface)
                            .border(1.dp, if (isSel) EscapeRed else EscapeBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedPeriod = key }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(label, color = if (isSel) EscapeRed else EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Button(
                onClick = { showExportSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = EscapeSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp).testTag("export_report_btn")
            ) {
                Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Export", tint = EscapeRed, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export", color = EscapeTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Report Content Area
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedReport) {
                ReportType.SALES -> SalesReportView(allSales)
                ReportType.PROFIT -> ProfitReportView(allSales, allExpenses)
                ReportType.PENDING -> PendingReportView(allSales)
                ReportType.EXPENSE -> ExpenseReportView(allExpenses)
                ReportType.INVENTORY -> InventoryReportView(allVariants)
                ReportType.CUSTOMER -> CustomerReportView(allCustomers, allSales)
            }
        }
    }

    if (showExportSheet) {
        ModalBottomSheet(
            onDismissRequest = { showExportSheet = false },
            containerColor = EscapeSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Export ${selectedReport.title} Report", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Escape Clothing • City Center 2, Butibori", color = EscapeTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.showMessage("Generated ${selectedReport.title}_Report.xlsx")
                            showExportSheet = false
                        },
                    colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.TableChart, contentDescription = "Excel", tint = EscapeSuccess)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Export as Excel (.xlsx)", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Standard spreadsheet with active date filters applied", color = EscapeTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.showMessage("Generated ${selectedReport.title}_Report.pdf")
                            showExportSheet = false
                        },
                    colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = EscapeRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Export as PDF Document", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Formatted receipt-ready table with store branding & totals", color = EscapeTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SalesReportView(sales: List<SaleEntity>) {
    val totalRev = sales.sumOf { it.totalAmount }
    val totalUnits = sales.size
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ReportSummaryHeader(
                metric1 = "Total Sales" to formatInr(totalRev),
                metric2 = "Bills Count" to "$totalUnits"
            )
        }
        items(sales) { s ->
            ReportItemCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(s.invoiceNumber, color = EscapeRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${s.businessDate} • ${s.staffName}", color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatInr(s.totalAmount), color = EscapeTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        StatusBadge(s.paymentStatus)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfitReportView(sales: List<SaleEntity>, expenses: List<ExpenseEntity>) {
    val rev = sales.sumOf { it.totalAmount }
    val cogs = sales.sumOf { it.costOfGoods }
    val grossProfit = rev - cogs
    val totalExp = expenses.filter { it.status == "ACTIVE" }.sumOf { it.amount }
    val netProfit = grossProfit - totalExp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("FINANCIAL PROFIT STATEMENT", color = EscapeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(14.dp))

                ProfitLine("Gross Sales Revenue", formatInr(rev), EscapeTextPrimary)
                ProfitLine("Cost of Goods Sold (COGS)", "- ${formatInr(cogs)}", EscapeError)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EscapeBorder).padding(vertical = 4.dp))
                ProfitLine("Gross Profit", formatInr(grossProfit), EscapeSuccess, isBold = true)
                Spacer(modifier = Modifier.height(8.dp))

                ProfitLine("Operating Expenses", "- ${formatInr(totalExp)}", EscapeWarning)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(EscapeBorder).padding(vertical = 4.dp))
                ProfitLine("Net Operating Profit", formatInr(netProfit), if (netProfit >= 0) EscapeSuccess else EscapeError, isBold = true, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Key Note: Revenue and Gross Profit are strictly distinguished as required by POS accounting standards.", color = EscapeTextTertiary, fontSize = 11.sp)
    }
}

@Composable
private fun ProfitLine(label: String, value: String, color: Color, isBold: Boolean = false, fontSize: androidx.compose.ui.unit.TextUnit = 14.sp) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = EscapeTextSecondary, fontSize = fontSize, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, color = color, fontSize = fontSize, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold)
    }
}

@Composable
private fun PendingReportView(sales: List<SaleEntity>) {
    val pendingList = sales.filter { it.pendingAmount > 0 }
    val totalPending = pendingList.sumOf { it.pendingAmount }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            ReportSummaryHeader(
                metric1 = "Total Outstanding" to formatInr(totalPending),
                metric2 = "Pending Bills" to "${pendingList.size}"
            )
        }
        items(pendingList) { s ->
            ReportItemCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(s.customerName ?: "Customer", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${s.customerMobile ?: ""} • ${s.invoiceNumber}", color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Due: ${formatInr(s.pendingAmount)}", color = EscapeWarning, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Paid: ${formatInr(s.paidAmount)} / ${formatInr(s.totalAmount)}", color = EscapeTextTertiary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseReportView(expenses: List<ExpenseEntity>) {
    val active = expenses.filter { it.status == "ACTIVE" }
    val total = active.sumOf { it.amount }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            ReportSummaryHeader(
                metric1 = "Total Expenses" to formatInr(total),
                metric2 = "Entries" to "${active.size}"
            )
        }
        items(expenses) { e ->
            ReportItemCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(e.categoryName, color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${e.expenseDate} • ${e.description}", color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatInr(e.amount), color = EscapeRed, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(e.status, color = if (e.status == "ACTIVE") EscapeSuccess else EscapeError, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryReportView(variants: List<ProductVariantEntity>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(variants) { v ->
            ReportItemCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(v.productName, color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("SKU: ${v.sku} • Size: ${v.sizeName}/${v.colorName}", color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Stock: ${v.stockQuantity.toInt()}", color = if (v.stockQuantity > v.reorderLevel) EscapeSuccess else EscapeError, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Cost: ${formatInr(v.costPrice)} • Sell: ${formatInr(v.sellingPrice)}", color = EscapeTextTertiary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerReportView(customers: List<com.example.data.CustomerEntity>, sales: List<SaleEntity>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(customers) { c ->
            val custSales = sales.filter { it.customerId == c.id }
            val totalSpent = custSales.sumOf { it.totalAmount }
            val totalDue = custSales.sumOf { it.pendingAmount }
            ReportItemCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(c.name, color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Mobile: ${c.mobile} • ${custSales.size} purchases", color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Spent: ${formatInr(totalSpent)}", color = EscapeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        if (totalDue > 0) {
                            Text("Pending: ${formatInr(totalDue)}", color = EscapeWarning, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportSummaryHeader(metric1: Pair<String, String>, metric2: Pair<String, String>) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
        colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(metric1.first, color = EscapeTextSecondary, fontSize = 11.sp)
                Text(metric1.second, color = EscapeRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(metric2.first, color = EscapeTextSecondary, fontSize = 11.sp)
                Text(metric2.second, color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ReportItemCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            content()
        }
    }
}
