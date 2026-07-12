package app.purecipes.feature.main.ui.analytics

import app.purecipes.feature.analytics.domain.model.AnalyticsScreenName
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.usecase.TrackScreenViewUseCase
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ScreenViewTrackerTest {

	@Test
	fun `emits once per distinct screen and includes previous screen as origin`() {
		val analyticsRepository = FakeAnalyticsRepository()
		val tracker = ScreenViewTracker(TrackScreenViewUseCase(analyticsRepository))

		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)
		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS)
		tracker.onScreenVisible(AnalyticsScreenName.COOKING)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS)
		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)

		analyticsRepository.trackedScreenViews.map { it.screenName } shouldBe listOf(
			AnalyticsScreenName.SEARCH,
			AnalyticsScreenName.RECIPE_DETAILS,
			AnalyticsScreenName.COOKING,
			AnalyticsScreenName.RECIPE_DETAILS,
			AnalyticsScreenName.SEARCH,
		)
		analyticsRepository.trackedScreenViews.map { it.properties["origin"] } shouldBe listOf(
			null,
			AnalyticsValue.TextValue(AnalyticsScreenName.SEARCH),
			AnalyticsValue.TextValue(AnalyticsScreenName.RECIPE_DETAILS),
			AnalyticsValue.TextValue(AnalyticsScreenName.COOKING),
			AnalyticsValue.TextValue(AnalyticsScreenName.RECIPE_DETAILS),
		)
	}

	@Test
	fun `re-emits when a previous screen resurfaces after another screen`() {
		val analyticsRepository = FakeAnalyticsRepository()
		val tracker = ScreenViewTracker(TrackScreenViewUseCase(analyticsRepository))

		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS)
		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)

		analyticsRepository.trackedScreenViews.map { it.screenName } shouldBe listOf(
			AnalyticsScreenName.SEARCH,
			AnalyticsScreenName.RECIPE_DETAILS,
			AnalyticsScreenName.SEARCH,
		)
	}
}
