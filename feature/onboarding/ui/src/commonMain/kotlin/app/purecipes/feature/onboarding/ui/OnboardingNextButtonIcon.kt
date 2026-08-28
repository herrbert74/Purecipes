package app.purecipes.feature.onboarding.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

private const val ICON_HIDE_THRESHOLD = 0.15f
private const val ICON_SHRUNK_SCALE = 0.7f
private const val ICON_VISIBILITY_CUTOFF = 0.05f
private const val ICON_TUCKED_ROTATION = -45f
private const val ICON_SCALE_DAMPING = 0.95f
private const val ICON_ROTATION_DAMPING = 0.98f
private const val ICON_ROTATION_STIFFNESS = 180f

/**
 * The chevron inside the next button. It tucks away as soon as the button starts morphing so the
 * oval reads as liquid rather than as a stretched button.
 */
@Composable
internal fun OnboardingNextButtonIcon(
	pagerState: PagerState,
	page: Int,
	tint: Color,
	modifier: Modifier = Modifier,
) {
	val targetScale by remember(page, pagerState) {
		derivedStateOf {
			val isOutgoing = page == pagerState.settledPage
			val progress = pagerState.liquidSwipeProgress(page)
			when {
				isOutgoing && !pagerState.isScrollInProgress -> 1f
				isOutgoing && progress < ICON_HIDE_THRESHOLD -> ICON_SHRUNK_SCALE
				!isOutgoing && progress > 1f - ICON_HIDE_THRESHOLD ->
					(progress - (1f - ICON_HIDE_THRESHOLD)) / ICON_HIDE_THRESHOLD

				else -> 0f
			}.coerceIn(0f, 1f)
		}
	}
	val iconScale by animateFloatAsState(
		targetValue = targetScale,
		animationSpec = spring(
			dampingRatio = ICON_SCALE_DAMPING,
			stiffness = Spring.StiffnessLow,
		),
		label = "onboardingNextButtonIconScale",
	)
	val isSettled by remember(page, pagerState) {
		derivedStateOf { page == pagerState.settledPage && !pagerState.isScrollInProgress }
	}
	val iconRotation by animateFloatAsState(
		targetValue = if (isSettled) 0f else ICON_TUCKED_ROTATION,
		animationSpec = spring(
			dampingRatio = ICON_ROTATION_DAMPING,
			stiffness = ICON_ROTATION_STIFFNESS,
		),
		label = "onboardingNextButtonIconRotation",
	)

	Icon(
		imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
		contentDescription = null,
		tint = tint,
		modifier = modifier.graphicsLayer {
			alpha = if (iconScale > ICON_VISIBILITY_CUTOFF) iconScale else 0f
			scaleX = iconScale
			scaleY = iconScale
			rotationZ = iconRotation
		},
	)
}
