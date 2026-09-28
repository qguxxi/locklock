package com.locklock.applock.designsystem.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.locklock.applock.designsystem.theme.SereneVaultTheme

enum class SereneBadgeVariant {
    PROTECTED_ACTIVE,
    VAULT_LOCKED,
    WARNING_UNVERIFIED,
    INTRUDER_ANOMALY
}

/**
 * Primary Confirm Button (LockLock_Design.md):
 * Solid deep slate fill (#4B5563) with clean white typography (#FFFFFF).
 * Active state shifts smoothly to #334155.
 */
@Composable
fun SerenePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    verticalPadding: Dp = 14.dp,
    fontSize: TextUnit = TextUnit.Unspecified,
    shape: Shape = SereneVaultTheme.shapes.md,
    containerColor: Color = SereneVaultTheme.colors.primary,
    pressedContainerColor: Color = SereneVaultTheme.colors.primaryActive
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val colors = SereneVaultTheme.colors
    val baseStyle = SereneVaultTheme.typography.labelLg

    val animatedContainerColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.surfaceSunken
            isPressed -> pressedContainerColor
            else -> containerColor
        },
        animationSpec = tween(durationMillis = 160),
        label = "PrimaryBtnColor"
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(animatedContainerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = verticalPadding),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = baseStyle.copy(
                fontSize = if (fontSize != TextUnit.Unspecified) fontSize else baseStyle.fontSize,
                lineHeight = if (fontSize != TextUnit.Unspecified) fontSize * 1.25f else baseStyle.lineHeight,
                fontWeight = if (fontSize != TextUnit.Unspecified) FontWeight.Bold else baseStyle.fontWeight,
                color = if (enabled) colors.onPrimary else colors.textMuted
            )
        )
    }
}

/**
 * Secondary / Ghost Button (LockLock_Design.md):
 * Translucent slate container (#F1F5F9) with #475569 text and a subtle #E2E8F0 border.
 */
@Composable
fun SereneSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    Box(
        modifier = modifier
            .clip(SereneVaultTheme.shapes.md)
            .background(colors.surfaceSubtle)
            .border(1.dp, colors.borderSoft, SereneVaultTheme.shapes.md)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = SereneVaultTheme.typography.labelLg.copy(color = Color(0xFF475569))
        )
    }
}

/**
 * Status Badges & Chips (LockLock_Design.md):
 * Compact pill-shaped chips with low-contrast, pastel-tinted substrates.
 */
@Composable
fun StatusBadgeChip(
    text: String,
    variant: SereneBadgeVariant,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    val (bgColor, textColor, borderColor) = when (variant) {
        SereneBadgeVariant.PROTECTED_ACTIVE -> Triple(
            colors.statusSafeBg,
            colors.statusSafe,
            colors.statusSafeBorder
        )
        SereneBadgeVariant.VAULT_LOCKED -> Triple(
            colors.surfaceSubtle,
            Color(0xFF475569),
            colors.borderSoft
        )
        SereneBadgeVariant.WARNING_UNVERIFIED -> Triple(
            colors.statusAmberBg,
            colors.statusAmber,
            Color(0xFFFDE68A)
        )
        SereneBadgeVariant.INTRUDER_ANOMALY -> Triple(
            Color(0xFFFFF1F2),
            colors.statusRose,
            Color(0xFFFECDD3)
        )
    }

    Box(
        modifier = modifier
            .clip(SereneVaultTheme.shapes.full)
            .background(bgColor)
            .border(1.dp, borderColor, SereneVaultTheme.shapes.full)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = text,
            style = SereneVaultTheme.typography.labelSm.copy(color = textColor)
        )
    }
}

/**
 * Shackle Lock Toggle (LockLock_Design.md):
 * Soft pill track.
 * - Locked: slate fill (#4B5563) with smooth white thumb holding a minimal closed shackle outline.
 * - Unlocked: pale gray track (#E2E8F0) with white thumb and open shackle icon.
 */
