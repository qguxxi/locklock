package com.locklock.applock.presentation.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.locklock.applock.R
import com.locklock.applock.designsystem.components.SerenePrimaryButton
import com.locklock.applock.designsystem.theme.SereneVaultTheme
import kotlin.math.cos
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Đường cong điều hòa Cosine (C∞ continuous):
 * Đảm bảo vận tốc bằng 0 tại cả 2 đầu đảo chiều (t=0 và t=1),
 * triệt tiêu hoàn toàn hiện tượng giật/khựng (velocity snap) khi dùng với RepeatMode.Reverse.
 */
private val HarmonicSineEasing = Easing { fraction ->
    (1f - cos(fraction * Math.PI.toFloat())) * 0.5f
}

@Composable
fun SereneOnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val colors = SereneVaultTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.slateBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = SereneVaultTheme.spacing.screenMargin, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Center Illustration & Tagline Area ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WavingLockMascotWithSafe()

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedOnboardingTagline()
        }

        // --- Button "Tiếp tục" ở phía dưới (Nền đen đồng bộ Mascot, kích thước 19.dp / 19.sp) ---
        SerenePrimaryButton(
            text = "Tiếp tục",
            onClick = onFinishOnboarding,
            verticalPadding = 19.dp,
            fontSize = 19.sp,
            shape = SereneVaultTheme.shapes.lg,
            containerColor = Color(0xFF050505),
            pressedContainerColor = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Hiệu ứng xuất hiện dần dần (Progressive Reveal) cho câu slogan:
 * - Không dùng Offscreen Buffer (saveLayer) và không cấp phát Brush/LinearGradient mới mỗi frame.
 * - Dùng duy nhất 1 Brush được tạo sẵn trong drawWithCache và dịch chuyển bằng ma trận GPU.
 */
@Composable
private fun AnimatedOnboardingTagline() {
    val colors = SereneVaultTheme.colors
    val typography = SereneVaultTheme.typography
    val bgMaskColor = colors.slateBg

    val line1Progress = remember { Animatable(0f) }
    val line2Progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Chờ Splash Screen hoàn tất chuyển cảnh (240ms) để tránh tranh chấp frame đầu tiên
        delay(240L)
        // Bước 1: Hiện dần dòng "Mọi bí mật đã có"
        line1Progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 750, easing = LinearOutSlowInEasing)
        )
        // Bước 2: Hiện dần chữ "LockLock lo" (cỡ chữ to gấp đôi & in đậm nổi bật)
        line2Progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = LinearOutSlowInEasing)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        // Dòng 1: "Mọi bí mật đã có" (cỡ chữ chuẩn 19.sp)
        BasicText(
            text = "Mọi bí mật đã có",
            style = typography.headlineMd.copy(
                fontSize = 19.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textMuted,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .graphicsLayer {
                    val p = line1Progress.value
                    alpha = (p * 1.6f).coerceIn(0f, 1f)
                    translationY = (1f - p) * 5.dp.toPx()
                }
                .drawWithCache {
                    val featherPx = 40.dp.toPx()
                    val featherBrush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, bgMaskColor),
                        startX = 0f,
                        endX = featherPx
                    )
                    val featherSize = Size(featherPx, size.height)

                    onDrawWithContent {
                        drawContent()
                        val p = line1Progress.value
                        if (p < 1f) {
                            val totalW = size.width + featherPx
                            val startFade = (totalW * p - featherPx).coerceAtLeast(0f)
                            withTransform({
                                translate(left = startFade, top = 0f)
                            }) {
                                drawRect(
                                    brush = featherBrush,
                                    topLeft = Offset.Zero,
                                    size = featherSize
                                )
                            }
                            val solidStart = startFade + featherPx
                            if (solidStart < size.width) {
                                drawRect(
                                    color = bgMaskColor,
                                    topLeft = Offset(solidStart, 0f),
                                    size = Size(size.width - solidStart, size.height)
                                )
                            }
                        }
                    }
                }
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Dòng 2: "LockLock lo" (cỡ chữ 38.sp = gấp đôi 19.sp, ExtraBold nổi bật)
        BasicText(
            text = "LockLock lo",
            style = typography.display.copy(
                fontSize = 38.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textStrong,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .graphicsLayer {
                    val p = line2Progress.value
                    alpha = (p * 1.6f).coerceIn(0f, 1f)
                    val scale = 0.94f + 0.06f * p
                    scaleX = scale
                    scaleY = scale
                }
                .drawWithCache {
                    val featherPx = 48.dp.toPx()
                    val featherBrush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, bgMaskColor),
                        startX = 0f,
                        endX = featherPx
                    )
                    val featherSize = Size(featherPx, size.height)

                    onDrawWithContent {
                        drawContent()
                        val lp = line2Progress.value
                        if (lp < 1f) {
                            val totalW = size.width + featherPx
                            val startFade = (totalW * lp - featherPx).coerceAtLeast(0f)
                            withTransform({
                                translate(left = startFade, top = 0f)
                            }) {
                                drawRect(
                                    brush = featherBrush,
                                    topLeft = Offset.Zero,
                                    size = featherSize
                                )
                            }
                            val solidStart = startFade + featherPx
                            if (solidStart < size.width) {
                                drawRect(
                                    color = bgMaskColor,
                                    topLeft = Offset(solidStart, 0f),
                                    size = Size(size.width - solidStart, size.height)
                                )
                            }
                        }
                    }
                }
        )
    }
}

