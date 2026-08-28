package app.purecipes.feature.onboarding.ui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

private const val PAGE_SETTLE_MILLIS = 5_000L

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class OnboardingScreenTest {

	@Test
	fun onboardingStartsOnTheFirstPage() = runRecompositionTrackingUiTest {
		setOnboardingContent()

		onNodeWithText(onboardingPages.first().title).assertIsDisplayed()
		onNodeWithText(onboardingPages.first().description).assertIsDisplayed()
		onNodeWithTag(ONBOARDING_SKIP_TAG).assertIsDisplayed()
	}

	@Test
	fun nextButtonAdvancesToTheFollowingPage() = runRecompositionTrackingUiTest {
		setOnboardingContent()

		advanceToNextPage()

		onNodeWithText(onboardingPages[1].title).assertIsDisplayed()
	}

	@Test
	fun skipReportsASkippedOutcome() = runRecompositionTrackingUiTest {
		var outcome: OnboardingOutcome? = null
		setOnboardingContent { outcome = it }

		onNodeWithTag(ONBOARDING_SKIP_TAG).performClick()
		waitForIdle()

		assertEquals(
			OnboardingOutcome(skipped = true, lastPageIndex = 0, pageCount = onboardingPages.size),
			outcome,
		)
	}

	@Test
	fun skipIsGoneOnTheLastPage() = runRecompositionTrackingUiTest {
		setOnboardingContent()

		repeat(onboardingPages.lastIndex) { advanceToNextPage() }

		onNodeWithText(onboardingPages.last().title).assertIsDisplayed()
		onNodeWithTag(ONBOARDING_SKIP_TAG).assertDoesNotExist()
	}

	@Test
	fun finishingTheLastPageReportsACompletedOutcome() = runRecompositionTrackingUiTest {
		var outcome: OnboardingOutcome? = null
		setOnboardingContent { outcome = it }

		repeat(onboardingPages.lastIndex) { advanceToNextPage() }
		assertNull(outcome)

		onNodeWithTag(ONBOARDING_NEXT_BUTTON_TAG).performClick()
		waitForIdle()

		assertEquals(
			OnboardingOutcome(
				skipped = false,
				lastPageIndex = onboardingPages.lastIndex,
				pageCount = onboardingPages.size,
			),
			outcome,
		)
	}
}

/**
 * The sparkle and sheen animations never end, so the clock is driven manually to keep
 * `waitForIdle` from blocking on them.
 */
@OptIn(ExperimentalTestApi::class)
private fun ComposeUiTest.setOnboardingContent(onFinish: (OnboardingOutcome) -> Unit = {}) {
	mainClock.autoAdvance = false
	setTrackedContent {
		PurecipesTheme {
			OnboardingScreen(onFinish = onFinish)
		}
	}
	waitForIdle()
}

@OptIn(ExperimentalTestApi::class)
private fun ComposeUiTest.advanceToNextPage() {
	onNodeWithTag(ONBOARDING_NEXT_BUTTON_TAG).performClick()
	mainClock.advanceTimeBy(PAGE_SETTLE_MILLIS)
	waitForIdle()
}
