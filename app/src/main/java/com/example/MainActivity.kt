package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.AppDatabase
import com.example.data.model.VmInstance
import com.example.data.model.VmStatus
import com.example.data.repository.VmRepository
import com.example.engine.VmHypervisorEngine
import com.example.ui.AppTab
import com.example.ui.VmViewModel
import com.example.ui.components.SnapshotDialog
import com.example.ui.components.VmConfigDialog
import com.example.ui.screens.FlasherToolScreen
import com.example.ui.screens.GsiCatalogScreen
import com.example.ui.screens.LogcatScreen
import com.example.ui.screens.VmListScreen
import com.example.ui.screens.VmRuntimeScreen
import com.example.ui.theme.BrightEmerald
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DroidVmTheme
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DroidVmTheme {
                DroidVmApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroidVmApp(viewModel: VmViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val vms by viewModel.vms.collectAsState()
    val activeVm by viewModel.activeVm.collectAsState()
    val vmStatus by viewModel.engine.vmStatus.collectAsState()

    var configVmTarget by remember { mutableStateOf<VmInstance?>(null) }
    var snapshotVmTarget by remember { mutableStateOf<VmInstance?>(null) }


    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "DroidVM",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "GSI SANDBOX",
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                actions = {
                    if (activeVm != null) {
                        Surface(
                            color = DarkCard,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrightEmerald.copy(alpha = 0.5f)),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (vmStatus == VmStatus.RUNNING) BrightEmerald else CyberCyan)
                                )
                                Text(
                                    text = activeVm!!.name.take(14),
                                    color = BrightEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = CyberCyan,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.VMS,
                    onClick = { viewModel.selectTab(AppTab.VMS) },
                    icon = {
                        Icon(imageVector = Icons.Default.Computer, contentDescription = "Virtual Machines")
                    },
                    label = { Text("VMs", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_vms")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.RUNTIME,
                    onClick = { viewModel.selectTab(AppTab.RUNTIME) },
                    icon = {
                        if (vmStatus == VmStatus.RUNNING) {
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = BrightEmerald) {
                                        Text("ON", fontSize = 8.sp, color = DarkBg, fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Live Sandbox")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Live Sandbox")
                        }
                    },
                    label = { Text("Sandbox", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_runtime")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.CATALOG,
                    onClick = { viewModel.selectTab(AppTab.CATALOG) },
                    icon = {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "ROM Hub")
                    },
                    label = { Text("ROM Hub", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_catalog")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.FLASHER,
                    onClick = { viewModel.selectTab(AppTab.FLASHER) },
                    icon = {
                        Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Flasher")
                    },
                    label = { Text("Flasher", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_flasher")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.LOGCAT,
                    onClick = { viewModel.selectTab(AppTab.LOGCAT) },
                    icon = {
                        Icon(imageVector = Icons.Default.Terminal, contentDescription = "Logcat")
                    },
                    label = { Text("Logcat", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBg,
                        selectedTextColor = CyberCyan,
                        indicatorColor = CyberCyan,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("tab_logcat")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                AppTab.VMS -> VmListScreen(
                    viewModel = viewModel,
                    vms = vms,
                    onOpenVmConfig = { configVmTarget = it },
                    onOpenSnapshots = { snapshotVmTarget = it }
                )
                AppTab.RUNTIME -> VmRuntimeScreen(
                    viewModel = viewModel,
                    onOpenSnapshots = { snapshotVmTarget = it }
                )
                AppTab.CATALOG -> GsiCatalogScreen(
                    viewModel = viewModel
                )
                AppTab.FLASHER -> FlasherToolScreen(
                    viewModel = viewModel
                )
                AppTab.LOGCAT -> LogcatScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    // Config Dialog
    if (configVmTarget != null) {
        VmConfigDialog(
            vm = configVmTarget!!,
            onDismiss = { configVmTarget = null },
            onSave = { updated ->
                viewModel.updateVm(updated)
                configVmTarget = null
            }
        )
    }

    // Snapshots Dialog
    if (snapshotVmTarget != null) {
        SnapshotDialog(
            vm = snapshotVmTarget!!,
            viewModel = viewModel,
            onDismiss = { snapshotVmTarget = null }
        )
    }
}

