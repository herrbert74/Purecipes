package app.purecipes.feature.analytics.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ConsentStateTest {

	@Test
	fun `allowsAnalytics returns true when obtained`() {
		ConsentState.OBTAINED.allowsAnalytics() shouldBe true
	}

	@Test
	fun `allowsAnalytics returns true when not required`() {
		ConsentState.NOT_REQUIRED.allowsAnalytics() shouldBe true
	}

	@Test
	fun `allowsAnalytics returns false when unknown`() {
		ConsentState.UNKNOWN.allowsAnalytics() shouldBe false
	}

	@Test
	fun `allowsAnalytics returns false when required`() {
		ConsentState.REQUIRED.allowsAnalytics() shouldBe false
	}

	@Test
	fun `allowsAnalytics returns false when denied`() {
		ConsentState.DENIED.allowsAnalytics() shouldBe false
	}
}
