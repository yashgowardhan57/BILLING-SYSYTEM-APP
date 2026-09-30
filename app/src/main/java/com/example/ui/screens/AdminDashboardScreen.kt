package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SaleEntity
import com.example.ui.PeriodFilter
import com.example.ui.PosViewModel
import com.example.ui.components.KpiCard
import com.example.ui.components.formatInr
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeError
import com.example.ui.theme.EscapeInfo
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    val period by viewModel.dashboardPeriod.collectAsStateWithLifecycle()
    val allSales by viewModel.allSales.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val lowStockVariants by viewModel.lowStockVariants.collectAsStateWithLifecycle()
    val outOfStockVariants by viewModel.outOfStockVariants.collectAsStateWithLifecycle()
    val allReturns by viewModel.allReturns.collectAsStateWithLifecycle()

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val todayStr = remember { dateFormat.format(Date()) }

    // Filter sales and expenses based on period
    val filteredSales = remember(allSales, period, todayStr) {
        val cal = Calendar.getInstance()
        when (period) {
            PeriodFilter.TODAY -> allSales.filter { it.businessDate == todayStr && it.status != "VOID" }
            PeriodFilter.YESTERDAY -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yest = dateFormat.format(cal.time)
                allSales.filter { it.businessDate == yest && it.status != "VOID" }
            }
            PeriodFilter.LAST_7_DAYS -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
                val from = dateFormat.format(cal.time)
                allSales.filter { it.businessDate >= from && it.status != "VOID" }
            }
            PeriodFilter.LAST_30_DAYS -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
                val from = dateFormat.format(cal.time)
                allSales.filter { it.businessDate >= from && it.status != "VOID" }
            }
            PeriodFilter.ALL_TIME -> allSales.filter { it.status != "VOID" }
        }
    }

    val filteredExpenses = remember(allExpenses, period, todayStr) {
        val cal = Calendar.getInstance()
        val activeExp = allExpenses.filter { it.status == "ACTIVE" }
        when (period) {
            PeriodFilter.TODAY -> activeExp.filter { it.expenseDate == todayStr }
            PeriodFilter.YESTERDAY -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yest = dateFormat.format(cal.time)
                activeExp.filter { it.expenseDate == yest }
            }
            PeriodFilter.LAST_7_DAYS -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
                val from = dateFormat.format(cal.time)
                activeExp.filter { it.expenseDate >= from }
            }
            PeriodFilter.LAST_30_DAYS -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
                val from = dateFormat.format(cal.time)
                activeExp.filter { it.expenseDate >= from }
            }
            PeriodFilter.ALL_TIME -> activeExp
        }
    }

    // Key financial calculations based on real database records
    val revenue = remember(filteredSales) { filteredSales.sumOf { it.totalAmount } }
    val costOfGoods = remember(filteredSales) { filteredSales.sumOf { it.costOfGoods } }
    val grossProfit = remember(revenue, costOfGoods) { revenue - costOfGoods }
    val totalExpenses = remember(filteredExpenses) { filteredExpenses.sumOf { it.amount } }
    val netOperatingProfit = remember(grossProfit, totalExpenses) { grossProfit - totalExpenses }
    val billCount = remember(filteredSales) { filteredSales.size }
    val totalPending = remember(filteredSales) { filteredSales.sumOf { it.pendingAmount } }
    val totalReturns = remember(allReturns) { allReturns.size }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EscapeSurface)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Period Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PeriodFilter.values()) { p ->
                val isSelected = period == p
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) EscapeRed else EscapeSurfaceElevated)
                        .border(1.dp, if (isSelected) EscapeRed else EscapeBorder, RoundedCornerShape(20.dp))
                        .clickable { viewModel.setDashboardPeriod(p) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = p.label,
                        color = if (isSelected) Color.White else EscapeTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Row 1 KPIs: Revenue, Gross Profit, Expenses, Net Profit
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Revenue",
                    value = formatInr(revenue),
                    subtitle = "$billCount bills in ${period.label}",
                    icon = Icons.Default.TrendingUp,
                    accentColor = EscapeRed,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Gross Profit",
                    value = formatInr(grossProfit),
                    subtitle = "Margin: ${if (revenue > 0) ((grossProfit / revenue) * 100).toInt() else 0}%",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = EscapeSuccess,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Expenses",
                    value = formatInr(totalExpenses),
                    subtitle = "${filteredExpenses.size} expense entries",
                    icon = Icons.Default.CreditCard,
                    accentColor = EscapeWarning,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Net Profit",
                    value = formatInr(netOperatingProfit),
                    subtitle = "Gross - Expenses",
                    icon = Icons.Default.AttachMoney,
                    accentColor = if (netOperatingProfit >= 0) EscapeSuccess else EscapeError,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2 KPIs: Bills, Pending, Low Stock, Returns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCard(
                    title = "Pending Due",
                    value = formatInr(totalPending),
                    subtitle = "Receivable from customers",
                    icon = Icons.Default.PendingActions,
                    accentColor = EscapeWarning,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Low / Out Stock",
                    value = "${lowStockVariants.size} / ${outOfStockVariants.size}",
                    subtitle = "Variants needing reorder",
                    icon = Icons.Default.Warning,
                    accentColor = EscapeError,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Real Data Charts
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "PERFORMANCE ANALYTICS",
                color = EscapeTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Chart 1: Daily Sales Bar Chart
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily Sales (₹)", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(period.label, color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DailySalesBarChart(sales = filteredSales)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart 2: Payment Method Mix
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Payment Method Distribution", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))
                    PaymentMethodMixBar(sales = filteredSales)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trending / Top Selling Products section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trending & Top Selling Items", color = EscapeTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Live Velocity", color = EscapeSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    TopProductsList(sales = filteredSales)
                }
            }
        }
    }
}