/**
 * Cảnh minh họa Onboarding (100% Hardware RenderNode Transform - 0 Canvas Redraws Per Frame):
 * - Cánh tay phải và Chìa khóa vàng được tách thành các lớp `graphicsLayer` riêng biệt.
 * - Các hình khối (tay, chìa khóa, két sắt, thân mascot) chỉ được vẽ vào DisplayList ĐÚNG 1 LẦN khi mở màn hình.
 * - Ở mỗi khung hình 120Hz, GPU RenderThread chỉ nhân ma trận xoay/dịch chuyển (`rotationZ`, `translationY`)
 *   với đường cong điều hòa `HarmonicSineEasing` mượt tuyệt đối.
 */
@Composable
fun WavingLockMascotWithSafe(
    modifier: Modifier = Modifier
) {
    val colors = SereneVaultTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "MascotSafeAnimation")

    // 1. Chuyển động vẫy tay phải (HarmonicSineEasing mượt 2 đầu đảo chiều)
    val waveAngleState = infiniteTransition.animateFloat(
        initialValue = -14f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 560, easing = HarmonicSineEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveArmAngle"
    )

    // 2. Độ lắc nhẹ của chiếc chìa khóa trên tay (HarmonicSineEasing không bị giật ở điểm đầu)
    val keySwingAngleState = infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 560, easing = HarmonicSineEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "KeySwingAngle"
    )

    // 3. Chuyển động nhấp nhô điều hòa của cơ thể nhân vật
    val bodyBounceYState = infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = HarmonicSineEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BodyBounceY"
    )

    // 4. Hiệu ứng chớp mắt tự nhiên mỗi 3.2 giây
    val eyeScaleY = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(3200L)
            eyeScaleY.animateTo(0.08f, animationSpec = tween(70, easing = HarmonicSineEasing))
            eyeScaleY.animateTo(1f, animationSpec = tween(110, easing = HarmonicSineEasing))
        }
    }

    Box(
        modifier = modifier.size(340.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vòng tròn hào quang nền Serene Vault dịu mắt (tĩnh 100%)
        val outerHaloColor = colors.secondary.copy(alpha = 0.05f)
        val innerHaloColor = colors.secondary.copy(alpha = 0.08f)
        Spacer(
            modifier = Modifier
                .size(300.dp)
                .drawBehind {
                    drawCircle(color = outerHaloColor)
                }
        )
        Spacer(
            modifier = Modifier
                .size(236.dp)
                .drawBehind {
                    drawCircle(color = innerHaloColor)
                }
        )

        // --- LỚP 1 (PHÍA SAU): Két sắt 3D (Tĩnh 100%) ---
        Image(
            painter = painterResource(id = R.drawable.ic_vault_safe),
            contentDescription = "Vault Safe",
            modifier = Modifier
                .size(268.dp)
                .offset(x = (-22).dp, y = (-26).dp)
        )

        // Bóng đổ dưới chân nhân vật (Tĩnh 100%)
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 28.dp, y = (-18).dp)
                .size(width = 118.dp, height = 20.dp)
                .drawBehind {
                    drawOval(
                        color = Color(0x331E293B),
                        topLeft = Offset.Zero,
                        size = size
                    )
                }
        )

        // --- LỚP 2 (PHÍA TRƯỚC): Nhân vật Mascot cầm chìa khóa vẫy tay ---
        Box(
            modifier = Modifier
                .size(210.dp)
                .offset(x = 28.dp, y = 44.dp)
                .graphicsLayer {
                    translationY = bodyBounceYState.value.dp.toPx()
                },
            contentAlignment = Alignment.Center
        ) {
            // 2A. Tay trái nhỏ nghỉ nhẹ bên hông trái (Tĩnh 100% - Vẽ đúng 1 lần duy nhất)
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        val w = size.width
                        val h = size.height
                        val mascotBlack = Color(0xFF050505)
                        val leftArmPivot = Offset(w * 0.22f, h * 0.56f)
                        val leftArmTopLeft = Offset(w * 0.07f, h * 0.52f)
                        val leftArmSize = Size(w * 0.18f, h * 0.105f)
                        val armCorner = CornerRadius(h * 0.06f, h * 0.06f)

                        onDrawBehind {
                            withTransform({
                                rotate(degrees = 20f, pivot = leftArmPivot)
                            }) {
                                drawRoundRect(
                                    color = mascotBlack,
                                    topLeft = leftArmTopLeft,
                                    size = leftArmSize,
                                    cornerRadius = armCorner
                                )
                            }
                        }
                    }
            )

            // 2B. Cụm Tay phải + Chìa khóa: Xoay bằng phần cứng GPU RenderNode (0 Canvas redraws!)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.74f, 0.50f)
                        rotationZ = waveAngleState.value - 34f
                    }
            ) {
                // Cánh tay phải (Vẽ đúng 1 lần duy nhất vào RenderNode)
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithCache {
                            val w = size.width
                            val h = size.height
                            val mascotBlack = Color(0xFF050505)
                            val rightArmTopLeft = Offset(w * 0.70f, h * 0.43f)
                            val rightArmSize = Size(w * 0.23f, h * 0.11f)
                            val armCorner = CornerRadius(h * 0.06f, h * 0.06f)

                            onDrawBehind {
                                drawRoundRect(
                                    color = mascotBlack,
                                    topLeft = rightArmTopLeft,
                                    size = rightArmSize,
                                    cornerRadius = armCorner
                                )
                            }
                        }
                )

                // Chìa khóa vàng (RenderNode con độc lập: Vẽ đúng 1 lần, xoay bằng ma trận GPU)
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            transformOrigin = TransformOrigin(0.8978f, 0.485f)
                            rotationZ = keySwingAngleState.value + 42f
                        }
                        .drawWithCache {
                            val w = size.width
                            val h = size.height
                            val mascotBlack = Color(0xFF050505)
                            val keyGold = Color(0xFFF59E0B)
                            val keyGoldHighlight = Color(0xFFFDE68A)
                            val keyOutline = Color(0xFF1E293B)

                            val handGrip = Offset(
                                x = w * 0.8978f,
                                y = h * 0.485f
                            )
                            val keyStroke = Stroke(width = 2.2.dp.toPx())
                            val shaftWidth = w * 0.036f
                            val shaftLength = w * 0.19f
                            val shaftLeft = handGrip.x - shaftWidth / 2f
                            val shaftTop = handGrip.y - shaftLength
                            val shaftTopLeft = Offset(shaftLeft, shaftTop)
                            val shaftSize = Size(shaftWidth, shaftLength)
                            val shaftCorner = CornerRadius(shaftWidth / 2f, shaftWidth / 2f)

                            val toothWidth = w * 0.055f
                            val toothHeight = w * 0.026f
                            val tooth1TopLeft = Offset(shaftLeft + shaftWidth * 0.6f, shaftTop + w * 0.015f)
                            val tooth1Size = Size(toothWidth, toothHeight)
                            val tooth2TopLeft = Offset(shaftLeft + shaftWidth * 0.6f, shaftTop + w * 0.055f)
                            val tooth2Size = Size(toothWidth * 0.82f, toothHeight)
                            val toothCorner = CornerRadius(3.dp.toPx(), 3.dp.toPx())

                            val bowRadius = w * 0.058f
                            val bowCenter = Offset(handGrip.x, handGrip.y + bowRadius * 0.45f)
                            val bowHighlightRadius = bowRadius * 0.72f
                            val bowHoleRadius = bowRadius * 0.34f

                            onDrawBehind {
                                // Răng 1
                                drawRoundRect(
                                    color = keyGold,
                                    topLeft = tooth1TopLeft,
                                    size = tooth1Size,
                                    cornerRadius = toothCorner
                                )
                                drawRoundRect(
                                    color = keyOutline,
                                    topLeft = tooth1TopLeft,
                                    size = tooth1Size,
                                    cornerRadius = toothCorner,
                                    style = keyStroke
                                )

                                // Răng 2
                                drawRoundRect(
                                    color = keyGold,
                                    topLeft = tooth2TopLeft,
                                    size = tooth2Size,
                                    cornerRadius = toothCorner
                                )
                                drawRoundRect(
                                    color = keyOutline,
                                    topLeft = tooth2TopLeft,
                                    size = tooth2Size,
                                    cornerRadius = toothCorner,
                                    style = keyStroke
                                )

                                // Thân chính của chìa khóa
                                drawRoundRect(
                                    color = keyGold,
                                    topLeft = shaftTopLeft,
                                    size = shaftSize,
                                    cornerRadius = shaftCorner
                                )
                                drawRoundRect(
                                    color = keyOutline,
                                    topLeft = shaftTopLeft,
                                    size = shaftSize,
                                    cornerRadius = shaftCorner,
                                    style = keyStroke
                                )

                                // Đầu tròn của chìa khóa (Key Bow)
                                drawCircle(
                                    color = keyGold,
                                    radius = bowRadius,
                                    center = bowCenter
                                )
                                drawCircle(
                                    color = keyGoldHighlight,
                                    radius = bowHighlightRadius,
                                    center = bowCenter
                                )
                                drawCircle(
                                    color = keyOutline,
                                    radius = bowRadius,
                                    center = bowCenter,
                                    style = keyStroke
                                )
                                drawCircle(
                                    color = mascotBlack,
                                    radius = bowHoleRadius,
                                    center = bowCenter
                                )
                            }
                        }
                )
            }

            // 2C. Thân nhân vật gốc (ic_mascot_body) - Tĩnh 100%
            Image(
                painter = painterResource(id = R.drawable.ic_mascot_body),
                contentDescription = "LockLock Mascot",
                modifier = Modifier.size(168.dp)
            )

            // 2D. Đôi mắt trắng: Tĩnh 100%, chớp mắt bằng ma trận scaleY của GPU RenderNode (0 Canvas redraws!)
            Spacer(
                modifier = Modifier
                    .size(168.dp)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 179f / 500f)
                        scaleY = eyeScaleY.value
                    }
                    .drawWithCache {
                        val canvasW = size.width
                        val canvasH = size.height
                        val eyeRadius = canvasW * (23.5f / 500f)
                        val eyeDiameter = eyeRadius * 2f
                        val leftEyeX = canvasW * (202.5f / 500f) - eyeRadius
                        val rightEyeX = canvasW * (297.5f / 500f) - eyeRadius
                        val eyeTopY = canvasH * (179.0f / 500f) - eyeRadius
                        val eyeSize = Size(width = eyeDiameter, height = eyeDiameter)

                        onDrawBehind {
                            drawOval(
                                color = Color.White,
                                topLeft = Offset(x = leftEyeX, y = eyeTopY),
                                size = eyeSize
                            )
                            drawOval(
                                color = Color.White,
                                topLeft = Offset(x = rightEyeX, y = eyeTopY),
                                size = eyeSize
                            )
                        }
                    }
            )
        }
    }
}
