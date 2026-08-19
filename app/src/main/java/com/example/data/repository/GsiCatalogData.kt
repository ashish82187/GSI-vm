package com.example.data.repository

import com.example.data.model.GappsType
import com.example.data.model.GsiArch
import com.example.data.model.GsiRomCatalogItem
import com.example.data.model.PartitionLayout

object GsiCatalogData {
    val items = listOf(
        GsiRomCatalogItem(
            id = "lineage-21",
            name = "LineageOS 21.0 GSI",
            maintainer = "AndyYan / Treble AOSP",
            androidVersion = "Android 14 (UpsideDownCake)",
            apiLevel = 34,
            tag = "lineage-21.0-arm64-bgN.img.xz",
            arch = GsiArch.ARM64_V8A,
            partitionLayout = PartitionLayout.A_B,
            vndkCompat = listOf(30, 31, 32, 33, 34),
            gappsIncluded = GappsType.VANILLA,
            downloadSizeMb = 1380,
            buildDate = "2024-05-18",
            description = "Clean, highly stable AOSP-based Treble GSI with signature spoofing support, Phh Treble settings, and vanilla pure experience.",
            features = listOf(
                "Phh Treble App with Audio/VoLTE fixes",
                "Built-in Root Debugging & Magisk v27 ready",
                "Signature spoofing for microG integration",
                "Lightweight memory footprint (<1.2GB RAM)",
                "Full Treble VNDK 30-34 shim support"
            ),
            trebleCompatibilityScore = 98,
            wallpaperColor = 0xFF0D9488
        ),
        GsiRomCatalogItem(
            id = "pixel-os-14",
            name = "PixelOS 14 GSI",
            maintainer = "PixelGSI Team / phhusson",
            androidVersion = "Android 14 (QPR3)",
            apiLevel = 34,
            tag = "PixelOS_gsi-arm64-bgZ.img.xz",
            arch = GsiArch.ARM64_V8A,
            partitionLayout = PartitionLayout.A_B,
            vndkCompat = listOf(31, 32, 33, 34),
            gappsIncluded = GappsType.PIXEL_GAPPS,
            downloadSizeMb = 2140,
            buildDate = "2024-06-02",
            description = "Complete Google Pixel Experience GSI with Pixel Launcher, Monet dynamic theming engine, Pixel Camera support, and built-in Google Mobile Services.",
            features = listOf(
                "Full Google Mobile Services & Play Store",
                "Pixel Launcher with At a Glance & Search Bar",
                "Material You Monet dynamic wallpaper theming",
                "Pixel exclusive features & Google Photos backup",
                "AVF / KVM virtualization native drivers"
            ),
            trebleCompatibilityScore = 95,
            wallpaperColor = 0xFF4285F4
        ),
        GsiRomCatalogItem(
            id = "evolution-x-8",
            name = "Evolution X 8.6 GSI",
            maintainer = "Joey Huab / EvoX Treble",
            androidVersion = "Android 14 (Evo-X)",
            apiLevel = 34,
            tag = "evolution_gsi-arm64-bvN.img.xz",
            arch = GsiArch.ARM64_V8A,
            partitionLayout = PartitionLayout.A_B,
            vndkCompat = listOf(32, 33, 34),
            gappsIncluded = GappsType.PIXEL_GAPPS,
            downloadSizeMb = 2280,
            buildDate = "2024-05-24",
            description = "Feature-packed custom ROM GSI with 'The Evolver' deep UI customization dashboard, Gaming Space, and status bar modding.",
            features = listOf(
                "The Evolver customization center (Status bar, QS, Lockscreen)",
                "Gaming Space with FPS stabilization & touch boost",
                "Custom lock screen clocks & fonts",
                "Face Unlock & App Lock built-in",
                "SELinux Permissive/Enforcing dynamic switcher"
            ),
            trebleCompatibilityScore = 93,
            wallpaperColor = 0xFF8B5CF6
        ),
        GsiRomCatalogItem(
            id = "crdroid-10",
            name = "crDroid v10.4 GSI",
            maintainer = "crDroid Treble Project",
            androidVersion = "Android 14 (crDroid)",
            apiLevel = 34,
            tag = "crDroid-10.4-arm64-bgN.img.xz",
            arch = GsiArch.ARM64_V8A,
            partitionLayout = PartitionLayout.A_B,
            vndkCompat = listOf(30, 31, 32, 33, 34),
            gappsIncluded = GappsType.MICRO_G,
            downloadSizeMb = 1450,
            buildDate = "2024-05-30",
            description = "High-performance privacy-oriented GSI based on LineageOS with crDroid Settings, microG core, and advanced battery profiles.",
            features = listOf(
                "crDroid Settings suite & QS tiles",
                "microG GmsCore integrated & pre-configured",
                "Privacy Guard & Network traffic monitor in status bar",
                "Custom sound engine with Dirac Audio virtualization",
                "Super low latency touch response"
            ),
            trebleCompatibilityScore = 96,
            wallpaperColor = 0xFF10B981
        ),
        GsiRomCatalogItem(
            id = "bliss-roms-17",
            name = "BlissRoms v17.2 GSI",
            maintainer = "Bliss Labs",
            androidVersion = "Android 14 (Blissfy)",
            apiLevel = 34,
            tag = "Bliss-v17.2-arm64-bgZ.img.xz",
            arch = GsiArch.ARM64_V8A,
            partitionLayout = PartitionLayout.A_B,
            vndkCompat = listOf(31, 32, 33, 34),
            gappsIncluded = GappsType.PIXEL_GAPPS,
            downloadSizeMb = 1980,
            buildDate = "2024-04-15",
            description = "Distinctive custom ROM featuring Blissify theme engine, Desktop PC Mode windowing support, and custom gesture navigation.",
            features = listOf(
                "Blissify UI engine & custom icon packs",
                "Android Desktop Mode windowing & multi-display",
                "Smart charging & battery health guardian",
                "Extensive power menu & sound profiles",
                "Overlayfs dynamic vendor patcher"
            ),
            trebleCompatibilityScore = 92,
            wallpaperColor = 0xFFF59E0B
        ),
        GsiRomCatalogItem(
            id = "leos-degoogled",
            name = "LeOS-U de-Googled GSI",
            maintainer = "Harvey186 / LeOS",
            androidVersion = "Android 14 (LeOS FOSS)",
            apiLevel = 34,
            tag = "LeOS-14-arm64-bvS.img.xz",
            arch = GsiArch.ARM64_V8A,
            partitionLayout = PartitionLayout.A_B,
            vndkCompat = listOf(30, 31, 32, 33, 34),
            gappsIncluded = GappsType.VANILLA,
            downloadSizeMb = 1120,
            buildDate = "2024-05-10",
            description = "Completely de-Googled and privacy-hardened Treble GSI with zero proprietary trackers, Aurora Store, and F-Droid.",
            features = listOf(
                "100% Zero Google telemetry or tracking binaries",
                "F-Droid and Aurora Store pre-installed",
                "Hardened SELinux Enforcing security",
                "Extreme battery life (minimal background services)",
                "System-wide DNS-over-HTTPS AdBlocker"
            ),
            trebleCompatibilityScore = 97,
            wallpaperColor = 0xFF06B6D4
        ),
        GsiRomCatalogItem(
            id = "aosp-15-preview",
            name = "Generic AOSP 15 Preview GSI",
            maintainer = "Google AOSP / Treble CI",
            androidVersion = "Android 15 (VanillaIceCream)",
            apiLevel = 35,
            tag = "aosp-arm64-15_preview-raw.img.xz",
            arch = GsiArch.ARM64_V8A,
            partitionLayout = PartitionLayout.A_B,
            vndkCompat = listOf(33, 34, 35),
            gappsIncluded = GappsType.VANILLA,
            downloadSizeMb = 1620,
            buildDate = "2024-06-15",
            description = "Bleeding-edge Android 15 Vanilla Ice Cream preview Generic System Image for testing next-gen OS APIs, Private Space, and predictive back animations.",
            features = listOf(
                "Android 15 Private Space & App Archiving",
                "Predictive Back gesture & Edge-to-Edge default",
                "Latest 16KB page size kernel compatibility",
                "Updated Linux 6.6 virtual kernel compatibility",
                "Treble v15 HAL interface binding"
            ),
            trebleCompatibilityScore = 90,
            wallpaperColor = 0xFFEC4899
        )
    )
}
