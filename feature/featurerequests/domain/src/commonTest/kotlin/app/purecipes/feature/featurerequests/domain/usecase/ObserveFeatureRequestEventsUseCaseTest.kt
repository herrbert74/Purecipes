package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.feature.featurerequests.domain.model.FeatureRequestEvent
import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveFeatureRequestEventsUseCaseTest {

	@Test
	fun `adding a comment emits a comment added event`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(fakeFeatureRequest(id = 4)),
		)
		val useCase = ObserveFeatureRequestEventsUseCase(repository)
		val event = async { useCase().take(1).single() }
		runCurrent()

		repository.addFeatureRequestComment(requestId = 4, body = "Ship it")

		event.await() shouldBe FeatureRequestEvent.CommentAdded(requestId = 4)
	}
}
