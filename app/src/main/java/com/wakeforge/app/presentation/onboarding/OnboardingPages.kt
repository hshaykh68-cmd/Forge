package com.wakeforge.app.presentation.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakeforge.app.R
import com.wakeforge.app.core.theme.LocalWakeForgeColors
import com.wakeforge.app.core.theme.PrimaryAccent
import com.wakeforge.app.core.theme.SecondaryAccent
import com.wakeforge.app.core.theme.Success
import com.wakeforge.app.core.theme.Warning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data class representing a single onboarding page in the premium 5-page flow.
 *
 * @property titleRes The page title string resource ID.
 * @property descriptionRes The page description string resource ID.
 * @property hintRes Optional hint text resource ID for additional guidance.
 * @property illustration Composable function that renders the animated illustration.
 */
data class OnboardingPage(
    val titleRes: Int,
    val descriptionRes: Int,
    val hintRes: Int? = null,
    val illustration: @Composable () -> Unit,
)

/**
 * Returns the list of 5 premium onboarding pages with cinematic animated illustrations.
 */
fun getOnboardingPages(): List<OnboardingPage> = listOf(
    OnboardingPage(
        titleRes = R.string.onboarding_title_1,
        descriptionRes = R.string.onboarding_description_1,
        hintRes = R.string.onboarding_hint_1,
        illustration = { WelcomeLogoIllustration() },
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_2,
        descriptionRes = R.string.onboarding_description_2,
        hintRes = R.string.onboarding_hint_2,
        illustration = { MissionPreviewIllustration() },
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_3,
        descriptionRes = R.string.onboarding_description_3,
        hintRes = R.string.onboarding_hint_3,
        illustration = { StatsShowcaseIllustration() },
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_4,
        descriptionRes = R.string.onboarding_description_4,
        hintRes = R.string.onboarding_hint_4,
        illustration = { SmartFeaturesIllustration() },
    ),
    OnboardingPage(
        titleRes = R.string.onboarding_title_5,
        descriptionRes = R.string.onboarding_description_5,
        hintRes = null,
        illustration = { ReadyCelebrationIllustration() },
    ),
)

// ═══════════════════════════════════════════════════════════════════════════════
// Premium Animated Illustrations
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Page 1: Welcome - Animated logo reveal with pulsing glow and floating particles.
 */
@Composable
private fun WelcomeLogoIllustration() {
    val colors = LocalWakeForgeColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "welcome")

    // Logo scale animation
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "logoScale",
    )

    // Glow alpha animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )

    // Ring rotation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringRotation",
    )

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Outer rotating ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = size.width * 0.45f

            drawCircle(
                color = colors.primaryAccent.copy(alpha = 0.2f),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx()),
            )

            // Rotating arc segments
            val segmentCount = 3
            for (i in 0 until segmentCount) {
                val startAngle = ringRotation + i * (360f / segmentCount)
                drawArc(
                    color = colors.primaryAccent.copy(alpha = 0.6f * glowAlpha),
                    startAngle = startAngle,
                    sweepAngle = 30f,
                    useCenter = false,
                    topLeft = Offset(cx - radius, cy - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                )
            }
        }

        // Glow effect
        Box(
            modifier = Modifier
                .size(180.dp)
                .graphicsLayer {
                    alpha = glowAlpha
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.primaryAccent.copy(alpha = 0.4f),
                            colors.primaryAccent.copy(alpha = 0.1f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        // Logo text with scale
        Column(
            modifier = Modifier.scale(logoScale),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "WAKE",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.primaryText,
                letterSpacing = 4.sp,
            )
            Text(
                text = "FORGE",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.primaryAccent,
                letterSpacing = 4.sp,
            )
        }
    }
}

/**
 * Page 2: Mission Preview - Interactive preview of alarm missions with animated icons.
 */
@Composable
private fun MissionPreviewIllustration() {
    val colors = LocalWakeForgeColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "mission")

    // Bounce animations for different mission icons
    val bounce1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, delayMillis = 0, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bounce1",
    )

    val bounce2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bounce2",
    )

    val bounce3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bounce3",
    )

    Column(
        modifier = Modifier.size(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Math mission icon
            MissionIcon(
                color = PrimaryAccent,
                bounce = bounce1,
                symbol = "×",
                symbolColor = Color.White,
            )

            // Memory mission icon
            MissionIcon(
                color = SecondaryAccent,
                bounce = bounce2,
                symbol = "◆",
                symbolColor = Color.White,
            )

            // Shake mission icon
            MissionIcon(
                color = Success,
                bounce = bounce3,
                symbol = "⇡",
                symbolColor = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Animated challenge text
        val challengeAlpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "challengeAlpha",
        )

        Text(
            text = "7 × 8 = ?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colors.primaryAccent.copy(alpha = challengeAlpha),
        )
    }
}

@Composable
private fun MissionIcon(
    color: Color,
    bounce: Float,
    symbol: String,
    symbolColor: Color,
) {
    val colors = LocalWakeForgeColors.current

    Box(
        modifier = Modifier
            .size(70.dp)
            .offset(y = bounce.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.8f),
                        color.copy(alpha = 0.4f),
                    ),
                ),
                shape = RoundedCornerShape(20.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = symbolColor,
        )
    }
}

