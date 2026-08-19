package com.example.engine

import com.example.data.model.VmInstance
import com.example.data.model.VmLogItem
import com.example.data.model.VmStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class VmTelemetry(
    val cpuLoads: List<Float> = listOf(12f, 15f, 8f, 20f),
    val overallCpuPercent: Int = 14,
    val ramUsedMb: Int = 1420,
    val ramTotalMb: Int = 3072,
    val fps: Float = 119.8f,
    val temperatureC: Float = 38.4f,
    val ioReadKbps: Int = 240,
    val ioWriteKbps: Int = 80,
    val activeProcessCount: Int = 148,
    val uptimeSeconds: Long = 0L
)

data class TrebleFixesState(
    val audioInCallFix: Boolean = true,
    val force120Hz: Boolean = true,
    val disableDmVerity: Boolean = true,
    val volteImsFix: Boolean = false,
    val bypassSafeVolume: Boolean = true,
    val spoofPixelCertification: Boolean = true,
    val cameraHal3Forced: Boolean = true,
    val invertColors: Boolean = false,
    val dt2wEnabled: Boolean = true
)

class VmHypervisorEngine(private val scope: CoroutineScope) {

    private val _currentVm = MutableStateFlow<VmInstance?>(null)
    val currentVm: StateFlow<VmInstance?> = _currentVm.asStateFlow()

    private val _vmStatus = MutableStateFlow(VmStatus.STOPPED)
    val vmStatus: StateFlow<VmStatus> = _vmStatus.asStateFlow()

    private val _bootProgress = MutableStateFlow(0f)
    val bootProgress: StateFlow<Float> = _bootProgress.asStateFlow()

    private val _bootStepText = MutableStateFlow("")
    val bootStepText: StateFlow<String> = _bootStepText.asStateFlow()

    private val _telemetry = MutableStateFlow(VmTelemetry())
    val telemetry: StateFlow<VmTelemetry> = _telemetry.asStateFlow()

    private val _trebleFixes = MutableStateFlow(TrebleFixesState())
    val trebleFixes: StateFlow<TrebleFixesState> = _trebleFixes.asStateFlow()

    private val _logs = MutableStateFlow<List<VmLogItem>>(emptyList())
    val logs: StateFlow<List<VmLogItem>> = _logs.asStateFlow()

    private val _terminalHistory = MutableStateFlow<List<String>>(
        listOf(
            "DroidVM Android Linux Terminal (Treble Sandbox v2.4)",
            "Linux localhost 6.1.75-android14-gsi #1 SMP PREEMPT aarch64",
            "Type 'help' for a list of available GSI debugging commands.",
            ""
        )
    )
    val terminalHistory: StateFlow<List<String>> = _terminalHistory.asStateFlow()

    private val _toastEvents = MutableSharedFlow<String>()
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    private var telemetryJob: Job? = null
    private var bootJob: Job? = null
    private var isRootShell: Boolean = false
    private var logCounter: Long = 0

