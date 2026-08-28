package app.purecipes.feature.onboarding.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private const val INDICATOR_ANIMATION_MILLIS = 300
private const val INDICATOR_INACTIVE_ALPHA = 0.35f

private val INDICATOR_DOT_SIZE = 8.dp
private val INDICATOR_ACTIVE_DOT_WIDTH = 26.dp
private val INDICATOR_DOT_GAP = 8.dp

@Composable
internal fun OnboardingPageIndicator(
	pageCount: Int,
	currentPage: Int,
	color: Color,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(INDICATOR_DOT_GAP),
	) {
		repeat(pageCount) { index ->
			val isActive = index == currentPage
			val dotWidth by animateDpAsState(
				targetValue = if (isActive) INDICATOR_ACTIVE_DOT_WIDTH else INDICATOR_DOT_SIZE,
				animationSpec = tween(durationMillis = INDICATOR_ANIMATION_MILLIS),
				label = "onboardingIndicatorWidth",
			)
			val dotColor by animateColorAsState(
				targetValue = if (isActive) color else color.copy(alpha = INDICATOR_INACTIVE_ALPHA),
				animationSpec = tween(durationMillis = INDICATOR_ANIMATION_MILLIS),
				label = "onboardingIndicatorColor",
			)
			Box(
				modifier = Modifier
					.width(dotWidth)
					.height(INDICATOR_DOT_SIZE)
					.clip(CircleShape)
					.background(dotColor),
			)
		}
	}
}
