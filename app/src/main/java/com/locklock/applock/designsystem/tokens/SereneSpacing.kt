package com.locklock.applock.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Hệ thống Spacing & Ergonomics chuẩn từ LockLock_Design.md:
 * - margin: 1.25rem (20dp)
 * - gutter: 1rem (16dp)
 * - space-xs: 0.25rem (4dp)
 * - space-sm: 0.5rem (8dp)
 * - space-md: 0.875rem (14dp)
 * - space-lg: 1.25rem (20dp)
 * - space-xl: 2rem (32dp)
 * - authMaxWidth: 400dp (khi màn hình > 480dp)
 */
@Immutable
data class SereneSpacingSystem(
    val gutter: Dp = 16.dp,
    val screenMargin: Dp = 20.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 14.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 32.dp,
    val dialerCellSize: Dp = 72.dp,
    val pinDotSize: Dp = 10.dp,
    val appIconSize: Dp = 40.dp,
    val authMaxWidth: Dp = 400.dp
)

val LocalSereneSpacing = staticCompositionLocalOf { SereneSpacingSystem() }
