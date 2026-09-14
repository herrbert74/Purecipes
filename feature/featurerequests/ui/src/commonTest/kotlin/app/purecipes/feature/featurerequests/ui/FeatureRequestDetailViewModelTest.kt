package app.purecipes.feature.featurerequests.ui

import app.purecipes.feature.featurerequests.domain.usecase.AddFeatureRequestCommentUseCase
import app.purecipes.feature.featurerequests.domain.usecase.GetFeatureRequestCommentsUseCase
import app.purecipes.feature.featurerequests.domain.usecase.GetFeatureRequestUseCase
import app.purecipes.feature.featurerequests.domain.usecase.ToggleFeatureRequestVoteUseCase
import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequest
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequestComment
import app.purecipes.shared.testfixtures.runViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureRequestDetailViewModelTest {

	@Test
	fun `detail loads the request with its comments`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(fakeFeatureRequest(id = 4, title = "Dark mode", commentCount = 1)),
			initialComments = listOf(fakeFeatureRequestComment(id = 1, requestId = 4, body = "Yes please")),
		)
		val viewModel = detailViewModel(repository, requestId = 4)
		advanceUntilIdle()

		assertEquals("Dark mode", viewModel.featureRequest?.title)
		assertEquals(listOf("Yes please"), viewModel.comments.map { it.body })
		assertEquals(false, viewModel.isLoading)
		assertEquals(null, viewModel.errorMessage)
	}

	@Test
	fun `upvote updates the vote count`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(fakeFeatureRequest(id = 4, voteCount = 6)),
		)
		val viewModel = detailViewModel(repository, requestId = 4)
		advanceUntilIdle()

		viewModel.onToggleVote()
		advanceUntilIdle()

		assertEquals(listOf(4), repository.toggledVoteIds.toList())
		assertEquals(7, viewModel.featureRequest?.voteCount)
		assertEquals(true, viewModel.featureRequest?.votedByCurrentUser)
	}

	@Test
	fun `adding a comment appends it and bumps the comment count`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(fakeFeatureRequest(id = 4, commentCount = 0)),
		)
		val viewModel = detailViewModel(repository, requestId = 4)
		advanceUntilIdle()

		var added: Boolean? = null
		viewModel.addComment(" Ship it ") { added = it }
		advanceUntilIdle()

		assertEquals(true, added)
		assertEquals(listOf("Ship it"), viewModel.comments.map { it.body })
		assertEquals(1, viewModel.featureRequest?.commentCount)
		assertEquals(false, viewModel.isSendingComment)
	}

	@Test
	fun `blank comment is not sent`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository(
			initialFeatureRequests = listOf(fakeFeatureRequest(id = 4)),
		)
		val viewModel = detailViewModel(repository, requestId = 4)
		advanceUntilIdle()

		var added: Boolean? = null
		viewModel.addComment("   ") { added = it }
		advanceUntilIdle()

		assertEquals(false, added)
		assertEquals(emptyList(), repository.addedCommentBodies.toList())
	}

	@Test
	fun `missing request exposes an error message`() = runViewModelTest {
		val repository = FakeFeatureRequestsRepository()
		val viewModel = detailViewModel(repository, requestId = 4)
		advanceUntilIdle()

		assertEquals(null, viewModel.featureRequest)
		assertEquals("No feature request found for id 4", viewModel.errorMessage)
	}

	private fun detailViewModel(
		repository: FakeFeatureRequestsRepository,
		requestId: Int,
	): FeatureRequestDetailViewModel = FeatureRequestDetailViewModel(
		getFeatureRequest = GetFeatureRequestUseCase(repository),
		getFeatureRequestComments = GetFeatureRequestCommentsUseCase(repository),
		addFeatureRequestComment = AddFeatureRequestCommentUseCase(repository),
		toggleFeatureRequestVote = ToggleFeatureRequestVoteUseCase(repository),
		requestId = requestId,
	)
}
