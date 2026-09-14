package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CreateFeatureRequestUseCaseTest {

	@Test
	fun `create feature request trims input and starts as open`() = runTest {
		val repository = FakeFeatureRequestsRepository()
		val useCase = CreateFeatureRequestUseCase(repository)

		val outcome = useCase(title = "  Meal planner  ", description = "  Plan a week  ")

		repository.createdTitles shouldBe listOf("Meal planner")
		outcome.get()?.description shouldBe "Plan a week"
		outcome.get()?.status shouldBe FeatureRequestStatus.OPEN
		outcome.get()?.voteCount shouldBe 0
	}

	@Test
	fun `create feature request surfaces repository failure`() = runTest {
		val repository = FakeFeatureRequestsRepository(failureMessage = "nope")
		val useCase = CreateFeatureRequestUseCase(repository)

		val outcome = useCase(title = "Meal planner", description = "Plan a week")

		outcome.get() shouldBe null
	}
}
