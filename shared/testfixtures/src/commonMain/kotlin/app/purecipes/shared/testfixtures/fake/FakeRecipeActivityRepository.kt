package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.recipedetails.domain.repository.RecipeActivityRepository
import app.purecipes.shared.domain.model.RecipeActivityEvent
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeRecipeActivityRepository : RecipeActivityRepository {

	val events = mutableListOf<Pair<Int, RecipeActivityEvent>>()

	private val recipeViews = MutableSharedFlow<Int>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)

	override suspend fun record(recipeId: Int, event: RecipeActivityEvent): Outcome<Unit> {
		events += recipeId to event
		if (event == RecipeActivityEvent.OPEN) {
			recipeViews.tryEmit(recipeId)
		}
		return Ok(Unit)
	}

	override fun observeRecipeViews(): Flow<Int> = recipeViews.asSharedFlow()
}
