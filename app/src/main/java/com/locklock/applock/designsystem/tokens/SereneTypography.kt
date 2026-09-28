package com.locklock.applock.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * Hệ thống Typography chuẩn "Serene Vault" (LockLock_Design.md):
 * - Manrope (kích hoạt tabular-nums "tnum" cho các chỉ số, mã PIN, bộ đếm)
 * - Plus Jakarta Sans cho nội dung mô tả, hộp thoại hướng dẫn và quyền thiết bị.
 */
private val ManropeFontFamily = FontFamily.SansSerif
private val PlusJakartaFontFamily = FontFamily.SansSerif

@Immutable
data class SereneTypographySystem(
    val display: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 34.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 42.sp,
        fontFeatureSettings = "tnum"
    ),
    val headlineLg: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 26.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 34.sp,
        fontFeatureSettings = "tnum"
    ),
    val headlineMd: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 26.sp,
        fontFeatureSettings = "tnum"
    ),
    val headlineSm: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 22.sp
    ),
    val bodyLg: TextStyle = TextStyle(
        fontFamily = PlusJakartaFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    ),
    val bodyMd: TextStyle = TextStyle(
        fontFamily = PlusJakartaFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    ),
    val bodySm: TextStyle = TextStyle(
        fontFamily = PlusJakartaFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp
    ),
    val labelLg: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 18.sp,
        letterSpacing = 0.02.em
    ),
    val labelMd: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.02.em,
        fontFeatureSettings = "tnum"
    ),
    val labelSm: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 14.sp,
        letterSpacing = 0.03.em,
        fontFeatureSettings = "tnum"
    ),
    val numericKeypad: TextStyle = TextStyle(
        fontFamily = ManropeFontFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 32.sp,
        fontFeatureSettings = "tnum"
    )
)

val LocalSereneTypography = staticCompositionLocalOf { SereneTypographySystem() }
