package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VmInstance
import com.example.data.model.VmStatus
import com.example.ui.AppTab
import com.example.ui.VmViewModel
import com.example.ui.components.HardwareGauge
import com.example.ui.components.MetricPill
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BrightEmerald
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VmListScreen(
    viewModel: VmViewModel,
    vms: List<VmInstance>,
    onOpenVmConfig: (VmInstance) -> Unit,
    onOpenSnapshots: (VmInstance) -> Unit,
    modifier: Modifier = Modifier
) {
    var vmToDelete by remember { mutableStateOf<VmInstance?>(null) }

    Scaffold(
        containerColor = DarkBg,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Host Hypervisor Banner
            item {
                HostHypervisorHeader(
                    vmCount = vms.size,
                    activeCount = vms.count { it.status == VmStatus.RUNNING },
                    onNavigateToCatalog = { viewModel.selectTab(AppTab.CATALOG) },
                    onNavigateToFlasher = { viewModel.selectTab(AppTab.FLASHER) }
                )
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VIRTUAL SANDBOXES",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Installed Custom GSI Instances",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "${vms.size} Sandboxes",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // List of VM cards
            if (vms.isEmpty()) {
                item {
                    EmptyVmState(
                        onExploreCatalog = { viewModel.selectTab(AppTab.CATALOG) }
                    )
                }
            } else {
                items(vms, key = { it.id }) { vm ->
                    VmInstanceCard(
                        vm = vm,
                        onBoot = { viewModel.bootVm(vm) },
                        onPause = { viewModel.pauseActiveVm() },
                        onResume = { viewModel.resumeActiveVm() },
                        onTerminal = {
                            viewModel.bootVm(vm)
                            viewModel.openInVmApp("terminal")
                        },
                        onRecovery = {
                            viewModel.bootVm(vm)
                            viewModel.rebootToRecovery()
                        },
                        onOpenSettings = { onOpenVmConfig(vm) },
                        onOpenSnapshots = { onOpenSnapshots(vm) },
                        onDelete = { vmToDelete = vm }
                    )
                }
            }

            // Quick Quick Guides & Treble Specifications item
            item {
                TrebleCapabilitiesSummaryCard()
            }
        }
    }

    // Delete Confirmation Dialog
    if (vmToDelete != null) {
        val vm = vmToDelete!!
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { vmToDelete = null },
            title = {
                Text(text = "Delete Virtual Machine?", color = TextPrimary)
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${vm.name}'? This will remove its simulated dynamic system partition and userdata (${vm.userdataSizeMb / 1024} GB).",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteVm(vm)
                        vmToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonError)
                ) {
                    Text("Delete VM", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { vmToDelete = null }) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = DarkCardElevated
        )
    }
}

@Composable
fun HostHypervisorHeader(
    vmCount: Int,
    activeCount: Int,
    onNavigateToCatalog: () -> Unit,
    onNavigateToFlasher: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(CyberCyan.copy(alpha = 0.2f), NeonPurple.copy(alpha = 0.2f))
                                )
                            )
                            .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Hypervisor",
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "DroidVM KVM Hypervisor",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "VIRTUAL READY",
                                    color = NeonEmerald,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "AVF Microdroid & Project Treble Sandbox Engine",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick Host Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    label = "Virtual CPUs",
                    value = "8 Cores",
                    accentColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Host RAM Pool",
                    value = "8.0 GB",
                    accentColor = NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Treble VNDK",
                    value = "v30 - v35",
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            // Quick action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToCatalog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = DarkBg
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hub_flash_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GSI ROM Hub",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToFlasher,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("custom_img_flasher_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = BrightEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Flash Custom .img",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VmInstanceCard(
    vm: VmInstance,
    onBoot: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onTerminal: () -> Unit,
    onRecovery: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSnapshots: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = vm.status == VmStatus.RUNNING
    val isPaused = vm.status == VmStatus.PAUSED
    val isBooting = vm.status == VmStatus.BOOTING

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isRunning) CyberCyan.copy(alpha = 0.8f) else DarkBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("vm_card_${vm.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    vm.romName.contains("Pixel") -> Color(0xFF4285F4).copy(alpha = 0.2f)
                                    vm.romName.contains("Evolution") -> NeonPurple.copy(alpha = 0.2f)
                                    vm.romName.contains("crDroid") -> NeonEmerald.copy(alpha = 0.2f)
                                    else -> CyberCyan.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            tint = when {
                                vm.romName.contains("Pixel") -> Color(0xFF4285F4)
                                vm.romName.contains("Evolution") -> NeonPurple
                                vm.romName.contains("crDroid") -> NeonEmerald
                                else -> CyberCyan
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = vm.name,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${vm.romName} • ${vm.androidVersion}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                StatusBadge(status = vm.status)
            }

            // Specs badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecBadge(label = "${vm.cpuCores} vCPUs")
                SpecBadge(label = "${vm.ramAllocatedMb / 1024} GB RAM")
                SpecBadge(label = "VNDK v${vm.vndkVersion}")
                SpecBadge(label = vm.gappsType.displayName.substringBefore(" "))
                if (vm.isRooted) {
                    SpecBadge(label = "Magisk Root", color = BrightEmerald)
                }
            }

            // Partition & Layout Info
            Surface(
                color = DarkBg,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Partition: ${if (vm.partitionLayout == com.example.data.model.PartitionLayout.A_B) "A/B Dynamic (SAR)" else "A-Only"}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "SELinux: ${vm.selinuxMode.displayName}",
                        color = if (vm.selinuxMode == com.example.data.model.SelinuxMode.PERMISSIVE) AmberWarning else NeonEmerald,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Control Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary Boot / Stop Button
                if (isRunning) {
                    Button(
                        onClick = onPause,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(imageVector = Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkBg)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else if (isPaused) {
                    Button(
                        onClick = onResume,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkBg)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resume", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onBoot,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = DarkBg)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Start VM", color = DarkBg, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // Terminal Shell Button
                FilledTonalButton(
                    onClick = onTerminal,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = DarkCardElevated,
                        contentColor = BrightEmerald
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Shell", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Snapshots Button
                IconButton(
                    onClick = onOpenSnapshots,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Snapshots",
                        tint = NeonPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Settings Button
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Config",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = CrimsonError.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SpecBadge(
    label: String,
    color: Color = TextSecondary,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkCardElevated,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EmptyVmState(
    onExploreCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = "No Virtual Machines Found",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Download a pre-configured Treble GSI from the ROM Hub or import your custom .img system image to start testing.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onExploreCatalog,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DarkBg),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Open GSI ROM Hub", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TrebleCapabilitiesSummaryCard(modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BrightEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Project Treble & DSU Sandbox Safety",
                    color = BrightEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "All virtual machines run in isolated KVM / AVF virtual partitions. Testing GSI ROMs in DroidVM never affects your physical Android host OS or physical partitions.",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
