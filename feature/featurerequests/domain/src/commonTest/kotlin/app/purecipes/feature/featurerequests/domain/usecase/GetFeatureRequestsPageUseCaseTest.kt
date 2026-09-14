package app.purecipes.feature.featurerequests.domain.usecase

import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequest
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetFeatureRequestsPageUseCaseTest {

	@Test
	fun `top votes sort returns most voted request first`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(
				fakeFeatureRequest(id = 1, title = "Few votes", voteCount = 2),
				fakeFeatureRequest(id = 2, title = "Many votes", voteCount = 9),
			),
		)
		val useCase = GetFeatureRequestsPageUseCase(repository)

		val outcome = useCase(
			sort = FeatureRequestSort.TOP_VOTES,
			status = null,
			pageNumber = 1,
			pageSize = 20,
		)

		outcome.get()?.items?.map { it.id } shouldBe listOf(2, 1)
		outcome.get()?.totalMatches shouldBe 2
		outcome.getError() shouldBe null
	}

	@Test
	fun `newest sort returns most recent request first`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(
				fakeFeatureRequest(id = 1, createdAtEpochMillis = 10L),
				fakeFeatureRequest(id = 2, createdAtEpochMillis = 40L),
			),
		)
		val useCase = GetFeatureRequestsPageUseCase(repository)

		val outcome = useCase(
			sort = FeatureRequestSort.NEWEST,
			status = null,
			pageNumber = 1,
			pageSize = 20,
		)

		outcome.get()?.items?.map { it.id } shouldBe listOf(2, 1)
	}

	@Test
	fun `status filter is passed to the repository and applied`() = runTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(
				fakeFeatureRequest(id = 1, status = FeatureRequestStatus.OPEN),
				fakeFeatureRequest(id = 2, status = FeatureRequestStatus.DONE),
			),
		)
		val useCase = GetFeatureRequestsPageUseCase(repository)

		val outcome = useCase(
			sort = FeatureRequestSort.NEWEST,
			status = FeatureRequestStatus.DONE,
			pageNumber = 1,
			pageSize = 20,
		)

		repository.requestedStatuses shouldBe listOf(FeatureRequestStatus.DONE)
		outcome.get()?.items?.map { it.id } shouldBe listOf(2)
	}

	@Test
	fun `repository failure is returned as failure`() = runTest {
		val repository = FakeFeatureRequestsRepository(failureMessage = "boom")
		val useCase = GetFeatureRequestsPageUseCase(repository)

		val outcome = useCase(
			sort = FeatureRequestSort.TOP_VOTES,
			status = null,
			pageNumber = 1,
			pageSize = 20,
		)

		outcome.get() shouldBe null
		outcome.getError()?.message shouldBe "boom"
	}
}
