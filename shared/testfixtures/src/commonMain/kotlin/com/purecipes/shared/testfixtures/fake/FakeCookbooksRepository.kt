package com.purecipes.shared.testfixtures.fake

import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository
import com.purecipes.shared.domain.model.CookbookListPage
import com.purecipes.shared.domain.model.CookbookRef
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.domain.model.SearchResultsPage

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
) : CookbooksRepository {

	override suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage> =
		cookbooksPageResult

	override suspend fun createCookbook(name: String): Outcome<CookbookSummary> = createCookbookResult

	override suspend fun deleteCookbook(cookbookId: Int): Outcome<Unit> = Ok(Unit)

	override suspend fun getCookbookRecipesPage(
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> = cookbookRecipesPageResult

	override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> = Ok(Unit)

	override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> = Ok(Unit)

	override suspend fun getRecipeCookbooks(recipeId: Int): Outcome<List<CookbookRef>> = Ok(emptyList())
}
