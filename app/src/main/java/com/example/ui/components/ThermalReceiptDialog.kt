package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.ReceiptModalData
import com.example.ui.theme.EscapeBorder
import com.example.ui.theme.EscapeRed
import com.example.ui.theme.EscapeSuccess
import com.example.ui.theme.EscapeSuccessContainer
import com.example.ui.theme.EscapeSurface
import com.example.ui.theme.EscapeSurfaceElevated
import com.example.ui.theme.EscapeSurfaceVariant
import com.example.ui.theme.EscapeTextPrimary
import com.example.ui.theme.EscapeTextSecondary

@Composable
fun ThermalReceiptDialog(
    data: ReceiptModalData,
    onDismiss: () -> Unit,
    onNewBill: () -> Unit
) {
    var isPrintSuccess by remember { mutableStateOf(false) }
    var paperWidth by remember { mutableStateOf(80) } // 58 or 80 mm

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = EscapeSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header success
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(EscapeSuccessContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Saved", tint = EscapeSuccess)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Sale Confirmed!", color = EscapeTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(data.sale.invoiceNumber, color = EscapeRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Paper toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeSurfaceVariant)
                            .padding(2.dp)
                    ) {
                        TextButton(
                            onClick = { paperWidth = 58 },
                            modifier = Modifier.height(28.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (paperWidth == 58) EscapeRed else Color.Transparent
                            )
                        ) {
                            Text("58mm", fontSize = 11.sp, color = if (paperWidth == 58) Color.White else EscapeTextSecondary)
                        }
                        TextButton(
                            onClick = { paperWidth = 80 },
                            modifier = Modifier.height(28.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (paperWidth == 80) EscapeRed else Color.Transparent
                            )
                        ) {
                            Text("80mm", fontSize = 11.sp, color = if (paperWidth == 80) Color.White else EscapeTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Thermal simulated paper preview card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D0D0))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = data.formattedThermalText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = if (paperWidth == 80) 11.sp else 9.5.sp,
                            color = Color(0xFF1A1A1A),
                            lineHeight = if (paperWidth == 80) 15.sp else 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isPrintSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeSuccessContainer)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Printed successfully to ${paperWidth}mm Thermal Printer", color = EscapeSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { isPrintSuccess = true },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("print_thermal_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = "Print", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isPrintSuccess) "Reprint" else "Print Thermal", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onNewBill()
                        },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("new_bill_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EscapeTextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "New Bill", tint = EscapeTextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("New Bill", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
