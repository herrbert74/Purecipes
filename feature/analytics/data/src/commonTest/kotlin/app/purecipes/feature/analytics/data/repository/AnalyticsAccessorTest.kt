package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsAccessorTest {

	@Test
	fun `trackEvent skips dispatch when consent denies analytics`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.DENIED),
		)

		accessor.trackEvent(
			AnalyticsEvent.RecipeViewed(
				recipeId = 42,
				recipeName = "Tomato Pasta",
				origin = AnalyticsOrigin.SEARCH,
				isPrivate = false,
			),
		)

		dataSource.lastTrackedEventName shouldBe null
	}

	@Test
	fun `trackEvent skips dispatch when consent is unknown`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.UNKNOWN),
		)

		accessor.trackEvent(
			AnalyticsEvent.RecipeViewed(
				recipeId = 1,
				recipeName = "Tomato Pasta",
				origin = AnalyticsOrigin.SEARCH,
				isPrivate = false,
			),
		)

		dataSource.lastTrackedEventName shouldBe null
	}

	@Test
	fun `trackEvent skips dispatch when consent is required`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.REQUIRED),
		)

		accessor.trackEvent(
			AnalyticsEvent.RecipeViewed(
				recipeId = 1,
				recipeName = "Tomato Pasta",
				origin = AnalyticsOrigin.SEARCH,
				isPrivate = false,
			),
		)

		dataSource.lastTrackedEventName shouldBe null
	}

	@Test
	fun `trackEvent dispatches typed properties when consent allows analytics`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.OBTAINED),
		)

		accessor.trackEvent(
			AnalyticsEvent.FavoriteChanged(
				recipeId = 7,
				recipeName = "Tomato Pasta",
				isFavorite = true,
				origin = AnalyticsOrigin.RECIPE_DETAILS,
				isPrivate = false,
			),
		)

		dataSource.lastTrackedEventName shouldBe "favorite_changed"
		dataSource.lastTrackedProperties shouldBe mapOf(
			"recipe_id" to AnalyticsValue.NumberValue(7),
			"recipe_name" to AnalyticsValue.TextValue("Tomato Pasta"),
			"is_private" to AnalyticsValue.BooleanValue(false),
			"is_favorite" to AnalyticsValue.BooleanValue(true),
			"origin" to AnalyticsValue.TextValue("recipe_details"),
		)
	}

	@Test
	fun `trackEvent dispatches to all data sources when consent allows analytics`() = runAnalyticsTest {
		val first = RecordingAnalyticsDataSource()
		val second = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(first, second),
			consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
		)

		accessor.trackEvent(
			AnalyticsEvent.CookingStarted(
				recipeId = 5,
				recipeName = "Tomato Pasta",
				origin = AnalyticsOrigin.RECIPE_DETAILS,
				stepCount = 3,
				isPrivate = false,
			),
		)

		first.lastTrackedEventName shouldBe "cooking_started"
		second.lastTrackedEventName shouldBe "cooking_started"
	}

	@Test
	fun `trackEvent does not call setTrackingEnabled`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.OBTAINED),
		)
		val enabledCallsAfterInit = dataSource.setTrackingEnabledCallCount

		accessor.trackEvent(
			AnalyticsEvent.RecipeViewed(
				recipeId = 1,
				recipeName = "Tomato Pasta",
				origin = AnalyticsOrigin.SEARCH,
				isPrivate = false,
			),
		)

		dataSource.setTrackingEnabledCallCount shouldBe enabledCallsAfterInit
		dataSource.lastTrackedEventName shouldBe "recipe_viewed"
	}

	@Test
	fun `trackScreenView skips dispatch when consent denies analytics`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.DENIED),
		)

		accessor.trackScreenView(
			screenName = "search",
			properties = mapOf("origin" to AnalyticsValue.TextValue("favorites")),
		)

		dataSource.lastScreenViewName shouldBe null
	}

	@Test
	fun `trackScreenView dispatches to all data sources when consent allows analytics`() = runAnalyticsTest {
		val first = RecordingAnalyticsDataSource()
		val second = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(first, second),
			consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
		)
		val properties = mapOf("origin" to AnalyticsValue.TextValue("search"))

		accessor.trackScreenView(screenName = "recipe_details", properties = properties)

		first.lastScreenViewName shouldBe "recipe_details"
		first.lastScreenViewProperties shouldBe properties
		second.lastScreenViewName shouldBe "recipe_details"
		second.lastScreenViewProperties shouldBe properties
	}

	@Test
	fun `init applies tracking enabled from current consent`() = runAnalyticsTest {
		val deniedSource = RecordingAnalyticsDataSource()
		createAccessor(
			analyticsDataSources = setOf(deniedSource),
			consentRepository = FakeConsentRepository(ConsentState.DENIED),
		)
		deniedSource.lastTrackingEnabled shouldBe false

		val allowedSource = RecordingAnalyticsDataSource()
		createAccessor(
			analyticsDataSources = setOf(allowedSource),
			consentRepository = FakeConsentRepository(ConsentState.OBTAINED),
		)
		allowedSource.lastTrackingEnabled shouldBe true
	}

	@Test
	fun `consent changes push tracking enabled to data sources`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val consentRepository = FakeConsentRepository(ConsentState.DENIED)
		createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = consentRepository,
		)
		dataSource.lastTrackingEnabled shouldBe false

		consentRepository.updateConsentState(ConsentState.OBTAINED)

		dataSource.lastTrackingEnabled shouldBe true
		dataSource.lastTrackedEventName shouldBe "consent_changed"
		dataSource.lastTrackedProperties shouldBe mapOf(
			"state" to AnalyticsValue.TextValue("obtained"),
		)

		consentRepository.updateConsentState(ConsentState.DENIED)

		dataSource.lastTrackingEnabled shouldBe false
		dataSource.lastTrackedEventName shouldBe "consent_changed"
		dataSource.lastTrackedProperties shouldBe mapOf(
			"state" to AnalyticsValue.TextValue("denied"),
		)
	}

	@Test
	fun `consent enablement re-applies global properties`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val consentRepository = FakeConsentRepository(ConsentState.DENIED)
		createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = consentRepository,
		)

		dataSource.setGlobalPropertiesCallCount shouldBe 1
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.ENVIRONMENT) shouldBe
			AnalyticsValue.TextValue("debug")

		consentRepository.updateConsentState(ConsentState.OBTAINED)

		dataSource.setGlobalPropertiesCallCount shouldBeGreaterThan 1
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.ENVIRONMENT) shouldBe
			AnalyticsValue.TextValue("debug")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.PLATFORM) shouldBe
			AnalyticsValue.TextValue("desktop")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.APP_VERSION) shouldBe
			AnalyticsValue.TextValue("1.2.3-test")
	}

	@Test
	fun `init sets static global properties`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
		)

		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.ENVIRONMENT) shouldBe
			AnalyticsValue.TextValue("debug")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.PLATFORM) shouldBe
			AnalyticsValue.TextValue("desktop")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.APP_VERSION) shouldBe
			AnalyticsValue.TextValue("1.2.3-test")
	}

	@Test
	fun `setGlobalProperties merges into existing globals`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
		)

		accessor.setGlobalProperties(
			mapOf(
				AnalyticsGlobalProperty.USER_STATE to AnalyticsValue.TextValue("logged_in"),
				AnalyticsGlobalProperty.ACTIVE_TAB to AnalyticsValue.TextValue("search"),
			),
		)

		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.ENVIRONMENT) shouldBe
			AnalyticsValue.TextValue("debug")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.USER_STATE) shouldBe
			AnalyticsValue.TextValue("logged_in")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.ACTIVE_TAB) shouldBe
			AnalyticsValue.TextValue("search")

		accessor.setGlobalProperties(
			mapOf(
				AnalyticsGlobalProperty.ACTIVE_TAB to AnalyticsValue.TextValue("favorites"),
				AnalyticsGlobalProperty.CURRENT_SCREEN to AnalyticsValue.TextValue("favorites"),
			),
		)

		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.USER_STATE) shouldBe
			AnalyticsValue.TextValue("logged_in")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.ACTIVE_TAB) shouldBe
			AnalyticsValue.TextValue("favorites")
		dataSource.lastGlobalProperties?.get(AnalyticsGlobalProperty.CURRENT_SCREEN) shouldBe
			AnalyticsValue.TextValue("favorites")
	}

	@Test
	fun `setUserId propagates user id when consent allows analytics`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
		)

		accessor.setUserId("user-123")

		dataSource.lastUserId shouldBe "user-123"
	}

	@Test
	fun `setUserId clears user id when consent denies analytics`() = runAnalyticsTest {
		val dataSource = RecordingAnalyticsDataSource()
		val accessor = createAccessor(
			analyticsDataSources = setOf(dataSource),
			consentRepository = FakeConsentRepository(ConsentState.DENIED),
		)

		accessor.setUserId("user-123")

		dataSource.lastUserId shouldBe null
	}

	private fun runAnalyticsTest(testBody: suspend AnalyticsTestContext.() -> Unit) =
		runTest(UnconfinedTestDispatcher()) {
			AnalyticsTestContext(backgroundScope).testBody()
		}

	private class AnalyticsTestContext(
		private val observationScope: kotlinx.coroutines.CoroutineScope,
	) {

		fun createAccessor(
			analyticsDataSources: Set<AnalyticsDataSource>,
			consentRepository: FakeConsentRepository,
		): AnalyticsAccessor {
			return AnalyticsAccessor(
				analyticsDataSources = analyticsDataSources,
				consentRepository = consentRepository,
				purecipesConfig = object : PurecipesConfig {
					override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

					override fun versionName(): String = "1.2.3-test"

					override fun versionCode(): Long = 123L
				},
				observationScope = observationScope,
			)
		}
	}

	private class RecordingAnalyticsDataSource : AnalyticsDataSource {

		var lastTrackedEventName: String? = null
		var lastTrackedProperties: Map<String, AnalyticsValue>? = null
		var lastScreenViewName: String? = null
		var lastScreenViewProperties: Map<String, AnalyticsValue>? = null
		var lastGlobalProperties: Map<String, AnalyticsValue>? = null
		var lastTrackingEnabled: Boolean? = null
		var lastUserId: String? = null
		var setTrackingEnabledCallCount = 0
		var setGlobalPropertiesCallCount = 0

		override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
			lastTrackedEventName = eventName
			lastTrackedProperties = properties
		}

		override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) {
			lastScreenViewName = screenName
			lastScreenViewProperties = properties
		}

		override fun setGlobalProperties(properties: Map<String, AnalyticsValue>) {
			setGlobalPropertiesCallCount += 1
			lastGlobalProperties = properties
		}

		override fun setTrackingEnabled(isEnabled: Boolean) {
			setTrackingEnabledCallCount += 1
			lastTrackingEnabled = isEnabled
		}

		override fun setUserId(userId: String?) {
			lastUserId = userId
		}
	}
}
