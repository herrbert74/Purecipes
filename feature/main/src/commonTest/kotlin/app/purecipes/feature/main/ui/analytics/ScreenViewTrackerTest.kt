package app.purecipes.feature.main.ui.analytics

import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsScreenName
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.fakeTrackScreenViewUseCase
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ScreenViewTrackerTest {

	@Test
	fun `emits once per distinct screen and includes previous screen as origin`() {
		val analyticsRepository = FakeAnalyticsRepository()
		val crashRepository = FakeCrashRepository()
		val tracker = ScreenViewTracker(fakeTrackScreenViewUseCase(analyticsRepository, crashRepository))

		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)
		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS, recipeId = 42)
		tracker.onScreenVisible(AnalyticsScreenName.COOKING, recipeId = 42)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS, recipeId = 42)
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
		analyticsRepository.trackedScreenViews.map { it.properties["recipe_id"] } shouldBe listOf(
			null,
			AnalyticsValue.NumberValue(42L),
			AnalyticsValue.NumberValue(42L),
			AnalyticsValue.NumberValue(42L),
			null,
		)
		analyticsRepository.globalProperties[AnalyticsGlobalProperty.CURRENT_SCREEN] shouldBe
			AnalyticsValue.TextValue(AnalyticsScreenName.SEARCH)
		crashRepository.breadcrumbs shouldBe listOf(
			CrashBreadcrumb.screen(AnalyticsScreenName.SEARCH),
			CrashBreadcrumb.screen(AnalyticsScreenName.RECIPE_DETAILS),
			CrashBreadcrumb.screen(AnalyticsScreenName.COOKING),
			CrashBreadcrumb.screen(AnalyticsScreenName.RECIPE_DETAILS),
			CrashBreadcrumb.screen(AnalyticsScreenName.SEARCH),
		)
		crashRepository.customValues[AnalyticsGlobalProperty.CURRENT_SCREEN] shouldBe AnalyticsScreenName.SEARCH
	}

	@Test
	fun `re-emits when a previous screen resurfaces after another screen`() {
		val analyticsRepository = FakeAnalyticsRepository()
		val tracker = ScreenViewTracker(fakeTrackScreenViewUseCase(analyticsRepository))

		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS, recipeId = 1)
		tracker.onScreenVisible(AnalyticsScreenName.SEARCH)

		analyticsRepository.trackedScreenViews.map { it.screenName } shouldBe listOf(
			AnalyticsScreenName.SEARCH,
			AnalyticsScreenName.RECIPE_DETAILS,
			AnalyticsScreenName.SEARCH,
		)
	}

	@Test
	fun `re-emits recipe details when recipe id changes`() {
		val analyticsRepository = FakeAnalyticsRepository()
		val tracker = ScreenViewTracker(fakeTrackScreenViewUseCase(analyticsRepository))

		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS, recipeId = 1)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS, recipeId = 1)
		tracker.onScreenVisible(AnalyticsScreenName.RECIPE_DETAILS, recipeId = 2)

		analyticsRepository.trackedScreenViews.map { it.properties["recipe_id"] } shouldBe listOf(
			AnalyticsValue.NumberValue(1L),
			AnalyticsValue.NumberValue(2L),
		)
	}
}
