package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFolderUpload
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GsiAnalysisResult
import com.example.ui.VmViewModel
import com.example.ui.components.TrebleCompatibilityBadge
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlasherToolScreen(
    viewModel: VmViewModel,
    modifier: Modifier = Modifier
) {
    var selectedToolTab by remember { mutableIntStateOf(0) }
    val analysisResult by viewModel.customAnalysisResult.collectAsState()
    val flashingState by viewModel.flashingState.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Flasher Tool Tabs (Analyzer & Flasher, Dynamic Partitions, Fastboot Studio)
            TabRow(
                selectedTabIndex = selectedToolTab,
                containerColor = DarkSurface,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedToolTab]),
                        color = CyberCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedToolTab == 0,
                    onClick = { selectedToolTab = 0 },
                    text = { Text("Custom GSI Flasher", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedToolTab == 1,
                    onClick = { selectedToolTab = 1 },
                    text = { Text("Dynamic Partitions", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedToolTab == 2,
                    onClick = { selectedToolTab = 2 },
                    text = { Text("Fastboot Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedToolTab) {
                    0 -> CustomGsiAnalyzerView(
                        viewModel = viewModel,
                        analysisResult = analysisResult,
                        flashingState = flashingState
                    )
                    1 -> DynamicPartitionsManagerView()
                    2 -> FastbootStudioView()
                }
            }
        }
    }

    if (flashingState.isFlashing) {
        FlashingOverlayDialog(flashingState = flashingState)
    }
}

@Composable
fun CustomGsiAnalyzerView(
    viewModel: VmViewModel,
    analysisResult: GsiAnalysisResult?,
    flashingState: com.example.ui.FlashingProgressState
) {
    val context = LocalContext.current
    val importedRomFiles by viewModel.importedRomFiles.collectAsState()

    // Android Document File Picker Launcher
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importGsiFileFromUri(context, it)
        }
    }

    // Fallback Generic Content Picker Launcher
    val getContentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importGsiFileFromUri(context, it)
        }
    }

    var fileNameInput by remember { mutableStateOf("lineage-21.0-20240518-UNOFFICIAL-arm64-bgN.img") }
    var customVmName by remember { mutableStateOf("Custom Lineage 21 GSI") }
    var selectedRamMb by remember { mutableIntStateOf(3072) }
    var selectedCores by remember { mutableIntStateOf(4) }

    // Synchronize VM name if analysis result updates
    androidx.compose.runtime.LaunchedEffect(analysisResult) {
        if (analysisResult != null) {
            val romBase = analysisResult.fileName
                .removeSuffix(".img")
                .removeSuffix(".img.xz")
                .removeSuffix(".zip")
                .removeSuffix(".gz")
            customVmName = "${analysisResult.detectedRomFamily} ($romBase)".take(30)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero File Picker Action Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    CyberCyan.copy(alpha = 0.12f),
                                    DarkSurface
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DriveFolderUpload,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "IMPORT GSI FROM DEVICE STORAGE",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Select any .img, .xz, or .zip Treble ROM from Downloads or SD card",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Big Storage File Picker Button
                        Button(
                            onClick = {
                                try {
                                    openDocumentLauncher.launch(
                                        arrayOf(
                                            "*/*",
                                            "application/octet-stream",
                                            "application/x-xz",
                                            "application/zip",
                                            "application/x-compressed",
                                            "application/gzip"
                                        )
                                    )
                                } catch (_: Exception) {
                                    getContentLauncher.launch("*/*")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan,
                                contentColor = DarkBg
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("import_gsi_file_picker_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileOpen,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Browse & Select GSI File (.img / .xz / .zip)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Supported Formats Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("ARM64 / ARM32", "A/B Dynamic SAR", "EROFS / ext4", "VNDK v30-v35").forEach { pill ->
                                Surface(
                                    color = DarkCard,
                                    shape = RoundedCornerShape(6.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
                                ) {
                                    Text(
                                        text = pill,
                                        color = TextMuted,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // List of Imported ROM files in session
        if (importedRomFiles.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "IMPORTED GSI ROMS (${importedRomFiles.size})",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Click to Inspect & Flash",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(importedRomFiles, key = { it.fileName }) { imported ->
                            val isSelected = analysisResult?.fileName == imported.fileName
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) DarkCardElevated else DarkSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) BrightEmerald else DarkBorder
                                ),
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable { viewModel.selectImportedRom(imported) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = imported.detectedRomFamily,
                                            color = CyberCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(
                                            onClick = { viewModel.removeImportedRom(imported) },
                                            modifier = Modifier.size(18.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = TextMuted,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = imported.fileName,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${imported.fileSizeMb} MB",
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(text = "•", color = TextMuted, fontSize = 10.sp)
                                        Text(
                                            text = "${imported.trebleScore}% Treble",
                                            color = BrightEmerald,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Manual Filename / Preset Emulator Tester Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "QUICK GSI TEST PRESETS OR MANUAL PATH",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    OutlinedTextField(
                        value = fileNameInput,
                        onValueChange = { fileNameInput = it },
                        label = { Text("GSI Image Filename or /sdcard/ path", fontSize = 11.sp) },
                        placeholder = { Text("e.g. PixelOS_gsi-arm64-bgZ.img", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_gsi_filename_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    // Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Lineage 21" to "lineage-21.0-arm64-bgN.img",
                            "PixelOS 14" to "PixelOS_gsi-arm64-bgZ.img",
                            "Evolution X" to "EvolutionX_14.0-arm64-bgZ.img",
                            "HyperOS" to "HyperOS_Port-arm64-ab.img"
                        ).forEach { (label, fname) ->
                            Surface(
                                color = DarkCardElevated,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable {
                                    fileNameInput = fname
                                    customVmName = "$label GSI VM"
                                    viewModel.analyzeCustomGsiFile(fname, 3400)
                                }
                            ) {
                                Text(
                                    text = label,
                                    color = CyberCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.analyzeCustomGsiFile(fileNameInput, 3500)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("analyze_gsi_button")
                    ) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analyze & Inspect Image", color = CyberCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Analysis Results & Flash to Sandbox Card
        if (analysisResult != null) {
            val res = analysisResult
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrightEmerald),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TREBLE ANALYSIS: ${res.detectedRomFamily.uppercase()}",
                                    color = BrightEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = res.fileName,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                if (res.fileUriString != null) {
                                    Text(
                                        text = "Imported from Storage File Picker",
                                        color = CyberCyan,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            TrebleCompatibilityBadge(score = res.trebleScore)
                        }

                        // Specs Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnalysisSpecCard("Target OS", res.detectedAndroidVersion.substringBefore(" "), Modifier.weight(1f))
                            AnalysisSpecCard("Architecture", res.targetArch.displayName.substringBefore(" "), Modifier.weight(1f))
                            AnalysisSpecCard("VNDK Compat", "v${res.vndkLevel}", Modifier.weight(1f))
                            AnalysisSpecCard("Size", "${res.fileSizeMb} MB", Modifier.weight(1f))
                        }

                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Image Format:", color = TextMuted, fontSize = 11.sp)
                                Text(text = res.format, color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Passed checks
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Treble Diagnostic Verifications:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            res.passedChecks.forEach { check ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BrightEmerald, modifier = Modifier.size(14.dp))
                                    Text(text = check, color = TextPrimary, fontSize = 11.sp)
                                }
                            }
                        }

                        // VM Creation Config Form
                        Text(text = "SANDBOX VM HARDWARE", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                        OutlinedTextField(
                            value = customVmName,
                            onValueChange = { customVmName = it },
                            label = { Text("Sandbox VM Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Allocated RAM: ${selectedRamMb / 1024} GB", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "vCPUs: $selectedCores Cores", color = TextSecondary, fontSize = 12.sp)
                        }

                        Slider(
                            value = selectedRamMb.toFloat(),
                            onValueChange = { selectedRamMb = (it.toInt() / 1024) * 1024 },
                            valueRange = 1024f..8192f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                        )

                        Button(
                            onClick = {
                                viewModel.flashCustomGsiToVm(res, customVmName, selectedRamMb, selectedCores)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrightEmerald, contentColor = DarkBg),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("flash_custom_gsi_button")
                        ) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Flash '${res.fileName.take(18)}' to Virtual Machine", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AnalysisSpecCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = DarkCardElevated,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text(text = value, color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DynamicPartitionsManagerView() {
    val partitions = listOf(
        Pair("system_a", 3584),
        Pair("system_b", 3584),
        Pair("vendor", 1280),
        Pair("product", 896),
        Pair("system_ext", 512),
        Pair("userdata", 8192),
        Pair("vbmeta", 64)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "DYNAMIC SUPER PARTITIONS (DSU)",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "DroidVM maps Android system images via dm-linear virtual block devices without requiring repartitioning of host storage.",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        items(partitions) { (name, sizeMb) ->
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
                        Column {
                            Text(text = "/dev/block/mapper/$name", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text(text = "Allocated Size: $sizeMb MB (ext4 SAR)", color = TextSecondary, fontSize = 10.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(BrightEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "MOUNTED", color = BrightEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun FastbootStudioView() {
    var commandInput by remember { mutableStateOf("") }
    var fastbootOutput by remember {
        mutableStateOf(
            listOf(
                "DroidVM Fastbootd Terminal Studio v2.4",
                "Connected target: droidvm_treble_arm64 (fastbootd)",
                "Type 'fastboot getvar all' or 'fastboot flash system <img_file>'",
                ""
            )
        )
    }

    Surface(
        color = TerminalBg,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header
            Text(
                text = "FASTBOOT PROTOCOL COMMAND CONSOLE",
                color = TerminalCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            // Console output
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(fastbootOutput) { line ->
                    Text(
                        text = line,
                        color = when {
                            line.startsWith("Sending") || line.startsWith("Writing") -> BrightEmerald
                            line.startsWith("OKAY") -> TerminalGreen
                            line.startsWith("FAILED") -> CrimsonError
                            else -> TextPrimary
                        },
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Quick Fastboot presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "fastboot getvar all",
                    "fastboot flash system system.img",
                    "fastboot flash vbmeta --disable-verity",
                    "fastboot -w"
                ).forEach { cmd ->
                    Surface(
                        color = DarkCardElevated,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable {
                            runFastbootCommand(cmd, fastbootOutput) { fastbootOutput = it }
                        }
                    ) {
                        Text(
                            text = cmd,
                            color = TerminalCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    placeholder = { Text("Enter fastboot command...", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = TerminalGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            runFastbootCommand(commandInput, fastbootOutput) { fastbootOutput = it }
                            commandInput = ""
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
                        runFastbootCommand(commandInput, fastbootOutput) { fastbootOutput = it }
                        commandInput = ""
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

private fun runFastbootCommand(cmd: String, current: List<String>, update: (List<String>) -> Unit) {
    val trimmed = cmd.trim()
    if (trimmed.isEmpty()) return

    val newLines = mutableListOf<String>()
    newLines.add("> $trimmed")

    when {
        trimmed == "fastboot getvar all" || trimmed == "fastboot getvar" -> {
            newLines.add("(bootloader) version-baseband: N/A")
            newLines.add("(bootloader) version-bootloader: DROIDVM-2024")
            newLines.add("(bootloader) product: droidvm_treble_arm64")
            newLines.add("(bootloader) secure: no")
            newLines.add("(bootloader) unlocked: yes")
            newLines.add("(bootloader) dynamic-partition: true")
            newLines.add("(bootloader) is-userspace: yes")
            newLines.add("(bootloader) slot-count: 2")
            newLines.add("(bootloader) current-slot: a")
            newLines.add("OKAY [  0.024s]")
        }
        trimmed.startsWith("fastboot flash system") -> {
            newLines.add("Resizing 'system_a'                        OKAY [  0.012s]")
            newLines.add("Sending sparse 'system_a' (3584000 KB)     OKAY [  0.420s]")
            newLines.add("Writing 'system_a'                         OKAY [  1.210s]")
            newLines.add("Finished. Total time: 1.642s")
        }
        trimmed.startsWith("fastboot flash vbmeta") -> {
            newLines.add("Sending 'vbmeta' (64 KB)                   OKAY [  0.005s]")
            newLines.add("Writing 'vbmeta' (flags: 2, disabled)     OKAY [  0.018s]")
            newLines.add("Finished. AVB 2.0 verification disabled.")
        }
        trimmed == "fastboot -w" || trimmed.contains("erase userdata") -> {
            newLines.add("Erasing 'userdata'                         OKAY [  0.080s]")
            newLines.add("Formatting 'userdata' (ext4)              OKAY [  0.150s]")
            newLines.add("Erasing 'cache'                            OKAY [  0.020s]")
            newLines.add("Finished.")
        }
        trimmed == "fastboot reboot" -> {
            newLines.add("Rebooting into Android System UI...")
            newLines.add("Finished.")
        }
        else -> {
            newLines.add("fastboot: command '$trimmed' executed in sandbox.")
            newLines.add("OKAY [  0.010s]")
        }
    }
    newLines.add("")
    update(current + newLines)
}
