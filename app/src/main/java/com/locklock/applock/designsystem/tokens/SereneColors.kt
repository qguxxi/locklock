package com.locklock.applock.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Bảng màu chuẩn từ đặc tả "Serene Vault" (LockLock_Design.md).
 * Hướng tới cảm giác thanh lịch, tĩnh tại (Warm Minimalist Glass & Editorial Precision).
 */
@Immutable
data class SereneColorPalette(
    // Core Roles & Substrates
    val slateBg: Color = Color(0xFFF8FAFC),           // Neutral Canvas (Soft Porcelain)
    val surface: Color = Color(0xFFFBF9FA),
    val surfaceElevated: Color = Color(0xFFFFFFFF),   // Cards, Vault Lists, Dialers
    val surfaceSubtle: Color = Color(0xFFF1F5F9),     // Interactive Depressions & Sliders
    val surfaceSunken: Color = Color(0xFFE2E8F0),
    val borderSoft: Color = Color(0xFFE2E8F0),        // 1px satin contour

    // Brand & Accent
    val primary: Color = Color(0xFF4B5563),           // Deep Slate (Primary actions & shields)
    val primaryActive: Color = Color(0xFF334155),     // Hover / Active press shift
    val primaryDeep: Color = Color(0xFF343E4B),
    val onPrimary: Color = Color(0xFFFFFFFF),
    val secondary: Color = Color(0xFF64748B),         // Cool Slate Gray
    val secondaryContainer: Color = Color(0xFFD0E1FB),
    val tertiary: Color = Color(0xFF5A7C75),          // Muted Sage

    // Typography Colors
    val textStrong: Color = Color(0xFF1E293B),
    val textMuted: Color = Color(0xFF64748B),
    val onSurface: Color = Color(0xFF1B1B1C),
    val onSurfaceVariant: Color = Color(0xFF44474C),

    // Status & Feedback
    val statusSafe: Color = Color(0xFF4D7C6F),        // Muted Sage (Verified / Secure)
    val statusSafeBg: Color = Color(0xFFECFDF5),
    val statusSafeBorder: Color = Color(0xFFD1FAE5),

    val statusAmber: Color = Color(0xFF92704C),       // Warm Dust Amber (Attention Required)
    val statusAmberBg: Color = Color(0xFFFFFBEB),

    val statusRose: Color = Color(0xFF9F5858),        // Muted Rosewood (Access Anomaly / Intruder)
    val statusRoseBg: Color = Color(0xFFFFDAD6)
)

val LocalSereneColors = staticCompositionLocalOf { SereneColorPalette() }
