package com.locklock.applock.presentation.pin

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.locklock.applock.R
import kotlinx.coroutines.delay

private val PinActiveGreen = Color(0xFF2AD200)
private val PinInactiveWhite = Color(0xFFFFFFFF)
private val PinScreenBlack = Color(0xFF000000)
private const val PIN_LENGTH = 6

/**
 * Màn hình "Thiết lập mã pin" triển khai từ thiết kế Figma (node-id: 12:2):
 * - Nền đen tuyệt đối (#000000)
 * - Tiêu đề "Thiết lập mã pin" (40sp, Bold, #FFFFFF)
 * - 6 biểu tượng linh vật LockLock (34dp):
 *   + Khi chưa nhập: màu trắng (#FFFFFF) và nhắm mắt
 *   + Khi vừa nhập: chuyển xanh lá (#2AD200), mở mắt ra rồi ngay lập tức nhắm mắt lại
 * - Linh vật LockLock lấp ló bên mép phải (node 12:46): khi nhập cũng mở mắt ra rồi lập tức nhắm mắt lại
 * - Bàn phím số cỡ chữ gấp đôi (48sp)
 */
@Composable
fun PinEntryScreen(
    title: String = "Thiết lập mã pin",
    initialPin: String = "",
    onPinComplete: (String) -> Unit = {}
) {
    var enteredPin by remember { mutableStateOf(initialPin.take(PIN_LENGTH)) }
    var inputBlinkTick by remember { mutableIntStateOf(0) }
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current

    if (!view.isInEditMode) {
        @Suppress("DEPRECATION")
        DisposableEffect(view) {
            val window = (view.context as? Activity)?.window
            val prevStatusBarColor = window?.statusBarColor
            val prevNavBarColor = window?.navigationBarColor
            val controller = window?.let { WindowCompat.getInsetsController(it, view) }
            val prevLightStatus = controller?.isAppearanceLightStatusBars ?: true
            val prevLightNav = controller?.isAppearanceLightNavigationBars ?: true

            window?.statusBarColor = PinScreenBlack.toArgb()
            window?.navigationBarColor = PinScreenBlack.toArgb()
            controller?.isAppearanceLightStatusBars = false
            controller?.isAppearanceLightNavigationBars = false

            onDispose {
                if (prevStatusBarColor != null) window.statusBarColor = prevStatusBarColor
                if (prevNavBarColor != null) window.navigationBarColor = prevNavBarColor
                controller?.isAppearanceLightStatusBars = prevLightStatus
                controller?.isAppearanceLightNavigationBars = prevLightNav
            }
        }
    }

    LaunchedEffect(enteredPin) {
        if (enteredPin.length == PIN_LENGTH) {
            delay(320L)
            onPinComplete(enteredPin)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(PinScreenBlack)
            .clipToBounds()
    ) {
        val screenHeight = maxHeight
        val titleTop = screenHeight * (96f / 800f)
        val indicatorsTop = screenHeight * (206f / 800f)
        val peekingMascotTop = screenHeight * 0.365f
        val keypadTop = screenHeight * (430f / 800f)

        // 1. Tiêu đề "Thiết lập mã pin"
        BasicText(
            text = title,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 48.sp,
                color = PinInactiveWhite,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = titleTop)
        )

        // 2. Cụm 6 chỉ báo mã PIN hình linh vật LockLock (nhập -> mở mắt ra rồi lập tức nhắm mắt lại)
        PinMascotIndicatorsRow(
            enteredCount = enteredPin.length,
            pinLength = PIN_LENGTH,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = indicatorsTop)
        )

        // 3. Linh vật LockLock lấp ló bên mép phải (node 12:46)
        val peekingEyesOpenAnim = remember { Animatable(0f) }
        LaunchedEffect(inputBlinkTick) {
            if (inputBlinkTick > 0) {
                peekingEyesOpenAnim.snapTo(0f)
                peekingEyesOpenAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                )
                peekingEyesOpenAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 27.28.dp, y = peekingMascotTop)
                .size(width = 87.28.dp, height = 89.27.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_pin_mascot_peeking),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 80.93.dp, height = 78.67.dp)
                    .graphicsLayer {
                        scaleX = -1f
                        rotationZ = 83.54f
                    }
                    .drawWithContent {
                        drawContent()
                        val w = size.width
                        val h = size.height
                        val openP = peekingEyesOpenAnim.value

                        val eyeW = w * (0.145f - 0.018f * openP)
                        val eyeH = h * (0.028f + 0.103f * openP)
                        val corner = CornerRadius(eyeW / 2f, eyeW / 2f)

                        val cy = h * 0.6885f
                        val cx1 = w * 0.3738f
                        val cx2 = w * 0.6245f

                        drawRoundRect(
                            color = PinScreenBlack,
                            topLeft = Offset(cx1 - eyeW / 2f, cy - eyeH / 2f),
                            size = Size(eyeW, eyeH),
                            cornerRadius = corner
                        )
                        drawRoundRect(
                            color = PinScreenBlack,
                            topLeft = Offset(cx2 - eyeW / 2f, cy - eyeH / 2f),
                            size = Size(eyeW, eyeH),
                            cornerRadius = corner
                        )
                    }
            )
        }

        // 4. Bàn phím số (cỡ số 48sp = gấp đôi 24sp)
        PinNumericKeypad(
            onDigitClick = { digit ->
                if (enteredPin.length < PIN_LENGTH) {
                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    enteredPin += digit
                    inputBlinkTick += 1
                }
            },
            onDeleteClick = {
                if (enteredPin.isNotEmpty()) {
                    haptic.performHapticFeedback(HapticFeedbackType.KeyboardTap)
                    enteredPin = enteredPin.dropLast(1)
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = keypadTop)
        )
    }
}

