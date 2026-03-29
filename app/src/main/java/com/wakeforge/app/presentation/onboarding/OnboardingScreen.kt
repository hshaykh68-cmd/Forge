package com.wakeforge.app.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wakeforge.app.R
import com.wakeforge.app.core.components.WFButton
import com.wakeforge.app.core.components.ButtonType
import com.wakeforge.app.core.theme.LocalWakeForgeColors
import com.wakeforge.app.core.theme.LocalWakeForgeTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Premium onboarding screen with cinematic 5-page flow.
 *
 * Features:
 * - Animated page transitions with slide/fade effects
 * - Premium gradient backgrounds with floating particles
 * - Interactive mission previews
 * - Glassmorphism cards and shimmer effects
 * - Celebration animations on completion
 *
 * @param navController Controller for navigation after onboarding.
 * @param viewModel The [OnboardingViewModel] managing onboarding state.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val colors = LocalWakeForgeColors.current
    val typography = LocalWakeForgeTypography.current
    val coroutineScope = rememberCoroutineScope()

    var currentPage by remember { mutableStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }
    val pages = remember { getOnboardingPages() }

    // Listen for completion / skip events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is OnboardingViewModel.Event.Complete,
                is OnboardingViewModel.Event.Skip -> {
                    navController.navigate("permissions") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            }
        }
    }

    // Page transition animation
    val transitionSpec = remember {
        { direction: Int ->
            if (direction > 0) {
                // Forward navigation
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)) with
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            } else {
                // Backward navigation (not used but defined for completeness)
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)) with
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.background,
                        colors.background,
                        colors.primaryAccent.copy(alpha = 0.05f),
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY,
                )
            ),
    ) {
        // Animated background particles
        FloatingParticles()

        // Skip button (hidden on last page)
        if (currentPage < pages.size - 1) {
            TextButton(
                onClick = { viewModel.skip() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .alpha(0.7f),
            ) {
                Text(
                    text = stringResource(id = R.string.onboarding_skip),
                    style = typography.labelLarge,
                    color = colors.secondaryText,
                )
            }
        }

        // Page indicator at top
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            pages.forEachIndexed { index, _ ->
                val isActive = index == currentPage
                val infiniteTransition = rememberInfiniteTransition(label = "dotPulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = if (isActive) 1f else 0.8f,
                    targetValue = if (isActive) 1.2f else 0.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "dotScale",
                )

                Box(
                    modifier = Modifier
                        .size(if (isActive) 12.dp else 8.dp)
                        .scale(if (isActive) scale else 1f)
                        .clip(CircleShape)
                        .background(
                            if (isActive) colors.primaryAccent
                            else colors.secondaryText.copy(alpha = 0.3f),
                        ),
                )
            }
        }

        // Animated content area
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                transitionSpec(direction)
            },
            modifier = Modifier.fillMaxSize(),
            label = "pageContent",
        ) { pageIndex ->
            val page = pages[pageIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Premium animated illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    page.illustration()
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title with shimmer effect on first page
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = page.titleRes),
                        style = typography.headlineLarge,
                        color = colors.primaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description with enhanced typography
                Text(
                    text = stringResource(id = page.descriptionRes),
                    style = typography.bodyLarge,
                    color = colors.secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Page hint text
                page.hintRes?.let { hintRes ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = hintRes),
                        style = typography.labelMedium,
                        color = colors.primaryAccent.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Action buttons area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val isLastPage = currentPage == pages.size - 1

                    WFButton(
                        text = when {
                            isLastPage -> stringResource(id = R.string.onboarding_get_started)
                            currentPage == 0 -> stringResource(id = R.string.onboarding_begin_journey)
                            else -> stringResource(id = R.string.onboarding_next)
                        },
                        onClick = {
                            if (isAnimating) return@WFButton
                            isAnimating = true

                            if (isLastPage) {
                                viewModel.complete()
                            } else {
                                coroutineScope.launch {
                                    delay(100) // Small delay for button press animation
                                    currentPage += 1
                                    isAnimating = false
                                }
                            }
                        },
                        type = if (isLastPage) ButtonType.Primary else ButtonType.Primary,
                        fullWidth = true,
                    )

                    // Secondary action on some pages
                    if (currentPage == 2 || currentPage == 3) {
                        Spacer(modifier = Modifier.height(12.dp))
                        WFButton(
                            text = stringResource(id = R.string.onboarding_see_more),
                            onClick = { /* Preview features */ },
                            type = ButtonType.Ghost,
                            fullWidth = false,
                        )
                    }
                }
            }
        }
    }
}