@Composable
fun ShackleLockToggle(
    isLocked: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    val haptic = LocalHapticFeedback.current

    val trackColor by animateColorAsState(
        targetValue = if (isLocked) colors.primary else colors.surfaceSunken,
        animationSpec = tween(220),
        label = "ToggleTrack"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (isLocked) 24.dp else 2.dp,
        animationSpec = tween(220),
        label = "ToggleThumb"
    )

    Box(
        modifier = modifier
            .width(52.dp)
            .height(30.dp)
            .clip(SereneVaultTheme.shapes.full)
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onToggle(!isLocked)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(26.dp)
                .clip(SereneVaultTheme.shapes.full)
                .background(colors.surfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLocked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                contentDescription = if (isLocked) "Đã khóa" else "Mở khóa",
                tint = if (isLocked) colors.primary else colors.secondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * App Locker Row Item (LockLock_Design.md):
 * Clean list item on #FFFFFF with 1dp #E2E8F0 contour, 40px rounded app icon,
 * application title in Manrope SemiBold (#1E293B), category subtext in #64748B, and Shackle Lock Toggle.
 */
@Composable
fun AppLockerRowItem(
    appName: String,
    categoryText: String,
    iconDrawable: Drawable?,
    isLocked: Boolean,
    onToggleLock: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    val iconBitmap = remember(iconDrawable) {
        iconDrawable?.toBitmap(width = 120, height = 120)?.asImageBitmap()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SereneVaultTheme.shapes.lg)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.borderSoft, SereneVaultTheme.shapes.lg)
            .clickable { onToggleLock(!isLocked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 40dp Rounded App Icon
        Box(
            modifier = Modifier
                .size(SereneVaultTheme.spacing.appIconSize)
                .clip(SereneVaultTheme.shapes.md)
                .background(colors.surfaceSubtle),
            contentAlignment = Alignment.Center
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = appName,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(SereneVaultTheme.shapes.default)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = appName,
                    tint = colors.secondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = appName,
                style = SereneVaultTheme.typography.headlineSm.copy(color = colors.textStrong),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            BasicText(
                text = categoryText,
                style = SereneVaultTheme.typography.bodySm.copy(color = colors.textMuted),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        ShackleLockToggle(
            isLocked = isLocked,
            onToggle = onToggleLock
        )
    }
}

/**
 * Vault Status Container (LockLock_Design.md):
 * Minimalist card with a soft radial tint (rgba(100, 116, 139, 0.05)).
 * Features an understated circular progress ring highlighting encrypted/protected percentage
 * in Deep Slate (#4B5563) and Muted Sage (#4D7C6F).
 */
@Composable
fun VaultStatusContainer(
    title: String,
    subtitle: String,
    protectedCount: Int,
    totalCount: Int,
    statusBadgeText: String,
    isServiceActive: Boolean,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null
) {
    val colors = SereneVaultTheme.colors
    val progress = if (totalCount > 0) (protectedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "VaultRingProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(SereneVaultTheme.shapes.lg)
            .background(colors.surfaceElevated)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF64748B).copy(alpha = 0.06f),
                        Color.Transparent
                    )
                )
            )
            .border(1.dp, colors.borderSoft, SereneVaultTheme.shapes.lg)
            .then(if (onActionClick != null) Modifier.clickable { onActionClick() } else Modifier)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                StatusBadgeChip(
                    text = statusBadgeText,
                    variant = if (isServiceActive) SereneBadgeVariant.PROTECTED_ACTIVE else SereneBadgeVariant.WARNING_UNVERIFIED
                )
                Spacer(modifier = Modifier.height(10.dp))
                BasicText(
                    text = title,
                    style = SereneVaultTheme.typography.headlineMd.copy(color = colors.textStrong)
                )
                Spacer(modifier = Modifier.height(4.dp))
                BasicText(
                    text = subtitle,
                    style = SereneVaultTheme.typography.bodyMd.copy(color = colors.textMuted)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Circular SVG/Canvas Progress Ring
            Box(
                modifier = Modifier.size(68.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(68.dp)) {
                    val strokeWidth = 6.dp.toPx()
                    drawArc(
                        color = colors.surfaceSunken,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = if (isServiceActive) colors.statusSafe else colors.primary,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                BasicText(
                    text = "$protectedCount",
                    style = SereneVaultTheme.typography.headlineSm.copy(color = colors.textStrong)
                )
            }
        }
    }
}

/**
 * Intruder Snapshot Tile (LockLock_Design.md):
 * White card bounded by a soft #E2E8F0 stroke. Contains an incident tag in Muted Rosewood (#9F5858),
 * a gently rounded camera preview (rounded-md), and timestamp metrics in clean tabular Manrope.
 */
@Composable
fun IntruderSnapshotTile(
    targetAppName: String,
    timestampFormatted: String,
    failedAttempts: Int,
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SereneVaultTheme.shapes.lg)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.borderSoft, SereneVaultTheme.shapes.lg)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(SereneVaultTheme.shapes.md)
                .background(Color(0xFFFFF1F2))
                .border(1.dp, Color(0xFFFECDD3), SereneVaultTheme.shapes.md),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonOff,
                contentDescription = null,
                tint = colors.statusRose,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadgeChip(
                    text = "SAI $failedAttempts LẦN",
                    variant = SereneBadgeVariant.INTRUDER_ANOMALY
                )
                BasicText(
                    text = timestampFormatted,
                    style = SereneVaultTheme.typography.labelMd.copy(color = colors.textMuted)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            BasicText(
                text = "Cố gắng mở $targetAppName",
                style = SereneVaultTheme.typography.headlineSm.copy(color = colors.textStrong)
            )
        }
    }
}