@Composable
private fun PinMascotIndicatorsRow(
    enteredCount: Int,
    pinLength: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pinLength) { index ->
            val isFilled = index < enteredCount
            val tintColor by animateColorAsState(
                targetValue = if (isFilled) PinActiveGreen else PinInactiveWhite,
                animationSpec = tween(durationMillis = 160),
                label = "PinMascotTint"
            )
            val scale by animateFloatAsState(
                targetValue = if (isFilled) 1.08f else 1.0f,
                animationSpec = tween(durationMillis = 140),
                label = "PinMascotScale"
            )

            // Khi vừa nhập (isFilled chuyển sang true): mở mắt ra (0f -> 1f), mở xong lập tức nhắm lại (1f -> 0f)
            val eyeOpenAnim = remember { Animatable(0f) }
            LaunchedEffect(isFilled) {
                if (isFilled) {
                    eyeOpenAnim.snapTo(0f)
                    eyeOpenAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    )
                    eyeOpenAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    )
                } else {
                    eyeOpenAnim.snapTo(0f)
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_pin_mascot_indicator),
                contentDescription = null,
                colorFilter = ColorFilter.tint(tintColor),
                modifier = Modifier
                    .size(34.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .drawWithContent {
                        drawContent()
                        val w = size.width
                        val h = size.height
                        val openP = eyeOpenAnim.value

                        val eyeW = w * (0.122f - 0.024f * openP)
                        val eyeH = h * (0.026f + 0.072f * openP)
                        val corner = CornerRadius(eyeW / 2f, eyeW / 2f)

                        val cy = h * 0.3596f
                        val cx1 = w * 0.4062f
                        val cx2 = w * 0.5938f

                        drawRoundRect(
                            color = PinScreenBlack,
                            topLeft = Offset(cx1 - eyeW / 2f, cy - eyeH / 2f),
                            size = Size(eyeW, eyeH),
                            cornerRadius = corner
                        )
                        drawRoundRect(
                            color = PinScreenBlack,
                            topLeft = Offset(cx2 - eyeW / 2f, cy - eyeH / 2f),
                            size = Size(eyeW, eyeH),
                            cornerRadius = corner
                        )
                    }
            )
        }
    }
}

@Composable
private fun PinNumericKeypad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(
        modifier = modifier.width(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        rows.forEach { rowDigits ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowDigits.forEach { digit ->
                    PinDigitKey(
                        digit = digit,
                        onClick = { onDigitClick(digit) }
                    )
                }
            }
        }

        // Final Row (node 12:43): cột trái trống, cột giữa là "0", cột phải là icon xóa
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(width = 76.dp, height = 60.dp))

            PinDigitKey(
                digit = "0",
                onClick = { onDigitClick("0") }
            )

            PinDeleteKey(
                onClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun PinDigitKey(
    digit: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "DigitKeyPressScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.65f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "DigitKeyPressAlpha"
    )

    Box(
        modifier = Modifier
            .size(width = 76.dp, height = 60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = digit,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                lineHeight = 56.sp,
                color = PinInactiveWhite,
                textAlign = TextAlign.Center,
                fontFeatureSettings = "tnum"
            )
        )
    }
}

@Composable
private fun PinDeleteKey(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "DeleteKeyPressScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.65f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "DeleteKeyPressAlpha"
    )

    Box(
        modifier = Modifier
            .size(width = 76.dp, height = 60.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_pin_delete),
            contentDescription = "Xóa",
            modifier = Modifier.size(44.dp)
        )
    }
}
