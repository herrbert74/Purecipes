package app.purecipes.feature.newrecipe.data.repository

import app.purecipes.feature.newrecipe.data.datasource.CreatedRecipeDataSource
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class CreatedRecipeAccessor(
	private val remoteDataSource: CreatedRecipeDataSource.Remote,
) : CreatedRecipeRepository {

	override suspend fun getCreatedRecipes() = remoteDataSource.getCreatedRecipes()

	override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest) =
		remoteDataSource.saveCreatedRecipe(request)
}
