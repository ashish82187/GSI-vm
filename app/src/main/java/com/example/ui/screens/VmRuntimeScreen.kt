package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VmInstance
import com.example.data.model.VmStatus
import com.example.engine.TrebleFixesState
import com.example.engine.VmTelemetry
import com.example.ui.AppTab
import com.example.ui.VmViewModel
import com.example.ui.components.HardwareGauge
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
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VmRuntimeScreen(
    viewModel: VmViewModel,
    onOpenSnapshots: (VmInstance) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeVm by viewModel.activeVm.collectAsState()
    val vmStatus by viewModel.engine.vmStatus.collectAsState()
    val bootProgress by viewModel.engine.bootProgress.collectAsState()
    val bootStepText by viewModel.engine.bootStepText.collectAsState()
    val telemetry by viewModel.engine.telemetry.collectAsState()
    val trebleFixes by viewModel.engine.trebleFixes.collectAsState()
    val currentInVmApp by viewModel.currentInVmApp.collectAsState()
    val isQuickSettingsOpen by viewModel.isQuickSettingsOpen.collectAsState()

    var showHardwareHud by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = DarkBg,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        if (activeVm == null) {
            NoActiveVmView(
                onSelectVm = { viewModel.selectTab(AppTab.VMS) },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            val vm = activeVm!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Top Hypervisor VM Bar
                VmTopControlBar(
                    vm = vm,
                    status = vmStatus,
                    fps = telemetry.fps,
                    cpuPercent = telemetry.overallCpuPercent,
                    showHud = showHardwareHud,
                    onToggleHud = { showHardwareHud = !showHardwareHud },
                    onShutdown = { viewModel.stopActiveVm() },
                    onRecovery = { viewModel.rebootToRecovery() },
                    onFastboot = { viewModel.rebootToFastboot() },
                    onSnapshot = { onOpenSnapshots(vm) }
                )

                // Optional Hardware Telemetry HUD
                AnimatedVisibility(visible = showHardwareHud && vmStatus == VmStatus.RUNNING) {
                    HardwareTelemetryHud(
                        telemetry = telemetry,
                        vm = vm
                    )
                }

                // Main Display Screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black)
                        .border(2.dp, DarkBorder, RoundedCornerShape(18.dp))
                ) {
                    when (vmStatus) {
                        VmStatus.BOOTING -> {
                            BootSequenceDisplay(
                                vm = vm,
                                progress = bootProgress,
                                stepText = bootStepText
                            )
                        }
                        VmStatus.STOPPED -> {
                            VmOfflineDisplay(
                                vm = vm,
                                onStart = { viewModel.bootVm(vm) }
                            )
                        }
                        VmStatus.PAUSED -> {
                            VmPausedDisplay(
                                onResume = { viewModel.resumeActiveVm() }
                            )
                        }
                        VmStatus.RECOVERY -> {
                            TwrpRecoveryDisplay(
                                vm = vm,
                                onRebootSystem = {
                                    viewModel.bootVm(vm)
                                }
                            )
                        }
                        VmStatus.FASTBOOT -> {
                            FastbootDisplay(
                                vm = vm,
                                onRebootSystem = {
                                    viewModel.bootVm(vm)
                                }
                            )
                        }
                        VmStatus.RUNNING, VmStatus.FLASHING -> {
                            // The Live Android OS Sandbox GUI
                            LiveAndroidOsGui(
                                vm = vm,
                                currentApp = currentInVmApp,
                                isQuickSettingsOpen = isQuickSettingsOpen,
                                trebleFixes = trebleFixes,
                                telemetry = telemetry,
                                viewModel = viewModel,
                                onOpenApp = { viewModel.openInVmApp(it) },
                                onToggleQuickSettings = { viewModel.toggleQuickSettings() },
                                onCloseQuickSettings = { viewModel.closeQuickSettings() },
                                onBackPress = {
                                    if (isQuickSettingsOpen) {
                                        viewModel.closeQuickSettings()
                                    } else if (currentInVmApp != "homescreen") {
                                        viewModel.openInVmApp("homescreen")
                                    }
                                },
                                onHomePress = {
                                    viewModel.closeQuickSettings()
                                    viewModel.openInVmApp("homescreen")
                                }
                            )
                        }
                    }
                }

                // Bottom Hypervisor Control Quick Bar
                BottomVmControlBar(
                    vmStatus = vmStatus,
                    onSendHome = { viewModel.openInVmApp("homescreen") },
                    onOpenTerminal = { viewModel.openInVmApp("terminal") },
                    onOpenTrebleApp = { viewModel.openInVmApp("treble_app") },
                    onOpenLogcat = { viewModel.selectTab(AppTab.LOGCAT) }
                )
            }
        }
    }
}

