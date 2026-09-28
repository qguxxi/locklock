package com.locklock.applock.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.locklock.applock.designsystem.theme.SereneVaultTheme
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * PIN Dot Indicators (LockLock_Design.md):
 * 10px circular dots.
 * - Idle: unfilled #E2E8F0 stroke.
 * - Filled: solid #4B5563.
 * - Error state: gentle transition to #9F5858 with horizontal dampening shake.
 */
@Composable
fun SerenePinDotIndicators(
    pinLength: Int,
    enteredCount: Int,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            val amplitudes = listOf(16f, -14f, 10f, -8f, 4f, -2f, 0f)
            for (amp in amplitudes) {
                shakeOffset.animateTo(amp, animationSpec = tween(45))
            }
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    Row(
        modifier = modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pinLength) { index ->
            val isFilled = index < enteredCount
            val dotColor by animateColorAsState(
                targetValue = when {
                    isError -> colors.statusRose
                    isFilled -> colors.primary
                    else -> Color.Transparent
                },
                animationSpec = tween(150),
                label = "PinDotFill"
            )
            val borderColor by animateColorAsState(
                targetValue = when {
                    isError -> colors.statusRose
                    isFilled -> colors.primary
                    else -> colors.borderSoft
                },
                animationSpec = tween(150),
                label = "PinDotBorder"
            )

            Box(
                modifier = Modifier
                    .size(SereneVaultTheme.spacing.pinDotSize)
                    .clip(SereneVaultTheme.shapes.full)
                    .background(dotColor)
                    .border(1.5.dp, borderColor, SereneVaultTheme.shapes.full)
            )
        }
    }
}

/**
 * Numeric Keypad & PIN Inputs (LockLock_Design.md):
 * - Dialer Cells: Circular 72dp touch cells backed by soft porcelain white (#FFFFFF)
 *   with a delicate 1dp border of #E2E8F0.
 * - Digits use Manrope Medium (tabular-nums).
 * - On press, cells subtly recess into #F1F5F9 with quiet haptic response.
 */
@Composable
fun SereneNumericKeypad(
    onDigitPress: (String) -> Unit,
    onBackspacePress: () -> Unit,
    onBiometricPress: (() -> Unit)? = null,
    randomizeKeys: Boolean = false,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val digits = remember(randomizeKeys) {
        val base = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        if (randomizeKeys) base.shuffled() else base
    }

    val rows = listOf(
        digits.subList(0, 3),
        digits.subList(3, 6),
        digits.subList(6, 9)
    )
    val lastDigit = digits[9]

    Column(
        modifier = modifier.widthIn(max = SereneVaultTheme.spacing.authMaxWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        rows.forEach { rowDigits ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowDigits.forEach { digit ->
                    SereneDialerCell(
                        label = digit,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                            onDigitPress(digit)
                        }
                    )
                }
            }
        }

        // Bottom row: Biometric (optional) | 0 | Backspace
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBiometricPress != null) {
                SereneIconDialerCell(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBiometricPress()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = "Vân tay",
                        tint = SereneVaultTheme.colors.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(SereneVaultTheme.spacing.dialerCellSize))
            }

            SereneDialerCell(
                label = lastDigit,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    onDigitPress(lastDigit)
                }
            )

            SereneIconDialerCell(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    onBackspacePress()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = "Xóa",
                    tint = SereneVaultTheme.colors.secondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun SereneDialerCell(
    label: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val colors = SereneVaultTheme.colors

    val bgColor by animateColorAsState(
        targetValue = if (isPressed) colors.surfaceSubtle else colors.surfaceElevated,
        animationSpec = tween(100),
        label = "DialerCellBg"
    )

    Box(
        modifier = Modifier
            .size(SereneVaultTheme.spacing.dialerCellSize)
            .clip(SereneVaultTheme.shapes.full)
            .background(bgColor)
            .border(1.dp, colors.borderSoft, SereneVaultTheme.shapes.full)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = label,
            style = SereneVaultTheme.typography.numericKeypad.copy(color = colors.textStrong)
        )
    }
}

