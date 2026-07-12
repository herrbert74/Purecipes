package app.purecipes.feature.main.ui.analytics

import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
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
			AnalyticsValue.TextValue(AnalyticsOrigin.SEARCH.value),
			AnalyticsValue.TextValue(AnalyticsOrigin.RECIPE_DETAILS.value),
			AnalyticsValue.TextValue(AnalyticsOrigin.COOKING.value),
			AnalyticsValue.TextValue(AnalyticsOrigin.RECIPE_DETAILS.value),
		)
		analyticsRepository.globalProperties[AnalyticsGlobalProperty.CURRENT_SCREEN] shouldBe
			AnalyticsValue.TextValue(AnalyticsScreenName.SEARCH)
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
