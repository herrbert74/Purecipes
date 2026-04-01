package com.purecipes.feature.recipedetails.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsViewModelTest {

	@Test
	fun detailsViewModelLoadsRecipeDetails() = runTest {
		val recipe = sampleRecipeDetails()
		val repository = FakeRecipeDetailsRepository(Ok(recipe))
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			coroutineScope = this,
		)

		advanceUntilIdle()

		assertEquals(recipe, viewModel.recipeDetails)
		assertNull(viewModel.errorMessage)
		assertFalse(viewModel.isLoading)
	}

	@Test
	fun detailsViewModelExposesRepositoryError() = runTest {
		val repository = FakeRecipeDetailsRepository(Err(Failure.ServerError("Recipe failed")))
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			coroutineScope = this,
		)

		advanceUntilIdle()

		assertEquals("Recipe failed", viewModel.errorMessage)
		assertNull(viewModel.recipeDetails)
		assertFalse(viewModel.isLoading)
	}

	@Test
	fun toggleFavoriteUpdatesRecipeState() = runTest {
		val repository = FakeRecipeDetailsRepository(Ok(sampleRecipeDetails()))
		val favoritesRepository = FakeFavoritesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()
		advanceUntilIdle()

		assertTrue(viewModel.recipeDetails?.isFavorite == true)
		assertEquals(1, viewModel.favoriteChangeCount)
		assertNull(viewModel.favoriteErrorMessage)
	}

	@Test
	fun toggleFavoriteMarksUpdatingSynchronously() = runTest {
		val repository = FakeRecipeDetailsRepository(Ok(sampleRecipeDetails()))
		val favoriteStarted = CompletableDeferred<Unit>()
		val finishFavorite = CompletableDeferred<Unit>()
		val favoritesRepository = BlockingFavoritesRepository(favoriteStarted, finishFavorite)
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()

		assertTrue(viewModel.isFavoriteUpdating)
		assertNull(viewModel.favoriteErrorMessage)
		assertFalse(favoriteStarted.isCompleted)

		advanceUntilIdle()
		assertTrue(favoriteStarted.isCompleted)
		assertTrue(viewModel.isFavoriteUpdating)

		finishFavorite.complete(Unit)
		advanceUntilIdle()

		assertFalse(viewModel.isFavoriteUpdating)
		assertTrue(viewModel.recipeDetails?.isFavorite == true)
	}

	private class FakeRecipeDetailsRepository(
		private val result: Outcome<RecipeDetails>,
	) : RecipeDetailsRepository {

		override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = result
	}

	private class FakeFavoritesRepository : FavoritesRepository {

		override suspend fun addFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)

		override suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>> = Ok(emptyList())

		override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)
	}

	private class BlockingFavoritesRepository(
		private val favoriteStarted: CompletableDeferred<Unit>,
		private val finishFavorite: CompletableDeferred<Unit>,
	) : FavoritesRepository {

		override suspend fun addFavorite(recipeId: Int): Outcome<Unit> {
			favoriteStarted.complete(Unit)
			finishFavorite.await()
			return Ok(Unit)
		}

		override suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>> = Ok(emptyList())

		override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)
	}
}

internal fun sampleRecipeDetails(): RecipeDetails = RecipeDetails(
	id = 42,
	title = "Tomato Pasta",
	description = "Simple dinner.",
	imageUrl = "https://example.com/pasta.jpg",
	ingredientGroups = listOf(
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf("2 tomatoes", "1 garlic clove"),
		),
	),
	steps = listOf("Boil pasta", "Make sauce", "Serve"),
	totalTime = 25,
	yields = "2 servings",
	cuisine = Cuisine.ITALIAN,
)
