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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeError
import com.example.ui.theme.EscapeErrorContainer
import com.example.ui.theme.EscapeInfo
import com.example.ui.theme.EscapeInfoContainer
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSuccess
import com.example.ui.theme.EscapeSuccessContainer
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary
import com.example.ui.theme.EscapeTextTertiary
import com.example.ui.theme.EscapeWarning
import com.example.ui.theme.EscapeWarningContainer
import java.text.NumberFormat
import java.util.Locale

fun formatInr(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    formatter.maximumFractionDigits = if (amount % 1.0 == 0.0) 0 else 2
    return formatter.format(amount).replace("INR", "₹")
}

@Composable
fun EscapeTopBar(
    title: String,
    subtitle: String? = null,
    connectionState: String = "ONLINE",
    onConnectionClick: () -> Unit = {},
    actions: @Composable () -> Unit = {}
) {
    Surface(
        color = EscapeSurface,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EscapeRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = title,
                        color = EscapeTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = EscapeTextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // Connection State Badge
                ConnectionBadge(connectionState = connectionState, onClick = onConnectionClick)
                Spacer(modifier = Modifier.width(6.dp))
                actions()
            }
        }
    }
}

@Composable
fun ConnectionBadge(connectionState: String, onClick: () -> Unit) {
    val (bg, fg, icon) = when (connectionState) {
        "ONLINE" -> Triple(EscapeSuccessContainer, EscapeSuccess, Icons.Default.CheckCircle)
        "OFFLINE" -> Triple(EscapeErrorContainer, EscapeError, Icons.Default.CloudOff)
        "SYNCING" -> Triple(EscapeInfoContainer, EscapeInfo, Icons.Default.Sync)
        else -> Triple(EscapeSuccessContainer, EscapeSuccess, Icons.Default.CloudSync)
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, fg.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = connectionState, tint = fg, modifier = Modifier.size(11.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = connectionState, color = fg, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = EscapeRed,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = EscapeSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    color = EscapeTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = EscapeTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = EscapeTextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StockBadge(quantity: Double, reorderLevel: Double = 5.0) {
    val (bg, text, label) = when {
        quantity <= 0 -> Triple(EscapeErrorContainer, EscapeError, "OUT OF STOCK")
        quantity <= reorderLevel -> Triple(EscapeWarningContainer, EscapeWarning, "LOW: ${quantity.toInt()}")
        else -> Triple(EscapeSuccessContainer, EscapeSuccess, "IN STOCK: ${quantity.toInt()}")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bg, fg) = when (status) {
        "PAID", "COMPLETED", "ACTIVE", "CLOSED", "SYNCED" -> Pair(EscapeSuccessContainer, EscapeSuccess)
        "PARTIAL", "PENDING", "OPEN", "SYNCING" -> Pair(EscapeWarningContainer, EscapeWarning)
        "VOID", "RETURNED", "PARTIALLY_RETURNED", "ARCHIVED" -> Pair(EscapeErrorContainer, EscapeError)
        else -> Pair(EscapeSurfaceVariant, EscapeTextSecondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuantityStepper(
    quantity: Double,
    maxStock: Double,
    onQuantityChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EscapeSurfaceVariant)
            .border(1.dp, EscapeBorder, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onQuantityChange((quantity - 1.0).coerceAtLeast(0.0)) },
            modifier = Modifier.size(34.dp).testTag("stepper_decrease")
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = EscapeTextPrimary, modifier = Modifier.size(16.dp))
        }
        Text(
            text = quantity.toInt().toString(),
            color = EscapeTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp),
            textAlign = TextAlign.Center
        )
        IconButton(
            onClick = {
                if (quantity < maxStock) {
                    onQuantityChange(quantity + 1.0)
                }
            },
            modifier = Modifier.size(34.dp).testTag("stepper_increase"),
            enabled = quantity < maxStock
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = if (quantity < maxStock) EscapeTextPrimary else EscapeTextTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
