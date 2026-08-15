package app.purecipes.feature.library.data.repository

import app.purecipes.feature.library.data.datasource.CookbooksDataSource
import app.purecipes.feature.library.domain.repository.CookbooksRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
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
