package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequest
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetFeatureRequestUseCaseTest {

	@Test
	fun `get feature request returns the matching request`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(fakeFeatureRequest(id = 7, title = "Dark mode")),
		)
		val useCase = GetFeatureRequestUseCase(repository)

		val outcome = useCase(requestId = 7)

		outcome.get()?.title shouldBe "Dark mode"
	}

	@Test
	fun `get feature request fails for unknown id`() = runTest {
		val repository = FakeFeatureRequestsRepository()
		val useCase = GetFeatureRequestUseCase(repository)

		val outcome = useCase(requestId = 7)

		outcome.get() shouldBe null
	}
}
