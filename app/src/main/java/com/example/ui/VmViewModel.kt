package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.GappsType
import com.example.data.model.GpuRenderer
import com.example.data.model.GsiArch
import com.example.data.model.GsiRomCatalogItem
import com.example.data.model.PartitionLayout
import com.example.data.model.SelinuxMode
import com.example.data.model.VmInstance
import com.example.data.model.VmSnapshot
import com.example.data.model.VmStatus
import com.example.data.repository.GsiCatalogData
import com.example.data.repository.VmRepository
import com.example.engine.GsiAnalysisResult
import com.example.engine.TrebleAnalyzer
import com.example.engine.VmHypervisorEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    VMS("Virtual Machines"),
    CATALOG("GSI ROM Hub"),
    RUNTIME("Live Sandbox"),
    FLASHER("Flasher & DSU"),
    LOGCAT("Hypervisor Logs")
}

data class FlashingProgressState(
    val isFlashing: Boolean = false,
    val progress: Float = 0f,
    val currentStep: String = "",
    val targetRomName: String = "",
    val completed: Boolean = false
)

class VmViewModel(application: Application) : AndroidViewModel(application) {

    val repository: VmRepository
    val engine: VmHypervisorEngine

    val allVms: StateFlow<List<VmInstance>>
    val vms: StateFlow<List<VmInstance>> get() = allVms
    val catalogItems: List<GsiRomCatalogItem> = GsiCatalogData.items

    private val _selectedTab = MutableStateFlow(AppTab.VMS)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()
    val currentTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _activeVm = MutableStateFlow<VmInstance?>(null)
    val activeVm: StateFlow<VmInstance?> = _activeVm.asStateFlow()

    private val _selectedCatalogItem = MutableStateFlow<GsiRomCatalogItem?>(null)
    val selectedCatalogItem: StateFlow<GsiRomCatalogItem?> = _selectedCatalogItem.asStateFlow()

    private val _customAnalysisResult = MutableStateFlow<GsiAnalysisResult?>(null)
    val customAnalysisResult: StateFlow<GsiAnalysisResult?> = _customAnalysisResult.asStateFlow()

    private val _importedRomFiles = MutableStateFlow<List<GsiAnalysisResult>>(emptyList())
    val importedRomFiles: StateFlow<List<GsiAnalysisResult>> = _importedRomFiles.asStateFlow()


    private val _flashingState = MutableStateFlow(FlashingProgressState())
    val flashingState: StateFlow<FlashingProgressState> = _flashingState.asStateFlow()

    private val _snapshots = MutableStateFlow<List<VmSnapshot>>(emptyList())
    val snapshots: StateFlow<List<VmSnapshot>> = _snapshots.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Interactive In-VM App State (for the running VM screen)
    private val _currentInVmApp = MutableStateFlow("homescreen") // homescreen, treble_app, terminal, settings, magisk, files, benchmark, twrp, fastboot
    val currentInVmApp: StateFlow<String> = _currentInVmApp.asStateFlow()

    private val _isQuickSettingsOpen = MutableStateFlow(false)
    val isQuickSettingsOpen: StateFlow<Boolean> = _isQuickSettingsOpen.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VmRepository(database.vmDao(), database.snapshotDao())
        engine = VmHypervisorEngine(viewModelScope)