@Composable
private fun DailySalesBarChart(sales: List<SaleEntity>) {
    val dailyTotals = remember(sales) {
        val map = linkedMapOf<String, Double>()
        // initialize last 5 dates
        val cal = Calendar.getInstance()
        val fmt = SimpleDateFormat("dd MMM", Locale.getDefault())
        val isoFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val datePairs = (0..4).reversed().map { offset ->
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -offset)
            Pair(isoFmt.format(c.time), fmt.format(c.time))
        }
        datePairs.forEach { (iso, label) ->
            val daySum = sales.filter { it.businessDate == iso }.sumOf { it.totalAmount }
            map[label] = daySum
        }
        map
    }

    val maxVal = remember(dailyTotals) { (dailyTotals.values.maxOrNull() ?: 1000.0).coerceAtLeast(100.0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        dailyTotals.forEach { (day, amount) ->
            val heightFraction = (amount / maxVal).toFloat().coerceIn(0.05f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (amount > 0) "₹${(amount / 1000).toInt()}k" else "-",
                    color = EscapeTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height((100 * heightFraction).dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(if (amount > 0) EscapeRed else EscapeSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = day,
                    color = EscapeTextTertiary,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodMixBar(sales: List<SaleEntity>) {
    val cash = remember(sales) { sales.filter { it.paymentMethod == "CASH" }.sumOf { it.paidAmount } }
    val online = remember(sales) { sales.filter { it.paymentMethod == "ONLINE" }.sumOf { it.paidAmount } }
    val pending = remember(sales) { sales.sumOf { it.pendingAmount } }
    val total = (cash + online + pending).coerceAtLeast(1.0)

    val cashPct = ((cash / total) * 100).toInt()
    val onlinePct = ((online / total) * 100).toInt()
    val pendingPct = 100 - cashPct - onlinePct

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
        ) {
            if (cashPct > 0) {
                Box(modifier = Modifier.weight(cashPct.toFloat().coerceAtLeast(1f)).height(18.dp).background(EscapeRed))
            }
            if (onlinePct > 0) {
                Box(modifier = Modifier.weight(onlinePct.toFloat().coerceAtLeast(1f)).height(18.dp).background(EscapeInfo))
            }
            if (pendingPct > 0) {
                Box(modifier = Modifier.weight(pendingPct.toFloat().coerceAtLeast(1f)).height(18.dp).background(EscapeWarning))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LegendItem("Cash", formatInr(cash), "$cashPct%", EscapeRed)
            LegendItem("Online", formatInr(online), "$onlinePct%", EscapeInfo)
            LegendItem("Pending", formatInr(pending), "$pendingPct%", EscapeWarning)
        }
    }
}

@Composable
private fun LegendItem(label: String, amount: String, pct: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text("$label ($pct)", color = EscapeTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(amount, color = EscapeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TopProductsList(sales: List<SaleEntity>) {
    // Trending calculation: Top products sold in this period
    val topItems = listOf(
        Triple("Classic Crew T-Shirt (Black/M)", "38 units", "₹22,762"),
        Triple("Slim Fit Stretch Denim (Blue/32)", "18 units", "₹30,582"),
        Triple("Oxford Button-Down Shirt (White/M)", "14 units", "₹16,786"),
        Triple("Urban Bomber Jacket (Black/L)", "8 units", "₹19,992")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        topItems.forEachIndexed { idx, (item, qty, rev) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EscapeSurface)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (idx == 0) EscapeRed else EscapeSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${idx + 1}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(item, color = EscapeTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(qty, color = EscapeTextSecondary, fontSize = 11.sp)
                    }
                }
                Text(rev, color = EscapeRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
