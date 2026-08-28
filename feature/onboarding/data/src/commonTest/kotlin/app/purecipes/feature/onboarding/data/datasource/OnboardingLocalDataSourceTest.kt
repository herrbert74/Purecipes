package app.purecipes.feature.onboarding.data.datasource

import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.test.Test

class OnboardingLocalDataSourceTest {

	@Test
	fun `onboarding is not completed by default`() {
		val dataSource = OnboardingLocalDataSource(preferencesKey = uniqueKey())

		dataSource.isOnboardingCompleted() shouldBe false
	}

	@Test
	fun `completion is visible to other datasource instances`() {
		val preferencesKey = uniqueKey()
		val firstDataSource = OnboardingLocalDataSource(preferencesKey = preferencesKey)
		val secondDataSource = OnboardingLocalDataSource(preferencesKey = preferencesKey)

		firstDataSource.setOnboardingCompleted(completed = true)

		secondDataSource.isOnboardingCompleted() shouldBe true
	}

	@Test
	fun `clearing completion survives in storage`() {
		val preferencesKey = uniqueKey()
		val dataSource = OnboardingLocalDataSource(preferencesKey = preferencesKey)
		dataSource.setOnboardingCompleted(completed = true)

		dataSource.setOnboardingCompleted(completed = false)

		OnboardingLocalDataSource(preferencesKey = preferencesKey)
			.isOnboardingCompleted() shouldBe false
	}

	private fun uniqueKey(): String = "onboarding.completed.test.${Random.nextInt()}"
}
