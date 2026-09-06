package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = DarkBackground,
  primaryContainer = Color(0xFF0F3B36),
  onPrimaryContainer = NeonCyan,
  secondary = ElectricViolet,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF2C1A4A),
  onSecondaryContainer = Color(0xFFE2B2FF),
  tertiary = NeonLime,
  onTertiary = DarkBackground,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = DarkCardBorder,
  error = ErrorRed,
  onError = Color.White
)

private val LightColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = DarkBackground,
  primaryContainer = Color(0xFF0F3B36),
  onPrimaryContainer = NeonCyan,
  secondary = ElectricViolet,
  onSecondary = Color.White,
  tertiary = NeonLime,
  onTertiary = DarkBackground,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = DarkCardBorder
)

@Composable
fun ZipPayTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

