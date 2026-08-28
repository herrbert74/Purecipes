package app.purecipes.feature.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun rememberOnboardingNextButtonState(
	pages: ImmutableList<OnboardingPage>,
	page: Int,
	viewportWidth: Float,
	viewportHeight: Float,
): OnboardingNextButtonState {
	val density = LocalDensity.current
	val buttonRadius = remember(density) {
		with(density) { (ONBOARDING_NEXT_BUTTON_SIZE / 2).toPx() }
	}
	return remember(pages, page, viewportWidth, viewportHeight, buttonRadius) {
		val item = pages[page]
		OnboardingNextButtonState(
			fillColor = item.accentColor,
			iconColor = item.backgroundColor,
			previousBackground = pages[(page - 1).coerceAtLeast(0)].backgroundColor,
			nextBackground = pages[(page + 1).coerceAtMost(pages.lastIndex)].backgroundColor,
			geometry = LiquidSwipeGeometry(
				width = viewportWidth,
				height = viewportHeight,
				buttonRadius = buttonRadius,
			),
			isLastPage = page == pages.lastIndex,
		)
	}
}
