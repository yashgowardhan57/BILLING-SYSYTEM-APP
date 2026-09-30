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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DatabaseSeeder
import com.example.ui.PosViewModel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreModal(
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    val backupRecords by viewModel.backupRecords.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val supabaseUrl = settings?.supabaseUrl?.ifBlank { DatabaseSeeder.DEFAULT_SUPABASE_URL } ?: DatabaseSeeder.DEFAULT_SUPABASE_URL
    val supabaseKey = settings?.supabaseAnonKey?.ifBlank { DatabaseSeeder.DEFAULT_SUPABASE_KEY } ?: DatabaseSeeder.DEFAULT_SUPABASE_KEY

    var connectionTestStatus by remember { mutableStateOf<String?>(null) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var restoringId by remember { mutableStateOf<String?>(null) }
    var showConfigDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = EscapeSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                        text = "Supabase Cloud Backup",
                        color = EscapeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Encrypted snapshots stored in Supabase cloud",
                        color = EscapeTextSecondary,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { viewModel.triggerBackup() },
                    colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp).testTag("backup_now_btn")
                ) {
                    Icon(imageVector = Icons.Default.CloudSync, contentDescription = "Backup", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Backup Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SUPABASE CLOUD STATUS CARD
            val isConfigured = supabaseUrl.isNotBlank() && supabaseKey.isNotBlank()
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EscapeSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EscapeBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isConfigured) EscapeSuccess else EscapeTextTertiary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isConfigured) "SUPABASE CLOUD READY" else "SUPABASE NOT CONFIGURED",
                                color = if (isConfigured) EscapeSuccess else EscapeTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { showConfigDialog = true },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("config_supabase_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(12.dp), tint = EscapeTextPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Configure",
                                    color = EscapeTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    if (!isConfigured) {
                                        connectionTestStatus = "✗ Please configure Supabase URL and Anon Key first."
                                        return@Button
                                    }
                                    isTestingConnection = true
                                    connectionTestStatus = null
                                    viewModel.testSupabaseConnection(supabaseUrl, supabaseKey) { success, msg ->
                                        isTestingConnection = false
                                        connectionTestStatus = if (success) "✓ $msg" else "✗ $msg"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EscapeSurfaceVariant),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("test_supabase_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(12.dp), tint = EscapeTextPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isTestingConnection) "Testing..." else "Test",
                                    color = EscapeTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (supabaseUrl.isNotBlank()) "Endpoint: $supabaseUrl" else "Endpoint: Not configured",
                        color = EscapeTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (supabaseKey.isNotBlank()) {
                            if (supabaseKey.length > 25) "Auth Anon Key: ${supabaseKey.take(15)}...${supabaseKey.takeLast(6)}"
                            else "Auth Anon Key: Configured"
                        } else "Auth Anon Key: Not configured",
                        color = EscapeTextTertiary,
                        fontSize = 10.sp
                    )

                    if (connectionTestStatus != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val isSuccess = connectionTestStatus!!.startsWith("✓")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSuccess) EscapeSuccess.copy(alpha = 0.15f) else EscapeError.copy(alpha = 0.15f))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = connectionTestStatus!!,
                                color = if (isSuccess) EscapeSuccess else EscapeError,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Backup History (${backupRecords.size} snapshots):",
                color = EscapeTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (backupRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No snapshots recorded yet. Tap 'Backup Now' to create one.", color = EscapeTextSecondary, fontSize = 12.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    backupRecords.take(8).forEach { rec ->
                        val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(rec.createdAt))
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = null,
                                            tint = EscapeSuccess,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = dateFormatted,
                                            color = EscapeTextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${rec.recordCount} records • Supabase Cloud Synced",
                                        color = EscapeTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = { restoringId = rec.id },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Restore", color = EscapeRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    restoringId?.let { bId ->
        Dialog(onDismissRequest = { restoringId = null }) {
            Surface(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), color = EscapeSurface) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Confirm Restore Snapshot", color = EscapeRed, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Restore snapshot $bId? Checksum integrity will be validated prior to applying data.", color = EscapeTextPrimary, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { restoringId = null }, modifier = Modifier.weight(1f)) {
                            Text("Cancel", color = EscapeTextPrimary)
                        }
                        Button(
                            onClick = {
                                viewModel.triggerRestore(bId)
                                restoringId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EscapeRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirm Restore", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// PURGE DEMO DATA DIALOG
@Composable
fun PurgeDemoDataDialog(
    viewModel: PosViewModel,
    onDismiss: () -> Unit
) {
    var isPurging by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = EscapeSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EscapeError.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, tint = EscapeError, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Remove All Demo Data",
                            color = EscapeError,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clean store for real customer transactions",
                            color = EscapeTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "This operation will purge:\n• All demo sales and invoices\n• All payment transactions\n• All demo returns and exchanges\n• All demo expenses and daily closings\n\nYour clothing product catalog, sizes, colors, and business settings will be preserved.",
                    color = EscapeTextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

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
                            isPurging = true
                            viewModel.purgeAllDemoData { success, _ ->
                                isPurging = false
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EscapeError),
                        modifier = Modifier.weight(1f).testTag("confirm_purge_demo_data_btn")
                    ) {
                        Text(if (isPurging) "Purging..." else "Remove Demo Data", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
