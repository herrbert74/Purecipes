package app.purecipes.feature.onboarding.data.repository

import app.purecipes.feature.onboarding.data.datasource.OnboardingDataSource
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class OnboardingAccessorTest {

	@Test
	fun `completing onboarding delegates to the local datasource`() = runTest {
		val accessor = OnboardingAccessor(InMemoryOnboardingLocalDataSource())

		accessor.isOnboardingCompleted() shouldBe false

		accessor.completeOnboarding()

		accessor.isOnboardingCompleted() shouldBe true
	}

	@Test
	fun `resetting onboarding makes it due again`() = runTest {
		val accessor = OnboardingAccessor(InMemoryOnboardingLocalDataSource(completed = true))

		accessor.resetOnboarding()

		accessor.isOnboardingCompleted() shouldBe false
	}

	private class InMemoryOnboardingLocalDataSource(
		private var completed: Boolean = false,
	) : OnboardingDataSource.Local {

		override fun isOnboardingCompleted(): Boolean = completed

		override fun setOnboardingCompleted(completed: Boolean) {
			this.completed = completed
		}
	}
}
