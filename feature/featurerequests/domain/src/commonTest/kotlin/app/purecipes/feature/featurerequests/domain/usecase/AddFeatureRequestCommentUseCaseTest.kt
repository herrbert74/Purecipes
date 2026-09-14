package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AddFeatureRequestCommentUseCaseTest {

	@Test
	fun `add comment trims the body before delegating`() = runTest {
		val repository = FakeFeatureRequestsRepository()
		val useCase = AddFeatureRequestCommentUseCase(repository)

		val outcome = useCase(requestId = 5, body = "  Please build this  ")

		repository.addedCommentBodies shouldBe listOf("Please build this")
		outcome.get()?.requestId shouldBe 5
	}

	@Test
	fun `add comment surfaces repository failure`() = runTest {
		val repository = FakeFeatureRequestsRepository(failureMessage = "offline")
		val useCase = AddFeatureRequestCommentUseCase(repository)

		val outcome = useCase(requestId = 5, body = "Please build this")

		outcome.get() shouldBe null
	}
}
