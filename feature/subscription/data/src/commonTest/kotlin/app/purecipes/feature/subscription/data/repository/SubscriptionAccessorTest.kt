package app.purecipes.feature.subscription.data.repository

import app.purecipes.feature.subscription.data.datasource.FakeSubscriptionDataSource
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SubscriptionAccessorTest {

	@Test
	fun `initialize passes revenue cat api key to data source`() {
		val dataSource = FakeSubscriptionDataSource()
		val accessor = SubscriptionAccessor(
			subscriptionDataSource = dataSource,
			purecipesConfig = testPurecipesConfig(revenueCatApiKey = "test-api-key"),
		)

		accessor.initialize()

		dataSource.initializeCalls shouldBe 1
		dataSource.lastApiKey shouldBe "test-api-key"
	}

	@Test
	fun `observeSubscriptionState returns data source state`() = runTest {
		val premiumState = SubscriptionState(
			status = SubscriptionStatus.PREMIUM,
			isActive = true,
			expirationInstant = null,
			trialActive = false,
		)
		val dataSource = FakeSubscriptionDataSource(initialState = premiumState)
		val accessor = SubscriptionAccessor(
			subscriptionDataSource = dataSource,
			purecipesConfig = testPurecipesConfig(),
		)

		accessor.observeSubscriptionState().first() shouldBe premiumState
	}

	private fun testPurecipesConfig(
		revenueCatApiKey: String? = null,
	): PurecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

		override fun versionName(): String = "0.0.0-test"

		override fun versionCode(): Long = 0L

		override fun revenueCatApiKey(): String? = revenueCatApiKey
	}
}
