package app.purecipes.feature.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

internal const val ONBOARDING_SKIP_TAG = "onboardingSkip"

private val CHROME_PADDING = 8.dp
private val INDICATOR_BOTTOM_PADDING = 72.dp

/**
 * Skip action and page indicator, kept outside the pager so they do not travel with the liquid
 * swipe transition and stay tappable throughout it.
 */
@Composable
internal fun OnboardingChrome(
	pages: ImmutableList<OnboardingPage>,
	currentPage: Int,
	onSkip: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val page = pages[currentPage.coerceIn(0, pages.lastIndex)]
	Column(
		modifier = modifier
			.fillMaxSize()
			.safeDrawingPadding()
			.padding(CHROME_PADDING),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween,
	) {
		Box(modifier = Modifier.fillMaxWidth()) {
			if (currentPage != pages.lastIndex) {
				TextButton(
					onClick = onSkip,
					modifier = Modifier
						.align(Alignment.TopEnd)
						.testTag(ONBOARDING_SKIP_TAG),
				) {
					Text(
						text = "Skip",
						style = PurecipesTheme.typography.labelLarge,
						color = page.contentColor,
					)
				}
			}
		}
		OnboardingPageIndicator(
			pageCount = pages.size,
			currentPage = currentPage,
			color = page.contentColor,
			modifier = Modifier.padding(bottom = INDICATOR_BOTTOM_PADDING),
		)
	}
}
