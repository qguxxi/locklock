package com.locklock.applock.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.locklock.applock.designsystem.tokens.LocalSereneColors
import com.locklock.applock.designsystem.tokens.LocalSereneShapes
import com.locklock.applock.designsystem.tokens.LocalSereneSpacing
import com.locklock.applock.designsystem.tokens.LocalSereneTypography
import com.locklock.applock.designsystem.tokens.SereneColorPalette
import com.locklock.applock.designsystem.tokens.SereneShapeSystem
import com.locklock.applock.designsystem.tokens.SereneSpacingSystem
import com.locklock.applock.designsystem.tokens.SereneTypographySystem

/**
 * SereneVaultTheme — Điểm cung cấp duy nhất toàn bộ Design Tokens cho ứng dụng LockLock.
 * Không phụ thuộc vào giao diện Material 3 mặc định.
 */
object SereneVaultTheme {
    val colors: SereneColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalSereneColors.current

    val typography: SereneTypographySystem
        @Composable
        @ReadOnlyComposable
        get() = LocalSereneTypography.current

    val shapes: SereneShapeSystem
        @Composable
        @ReadOnlyComposable
        get() = LocalSereneShapes.current

    val spacing: SereneSpacingSystem
        @Composable
        @ReadOnlyComposable
        get() = LocalSereneSpacing.current
}

@Composable
fun SereneVaultTheme(
    colors: SereneColorPalette = SereneColorPalette(),
    typography: SereneTypographySystem = SereneTypographySystem(),
    shapes: SereneShapeSystem = SereneShapeSystem(),
    spacing: SereneSpacingSystem = SereneSpacingSystem(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSereneColors provides colors,
        LocalSereneTypography provides typography,
        LocalSereneShapes provides shapes,
        LocalSereneSpacing provides spacing,
        content = content
    )
}
