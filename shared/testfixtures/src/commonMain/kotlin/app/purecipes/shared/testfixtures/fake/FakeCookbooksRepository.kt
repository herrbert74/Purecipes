package app.purecipes.shared.testfixtures.fake

import com.github.michaelbull.result.Ok
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.SearchResultsPage

class FakeCookbooksRepository(
	private val cookbooksPageResult: Outcome<CookbookListPage> = Ok(
		CookbookListPage(
			items = emptyList(),
			pageNumber = 1,
			pageSize = 20,
			totalMatches = 0,
		),
	),
	private val createCookbookResult: Outcome<CookbookSummary> = Ok(
		CookbookSummary(id = 1, name = "Test", recipeCount = 0, updatedAtEpochMillis = 0L),
	),
	private val cookbookRecipesPageResult: Outcome<SearchResultsPage> = Ok(
		SearchResultsPage(
			items = emptyList(),
			pageNumber = 1,
			pageSize = 20,
			totalMatches = 0,
		),
	),
	private val deleteCookbookResult: Outcome<Unit> = Ok(Unit),
) : CookbooksRepository {

	var createCookbookCallCount: Int = 0
		private set

	var addRecipeToCookbookCallCount: Int = 0
		private set

	var deleteCookbookCallCount: Int = 0
		private set

	override suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage> =
		cookbooksPageResult

	override suspend fun createCookbook(name: String): Outcome<CookbookSummary> {
		createCookbookCallCount += 1
		return createCookbookResult
	}

	override suspend fun deleteCookbook(cookbookId: Int): Outcome<Unit> {
		deleteCookbookCallCount += 1
		return deleteCookbookResult
	}

	override suspend fun getCookbookRecipesPage(
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> = cookbookRecipesPageResult

	override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> {
		addRecipeToCookbookCallCount += 1
		return Ok(Unit)
	}

	override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> = Ok(Unit)

	override suspend fun getRecipeCookbooks(recipeId: Int): Outcome<List<CookbookRef>> = Ok(emptyList())
}