    fun startVm(vm: VmInstance, onStatusUpdate: (VmStatus) -> Unit) {
        bootJob?.cancel()
        _currentVm.value = vm
        _vmStatus.value = VmStatus.BOOTING
        onStatusUpdate(VmStatus.BOOTING)
        _bootProgress.value = 0f
        _logs.value = emptyList()

        appendLog("I", "DroidVM-Hypervisor", "Initializing AVF virtual machine instance '${vm.name}'...")
        appendLog("I", "Kernel", "Booting Linux on physical CPU 0x0000000000 [0x410fd034]")
        appendLog("I", "Kernel", "Linux version ${vm.linuxKernelVersion} (gcc-12) #1 SMP PREEMPT")
        appendLog("I", "Kernel", "Machine model: DroidVM KVM Virtual Board (ARM64)")
        appendLog("I", "Kernel", "Allocated RAM: ${vm.ramAllocatedMb}MB, vCPU cores: ${vm.cpuCores}")

        bootJob = scope.launch(Dispatchers.Default) {
            val bootSteps = listOf(
                Pair(0.12f, "Parsing dynamic device-tree and ACPI tables..."),
                Pair(0.25f, "Verifying AVB 2.0 metadata & Super Partition mapper..."),
                Pair(0.38f, "Mounting /dev/block/mapper/system_a (ext4/erofs, ro)..."),
                Pair(0.50f, "Binding Treble Vendor HAL interfaces (VNDK ${vm.vndkVersion})..."),
                Pair(0.65f, "SELinux: Loaded policy in ${vm.selinuxMode.displayName} mode."),
                Pair(0.78f, "Starting Android init services & Zygote64 daemon..."),
                Pair(0.90f, "Starting SurfaceFlinger (${vm.gpuRenderer.displayName})..."),
                Pair(0.98f, "Booting SystemUI (${vm.romName})..."),
                Pair(1.0f, "Android System Boot Completed.")
            )

            for ((progress, text) in bootSteps) {
                delay(320)
                _bootProgress.value = progress
                _bootStepText.value = text
                appendLog("I", "init", text)
            }

            delay(200)
            _vmStatus.value = VmStatus.RUNNING
            onStatusUpdate(VmStatus.RUNNING)
            startTelemetryLoop(vm)
            appendLog("I", "ActivityManager", "Displayed com.android.launcher3/.Launcher: +842ms")
            _toastEvents.emit("VM ${vm.romName} booted successfully!")
        }
    }

    fun stopVm(onStatusUpdate: (VmStatus) -> Unit) {
        bootJob?.cancel()
        telemetryJob?.cancel()
        appendLog("I", "DroidVM-Hypervisor", "Gracefully shutting down VM...")
        _vmStatus.value = VmStatus.STOPPED
        onStatusUpdate(VmStatus.STOPPED)
        _bootProgress.value = 0f
    }

    fun pauseVm(onStatusUpdate: (VmStatus) -> Unit) {
        telemetryJob?.cancel()
        _vmStatus.value = VmStatus.PAUSED
        onStatusUpdate(VmStatus.PAUSED)
        appendLog("I", "DroidVM-Hypervisor", "VM state paused in memory.")
    }

    fun resumeVm(onStatusUpdate: (VmStatus) -> Unit) {
        val vm = _currentVm.value ?: return
        _vmStatus.value = VmStatus.RUNNING
        onStatusUpdate(VmStatus.RUNNING)
        startTelemetryLoop(vm)
        appendLog("I", "DroidVM-Hypervisor", "VM state resumed.")
    }

    fun rebootToRecovery(onStatusUpdate: (VmStatus) -> Unit) {
        telemetryJob?.cancel()
        _vmStatus.value = VmStatus.RECOVERY
        onStatusUpdate(VmStatus.RECOVERY)
        appendLog("I", "init", "Restarting system into Recovery Mode (TWRP 3.7.0)...")
    }

    fun rebootToFastboot(onStatusUpdate: (VmStatus) -> Unit) {
        telemetryJob?.cancel()
        _vmStatus.value = VmStatus.FASTBOOT
        onStatusUpdate(VmStatus.FASTBOOT)
        appendLog("I", "fastbootd", "Entered Fastbootd user-space flashing environment.")
    }

    fun toggleTrebleFix(fixType: String) {
        val current = _trebleFixes.value
        val updated = when (fixType) {
            "audio" -> current.copy(audioInCallFix = !current.audioInCallFix)
            "120hz" -> current.copy(force120Hz = !current.force120Hz)
            "dmverity" -> current.copy(disableDmVerity = !current.disableDmVerity)
            "volte" -> current.copy(volteImsFix = !current.volteImsFix)
            "safe_volume" -> current.copy(bypassSafeVolume = !current.bypassSafeVolume)
            "pixel_spoof" -> current.copy(spoofPixelCertification = !current.spoofPixelCertification)
            "camera_hal3" -> current.copy(cameraHal3Forced = !current.cameraHal3Forced)
            "invert_colors" -> current.copy(invertColors = !current.invertColors)
            "dt2w" -> current.copy(dt2wEnabled = !current.dt2wEnabled)
            else -> current
        }
        _trebleFixes.value = updated
        appendLog("D", "PhhTrebleApp", "Toggled fix '$fixType' -> state updated in overlayfs.")
    }

