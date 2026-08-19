package com.example.data.repository

import com.example.data.db.SnapshotDao
import com.example.data.db.VmDao
import com.example.data.model.GappsType
import com.example.data.model.GpuRenderer
import com.example.data.model.GsiArch
import com.example.data.model.PartitionLayout
import com.example.data.model.SelinuxMode
import com.example.data.model.VmInstance
import com.example.data.model.VmSnapshot
import com.example.data.model.VmStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VmRepository(
    private val vmDao: VmDao,
    private val snapshotDao: SnapshotDao
) {
    val allVms: Flow<List<VmInstance>> = vmDao.getAllVms()

    suspend fun getVmById(id: Long): Flow<VmInstance?> = vmDao.getVmById(id)

    suspend fun getVmByIdDirect(id: Long): VmInstance? = withContext(Dispatchers.IO) {
        vmDao.getVmByIdDirect(id)
    }

    suspend fun createVm(vm: VmInstance): Long = withContext(Dispatchers.IO) {
        vmDao.insertVm(vm)
    }

    suspend fun updateVm(vm: VmInstance) = withContext(Dispatchers.IO) {
        vmDao.updateVm(vm)
    }

    suspend fun deleteVm(vm: VmInstance) = withContext(Dispatchers.IO) {
        vmDao.deleteVm(vm)
    }

    suspend fun updateVmStatus(id: Long, status: VmStatus) = withContext(Dispatchers.IO) {
        vmDao.updateVmStatus(id, status)
    }

    suspend fun resetAllStatuses() = withContext(Dispatchers.IO) {
        vmDao.resetAllStatusesToStopped()
    }

    fun getSnapshotsForVm(vmId: Long): Flow<List<VmSnapshot>> = snapshotDao.getSnapshotsForVm(vmId)

    suspend fun createSnapshot(snapshot: VmSnapshot): Long = withContext(Dispatchers.IO) {
        snapshotDao.insertSnapshot(snapshot)
    }

    suspend fun deleteSnapshot(snapshot: VmSnapshot) = withContext(Dispatchers.IO) {
        snapshotDao.deleteSnapshot(snapshot)
    }

    suspend fun initializeDefaultVmsIfEmpty() = withContext(Dispatchers.IO) {
        val count = vmDao.getVmCount()
        if (count == 0) {
            // Seed LineageOS 21 GSI default VM
            val lineageVm = VmInstance(
                name = "LineageOS 21 GSI (ARM64)",
                romName = "LineageOS 21.0 Treble",
                romCodename = "lineage-21.0-arm64-bgN",
                androidVersion = "Android 14 (Vanilla AOSP)",
                apiLevel = 34,
                arch = GsiArch.ARM64_V8A,
                partitionLayout = PartitionLayout.A_B,
                vndkVersion = 34,
                systemImageSizeMb = 3584,
                userdataSizeMb = 8192,
                ramAllocatedMb = 3072,
                cpuCores = 4,
                status = VmStatus.STOPPED,
                isRooted = true,
                gappsType = GappsType.VANILLA,
                selinuxMode = SelinuxMode.PERMISSIVE,
                displayWidth = 1080,
                displayHeight = 2400,
                displayDpi = 420,
                refreshRateHz = 120,
                gpuRenderer = GpuRenderer.VIRGL_3D,
                trebleAppInstalled = true,
                magiskVersion = "v27.0",
                linuxKernelVersion = "6.1.75-android14-gsi-droidvm+",
                createdAt = System.currentTimeMillis(),
                lastBootedAt = System.currentTimeMillis() - 3600000L,
                totalUptimeSeconds = 1420L,
                isDefaultTemplate = true
            )
            val lineageId = vmDao.insertVm(lineageVm)

            // Add default initial snapshot for LineageOS
            snapshotDao.insertSnapshot(
                VmSnapshot(
                    vmId = lineageId,
                    title = "Initial Clean Flash (Fresh Install)",
                    description = "Virgin GSI state after dynamic partition setup and Magisk v27 patch.",
                    sizeMb = 420,
                    romState = "Clean System"
                )
            )

            // Seed PixelOS 14 GSI default VM
            val pixelVm = VmInstance(
                name = "PixelOS 14 (Monet & GApps)",
                romName = "PixelOS 14 Treble GSI",
                romCodename = "PixelOS_gsi-arm64-bgZ",
                androidVersion = "Android 14 (Pixel Experience)",
                apiLevel = 34,
                arch = GsiArch.ARM64_V8A,
                partitionLayout = PartitionLayout.A_B,
                vndkVersion = 34,
                systemImageSizeMb = 4096,
                userdataSizeMb = 12288,
                ramAllocatedMb = 4096,
                cpuCores = 6,
                status = VmStatus.STOPPED,
                isRooted = true,
                gappsType = GappsType.PIXEL_GAPPS,
                selinuxMode = SelinuxMode.PERMISSIVE,
                displayWidth = 1080,
                displayHeight = 2400,
                displayDpi = 420,
                refreshRateHz = 120,
                gpuRenderer = GpuRenderer.VIRGL_3D,
                trebleAppInstalled = true,
                magiskVersion = "v27.0",
                linuxKernelVersion = "6.1.75-android14-gsi-droidvm+",
                createdAt = System.currentTimeMillis() - 86400000L,
                lastBootedAt = System.currentTimeMillis() - 7200000L,
                totalUptimeSeconds = 2840L,
                isDefaultTemplate = true
            )
            vmDao.insertVm(pixelVm)

            // Seed Evolution X 8.6 GSI
            val evoVm = VmInstance(
                name = "Evolution X 8.6 (Gaming & Customization)",
                romName = "Evolution X 8.6 Treble",
                romCodename = "evolution_gsi-arm64-bvN",
                androidVersion = "Android 14 (Evo-X)",
                apiLevel = 34,
                arch = GsiArch.ARM64_V8A,
                partitionLayout = PartitionLayout.A_B,
                vndkVersion = 34,
                systemImageSizeMb = 4608,
                userdataSizeMb = 16384,
                ramAllocatedMb = 4096,
                cpuCores = 8,
                status = VmStatus.STOPPED,
                isRooted = true,
                gappsType = GappsType.PIXEL_GAPPS,
                selinuxMode = SelinuxMode.PERMISSIVE,
                displayWidth = 1080,
                displayHeight = 2400,
                displayDpi = 440,
                refreshRateHz = 120,
                gpuRenderer = GpuRenderer.VIRGL_3D,
                trebleAppInstalled = true,
                magiskVersion = "v27.0",
                linuxKernelVersion = "6.1.75-android14-gsi-droidvm+",
                createdAt = System.currentTimeMillis() - 172800000L,
                lastBootedAt = 0L,
                totalUptimeSeconds = 0L,
                isDefaultTemplate = true
            )
            vmDao.insertVm(evoVm)
        }
    }
}
