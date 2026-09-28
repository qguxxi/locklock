package com.locklock.applock.designsystem.tokens

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Hệ thống bo góc (Shapes) chuẩn từ LockLock_Design.md:
 * - sm: 0.25rem (4dp)
 * - default: 0.5rem (8dp)
 * - md: 0.75rem (12dp)
 * - lg: 1rem (16dp) - Primary Cards & Containers
 * - xl: 1.5rem (24dp)
 * - full: 9999px (CircleShape) - Numeric Dialer Cells, Biometric Target Shields & Status Pills
 */
@Immutable
data class SereneShapeSystem(
    val sm: Shape = RoundedCornerShape(4.dp),
    val default: Shape = RoundedCornerShape(8.dp),
    val md: Shape = RoundedCornerShape(12.dp),
    val lg: Shape = RoundedCornerShape(16.dp),
    val xl: Shape = RoundedCornerShape(24.dp),
    val full: Shape = CircleShape
)

val LocalSereneShapes = staticCompositionLocalOf { SereneShapeSystem() }