    fun executeTerminalCommand(rawInput: String) {
        val command = rawInput.trim()
        if (command.isEmpty()) return

        val vm = _currentVm.value
        val prompt = if (isRootShell) "root@droidvm:/ # $command" else "shell@droidvm:$ $command"
        val outputLines = mutableListOf<String>()
        outputLines.add(prompt)

        when {
            command == "help" -> {
                outputLines.add("Available DroidVM Treble Sandbox Commands:")
                outputLines.add("  uname -a          : Display virtual kernel information")
                outputLines.add("  getprop [key]     : Read Android system properties")
                outputLines.add("  setprop <k> <v>   : Write Android system property")
                outputLines.add("  treble_check      : Run Treble HAL compatibility diagnostics")
                outputLines.add("  cat /proc/cpuinfo : Show simulated virtual CPU specs")
                outputLines.add("  cat /proc/meminfo : Show memory allocation stats")
                outputLines.add("  df -h             : Show partition disk usage")
                outputLines.add("  ls /system/bin    : List system binaries")
                outputLines.add("  pm list packages  : List installed Android packages")
                outputLines.add("  magisk -v         : Check Magisk root status")
                outputLines.add("  su                : Switch to SuperUser root prompt")
                outputLines.add("  exit              : Exit root shell")
                outputLines.add("  clear             : Clear terminal screen")
                outputLines.add("  reboot recovery   : Reboot VM into TWRP Recovery")
                outputLines.add("  reboot bootloader : Reboot VM into Fastboot Mode")
                outputLines.add("  logcat -d         : Dump latest Android runtime logs")
            }
            command == "clear" -> {
                _terminalHistory.value = listOf("Terminal cleared.")
                return
            }
            command == "su" -> {
                if (vm?.isRooted == true) {
                    isRootShell = true
                    outputLines.add("[Magisk] SuperUser access granted (UID=0, GID=0).")
                } else {
                    outputLines.add("Permission denied: VM is not rooted.")
                }
            }
            command == "exit" -> {
                if (isRootShell) {
                    isRootShell = false
                    outputLines.add("Exited root shell.")
                } else {
                    outputLines.add("Terminal session active.")
                }
            }
            command == "whoami" -> {
                outputLines.add(if (isRootShell) "root" else "shell (uid=2000)")
            }
            command == "uname -a" -> {
                outputLines.add("Linux localhost ${vm?.linuxKernelVersion ?: "6.1.75-android14-gsi"} #1 SMP PREEMPT aarch64 Android")
            }
            command.startsWith("getprop") -> {
                val prop = command.removePrefix("getprop").trim()
                if (prop.isEmpty()) {
                    outputLines.add("[ro.build.version.release]: [${vm?.androidVersion ?: "14"}]")
                    outputLines.add("[ro.build.version.sdk]: [${vm?.apiLevel ?: 34}]")
                    outputLines.add("[ro.treble.enabled]: [true]")
                    outputLines.add("[ro.vndk.version]: [${vm?.vndkVersion ?: 34}]")
                    outputLines.add("[ro.product.model]: [DroidVM Treble GSI Sandbox]")
                    outputLines.add("[ro.product.cpu.abilist]: [arm64-v8a,armeabi-v7a]")
                    outputLines.add("[ro.boot.selinux]: [${vm?.selinuxMode?.name?.lowercase() ?: "permissive"}]")
                    outputLines.add("[ro.boot.dynamic_partitions]: [true]")
                } else {
                    val value = when (prop) {
                        "ro.build.version.release" -> vm?.androidVersion ?: "14"
                        "ro.build.version.sdk" -> "${vm?.apiLevel ?: 34}"
                        "ro.treble.enabled" -> "true"
                        "ro.vndk.version" -> "${vm?.vndkVersion ?: 34}"
                        "ro.product.name" -> vm?.romCodename ?: "treble_arm64"
                        "ro.boot.selinux" -> vm?.selinuxMode?.name?.lowercase() ?: "permissive"
                        else -> "unknown property: $prop"
                    }
                    outputLines.add("[$prop]: [$value]")
                }
            }
            command == "treble_check" -> {
                outputLines.add("=== Treble Compliance Diagnostic Suite ===")
                outputLines.add("[✓] System-as-Root (SAR): ACTIVE")
                outputLines.add("[✓] Dynamic Partitions (super.img): PASS")
                outputLines.add("[✓] VNDK Compatibility Level: ${vm?.vndkVersion ?: 34} (PASS)")
                outputLines.add("[✓] SELinux State: ${vm?.selinuxMode?.displayName ?: "Permissive"}")
                outputLines.add("[✓] OverlayFS: Mounted on /system /vendor /product")
                outputLines.add("[✓] Camera2 / Camera HAL3: Initialized (Mock Virtual HAL)")
                outputLines.add("[✓] Audio HAL: OpenSL ES virgl binding PASS")
                outputLines.add("Status: Treble GSI fully operational on DroidVM.")
            }
            command == "magisk -v" -> {
                outputLines.add("Magisk ${vm?.magiskVersion ?: "v27.0"} (27000) (topjohnwu) - Zygisk: Enabled")
            }
            command == "df -h" -> {
                outputLines.add("Filesystem            Size  Used Avail Use% Mounted on")
                outputLines.add("/dev/block/dm-0       3.5G  2.8G  700M  80% /system")
                outputLines.add("/dev/block/dm-1       1.2G  980M  220M  82% /vendor")
                outputLines.add("/dev/block/dm-2       850M  620M  230M  73% /product")
                outputLines.add("/dev/block/dm-3       8.0G  2.1G  5.9G  26% /data")
                outputLines.add("tmpfs                 ${vm?.ramAllocatedMb ?: 3072}M  120M ${(vm?.ramAllocatedMb ?: 3072) - 120}M   4% /dev")
            }
            command == "cat /proc/cpuinfo" -> {
                val cores = vm?.cpuCores ?: 4
                outputLines.add("processor       : 0..${cores - 1}")
                outputLines.add("BogoMIPS        : 38.40")
                outputLines.add("Features        : fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics fphp asimdhp")
                outputLines.add("CPU implementer : 0x41 (ARM)")
                outputLines.add("CPU architecture: 8 (AArch64)")
                outputLines.add("CPU part        : 0xd03 (Cortex-A53 / Virt)")
                outputLines.add("Hardware        : DroidVM KVM Microdroid Virtual Platform")
            }
            command == "cat /proc/meminfo" -> {
                val ram = (vm?.ramAllocatedMb ?: 3072) * 1024
                outputLines.add("MemTotal:        ${ram} kB")
                outputLines.add("MemFree:         ${ram / 3} kB")
                outputLines.add("MemAvailable:    ${ram * 2 / 3} kB")
                outputLines.add("Buffers:          124800 kB")
                outputLines.add("Cached:           845600 kB")
                outputLines.add("Active:          1420500 kB")
            }
            command == "ls /system/bin" || command == "ls -la /system/bin" -> {
                outputLines.add("app_process64  dalvikvm64    getprop      logcat    sh")
                outputLines.add("bugreport      dmesg         grep         magisk    su")
                outputLines.add("cat            dumpsys       init         pm        top")
                outputLines.add("chmod          fastboot      iptables     ps        toybox")
                outputLines.add("chown          fsck.ext4     linker64     reboot    vold")
            }
            command == "pm list packages" -> {
                outputLines.add("package:com.android.systemui")
                outputLines.add("package:com.android.settings")
                outputLines.add("package:com.android.launcher3")
                outputLines.add("package:me.phh.treble.app")
                outputLines.add("package:io.github.topjohnwu.magisk")
                outputLines.add("package:com.android.terminal")
                outputLines.add("package:com.android.documentsui")
                if (vm?.gappsType != com.example.data.model.GappsType.VANILLA) {
                    outputLines.add("package:com.google.android.gms")
                    outputLines.add("package:com.android.vending")
                }
            }
            command == "reboot recovery" -> {
                outputLines.add("Requesting reboot to recovery...")
                scope.launch {
                    delay(500)
                    rebootToRecovery {}
                }
            }
            command == "reboot bootloader" || command == "reboot fastboot" -> {
                outputLines.add("Requesting reboot to fastboot...")
                scope.launch {
                    delay(500)
                    rebootToFastboot {}
                }
            }
            command == "dmesg" || command == "logcat -d" -> {
                outputLines.add("[0.000000] Linux version ${vm?.linuxKernelVersion}")
                outputLines.add("[0.024100] DroidVM: KVM hardware hypervisor active")
                outputLines.add("[0.120400] Treble: VNDK version ${vm?.vndkVersion} loaded")
                outputLines.add("[0.450100] SurfaceFlinger: GPU ${vm?.gpuRenderer?.displayName} attached")
                outputLines.add("[1.020000] SystemUI: Navigation bar & status bar attached")
            }
            else -> {
                outputLines.add("bash: $command: command simulated. (Type 'help' for supported commands)")
            }
        }

        outputLines.add("")
        _terminalHistory.value = _terminalHistory.value + outputLines
    }

