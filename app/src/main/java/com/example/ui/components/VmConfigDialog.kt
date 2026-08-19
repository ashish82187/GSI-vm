package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GpuRenderer
import com.example.data.model.SelinuxMode
import com.example.data.model.VmInstance
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BrightEmerald
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VmConfigDialog(
    vm: VmInstance,
    onDismiss: () -> Unit,
    onSave: (VmInstance) -> Unit
) {
    var name by remember { mutableStateOf(vm.name) }
    var cpuCores by remember { mutableIntStateOf(vm.cpuCores) }
    var ramAllocatedMb by remember { mutableIntStateOf(vm.ramAllocatedMb) }
    var selinuxPermissive by remember { mutableStateOf(vm.selinuxMode == SelinuxMode.PERMISSIVE) }
    var isRooted by remember { mutableStateOf(vm.isRooted) }
    var refreshRateHz by remember { mutableIntStateOf(vm.refreshRateHz) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = CyberCyan)
                Text(text = "Virtual Machine Settings", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("VM Name", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                // RAM Allocation
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Allocated RAM", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "${ramAllocatedMb / 1024} GB (${ramAllocatedMb} MB)", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = ramAllocatedMb.toFloat(),
                            onValueChange = { ramAllocatedMb = (it.toInt() / 1024) * 1024 },
                            valueRange = 1024f..8192f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan)
                        )
                    }
                }

                // CPU Cores
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Virtual CPU Cores", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "$cpuCores Cores (ARM64)", color = BrightEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = cpuCores.toFloat(),
                            onValueChange = { cpuCores = it.toInt() },
                            valueRange = 1f..8f,
                            steps = 6,
                            colors = SliderDefaults.colors(thumbColor = BrightEmerald, activeTrackColor = BrightEmerald)
                        )
                    }
                }

                // Display Refresh Rate
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Display Smoothness", color = TextPrimary, fontSize = 13.sp)
                            Text(text = if (refreshRateHz == 120) "120Hz Ultra Smooth" else "60Hz Standard", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = refreshRateHz == 120,
                            onCheckedChange = { refreshRateHz = if (it) 120 else 60 },
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkSurface, checkedTrackColor = CyberCyan)
                        )
                    }
                }

                // SELinux Permissive
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "SELinux Permissive Mode", color = TextPrimary, fontSize = 13.sp)
                            Text(text = "Helps bypass broken vendor HAL rules", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = selinuxPermissive,
                            onCheckedChange = { selinuxPermissive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkSurface, checkedTrackColor = AmberWarning)
                        )
                    }
                }

                // Magisk Root
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Enable Magisk Root & SU", color = TextPrimary, fontSize = 13.sp)
                            Text(text = "Installs /system/xbin/su daemon", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = isRooted,
                            onCheckedChange = { isRooted = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = DarkSurface, checkedTrackColor = NeonPurple)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        vm.copy(
                            name = name,
                            cpuCores = cpuCores,
                            ramAllocatedMb = ramAllocatedMb,
                            selinuxMode = if (selinuxPermissive) SelinuxMode.PERMISSIVE else SelinuxMode.ENFORCING,
                            isRooted = isRooted,
                            refreshRateHz = refreshRateHz
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DarkSurface)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = TextPrimary)
            }
        },
        containerColor = DarkCardElevated
    )
}
