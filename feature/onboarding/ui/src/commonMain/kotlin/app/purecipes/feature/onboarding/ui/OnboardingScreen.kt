package app.purecipes.feature.onboarding.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import app.purecipes.shared.ui.component.HandleSystemBack
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

private const val SPARKLE_CYCLE = 10f
private const val SPARKLE_DURATION_MILLIS = 10_000
private const val SHEEN_DURATION_MILLIS = 2_500
private const val NEXT_PAGE_STIFFNESS = 140f
private const val ARRIVING_PAGE_Z_INDEX = 1f
private const val CHROME_Z_INDEX = 2f

/**
 * First launch walkthrough of what Purecipes is for, using the liquid swipe transition from the
 * reference design: the next button melts into an oval that reveals the arriving page.
 *
 * Swiping and the next button both advance, and skipping is available until the last page.
 * [onFinish] reports whether the user skipped so the caller can persist completion and report it.
 */
@Composable
fun OnboardingScreen(
	onFinish: (OnboardingOutcome) -> Unit,
	modifier: Modifier = Modifier,
	pages: ImmutableList<OnboardingPage> = onboardingPages,
) {
	val pagerState = rememberPagerState(pageCount = { pages.size })
	val coroutineScope = rememberCoroutineScope()
	val currentOnFinish by rememberUpdatedState(onFinish)
	var viewportWidth by remember { mutableFloatStateOf(0f) }
	var viewportHeight by remember { mutableFloatStateOf(0f) }

	val decorations = rememberInfiniteTransition(label = "onboardingDecorations")
	val sparkleTime = decorations.animateFloat(
		initialValue = 0f,
		targetValue = SPARKLE_CYCLE,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = SPARKLE_DURATION_MILLIS, easing = LinearEasing),
			repeatMode = RepeatMode.Restart,
		),
		label = "onboardingSparkleTime",
	)
	val sheenSweep = decorations.animateFloat(
		initialValue = 0f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = SHEEN_DURATION_MILLIS, easing = LinearEasing),
			repeatMode = RepeatMode.Restart,
		),
		label = "onboardingSheenSweep",
	)

	HandleSystemBack(enabled = true) {
		val target = pagerState.currentPage - 1
		if (target >= 0) {
			coroutineScope.launch { pagerState.animateScrollToPage(target) }
		}
	}

	Box(
		modifier = modifier
			.fillMaxSize()
			.onSizeChanged { size ->
				viewportWidth = size.width.toFloat()
				viewportHeight = size.height.toFloat()
			},
	) {
		HorizontalPager(
			state = pagerState,
			modifier = Modifier.fillMaxSize(),
		) { page ->
			val isArriving = page != pagerState.settledPage
			Box(
				modifier = Modifier
					.fillMaxSize()
					.zIndex(if (isArriving) ARRIVING_PAGE_Z_INDEX else 0f)
					.graphicsLayer { translationX = pagerState.offsetForPage(page) * size.width },
			) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.liquidSwipeClip(
							pagerState = pagerState,
							page = page,
							buttonSize = ONBOARDING_NEXT_BUTTON_SIZE,
							bottomPadding = ONBOARDING_NEXT_BUTTON_BOTTOM_PADDING,
						),
				) {
					OnboardingPageContent(
						page = pages[page],
						pageOffset = { pagerState.offsetForPage(page) },
						sparkleTime = { sparkleTime.value },
						sheenSweep = { sheenSweep.value },
					)
				}
				OnboardingNextButton(
					pagerState = pagerState,
					page = page,
					state = rememberOnboardingNextButtonState(
						pages = pages,
						page = page,
						viewportWidth = viewportWidth,
						viewportHeight = viewportHeight,
					),
					onClick = {
						if (page == pages.lastIndex) {
							currentOnFinish(pages.outcome(page, skipped = false))
						} else {
							coroutineScope.launch {
								pagerState.animateScrollToPage(
									page = page + 1,
									animationSpec = spring(
										dampingRatio = 1f,
										stiffness = NEXT_PAGE_STIFFNESS,
									),
								)
							}
						}
					},
				)
			}
		}

		OnboardingChrome(
			pages = pages,
			currentPage = pagerState.currentPage,
			onSkip = {
				currentOnFinish(pages.outcome(pagerState.currentPage, skipped = true))
			},
			modifier = Modifier.zIndex(CHROME_Z_INDEX),
		)
	}
}

private fun ImmutableList<OnboardingPage>.outcome(page: Int, skipped: Boolean) = OnboardingOutcome(
	skipped = skipped,
	lastPageIndex = page,
	pageCount = size,
)

@Preview
@Composable
private fun OnboardingScreenPreview() {
	PurecipesTheme {
		OnboardingScreen(onFinish = {})
	}
}