    private fun startTelemetryLoop(vm: VmInstance) {
        telemetryJob?.cancel()
        telemetryJob = scope.launch(Dispatchers.Default) {
            var uptime = 0L
            while (isActive) {
                delay(1000)
                uptime++
                val coreCount = vm.cpuCores
                val loads = List(coreCount) {
                    (Random.nextFloat() * 25f + 5f).coerceIn(2f, 95f)
                }
                val avgLoad = loads.average().toInt()
                val usedRam = (vm.ramAllocatedMb * (0.35f + Random.nextFloat() * 0.15f)).toInt()
                val fps = if (vm.refreshRateHz >= 120) (118f + Random.nextFloat() * 2f) else (59.2f + Random.nextFloat() * 1.5f)
                val temp = 36.5f + (avgLoad / 100f) * 12f + Random.nextFloat() * 0.8f

                _telemetry.value = VmTelemetry(
                    cpuLoads = loads,
                    overallCpuPercent = avgLoad,
                    ramUsedMb = usedRam,
                    ramTotalMb = vm.ramAllocatedMb,
                    fps = fps,
                    temperatureC = temp,
                    ioReadKbps = Random.nextInt(40, 480),
                    ioWriteKbps = Random.nextInt(10, 150),
                    activeProcessCount = 140 + Random.nextInt(1, 15),
                    uptimeSeconds = uptime
                )

                // Periodically add realistic logcat line
                if (Random.nextInt(0, 3) == 0) {
                    val sampleLogs = listOf(
                        Triple("D", "SurfaceFlinger", "vsync_event: period=${1000000000 / vm.refreshRateHz}ns, vsync_count=1"),
                        Triple("V", "TrebleApp", "Audio HAL keepalive ping OK"),
                        Triple("D", "ActivityTaskManager", "Activity top resumed state changed: isTop=true"),
                        Triple("I", "NetworkController", "Wi-Fi virtual bridge signal level: -45dBm (Excellent)"),
                        Triple("D", "PowerManagerService", "Screen state: ON, user activity reset"),
                        Triple("V", "hwcomposer", "PresentDisplay: 1080x2400 @ ${vm.refreshRateHz}Hz VirGL")
                    )
                    val pick = sampleLogs.random()
                    appendLog(pick.first, pick.second, pick.third)
                }
            }
        }
    }

    fun appendLog(level: String, tag: String, message: String) {
        val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
        val logItem = VmLogItem(
            id = ++logCounter,
            timestamp = timeFormat.format(Date()),
            level = level,
            tag = tag,
            message = message
        )
        _logs.value = (_logs.value + logItem).takeLast(200)
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }
}
