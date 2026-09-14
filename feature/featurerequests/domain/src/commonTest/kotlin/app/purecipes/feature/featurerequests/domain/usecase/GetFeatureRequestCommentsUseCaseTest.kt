package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequestComment
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetFeatureRequestCommentsUseCaseTest {

	@Test
	fun `get comments returns only the comments of the request`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialComments = listOf(
				fakeFeatureRequestComment(id = 1, requestId = 1, body = "Mine"),
				fakeFeatureRequestComment(id = 2, requestId = 2, body = "Other"),
			),
		)
		val useCase = GetFeatureRequestCommentsUseCase(repository)

		val outcome = useCase(requestId = 1)

		outcome.get()?.map { it.body } shouldBe listOf("Mine")
	}
}
