package app.purecipes.feature.search.domain.readiness

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SearchReadinessCoordinatorTest {

	@Test
	fun `is not ready before reporting`() {
		val coordinator = SearchReadinessCoordinator()

		coordinator.isReady.value shouldBe false
	}

	@Test
	fun `becomes ready after reporting`() {
		val coordinator = SearchReadinessCoordinator()

		coordinator.reportReady()

		coordinator.isReady.value shouldBe true
	}
}