@Composable
fun VmTopControlBar(
    vm: VmInstance,
    status: VmStatus,
    fps: Float,
    cpuPercent: Int,
    showHud: Boolean,
    onToggleHud: () -> Unit,
    onShutdown: () -> Unit,
    onRecovery: () -> Unit,
    onFastboot: () -> Unit,
    onSnapshot: () -> Unit
) {
    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (status == VmStatus.RUNNING) BrightEmerald else AmberWarning)
                )
                Column {
                    Text(
                        text = vm.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${vm.romName} • VNDK ${vm.vndkVersion}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Real-time telemetry indicators
            if (status == VmStatus.RUNNING) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkCardElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${fps.toInt()} FPS",
                            color = BrightEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkCardElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CPU $cpuPercent%",
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Control Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onToggleHud,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Toggle Telemetry HUD",
                        tint = if (showHud) CyberCyan else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onSnapshot,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Take Snapshot",
                        tint = NeonPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onRecovery,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "TWRP Recovery",
                        tint = AmberWarning,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onShutdown,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Power Off VM",
                        tint = CrimsonError,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HardwareTelemetryHud(
    telemetry: VmTelemetry,
    vm: VmInstance,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkCard,
        shape = RoundedCornerShape(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "VIRTUAL HARDWARE TELEMETRY",
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "GPU: ${vm.gpuRenderer.displayName.substringBefore(" ")} | Temp: ${String.format(Locale.US, "%.1f", telemetry.temperatureC)}°C",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // CPU Cores meters
                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = "vCPU Cores (${vm.cpuCores}x ARM64 @ 2.4GHz)",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        telemetry.cpuLoads.forEachIndexed { i, load ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(DarkBg),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((14 * (load / 100f)).dp)
                                        .background(if (load > 70f) AmberWarning else CyberCyan)
                                )
                            }
                        }
                    }
                }

                // RAM meter
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RAM: ${telemetry.ramUsedMb} / ${telemetry.ramTotalMb} MB",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { telemetry.ramUsedMb.toFloat() / telemetry.ramTotalMb },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = BrightEmerald,
                        trackColor = DarkBg
                    )
                }

                // I/O Throughput
                Column(modifier = Modifier.weight(0.9f)) {
                    Text(
                        text = "I/O Speed",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "R:${telemetry.ioReadKbps}K W:${telemetry.ioWriteKbps}K",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun LiveAndroidOsGui(
    vm: VmInstance,
    currentApp: String,
    isQuickSettingsOpen: Boolean,
    trebleFixes: TrebleFixesState,
    telemetry: VmTelemetry,
    viewModel: VmViewModel,
    onOpenApp: (String) -> Unit,
    onToggleQuickSettings: () -> Unit,
    onCloseQuickSettings: () -> Unit,
    onBackPress: () -> Unit,
    onHomePress: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background wallpaper for the VM
        val wallpaperGradient = when {
            vm.romName.contains("Pixel") -> listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
            vm.romName.contains("Evolution") -> listOf(Color(0xFF4C1D95), Color(0xFF0F172A))
            vm.romName.contains("crDroid") -> listOf(Color(0xFF064E3B), Color(0xFF0F172A))
            vm.romName.contains("Bliss") -> listOf(Color(0xFF78350F), Color(0xFF0F172A))
            else -> listOf(Color(0xFF134E4A), Color(0xFF0F172A))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(wallpaperGradient))
        )

        // Main In-VM Content
        Column(modifier = Modifier.fillMaxSize()) {
            // Status bar at top of VM screen
            InVmStatusBar(
                vm = vm,
                onPullDown = onToggleQuickSettings
            )

            // Current Active App Screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (currentApp) {
                    "homescreen" -> InVmHomescreen(
                        vm = vm,
                        onOpenApp = onOpenApp
                    )
                    "treble_app" -> InVmTrebleAppScreen(
                        vm = vm,
                        trebleFixes = trebleFixes,
                        onToggleFix = { viewModel.engine.toggleTrebleFix(it) }
                    )
                    "terminal" -> InVmTerminalScreen(
                        viewModel = viewModel
                    )
                    "settings" -> InVmSettingsScreen(
                        vm = vm
                    )
                    "magisk" -> InVmMagiskScreen(
                        vm = vm
                    )
                    "files" -> InVmFilesScreen(
                        vm = vm
                    )
                    "benchmark" -> InVmBenchmarkScreen(
                        vm = vm
                    )
                    "twrp" -> TwrpRecoveryDisplay(
                        vm = vm,
                        onRebootSystem = onHomePress
                    )
                    "fastboot" -> FastbootDisplay(
                        vm = vm,
                        onRebootSystem = onHomePress
                    )
                }

                // Quick Settings pull-down overlay
                if (isQuickSettingsOpen) {
                    InVmQuickSettingsPanel(
                        vm = vm,
                        trebleFixes = trebleFixes,
                        onToggleTrebleFix = { viewModel.engine.toggleTrebleFix(it) },
                        onClose = onCloseQuickSettings,
                        onOpenSettings = { onOpenApp("settings") }
                    )
                }

            }

            // Android In-VM 3-Button Navigation Bar
            InVmNavigationBar(
                onBack = onBackPress,
                onHome = onHomePress,
                onRecents = {
                    // Quick app switcher trigger
                    viewModel.openInVmApp("homescreen")
                }
            )
        }
    }
}

