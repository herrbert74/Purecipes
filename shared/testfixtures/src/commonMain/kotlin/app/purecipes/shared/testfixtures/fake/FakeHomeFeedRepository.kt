package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.home.domain.repository.HomeFeedRepository
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Ok

class FakeHomeFeedRepository(
	var result: Outcome<HomeFeed> = Ok(HomeFeed()),
) : HomeFeedRepository {

	var calls: Int = 0
		private set

	var shelfPages: Map<HomeShelfId, Outcome<SearchResultsPage>> = emptyMap()

	var collectionPages: Map<String, Outcome<SearchResultsPage>> = emptyMap()

	val shelfPageCalls = mutableListOf<HomeShelfId>()

	val habitCuisineCalls = mutableListOf<String?>()

	var habitShelfPages: Map<String, Outcome<SearchResultsPage>> = emptyMap()

	val collectionPageCalls = mutableListOf<String>()

	override suspend fun getHomeFeed(): Outcome<HomeFeed> {
		calls += 1
		return result
	}

	override suspend fun getShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
		habitCuisine: String?,
	): Outcome<SearchResultsPage> {
		shelfPageCalls += shelfId
		habitCuisineCalls += habitCuisine
		val habitPage = habitCuisine?.let { cuisine -> habitShelfPages[cuisine] }
		return habitPage ?: shelfPages[shelfId] ?: Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = 0,
			),
		)
	}

	override suspend fun getCollectionPage(
		slug: String,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> {
		collectionPageCalls += slug
		return collectionPages[slug] ?: Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = 0,
			),
		)
	}
}
