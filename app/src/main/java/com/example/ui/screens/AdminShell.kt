package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.PosViewModel
import com.example.ui.components.EscapeTopBar
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSuccess
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary

sealed class AdminTab(val title: String, val icon: ImageVector) {
    object Dashboard : AdminTab("Dashboard", Icons.Default.Dashboard)
    object Inventory : AdminTab("Inventory", Icons.Default.Inventory2)
    object Reports : AdminTab("Reports", Icons.Default.Assessment)
    object More : AdminTab("More", Icons.Default.MoreHoriz)
}

@Composable
fun AdminShell(
    viewModel: PosViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = remember { listOf(AdminTab.Dashboard, AdminTab.Inventory, AdminTab.Reports, AdminTab.More) }
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EscapeTopBar(
                title = "Escape Clothing",
                subtitle = "Admin • Butibori",
                connectionState = connectionState,
                onConnectionClick = { viewModel.toggleConnectionSimulation() },
                actions = {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeSurfaceVariant)
                            .border(1.dp, EscapeBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.switchToStaff()
                            }
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = "Staff Portal",
                            tint = EscapeSuccess,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "POS",
                            color = EscapeTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = EscapeSurfaceElevated,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = EscapeRed,
                            indicatorColor = EscapeRed,
                            unselectedIconColor = EscapeTextSecondary,
                            unselectedTextColor = EscapeTextSecondary
                        )
                    )
                }
            }
        },
        containerColor = EscapeSurface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> AdminDashboardScreen(viewModel = viewModel)
                1 -> AdminInventoryScreen(viewModel = viewModel)
                2 -> AdminReportsScreen(viewModel = viewModel)
                3 -> AdminMoreScreen(viewModel = viewModel)
            }
        }
    }
}