@Composable
fun InVmStatusBar(
    vm: VmInstance,
    onPullDown: () -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = timeFormat.format(Date())

    Surface(
        color = Color.Black.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPullDown() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time & GSI ROM Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = currentTime,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• ${vm.romName.substringBefore(" ")}",
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Status icons (WiFi, 5G, Battery, Treble)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = "SELinux", tint = NeonEmerald, modifier = Modifier.size(12.dp))
                Icon(imageVector = Icons.Default.Wifi, contentDescription = "WiFi", tint = Color.White, modifier = Modifier.size(13.dp))
                Icon(imageVector = Icons.Default.NetworkCheck, contentDescription = "5G", tint = Color.White, modifier = Modifier.size(13.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "98%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = "Battery", tint = BrightEmerald, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun InVmHomescreen(
    vm: VmInstance,
    onOpenApp: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    val currentTime = timeFormat.format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Clock & Weather / Treble Widget
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = currentDate,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Android, contentDescription = null, tint = BrightEmerald, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Treble Sandbox: ${vm.romName}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // App Icons Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppGridIcon(
                    title = "Phh Treble",
                    icon = Icons.Default.Tune,
                    color = Color(0xFF00E5FF),
                    onClick = { onOpenApp("treble_app") }
                )
                AppGridIcon(
                    title = "Root Shell",
                    icon = Icons.Default.Terminal,
                    color = BrightEmerald,
                    onClick = { onOpenApp("terminal") }
                )
                AppGridIcon(
                    title = "Magisk v27",
                    icon = Icons.Default.Security,
                    color = Color(0xFF00B0FF),
                    onClick = { onOpenApp("magisk") }
                )
                AppGridIcon(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF9E9E9E),
                    onClick = { onOpenApp("settings") }
                )
            }

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppGridIcon(
                    title = "Partitions",
                    icon = Icons.Default.Folder,
                    color = AmberWarning,
                    onClick = { onOpenApp("files") }
                )
                AppGridIcon(
                    title = "Benchmark",
                    icon = Icons.Default.Assessment,
                    color = NeonPurple,
                    onClick = { onOpenApp("benchmark") }
                )
                AppGridIcon(
                    title = "TWRP 3.7",
                    icon = Icons.Default.Build,
                    color = Color(0xFFFF9800),
                    onClick = { onOpenApp("twrp") }
                )
                AppGridIcon(
                    title = "Fastbootd",
                    icon = Icons.Default.ElectricBolt,
                    color = Color(0xFF29B6F6),
                    onClick = { onOpenApp("fastboot") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar Widget
            Surface(
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenApp("terminal") }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Search in GSI / Run Shell Command...",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = BrightEmerald, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AppGridIcon(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.25f))
                .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InVmTrebleAppScreen(
    vm: VmInstance,
    trebleFixes: TrebleFixesState,
    onToggleFix: (String) -> Unit
) {
    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                    Column {
                        Text(
                            text = "Phh Treble Settings (me.phh.treble.app)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hardware compatibility & VNDK ${vm.vndkVersion} shims",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "AUDIO & MEDIA FIXES",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                TrebleToggleRow(
                    title = "In-Call Audio Routing Fix",
                    subtitle = "Routes earpiece & mic through OpenSL virgl audio HAL",
                    checked = trebleFixes.audioInCallFix,
                    onCheckedChange = { onToggleFix("audio") }
                )
            }

            item {
                TrebleToggleRow(
                    title = "Bypass Safe Volume Warning",
                    subtitle = "Allows max headphone gain without safe audio dialog",
                    checked = trebleFixes.bypassSafeVolume,
                    onCheckedChange = { onToggleFix("safe_volume") }
                )
            }

            item {
                Text(
                    text = "DISPLAY & REFRESH RATE",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                TrebleToggleRow(
                    title = "Force 120Hz Refresh Rate",
                    subtitle = "Overwrites display HAL vsync to 120 FPS max smoothness",
                    checked = trebleFixes.force120Hz,
                    onCheckedChange = { onToggleFix("120hz") }
                )
            }

            item {
                TrebleToggleRow(
                    title = "Double Tap to Wake (DT2W)",
                    subtitle = "Enables virtual touch input sensor event listener",
                    checked = trebleFixes.dt2wEnabled,
                    onCheckedChange = { onToggleFix("dt2w") }
                )
            }

            item {
                Text(
                    text = "SECURITY & CERTIFICATION",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                TrebleToggleRow(
                    title = "Disable dm-verity & AVB 2.0",
                    subtitle = "Permits writing to /system and dynamic partition mounting",
                    checked = trebleFixes.disableDmVerity,
                    onCheckedChange = { onToggleFix("dmverity") }
                )
            }

            item {
                TrebleToggleRow(
                    title = "Spoof Pixel 8 Pro Device Profile",
                    subtitle = "Passes basic Play Integrity & certified device check",
                    checked = trebleFixes.spoofPixelCertification,
                    onCheckedChange = { onToggleFix("pixel_spoof") }
                )
            }

            item {
                TrebleToggleRow(
                    title = "Force Camera HAL3 Support",
                    subtitle = "Exposes full Camera2 API virtual sensor streams",
                    checked = trebleFixes.cameraHal3Forced,
                    onCheckedChange = { onToggleFix("camera_hal3") }
                )
            }
        }
    }
}

@Composable
fun TrebleToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DarkBg,
                    checkedTrackColor = CyberCyan
                )
            )
        }
    }
}

