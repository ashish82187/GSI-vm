package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DroidVmColorScheme = darkColorScheme(
  primary = CyberCyan,
  onPrimary = DarkBg,
  primaryContainer = DeepCyan,
  onPrimaryContainer = TextPrimary,
  secondary = NeonEmerald,
  onSecondary = DarkBg,
  secondaryContainer = DarkCardElevated,
  onSecondaryContainer = BrightEmerald,
  tertiary = NeonPurple,
  onTertiary = TextPrimary,
  background = DarkBg,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkCard,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder,
  error = CrimsonError,
  onError = TextPrimary
)

@Composable
fun DroidVmTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DroidVmColorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit,
) {
  DroidVmTheme(content = content)
}


