package app.purecipes.feature.library.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.CookbookCreateRequest
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class CookbooksRemoteDataSource(
	private val api: PurecipesApi,
) : CookbooksDataSource.Remote {

	override suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage> =
		runCatchingApi {
			api.getCookbooks(pageNumber = pageNumber, pageSize = pageSize)
		}

	override suspend fun createCookbook(name: String): Outcome<CookbookSummary> = runCatchingApi {
		api.createCookbook(CookbookCreateRequest(name = name))
	}

	override suspend fun deleteCookbook(cookbookId: Int): Outcome<Unit> = runCatchingApi {
		api.deleteCookbook(cookbookId)
	}

	override suspend fun getCookbookRecipesPage(
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> = runCatchingApi {
		api.getCookbookRecipes(cookbookId = cookbookId, pageNumber = pageNumber, pageSize = pageSize)
	}

	override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> = runCatchingApi {
		api.addRecipeToCookbook(cookbookId = cookbookId, recipeId = recipeId)
	}

	override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> = runCatchingApi {
		api.removeRecipeFromCookbook(cookbookId = cookbookId, recipeId = recipeId)
	}

	override suspend fun getRecipeCookbooks(recipeId: Int): Outcome<List<CookbookRef>> = runCatchingApi {
		api.getRecipeCookbooks(recipeId)
	}
}
