package app.purecipes.feature.recipedetails.data.repository

import app.purecipes.feature.recipedetails.data.datasource.RecipeDetailsRemoteDataSource
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.RecipeActivityEvent
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RecipeActivityAccessorTest {

	@Test
	fun `records the recipe activity event`() = runTest {
		val api = FakePurecipesApi()
		val accessor = RecipeActivityAccessor(RecipeDetailsRemoteDataSource(api))

		accessor.record(recipeId = 7, event = RecipeActivityEvent.COOK_COMPLETE).get() shouldBe Unit
		api.recordedRecipeActivity shouldBe listOf(7 to RecipeActivityEvent.COOK_COMPLETE)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `each successful open is published for recently viewed`() = runTest {
		val accessor = RecipeActivityAccessor(RecipeDetailsRemoteDataSource(FakePurecipesApi()))
		val opens = mutableListOf<Int>()
		val collecting = launch { accessor.observeRecipeViews().collect { recipeId -> opens += recipeId } }
		advanceUntilIdle()

		accessor.record(recipeId = 7, event = RecipeActivityEvent.OPEN)
		advanceUntilIdle()
		accessor.record(recipeId = 7, event = RecipeActivityEvent.OPEN)
		advanceUntilIdle()
		accessor.record(recipeId = 8, event = RecipeActivityEvent.COOK_COMPLETE)
		advanceUntilIdle()

		opens shouldBe listOf(7, 7)
		collecting.cancel()
	}
}