@Composable
fun InVmTerminalScreen(
    viewModel: VmViewModel
) {
    val history by viewModel.engine.terminalHistory.collectAsState()
    var inputCommand by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Surface(
        color = TerminalBg,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Terminal Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SHELL @ DROIDVM (ARM64 ROOT)",
                    color = TerminalCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Type 'help' | 'su' | 'treble_check'",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Terminal Output Console
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(history) { line ->
                    Text(
                        text = line,
                        color = when {
                            line.startsWith("root@") -> TerminalGreen
                            line.startsWith("shell@") -> TerminalCyan
                            line.startsWith("[✓]") -> BrightEmerald
                            line.startsWith("Magisk") -> NeonPurple
                            line.startsWith("bash:") || line.startsWith("Permission") -> CrimsonError
                            else -> TextPrimary
                        },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }

            // Quick suggestion chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("treble_check", "getprop", "su", "uname -a", "df -h", "magisk -v").forEach { cmd ->
                    Surface(
                        color = DarkCardElevated,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable {
                            viewModel.engine.executeTerminalCommand(cmd)
                        }
                    ) {
                        Text(
                            text = cmd,
                            color = TerminalCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = inputCommand,
                    onValueChange = { inputCommand = it },
                    placeholder = {
                        Text("Enter GSI shell command...", color = TextMuted, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("terminal_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = TerminalGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            viewModel.engine.executeTerminalCommand(inputCommand)
                            inputCommand = ""
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    )
                )

                Button(
                    onClick = {
                        viewModel.engine.executeTerminalCommand(inputCommand)
                        inputCommand = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DarkBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Run", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun InVmSettingsScreen(vm: VmInstance) {
    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Android System Settings",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "ABOUT VIRTUAL PHONE", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        SettingsSpecRow("Device Name", "DroidVM Treble Virtual Sandbox")
                        SettingsSpecRow("ROM Name", vm.romName)
                        SettingsSpecRow("Android Version", vm.androidVersion)
                        SettingsSpecRow("API Level", "SDK ${vm.apiLevel}")
                        SettingsSpecRow("Treble VNDK Version", "v${vm.vndkVersion}")
                        SettingsSpecRow("Kernel Version", vm.linuxKernelVersion)
                        SettingsSpecRow("SELinux Status", vm.selinuxMode.displayName)
                        SettingsSpecRow("Architecture", vm.arch.displayName)
                        SettingsSpecRow("Dynamic Partitions", "Enabled (A/B SAR)")
                        SettingsSpecRow("Display", "${vm.displayWidth}x${vm.displayHeight} @ ${vm.refreshRateHz}Hz (${vm.displayDpi} DPI)")
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSpecRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun InVmMagiskScreen(vm: VmInstance) {
    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(28.dp))
                Column {
                    Text(text = "Magisk Manager v27.0", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Systemless Root & Zygisk Sandbox", color = BrightEmerald, fontSize = 11.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "MAGISK STATUS", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    SettingsSpecRow("Installed", "v27.0 (27000) (topjohnwu)")
                    SettingsSpecRow("Ramdisk", "Yes (SAR Init)")
                    SettingsSpecRow("Zygisk", "Active (Virtual Sandbox)")
                    SettingsSpecRow("SU Binary", "/system/xbin/su (UID 0)")
                }
            }

            Text(text = "INSTALLED MODULES", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            MagiskModuleCard(name = "Universal SafetyNet Fix (Play Integrity NEXT)", author = "kdrag0n / Displax", version = "v2.4.0-RC2")
            MagiskModuleCard(name = "Busybox for Android NDK", author = "osm0sis", version = "1.36.1")
            MagiskModuleCard(name = "Treble App Overlay Enhancer", author = "phhusson", version = "v1.2")
        }
    }
}

@Composable
fun MagiskModuleCard(name: String, author: String, version: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "by $author • $version", color = TextMuted, fontSize = 10.sp)
            }
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = BrightEmerald, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun InVmFilesScreen(vm: VmInstance) {
    val partitions = listOf(
        Triple("/system", "3.5 GB (ext4 / erofs)", "ro,noatime,SAR"),
        Triple("/vendor", "1.2 GB (ext4)", "ro,noatime,Treble VNDK ${vm.vndkVersion}"),
        Triple("/product", "850 MB (ext4)", "ro,noatime,Product GApps"),
        Triple("/system_ext", "420 MB (ext4)", "ro,noatime,System Extensions"),
        Triple("/data", "${vm.userdataSizeMb / 1024} GB (f2fs/ext4)", "rw,nosuid,nodev,userdata"),
        Triple("/apex", "680 MB (tmpfs)", "com.android.runtime,art"),
        Triple("/storage/emulated/0", "${vm.userdataSizeMb / 1024 - 1} GB (fuse)", "sdcard virtual storage")
    )

    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(text = "Dynamic Partitions Explorer", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "Super Partition /dev/block/mapper", color = TextSecondary, fontSize = 11.sp)
            }
            items(partitions) { (name, size, flags) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(22.dp))
                        Column {
                            Text(text = name, color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text(text = "$size • $flags", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InVmBenchmarkScreen(vm: VmInstance) {
    var isRunningBenchmark by remember { mutableStateOf(false) }
    var singleCoreScore by remember { mutableStateOf(1180) }
    var multiCoreScore by remember { mutableStateOf(3420) }
    var vulkanScore by remember { mutableStateOf(4250) }

    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "GSI Virtual Benchmark Suite", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "Stress testing vCPUs and VirGL 3D GPU backend", color = TextSecondary, fontSize = 11.sp)

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$singleCoreScore", color = CyberCyan, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Single-Core", color = TextSecondary, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$multiCoreScore", color = BrightEmerald, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Multi-Core (${vm.cpuCores}c)", color = TextSecondary, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$vulkanScore", color = NeonPurple, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Text(text = "VirGL GLES", color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    if (isRunningBenchmark) {
                        CircularProgressIndicator(color = CyberCyan, modifier = Modifier.size(32.dp))
                        Text(text = "Running stress computations...", color = TextSecondary, fontSize = 11.sp)
                    } else {
                        Button(
                            onClick = {
                                isRunningBenchmark = true
                                // Simulated benchmark update
                                singleCoreScore = 1150 + kotlin.random.Random.nextInt(50)
                                multiCoreScore = 3300 + vm.cpuCores * 200 + kotlin.random.Random.nextInt(100)
                                vulkanScore = 4100 + kotlin.random.Random.nextInt(200)
                                isRunningBenchmark = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DarkBg),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Run Benchmark Stress Test", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InVmQuickSettingsPanel(
    vm: VmInstance,
    trebleFixes: TrebleFixesState,
    onToggleTrebleFix: (String) -> Unit,
    onClose: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.92f),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Settings & Treble Toggles",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                }
            }

            // Quick Toggle Tiles Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTile(
                    title = "SELinux",
                    subtitle = if (trebleFixes.disableDmVerity) "Permissive" else "Enforcing",
                    icon = Icons.Default.Security,
                    active = trebleFixes.disableDmVerity,
                    onClick = { onToggleTrebleFix("dmverity") },
                    modifier = Modifier.weight(1f)
                )
                QuickTile(
                    title = "120Hz FPS",
                    subtitle = if (trebleFixes.force120Hz) "Forced" else "60Hz",
                    icon = Icons.Default.Speed,
                    active = trebleFixes.force120Hz,
                    onClick = { onToggleTrebleFix("120hz") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTile(
                    title = "Audio In-Call",
                    subtitle = if (trebleFixes.audioInCallFix) "Patched" else "Stock",
                    icon = Icons.Default.VolumeUp,
                    active = trebleFixes.audioInCallFix,
                    onClick = { onToggleTrebleFix("audio") },
                    modifier = Modifier.weight(1f)
                )
                QuickTile(
                    title = "Pixel Spoof",
                    subtitle = if (trebleFixes.spoofPixelCertification) "Active" else "Disabled",
                    icon = Icons.Default.Android,
                    active = trebleFixes.spoofPixelCertification,
                    onClick = { onToggleTrebleFix("pixel_spoof") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickTile(
                    title = "Wi-Fi Bridge",
                    subtitle = "NAT Connected",
                    icon = Icons.Default.Wifi,
                    active = true,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                QuickTile(
                    title = "Dark Theme",
                    subtitle = "AMOLED",
                    icon = Icons.Default.DarkMode,
                    active = true,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onClose()
                    onOpenSettings()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkCardElevated),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Full Settings App", color = TextPrimary)
            }
        }
    }
}

@Composable
fun QuickTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (active) CyberCyan.copy(alpha = 0.2f) else DarkCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) CyberCyan else DarkBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (active) CyberCyan else TextSecondary, modifier = Modifier.size(20.dp))
            Column {
                Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = if (active) BrightEmerald else TextMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun InVmNavigationBar(
    onBack: () -> Unit,
    onHome: () -> Unit,
    onRecents: () -> Unit
) {
    Surface(
        color = Color.Black.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onHome, modifier = Modifier.size(40.dp)) {
                Icon(imageVector = Icons.Default.Circle, contentDescription = "Home", tint = Color.White, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onRecents, modifier = Modifier.size(40.dp)) {
                Icon(imageVector = Icons.Default.Square, contentDescription = "Recents", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun BootSequenceDisplay(
    vm: VmInstance,
    progress: Float,
    stepText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                color = CyberCyan,
                trackColor = DarkBorder,
                modifier = Modifier.size(64.dp)
            )
            Icon(
                imageVector = Icons.Default.Android,
                contentDescription = null,
                tint = BrightEmerald,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Booting ${vm.romName}...",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stepText,
            color = CyberCyan,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = BrightEmerald,
            trackColor = DarkBorder
        )
    }
}

@Composable
fun VmOfflineDisplay(
    vm: VmInstance,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Virtual Machine is Offline", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "${vm.name} (${vm.romName})", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DarkBg),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Power On VM", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun VmPausedDisplay(
    onResume: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = Icons.Default.Pause, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Virtual Machine Paused", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "CPU & Memory state frozen in hypervisor", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onResume,
            colors = ButtonDefaults.buttonColors(containerColor = AmberWarning, contentColor = DarkBg),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Resume Execution", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TwrpRecoveryDisplay(
    vm: VmInstance,
    onRebootSystem: () -> Unit
) {
    var currentTwrpAction by remember { mutableStateOf("") }

    Surface(
        color = Color(0xFF0F172A),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TWRP Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Team Win Recovery Project (TWRP 3.7.0)",
                    color = Color(0xFF00E5FF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "GSI Recovery Mode",
                    color = BrightEmerald,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (currentTwrpAction.isNotEmpty()) {
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = currentTwrpAction, color = BrightEmerald, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // TWRP 8 Main Tiles
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TwrpTile("Install", Icons.Default.ArrowDownward, Color(0xFF00E5FF), { currentTwrpAction = "Mounting /sdcard... Selecting GSI ROM ZIP package." }, Modifier.weight(1f))
                    TwrpTile("Wipe", Icons.Default.Refresh, AmberWarning, { currentTwrpAction = "Wiping Dalvik, ART cache, and formatted userdata." }, Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TwrpTile("Backup", Icons.Default.CameraAlt, NeonPurple, { currentTwrpAction = "Created backup of /system, /vendor and /boot partitions." }, Modifier.weight(1f))
                    TwrpTile("Restore", Icons.Default.Refresh, BrightEmerald, { currentTwrpAction = "Restored system image from virtual backup." }, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TwrpTile("Mount", Icons.Default.Folder, Color(0xFF60A5FA), { currentTwrpAction = "Mounted: /system_root, /vendor, /product, /data." }, Modifier.weight(1f))
                    TwrpTile("Terminal", Icons.Default.Terminal, TerminalGreen, { currentTwrpAction = "Opened root recovery busybox shell." }, Modifier.weight(1f))
                }
            }

            // Reboot System button
            Button(
                onClick = onRebootSystem,
                colors = ButtonDefaults.buttonColors(containerColor = BrightEmerald, contentColor = DarkBg),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reboot System (Exit Recovery)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TwrpTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = modifier
            .height(54.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FastbootDisplay(
    vm: VmInstance,
    onRebootSystem: () -> Unit
) {
    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "FASTBOOT MODE (user-space fastbootd)", color = CrimsonError, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text(text = "PRODUCT_NAME - droidvm_treble_arm64", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(text = "VARIANT - arm64-v8a A/B SAR", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(text = "BOOTLOADER VERSION - DROIDVM-BL-2024.05", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(text = "BASEBAND VERSION - N/A (Virtual)", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(text = "SECURE BOOT - DISABLED (AVB Permissive)", color = BrightEmerald, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(text = "DEVICE STATE - UNLOCKED", color = AmberWarning, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }

            Surface(
                color = DarkCardElevated,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Awaiting fastboot commands from host flasher...\nSupported: 'fastboot flash system <img_file>', 'fastboot reboot'",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Button(
                onClick = onRebootSystem,
                colors = ButtonDefaults.buttonColors(containerColor = BrightEmerald, contentColor = DarkBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("fastboot reboot (Start System)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BottomVmControlBar(
    vmStatus: VmStatus,
    onSendHome: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenTrebleApp: () -> Unit,
    onOpenLogcat: () -> Unit
) {
    Surface(
        color = DarkSurface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onSendHome,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = DarkCardElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Home", fontSize = 11.sp)
            }

            FilledTonalButton(
                onClick = onOpenTrebleApp,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = DarkCardElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Treble", fontSize = 11.sp)
            }

            FilledTonalButton(
                onClick = onOpenTerminal,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = DarkCardElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = BrightEmerald, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Shell", fontSize = 11.sp)
            }

            FilledTonalButton(
                onClick = onOpenLogcat,
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = DarkCardElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Logcat", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun NoActiveVmView(
    onSelectVm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = Icons.Default.Android, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "No Active Virtual Machine Running", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Select an existing VM sandbox or flash a new custom GSI ROM to launch the live virtualization sandbox display.",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSelectVm,
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DarkBg),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Select Virtual Machine", fontWeight = FontWeight.Bold)
        }
    }
}
