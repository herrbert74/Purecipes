package app.purecipes.feature.onboarding.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.absoluteValue

internal const val ONBOARDING_NEXT_BUTTON_TAG = "onboardingNextButton"

private const val FILL_TRIGGER_THRESHOLD = 0.2f
private const val FILL_DURATION_MILLIS = 430

/**
 * The circular next button that morphs into the reveal oval of the arriving page.
 *
 * While the reveal oval is still small the button paints itself in the neighbouring page's
 * background colour, so it reads as a hole punched through to the next page rather than a button
 * sitting on top of it.
 *
 * Every page owns a button, but only the current page's one is clickable and exposed to
 * accessibility, so retained neighbour pages do not announce a second "Next".
 */
@Composable
internal fun BoxScope.OnboardingNextButton(
	pagerState: PagerState,
	page: Int,
	state: OnboardingNextButtonState,
	onClick: () -> Unit,
) {
	val isFilled by remember(page, pagerState) {
		derivedStateOf {
			page == pagerState.settledPage ||
				pagerState.offsetForPage(page).absoluteValue < FILL_TRIGGER_THRESHOLD
		}
	}
	val fillProgress by animateFloatAsState(
		targetValue = if (isFilled) 1f else 0f,
		animationSpec = tween(durationMillis = FILL_DURATION_MILLIS, easing = FastOutSlowInEasing),
		label = "onboardingNextButtonFill",
	)
	val isActive by remember(page, pagerState) {
		derivedStateOf { page == pagerState.currentPage }
	}
	val label = if (state.isLastPage) "Start cooking" else "Next"

	Box(
		modifier = Modifier
			.align(Alignment.BottomCenter)
			.padding(bottom = ONBOARDING_NEXT_BUTTON_BOTTOM_PADDING)
			.size(ONBOARDING_NEXT_BUTTON_SIZE)
			.graphicsLayer {
				val transform = pagerState.nextButtonTransform(page = page, geometry = state.geometry)
				if (transform == null) {
					alpha = 0f
					return@graphicsLayer
				}
				alpha = 1f
				translationX = transform.translationX
				translationY = transform.translationY
				scaleX = transform.scaleX
				scaleY = transform.scaleY
			}
			.clip(CircleShape)
			.drawBehind {
				val camouflage = if (pagerState.offsetForPage(page) < 0f) {
					state.previousBackground
				} else {
					state.nextBackground
				}
				val radius = size.width / 2f
				when {
					fillProgress >= 1f -> drawCircle(color = state.fillColor)
					fillProgress <= 0f -> drawCircle(color = camouflage)
					else -> {
						drawCircle(color = camouflage)
						val strokeWidth = radius * fillProgress
						drawCircle(
							color = state.fillColor,
							radius = radius - strokeWidth / 2f,
							style = Stroke(width = strokeWidth),
						)
					}
				}
			}
			.then(
				if (isActive) {
					Modifier
						.clickable(onClick = onClick)
						.testTag(ONBOARDING_NEXT_BUTTON_TAG)
						.semantics { contentDescription = label }
				} else {
					Modifier
				},
			),
	) {
		OnboardingNextButtonIcon(
			pagerState = pagerState,
			page = page,
			tint = state.iconColor,
			modifier = Modifier.align(Alignment.Center),
		)
	}
}
