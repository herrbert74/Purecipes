package app.purecipes.feature.featurerequests.data.repository

import app.purecipes.feature.featurerequests.data.datasource.FeatureRequestsRemoteDataSource
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FeatureRequestsAccessorTest {

	@Test
	fun `feature requests page is sorted by votes`() = runTest {
		val api = FakePurecipesApi(initialFeatureRequests = featureRequests())
		val accessor = FeatureRequestsAccessor(FeatureRequestsRemoteDataSource(api))

		val outcome = accessor.getFeatureRequestsPage(
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
	fun `feature requests page can be filtered by status`() = runTest {
		val api = FakePurecipesApi(initialFeatureRequests = featureRequests())
		val accessor = FeatureRequestsAccessor(FeatureRequestsRemoteDataSource(api))

		val outcome = accessor.getFeatureRequestsPage(
			sort = FeatureRequestSort.NEWEST,
			status = FeatureRequestStatus.PLANNED,
			pageNumber = 1,
			pageSize = 20,
		)

		outcome.get()?.items?.map { it.id } shouldBe listOf(2)
	}

	@Test
	fun `created feature request is returned by the list endpoint`() = runTest {
		val api = FakePurecipesApi()
		val accessor = FeatureRequestsAccessor(FeatureRequestsRemoteDataSource(api))

		val created = accessor.createFeatureRequest(title = "Meal planner", description = "Plan a week")
		val page = accessor.getFeatureRequestsPage(
			sort = FeatureRequestSort.NEWEST,
			status = null,
			pageNumber = 1,
			pageSize = 20,
		)

		api.createdFeatureRequests.map { it.title } shouldBe listOf("Meal planner")
		created.get()?.status shouldBe FeatureRequestStatus.OPEN
		page.get()?.totalMatches shouldBe 1
	}

	@Test
	fun `toggling a vote twice restores the original vote count`() = runTest {
		val api = FakePurecipesApi(initialFeatureRequests = featureRequests())
		val accessor = FeatureRequestsAccessor(FeatureRequestsRemoteDataSource(api))

		val voted = accessor.toggleFeatureRequestVote(requestId = 1)
		val unvoted = accessor.toggleFeatureRequestVote(requestId = 1)

		api.toggledFeatureRequestVoteIds shouldBe listOf(1, 1)
		voted.get()?.voteCount shouldBe 4
		voted.get()?.votedByCurrentUser shouldBe true
		unvoted.get()?.voteCount shouldBe 3
		unvoted.get()?.votedByCurrentUser shouldBe false
	}

	@Test
	fun `added comment is returned by the comments endpoint`() = runTest {
		val api = FakePurecipesApi(initialFeatureRequests = featureRequests())
		val accessor = FeatureRequestsAccessor(FeatureRequestsRemoteDataSource(api))

		val added = accessor.addFeatureRequestComment(requestId = 1, body = "Yes please")
		val comments = accessor.getFeatureRequestComments(requestId = 1)

		added.getError() shouldBe null
		comments.get()?.map { it.body } shouldBe listOf("Yes please")
	}

	@Test
	fun `feature request detail is returned for a known id`() = runTest {
		val api = FakePurecipesApi(initialFeatureRequests = featureRequests())
		val accessor = FeatureRequestsAccessor(FeatureRequestsRemoteDataSource(api))

		val outcome = accessor.getFeatureRequest(requestId = 2)

		outcome.get()?.title shouldBe "Meal planner"
		outcome.getError() shouldBe null
	}

	private fun featureRequests(): List<FeatureRequest> = listOf(
		FeatureRequest(
			id = 1,
			title = "Shopping list",
			description = "Turn ingredients into a shopping list.",
			status = FeatureRequestStatus.OPEN,
			voteCount = 3,
			commentCount = 0,
			createdAtEpochMillis = 10L,
			votedByCurrentUser = false,
		),
		FeatureRequest(
			id = 2,
			title = "Meal planner",
			description = "Plan a whole week.",
			status = FeatureRequestStatus.PLANNED,
			voteCount = 8,
			commentCount = 0,
			createdAtEpochMillis = 40L,
			votedByCurrentUser = false,
		),
	)
}
