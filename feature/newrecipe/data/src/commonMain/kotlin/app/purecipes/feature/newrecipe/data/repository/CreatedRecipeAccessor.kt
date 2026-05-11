package app.purecipes.feature.newrecipe.data.repository

import app.purecipes.feature.newrecipe.data.datasource.CreatedRecipeDataSource
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository

class CreatedRecipeAccessor(
	private val remoteDataSource: CreatedRecipeDataSource.Remote,
) : CreatedRecipeRepository {

	override suspend fun getCreatedRecipes() = remoteDataSource.getCreatedRecipes()

	override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest) =
		remoteDataSource.saveCreatedRecipe(request)
}
