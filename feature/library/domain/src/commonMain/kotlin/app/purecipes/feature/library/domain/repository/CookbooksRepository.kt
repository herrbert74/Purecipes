package app.purecipes.feature.library.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.SearchResultsPage

interface CookbooksRepository {

	suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage>

	suspend fun createCookbook(name: String): Outcome<CookbookSummary>

	suspend fun deleteCookbook(cookbookId: Int): Outcome<Unit>

	suspend fun getCookbookRecipesPage(
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage>

	suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit>

	suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit>

	suspend fun getRecipeCookbooks(recipeId: Int): Outcome<List<CookbookRef>>
}
