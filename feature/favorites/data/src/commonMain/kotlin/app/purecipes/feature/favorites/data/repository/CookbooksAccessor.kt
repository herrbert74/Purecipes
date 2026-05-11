package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.CookbooksDataSource
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository

class CookbooksAccessor(
	private val remoteDataSource: CookbooksDataSource.Remote,
) : CookbooksRepository {

	override suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int) =
		remoteDataSource.getCookbooksPage(pageNumber, pageSize)

	override suspend fun createCookbook(name: String) = remoteDataSource.createCookbook(name)

	override suspend fun deleteCookbook(cookbookId: Int) = remoteDataSource.deleteCookbook(cookbookId)

	override suspend fun getCookbookRecipesPage(cookbookId: Int, pageNumber: Int, pageSize: Int) =
		remoteDataSource.getCookbookRecipesPage(cookbookId, pageNumber, pageSize)

	override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int) =
		remoteDataSource.addRecipeToCookbook(cookbookId, recipeId)

	override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int) =
		remoteDataSource.removeRecipeFromCookbook(cookbookId, recipeId)

	override suspend fun getRecipeCookbooks(recipeId: Int) = remoteDataSource.getRecipeCookbooks(recipeId)
}
