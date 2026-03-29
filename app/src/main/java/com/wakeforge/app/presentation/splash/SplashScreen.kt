package com.wakeforge.app.presentation.splash

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wakeforge.app.core.theme.LocalWakeForgeColors
import com.wakeforge.app.core.theme.LocalWakeForgeTypography
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Premium cinematic splash screen with logo reveal animation.
 *
 * Features:
 * - Animated logo reveal with scale and alpha transitions
 * - Pulsing glow effect behind the logo
 * - Floating geometric particles in background
 * - Tagline with typewriter-like reveal
 * - Smooth fade-out before navigation
 *
 * @param navController Controller for navigation after splash completes.
 * @param viewModel The [SplashViewModel] managing splash state.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val colors = LocalWakeForgeColors.current
    val typography = LocalWakeForgeTypography.current
    val splashState by viewModel.state.collectAsStateWithLifecycle()

    // Animation states
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val screenAlpha = remember { Animatable(1f) }
    var hasNavigated by remember { mutableStateOf(false) }

    // Infinite animation for background
    val infiniteTransition = rememberInfiniteTransition(label = "splashBackground")

    // Rotating ring animation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringRotation",
    )

    // Pulsing glow
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowPulse",
    )

    // Trigger entrance animations
    LaunchedEffect(Unit) {
        // Logo reveal
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing),
        )
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing),
        )

        // Tagline fade in
        delay(400)
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing),
        )
    }

    // Handle navigation with fade-out
    LaunchedEffect(splashState) {
        if (splashState is SplashViewModel.SplashUiState.Navigate && !hasNavigated) {
            val destination = (splashState as SplashViewModel.SplashUiState.Navigate).destination

            // Fade out
            screenAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(400, easing = FastOutSlowInEasing),
            )

            hasNavigated = true

            // Navigate and remove splash from back stack
            navController.navigate(destination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(screenAlpha.value)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.background,
                        colors.background,
                        colors.primaryAccent.copy(alpha = 0.08f),
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Background geometric patterns
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Outer rotating dotted ring
            val dotCount = 12
            val ringRadius = size.width * 0.4f
            for (i in 0 until dotCount) {
                val angle = (ringRotation + i * (360f / dotCount)) * PI.toFloat() / 180f
                val x = cx + cos(angle) * ringRadius
                val y = cy + sin(angle) * ringRadius
                drawCircle(
                    color = colors.primaryAccent.copy(alpha = 0.15f),
                    radius = 3.dp.toPx(),
                    center = Offset(x, y),
                )
            }

            // Inner concentric circles with gradient alpha
            repeat(4) { i ->
                val radius = size.width * (0.15f + i * 0.08f)
                drawCircle(
                    color = colors.primaryAccent.copy(alpha = 0.03f * (4 - i)),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                )
            }
        }

        // Floating particles
        FloatingSplashParticles()

        // Central logo content
        Column(
            modifier = Modifier
                .scale(logoScale.value)
                .alpha(logoAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Glow behind logo
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                colors.primaryAccent.copy(alpha = glowAlpha),
                                colors.primaryAccent.copy(alpha = glowAlpha * 0.3f),
                                Color.Transparent,
                            ),
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                // Logo text with premium styling
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "WAKE",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primaryText,
                        letterSpacing = 6.sp,
                    )
                    Text(
                        text = "FORGE",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colors.primaryAccent,
                        letterSpacing = 6.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tagline with reveal animation
            Text(
                text = "Forge Your Mornings",
                style = typography.bodyLarge.copy(
                    color = colors.secondaryText.copy(alpha = taglineAlpha.value * 0.8f),
                    letterSpacing = 2.sp,
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Premium badge
            Text(
                text = "PREMIUM",
                style = typography.labelMedium.copy(
                    color = colors.primaryAccent.copy(alpha = taglineAlpha.value * 0.6f),
                    letterSpacing = 4.sp,
                ),
            )
        }
    }
}

/**
 * Floating particles for splash background.
 */
@Composable
private fun FloatingSplashParticles() {
    val colors = LocalWakeForgeColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "splashParticles")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "particleTime",
    )

    // Create fewer, more subtle particles for splash
    val particles = remember {
        List(15) { index ->
            SplashParticleData(
                initialX = 0.1f + Random.nextFloat() * 0.8f,
                initialY = 0.1f + Random.nextFloat() * 0.8f,
                speed = 0.0003f + Random.nextFloat() * 0.0005f,
                size = 2f + Random.nextFloat() * 3f,
                phase = Random.nextFloat() * 2f * PI.toFloat(),
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = (particle.initialX + sin(time * 0.5f + particle.phase) * 0.1f) * size.width
            val y = (particle.initialY + (time * particle.speed) + cos(time * 0.3f + particle.phase) * 0.05f) * size.height % size.height

            drawCircle(
                color = colors.secondaryAccent.copy(alpha = 0.2f),
                radius = particle.size.dp.toPx(),
                center = Offset(
                    x.coerceIn(0f, size.width),
                    y.coerceIn(0f, size.height),
                ),
            )
        }
    }
}

private data class SplashParticleData(
    val initialX: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val phase: Float,
)