@Composable
private fun SereneIconDialerCell(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val colors = SereneVaultTheme.colors

    val bgColor by animateColorAsState(
        targetValue = if (isPressed) colors.surfaceSubtle else Color.Transparent,
        animationSpec = tween(100),
        label = "IconDialerBg"
    )

    Box(
        modifier = Modifier
            .size(SereneVaultTheme.spacing.dialerCellSize)
            .clip(SereneVaultTheme.shapes.full)
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Biometric Scan Zone (LockLock_Design.md):
 * - Replaces high-intensity laser scans with a calm breathing wave.
 * - Gentle concentric rings in semi-transparent #64748B (10% and 5% opacity).
 * - Biometric glyph renders in refined slate (#4B5563), transitioning softly to Muted Sage (#4D7C6F)
 *   on successful match, accompanied by a modest checkmark indicator.
 */
@Composable
fun BreathingBiometricZone(
    isVerified: Boolean,
    onTriggerBiometric: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "BreathingWave")

    val outerScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OuterBreathingRing"
    )

    val innerScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "InnerBreathingRing"
    )

    val glyphColor by animateColorAsState(
        targetValue = if (isVerified) colors.statusSafe else colors.primary,
        animationSpec = tween(300),
        label = "BiometricGlyphColor"
    )

    Box(
        modifier = modifier.size(116.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Concentric Ring (5% opacity #64748B)
        Box(
            modifier = Modifier
                .size(112.dp)
                .scale(outerScale)
                .clip(SereneVaultTheme.shapes.full)
                .background(colors.secondary.copy(alpha = 0.05f))
        )

        // Inner Concentric Ring (10% opacity #64748B)
        Box(
            modifier = Modifier
                .size(86.dp)
                .scale(innerScale)
                .clip(SereneVaultTheme.shapes.full)
                .background(colors.secondary.copy(alpha = 0.10f))
        )

        // Center Tactile Shield Target
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(SereneVaultTheme.shapes.full)
                .background(colors.surfaceElevated)
                .border(
                    width = 1.dp,
                    color = if (isVerified) colors.statusSafeBorder else colors.borderSoft,
                    shape = SereneVaultTheme.shapes.full
                )
                .clickable(onClick = onTriggerBiometric),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isVerified) Icons.Outlined.Check else Icons.Outlined.Fingerprint,
                contentDescription = "Xác thực sinh trắc học",
                tint = glyphColor,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

/**
 * Serene Pattern Lock Canvas (3x3 Grid):
 * Hỗ trợ vẽ mẫu hình mượt mà theo phong cách Serene Vault, kèm tùy chọn ẩn đường vẽ (Invisible Pattern).
 */
@Composable
fun SerenePatternCanvas(
    isInvisiblePattern: Boolean,
    isError: Boolean,
    onPatternComplete: (List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    val haptic = LocalHapticFeedback.current
    val selectedNodes = remember { mutableStateListOf<Int>() }
    var currentFingerPos by remember { mutableStateOf<Offset?>(null) }

    val activeColor = if (isError) colors.statusRose else colors.primary

    Box(
        modifier = modifier
            .size(280.dp)
            .clip(SereneVaultTheme.shapes.xl)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.borderSoft, SereneVaultTheme.shapes.xl),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(240.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            selectedNodes.clear()
                            currentFingerPos = offset
                            findHitNode(offset, size.width.toFloat(), size.height.toFloat())?.let { node ->
                                selectedNodes.add(node)
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        onDrag = { change, _ ->
                            currentFingerPos = change.position
                            findHitNode(change.position, size.width.toFloat(), size.height.toFloat())?.let { node ->
                                if (!selectedNodes.contains(node)) {
                                    selectedNodes.add(node)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            }
                        },
                        onDragEnd = {
                            currentFingerPos = null
                            if (selectedNodes.isNotEmpty()) {
                                onPatternComplete(selectedNodes.toList())
                            }
                        },
                        onDragCancel = {
                            currentFingerPos = null
                            selectedNodes.clear()
                        }
                    )
                }
        ) {
            val cellWidth = size.width / 3f
            val cellHeight = size.height / 3f

            fun nodeCenter(index: Int): Offset {
                val row = index / 3
                val col = index % 3
                return Offset(
                    x = col * cellWidth + cellWidth / 2f,
                    y = row * cellHeight + cellHeight / 2f
                )
            }

            // Vẽ đường nối nếu không bật chế độ ẩn đường vẽ
            if (!isInvisiblePattern && selectedNodes.size > 1) {
                val path = Path().apply {
                    val first = nodeCenter(selectedNodes.first())
                    moveTo(first.x, first.y)
                    for (i in 1 until selectedNodes.size) {
                        val pt = nodeCenter(selectedNodes[i])
                        lineTo(pt.x, pt.y)
                    }
                    currentFingerPos?.let { finger ->
                        lineTo(finger.x, finger.y)
                    }
                }
                drawPath(
                    path = path,
                    color = activeColor.copy(alpha = 0.45f),
                    style = Stroke(
                        width = 3.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Vẽ 9 điểm nút (3x3)
            for (i in 0 until 9) {
                val center = nodeCenter(i)
                val isSelected = selectedNodes.contains(i)

                if (isSelected && !isInvisiblePattern) {
                    drawCircle(
                        color = activeColor.copy(alpha = 0.14f),
                        radius = 22.dp.toPx(),
                        center = center
                    )
                }

                drawCircle(
                    color = if (isSelected && !isInvisiblePattern) activeColor else colors.surfaceSunken,
                    radius = if (isSelected && !isInvisiblePattern) 8.dp.toPx() else 6.dp.toPx(),
                    center = center
                )
            }
        }
    }
}

private fun findHitNode(touch: Offset, width: Float, height: Float): Int? {
    val cellW = width / 3f
    val cellH = height / 3f
    val hitRadius = minOf(cellW, cellH) * 0.36f
    for (i in 0 until 9) {
        val cx = (i % 3) * cellW + cellW / 2f
        val cy = (i / 3) * cellH + cellH / 2f
        if (hypot(touch.x - cx, touch.y - cy) <= hitRadius) {
            return i
        }
    }
    return null
}
