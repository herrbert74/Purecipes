package app.purecipes.feature.recipedetails.data.repository

import app.purecipes.feature.recipedetails.data.datasource.RecipeDetailsDataSource
import app.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class RecipeDetailsAccessor(
	private val remoteDataSource: RecipeDetailsDataSource.Remote,
) : RecipeDetailsRepository {

	override suspend fun getRecipeDetails(recipeId: Int) = remoteDataSource.getRecipeDetails(recipeId)
}