        allVms = repository.allVms.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.initializeDefaultVmsIfEmpty()
        }
    }


    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun openInVmApp(appName: String) {
        _currentInVmApp.value = appName
        _isQuickSettingsOpen.value = false
    }

    fun toggleQuickSettings() {
        _isQuickSettingsOpen.value = !_isQuickSettingsOpen.value
    }

    fun closeQuickSettings() {
        _isQuickSettingsOpen.value = false
    }

    fun bootVm(vm: VmInstance) {
        _activeVm.value = vm
        _selectedTab.value = AppTab.RUNTIME
        _currentInVmApp.value = "homescreen"
        engine.startVm(vm) { status ->
            viewModelScope.launch {
                repository.updateVmStatus(vm.id, status)
                val updated = repository.getVmByIdDirect(vm.id)
                _activeVm.value = updated
            }
        }
    }

    fun stopActiveVm() {
        val vm = _activeVm.value ?: return
        engine.stopVm { status ->
            viewModelScope.launch {
                repository.updateVmStatus(vm.id, status)
                val updated = repository.getVmByIdDirect(vm.id)
                _activeVm.value = updated
            }
        }
    }

    fun pauseActiveVm() {
        val vm = _activeVm.value ?: return
        engine.pauseVm { status ->
            viewModelScope.launch {
                repository.updateVmStatus(vm.id, status)
                val updated = repository.getVmByIdDirect(vm.id)
                _activeVm.value = updated
            }
        }
    }

    fun resumeActiveVm() {
        val vm = _activeVm.value ?: return
        engine.resumeVm { status ->
            viewModelScope.launch {
                repository.updateVmStatus(vm.id, status)
                val updated = repository.getVmByIdDirect(vm.id)
                _activeVm.value = updated
            }
        }
    }

    fun rebootToRecovery() {
        val vm = _activeVm.value ?: return
        _currentInVmApp.value = "twrp"
        engine.rebootToRecovery { status ->
            viewModelScope.launch {
                repository.updateVmStatus(vm.id, status)
            }
        }
    }

    fun rebootToFastboot() {
        val vm = _activeVm.value ?: return
        _currentInVmApp.value = "fastboot"
        engine.rebootToFastboot { status ->
            viewModelScope.launch {
                repository.updateVmStatus(vm.id, status)
            }
        }
    }

    fun showRomDetail(item: GsiRomCatalogItem?) {
        _selectedCatalogItem.value = item
    }

    fun analyzeCustomGsiFile(fileName: String, sizeMb: Int) {
        val result = TrebleAnalyzer.analyzeGsiFile(fileName, sizeMb)
        _customAnalysisResult.value = result
        if (_importedRomFiles.value.none { it.fileName == result.fileName }) {
            _importedRomFiles.value = listOf(result) + _importedRomFiles.value
        }
    }

    fun importGsiFileFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val result = TrebleAnalyzer.analyzeFromUri(context, uri)
            _customAnalysisResult.value = result
            _importedRomFiles.value = listOf(result) + _importedRomFiles.value.filterNot { it.fileName == result.fileName }
            _selectedTab.value = AppTab.FLASHER
            _userMessage.emit("Imported '${result.fileName}' (${result.fileSizeMb} MB) • Treble Score: ${result.trebleScore}%")
        }
    }

    fun selectImportedRom(result: GsiAnalysisResult) {
        _customAnalysisResult.value = result
        _selectedTab.value = AppTab.FLASHER
    }

    fun removeImportedRom(result: GsiAnalysisResult) {
        _importedRomFiles.value = _importedRomFiles.value.filterNot { it.fileName == result.fileName }
        if (_customAnalysisResult.value?.fileName == result.fileName) {
            _customAnalysisResult.value = null
        }
    }

    fun clearAnalysis() {
        _customAnalysisResult.value = null
    }


    fun flashCatalogRomToNewVm(item: GsiRomCatalogItem, customVmName: String? = null, ramMb: Int = 3072, cores: Int = 4) {
        viewModelScope.launch {
            _flashingState.value = FlashingProgressState(
                isFlashing = true,
                progress = 0.05f,
                currentStep = "Allocating virtual dynamic partition table (super.img)...",
                targetRomName = item.name
            )

            val steps = listOf(
                Pair(0.20f, "Decompressing ${item.tag} sparse archive..."),
                Pair(0.40f, "Validating AVB 2.0 footer and SHA-256 hash..."),
                Pair(0.60f, "Flashing system_a partition (ext4 system-as-root)..."),
                Pair(0.75f, "Injecting VNDK ${item.vndkCompat.last()} Treble shims & overlayfs..."),
                Pair(0.90f, "Patching boot.img with Magisk v27.0 root binaries..."),
                Pair(1.00f, "Flash successful. Virtual Machine ready.")
            )

            for ((p, step) in steps) {
                delay(450)
                _flashingState.value = _flashingState.value.copy(
                    progress = p,
                    currentStep = step
                )
            }

            // Create VM in database
            val newVm = VmInstance(
                name = customVmName?.ifBlank { null } ?: "${item.name} VM",
                romName = item.name,
                romCodename = item.tag.removeSuffix(".img.xz"),
                androidVersion = item.androidVersion,
                apiLevel = item.apiLevel,
                arch = item.arch,
                partitionLayout = item.partitionLayout,
                vndkVersion = item.vndkCompat.last(),
                systemImageSizeMb = item.downloadSizeMb + 1024,
                userdataSizeMb = 8192,
                ramAllocatedMb = ramMb,
                cpuCores = cores,
                status = VmStatus.STOPPED,
                isRooted = true,
                gappsType = item.gappsIncluded,
                selinuxMode = SelinuxMode.PERMISSIVE,
                gpuRenderer = GpuRenderer.VIRGL_3D
            )

            val newId = repository.createVm(newVm)
            repository.createSnapshot(
                VmSnapshot(
                    vmId = newId,
                    title = "Initial Clean Flash",
                    description = "Freshly flashed ${item.name} with Treble VNDK ${item.vndkCompat.last()}",
                    sizeMb = item.downloadSizeMb / 3,
                    romState = "Clean Install"
                )
            )

            delay(300)
            _flashingState.value = _flashingState.value.copy(
                isFlashing = false,
                completed = true
            )
            _selectedCatalogItem.value = null
            _userMessage.emit("Successfully flashed ${item.name} to VM!")
            _selectedTab.value = AppTab.VMS
        }
    }

    fun flashCustomGsiToVm(analysis: GsiAnalysisResult, customVmName: String, ramMb: Int, cores: Int) {
        viewModelScope.launch {
            _flashingState.value = FlashingProgressState(
                isFlashing = true,
                progress = 0.05f,
                currentStep = "Mounting custom system image '${analysis.fileName}'...",
                targetRomName = customVmName
            )

            val steps = listOf(
                Pair(0.25f, "Parsing header (${analysis.format})..."),
                Pair(0.50f, "Writing system_a dynamic partition (${analysis.fileSizeMb} MB)..."),
                Pair(0.75f, "Setting SELinux permissive flags and AVB disable-verity..."),
                Pair(0.90f, "Configuring Android ${analysis.detectedAndroidVersion} compatibility..."),
                Pair(1.00f, "Custom GSI VM initialized!")
            )

            for ((p, step) in steps) {
                delay(400)
                _flashingState.value = _flashingState.value.copy(
                    progress = p,
                    currentStep = step
                )
            }

            val newVm = VmInstance(
                name = customVmName,
                romName = "Custom GSI (${analysis.fileName})",
                romCodename = analysis.fileName.removeSuffix(".img"),
                androidVersion = analysis.detectedAndroidVersion,
                apiLevel = analysis.detectedApiLevel,
                arch = analysis.targetArch,
                partitionLayout = analysis.partitionType,
                vndkVersion = analysis.vndkLevel,
                systemImageSizeMb = analysis.fileSizeMb,
                userdataSizeMb = 8192,
                ramAllocatedMb = ramMb,
                cpuCores = cores,
                status = VmStatus.STOPPED,
                isRooted = true,
                gappsType = analysis.gappsType,
                selinuxMode = SelinuxMode.PERMISSIVE,
                gpuRenderer = GpuRenderer.VIRGL_3D
            )

            val newId = repository.createVm(newVm)
            repository.createSnapshot(
                VmSnapshot(
                    vmId = newId,
                    title = "Initial Custom Flash",
                    description = "Custom GSI image ${analysis.fileName} flashed via DroidVM Flasher.",
                    sizeMb = analysis.fileSizeMb / 4,
                    romState = "Custom ROM Fresh State"
                )
            )

            delay(200)
            _flashingState.value = _flashingState.value.copy(isFlashing = false, completed = true)
            _customAnalysisResult.value = null
            _userMessage.emit("Custom GSI VM '$customVmName' created successfully!")
            _selectedTab.value = AppTab.VMS
        }
    }

    fun deleteVm(vm: VmInstance) {
        viewModelScope.launch {
            if (_activeVm.value?.id == vm.id) {
                engine.stopVm {}
                _activeVm.value = null
            }
            repository.deleteVm(vm)
            _userMessage.emit("VM '${vm.name}' deleted.")
        }
    }

    fun updateVm(updated: VmInstance) {
        updateVmConfig(updated)
    }

    fun updateVmConfig(updated: VmInstance) {
        viewModelScope.launch {
            repository.updateVm(updated)
            if (_activeVm.value?.id == updated.id) {
                _activeVm.value = updated
            }
            _userMessage.emit("VM hardware configuration updated.")
        }
    }

    fun loadSnapshotsForVm(vmId: Long) {
        viewModelScope.launch {
            repository.getSnapshotsForVm(vmId).collect { list ->
                _snapshots.value = list
            }
        }
    }

    fun createSnapshot(vm: VmInstance, title: String, description: String) {
        createSnapshot(vm.id, title, description)
    }

    fun createSnapshot(vmId: Long, title: String, description: String) {
        viewModelScope.launch {
            val snap = VmSnapshot(
                vmId = vmId,
                title = title,
                description = description,
                sizeMb = 380,
                romState = "Checkpoint"
            )
            repository.createSnapshot(snap)
            _userMessage.emit("Snapshot '$title' created.")
        }
    }


    fun restoreSnapshot(snapshot: VmSnapshot) {
        viewModelScope.launch {
            engine.appendLog("I", "DroidVM-Hypervisor", "Restoring VM state from snapshot '${snapshot.title}'...")
            _userMessage.emit("Restored VM to checkpoint '${snapshot.title}'.")
        }
    }

    fun deleteSnapshot(snapshot: VmSnapshot) {
        viewModelScope.launch {
            repository.deleteSnapshot(snapshot)
            _userMessage.emit("Snapshot deleted.")
        }
    }
}
