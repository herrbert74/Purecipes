package app.purecipes.feature.onboarding.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Colours and geometry the morphing next button needs.
 *
 * [previousBackground] and [nextBackground] let the button camouflage itself against the page it is
 * melting into while the reveal oval is still small.
 */
@Immutable
internal data class OnboardingNextButtonState(
	val fillColor: Color,
	val iconColor: Color,
	val previousBackground: Color,
	val nextBackground: Color,
	val geometry: LiquidSwipeGeometry,
	val isLastPage: Boolean,
)
