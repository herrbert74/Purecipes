package com.purecipes.feature.favorites.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.domain.model.CookbookListPage
import com.purecipes.shared.domain.model.CookbookRef
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.domain.model.SearchResultsPage

interface CookbooksDataSource {

	interface Remote {
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
}
