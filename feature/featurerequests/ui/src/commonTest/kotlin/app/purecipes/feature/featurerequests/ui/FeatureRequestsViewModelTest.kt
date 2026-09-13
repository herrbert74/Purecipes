package app.purecipes.feature.featurerequests.ui

import app.purecipes.feature.featurerequests.domain.usecase.CreateFeatureRequestUseCase
import app.purecipes.feature.featurerequests.domain.usecase.GetFeatureRequestsPageUseCase
import app.purecipes.feature.featurerequests.domain.usecase.ToggleFeatureRequestVoteUseCase
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequest
import app.purecipes.shared.testfixtures.runViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureRequestsViewModelTest {

	@Test
	fun `first page is loaded sorted by top votes`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(initialFeatureRequests = featureRequests())
		val viewModel = featureRequestsViewModel(repository)
		advanceUntilIdle()

		assertEquals(listOf(2, 1), viewModel.featureRequests.map { it.id })
		assertEquals(2, viewModel.totalMatches)
		assertEquals(FeatureRequestSort.TOP_VOTES, viewModel.sort)
		assertEquals(false, viewModel.isLoading)
		assertEquals(null, viewModel.errorMessage)
	}

	@Test
	fun `selecting newest sort reloads the list`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(initialFeatureRequests = featureRequests())
		val viewModel = featureRequestsViewModel(repository)
		advanceUntilIdle()

		viewModel.onSortSelected(FeatureRequestSort.NEWEST)
		advanceUntilIdle()

		assertEquals(FeatureRequestSort.NEWEST, viewModel.sort)
		assertEquals(listOf(2, 1), viewModel.featureRequests.map { it.id })
		assertEquals(
			listOf(FeatureRequestSort.TOP_VOTES, FeatureRequestSort.NEWEST),
			repository.requestedSorts,
		)
	}

	@Test
	fun `selecting a status filter narrows the list`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(initialFeatureRequests = featureRequests())
		val viewModel = featureRequestsViewModel(repository)
		advanceUntilIdle()

		viewModel.onStatusFilterSelected(FeatureRequestStatus.PLANNED)
		advanceUntilIdle()

		assertEquals(FeatureRequestStatus.PLANNED, viewModel.statusFilter)
		assertEquals(listOf(2), viewModel.featureRequests.map { it.id })
		assertEquals(1, viewModel.totalMatches)
	}

	@Test
	fun `toggling a vote updates the row in place`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(initialFeatureRequests = featureRequests())
		val viewModel = featureRequestsViewModel(repository)
		advanceUntilIdle()

		viewModel.onToggleVote(viewModel.featureRequests.first { it.id == 1 })
		advanceUntilIdle()

		val updated = viewModel.featureRequests.first { it.id == 1 }
		assertEquals(4, updated.voteCount)
		assertEquals(true, updated.votedByCurrentUser)
	}

	@Test
	fun `blank input is rejected before calling the use case`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository()
		val viewModel = featureRequestsViewModel(repository)
		advanceUntilIdle()

		var created: Boolean? = null
		viewModel.createFeatureRequestFromInput(title = "  ", description = "Plan a week") { created = it }
		advanceUntilIdle()

		assertEquals(false, created)
		assertEquals(emptyList(), repository.createdTitles.toList())
		assertEquals("Add a title and a description", viewModel.createErrorMessage)
	}

	@Test
	fun `creating a request refreshes the list`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository()
		val viewModel = featureRequestsViewModel(repository)
		advanceUntilIdle()

		var created: Boolean? = null
		viewModel.createFeatureRequestFromInput(
			title = "Meal planner",
			description = "Plan a week",
		) { created = it }
		advanceUntilIdle()

		assertEquals(true, created)
		assertEquals(listOf("Meal planner"), repository.createdTitles.toList())
		assertEquals(listOf("Meal planner"), viewModel.featureRequests.map { it.title })
		assertEquals(null, viewModel.createErrorMessage)
	}

	@Test
	fun `signed out view model does not load anything`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(initialFeatureRequests = featureRequests())
		val viewModel = featureRequestsViewModel(repository, sessionKey = null)
		advanceUntilIdle()

		assertEquals(emptyList(), viewModel.featureRequests.toList())
		assertEquals(emptyList(), repository.requestedSorts.toList())
	}

	@Test
	fun `failing page load exposes the error message`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(failureMessage = "boom")
		val viewModel = featureRequestsViewModel(repository)
		advanceUntilIdle()

		assertEquals("boom", viewModel.errorMessage)
		assertEquals(false, viewModel.isLoading)
	}

	private fun featureRequests() = listOf(
		fakeFeatureRequest(
			id = 1,
			title = "Shopping list",
			status = FeatureRequestStatus.OPEN,
			voteCount = 3,
			createdAtEpochMillis = 10L,
		),
		fakeFeatureRequest(
			id = 2,
			title = "Meal planner",
			status = FeatureRequestStatus.PLANNED,
			voteCount = 8,
			createdAtEpochMillis = 40L,
		),
	)

	private fun featureRequestsViewModel(
		repository: FakeFeatureRequestsRepository,
		sessionKey: String? = "session",
	): FeatureRequestsViewModel = FeatureRequestsViewModel(
		getFeatureRequestsPage = GetFeatureRequestsPageUseCase(repository),
		createFeatureRequest = CreateFeatureRequestUseCase(repository),
		toggleFeatureRequestVote = ToggleFeatureRequestVoteUseCase(repository),
		sessionKey = sessionKey,
	)
}