/**
 * Page 3: Stats Showcase - Animated counter and achievement visualization.
 */
@Composable
private fun StatsShowcaseIllustration() {
    val colors = LocalWakeForgeColors.current
    val coroutineScope = rememberCoroutineScope()

    val counterAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            delay(300)
            counterAnim.animateTo(
                targetValue = 30f,
                animationSpec = tween(2000, easing = FastOutSlowInEasing),
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stats")

    // Fire animation
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "fireScale",
    )

    Column(
        modifier = Modifier.size(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Streak counter with fire
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Outer glow ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Warning.copy(alpha = 0.3f),
                            Warning.copy(alpha = 0.1f),
                            Color.Transparent,
                        ),
                        center = Offset(cx, cy),
                        radius = size.width * 0.5f,
                    ),
                    radius = size.width * 0.5f,
                    center = Offset(cx, cy),
                )
            }

            // Counter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${counterAnim.value.toInt()}",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.primaryText,
                )
                Text(
                    text = "day streak",
                    fontSize = 14.sp,
                    color = colors.secondaryText,
                )
            }

            // Fire emoji/icon positioned
            Text(
                text = "🔥",
                fontSize = 32.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .scale(fireScale)
                    .padding(8.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Achievement badges
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AchievementBadge(color = PrimaryAccent, icon = "★")
            AchievementBadge(color = Success, icon = "🏆")
            AchievementBadge(color = SecondaryAccent, icon = "⚡")
        }
    }
}

@Composable
private fun AchievementBadge(color: Color, icon: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = color.copy(alpha = 0.2f),
                shape = CircleShape,
            )
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = icon,
            fontSize = 24.sp,
        )
    }
}

/**
 * Page 4: Smart Features - Visualization of smart alarm features.
 */
@Composable
private fun SmartFeaturesIllustration() {
    val colors = LocalWakeForgeColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "smart")

    // Volume animation
    val volumeProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "volume",
    )

    // Clock pulse
    val clockPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "clockPulse",
    )

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Central alarm clock
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(clockPulse)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.primaryAccent.copy(alpha = 0.3f),
                            colors.primaryAccent.copy(alpha = 0.1f),
                        ),
                    ),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "⏰",
                fontSize = 48.sp,
            )
        }

        // Volume bars around
        VolumeBars(
            modifier = Modifier.align(Alignment.CenterStart),
            progress = volumeProgress,
            color = Success,
        )

        VolumeBars(
            modifier = Modifier.align(Alignment.CenterEnd),
            progress = volumeProgress,
            color = Warning,
        )
    }
}

@Composable
private fun VolumeBars(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(5) { index ->
            val barHeight = (index + 1) * 8
            val isActive = progress > (index / 5f)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight.dp)
                    .background(
                        color = if (isActive) color else color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
        }
    }
}

/**
 * Page 5: Ready Celebration - Confetti and celebration animation.
 */
@Composable
private fun ReadyCelebrationIllustration() {
    val colors = LocalWakeForgeColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")

    // Confetti animation values
    val confettiPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "confetti",
    )

    // Trophy bounce
    val trophyScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "trophy",
    )

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Confetti particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = Random(42)
            repeat(30) { i ->
                val x = (size.width * (i / 30f) + confettiPhase * 50f) % size.width
                val y = (size.height * 0.3f + (i * 10f) + confettiPhase * 100f) % size.height
                val color = listOf(
                    PrimaryAccent,
                    SecondaryAccent,
                    Success,
                    Warning,
                )[i % 4]

                drawCircle(
                    color = color.copy(alpha = 0.6f),
                    radius = 4.dp.toPx(),
                    center = Offset(x, y),
                )
            }
        }

        // Trophy with glow
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "🏆",
                fontSize = 80.sp,
                modifier = Modifier.scale(trophyScale),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You're Ready!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primaryAccent,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Shared Animation Components
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Floating background particles for premium visual effect.
 */
@Composable
fun FloatingParticles() {
    val colors = LocalWakeForgeColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    // Create multiple particles with different animation phases
    val particles = remember {
        List(20) { index ->
            ParticleData(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat(),
                speed = 0.0005f + Random.nextFloat() * 0.001f,
                size = 2f + Random.nextFloat() * 4f,
                alpha = 0.1f + Random.nextFloat() * 0.3f,
                phase = Random.nextFloat() * 2f * PI.toFloat(),
            )
        }
    }

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "particleTime",
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = (particle.initialX + (time * particle.speed) + sin(time + particle.phase) * 0.1f) * size.width
            val y = (particle.initialY + (time * particle.speed * 0.5f) + cos(time * 0.7f + particle.phase) * 0.1f) * size.height

            drawCircle(
                color = colors.primaryAccent.copy(alpha = particle.alpha),
                radius = particle.size.dp.toPx(),
                center = Offset(
                    x.coerceIn(0f, size.width),
                    y.coerceIn(0f, size.height),
                ),
            )
        }
    }
}

private data class ParticleData(
    val initialX: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val phase: Float,
)
