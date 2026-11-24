package app.purecipes.feature.newrecipe.data.repository

import app.purecipes.feature.newrecipe.data.datasource.CreatedRecipeDataSource
import app.purecipes.feature.newrecipe.domain.model.CreatedRecipeEvent
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class CreatedRecipeAccessor(
	private val remoteDataSource: CreatedRecipeDataSource.Remote,
) : CreatedRecipeRepository {

	private val createdRecipeEvents = MutableSharedFlow<CreatedRecipeEvent>(extraBufferCapacity = 1)

	override suspend fun getCreatedRecipes() = remoteDataSource.getCreatedRecipes()

	override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest) =
		remoteDataSource.saveCreatedRecipe(request).also { outcome ->
			outcome.get()?.let { savedRecipe ->
				createdRecipeEvents.tryEmit(CreatedRecipeEvent.Saved(savedRecipe.id))
			}
		}

	override suspend fun deleteCreatedRecipe(recipeId: Int) =
		remoteDataSource.deleteCreatedRecipe(recipeId).also { outcome ->
			if (outcome.getError() == null) {
				createdRecipeEvents.tryEmit(CreatedRecipeEvent.Deleted(recipeId))
			}
		}

	override fun observeCreatedRecipeEvents(): Flow<CreatedRecipeEvent> = createdRecipeEvents.asSharedFlow()
}
