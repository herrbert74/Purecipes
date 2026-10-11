package app.purecipes.feature.recipedetails.data.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.recipedetails.data.datasource.RecipeDetailsDataSource
import app.purecipes.feature.recipedetails.domain.repository.RecipeActivityRepository
import app.purecipes.shared.domain.model.RecipeActivityEvent
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class RecipeActivityAccessor(
	private val remoteDataSource: RecipeDetailsDataSource.Remote,
) : RecipeActivityRepository {

	private val recipeViews = MutableSharedFlow<Int>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)

	override suspend fun record(recipeId: Int, event: RecipeActivityEvent): Outcome<Unit> {
		return remoteDataSource.recordRecipeActivity(recipeId, event).also { outcome ->
			if (outcome.getError() == null && event == RecipeActivityEvent.OPEN) {
				recipeViews.tryEmit(recipeId)
			}
		}
	}

	override fun observeRecipeViews(): Flow<Int> = recipeViews.asSharedFlow()
}
