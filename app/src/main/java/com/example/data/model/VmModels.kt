package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VmStatus {
    STOPPED,
    BOOTING,
    RUNNING,
    PAUSED,
    RECOVERY,
    FASTBOOT,
    FLASHING
}

enum class GsiArch(val displayName: String) {
    ARM64_V8A("ARM64 (aarch64)"),
    ARM32_V7A("ARM32 (armv7)"),
    X86_64("x86_64")
}

enum class PartitionLayout(val displayName: String) {
    A_B("A/B Dynamic Partition (System-as-root)"),
    A_ONLY("A-Only Legacy Partition")
}

enum class GappsType(val displayName: String) {
    VANILLA("Vanilla (No GApps)"),
    PIXEL_GAPPS("Pixel GApps (GMS Core)"),
    MICRO_G("microG (FOSS)"),
    NIK_GAPPS("NikGApps Core")
}

enum class SelinuxMode(val displayName: String) {
    PERMISSIVE("Permissive"),
    ENFORCING("Enforcing")
}

enum class GpuRenderer(val displayName: String) {
    VIRGL_3D("VirGL 3D (Hardware Accel)"),
    SWIFTSHADER("SwiftShader (CPU Vulkan/GLES)"),
    SOFTWARE("Software Rasterizer")
}

@Entity(tableName = "vm_instances")
data class VmInstance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val romName: String,
    val romCodename: String,
    val androidVersion: String,
    val apiLevel: Int,
    val arch: GsiArch = GsiArch.ARM64_V8A,
    val partitionLayout: PartitionLayout = PartitionLayout.A_B,
    val vndkVersion: Int = 34,
    val systemImageSizeMb: Int = 3584, // 3.5 GB
    val userdataSizeMb: Int = 8192,     // 8 GB
    val ramAllocatedMb: Int = 3072,     // 3 GB
    val cpuCores: Int = 4,
    val status: VmStatus = VmStatus.STOPPED,
    val isRooted: Boolean = true,
    val gappsType: GappsType = GappsType.PIXEL_GAPPS,
    val selinuxMode: SelinuxMode = SelinuxMode.PERMISSIVE,
    val displayWidth: Int = 1080,
    val displayHeight: Int = 2400,
    val displayDpi: Int = 420,
    val refreshRateHz: Int = 120,
    val gpuRenderer: GpuRenderer = GpuRenderer.VIRGL_3D,
    val trebleAppInstalled: Boolean = true,
    val magiskVersion: String = "v27.0",
    val linuxKernelVersion: String = "6.1.75-android14-gsi-droidvm+",
    val createdAt: Long = System.currentTimeMillis(),
    val lastBootedAt: Long = 0L,
    val totalUptimeSeconds: Long = 0L,
    val isDefaultTemplate: Boolean = false
)

@Entity(tableName = "vm_snapshots")
data class VmSnapshot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vmId: Long,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val sizeMb: Int = 512,
    val romState: String = "Clean State"
)

data class GsiRomCatalogItem(
    val id: String,
    val name: String,
    val maintainer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val tag: String,
    val arch: GsiArch,
    val partitionLayout: PartitionLayout,
    val vndkCompat: List<Int>,
    val gappsIncluded: GappsType,
    val downloadSizeMb: Int,
    val buildDate: String,
    val description: String,
    val features: List<String>,
    val trebleCompatibilityScore: Int, // 0-100%
    val wallpaperColor: Long
)

data class PartitionSpec(
    val name: String,
    val mountPoint: String,
    val filesystem: String,
    val sizeMb: Int,
    val usedMb: Int,
    val flags: String
)

data class VmLogItem(
    val id: Long,
    val timestamp: String,
    val level: String, // V, D, I, W, E
    val tag: String,
    val message: String
)
