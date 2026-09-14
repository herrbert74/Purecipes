package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequest
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ToggleFeatureRequestVoteUseCaseTest {

	@Test
	fun `toggle vote adds a vote when the user has not voted`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(
				fakeFeatureRequest(id = 3, voteCount = 4, votedByCurrentUser = false),
			),
		)
		val useCase = ToggleFeatureRequestVoteUseCase(repository)

		val outcome = useCase(requestId = 3)

		repository.toggledVoteIds shouldBe listOf(3)
		outcome.get()?.voteCount shouldBe 5
		outcome.get()?.votedByCurrentUser shouldBe true
	}

	@Test
	fun `toggle vote removes an existing vote`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(
				fakeFeatureRequest(id = 3, voteCount = 4, votedByCurrentUser = true),
			),
		)
		val useCase = ToggleFeatureRequestVoteUseCase(repository)

		val outcome = useCase(requestId = 3)

		outcome.get()?.voteCount shouldBe 3
		outcome.get()?.votedByCurrentUser shouldBe false
	}
}
