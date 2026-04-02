package com.purecipes.feature.favorites.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

	@Test
	fun loadFavoritesPopulatesRecipes() = runTest {
		val expected = listOf(
			RecipeSummary(
				id = 42,
				title = "Tomato Pasta",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 25,
				isFavorite = true,
			),
		)
		val viewModel = FavoritesViewModel(
			getFavoriteRecipes = GetFavoriteRecipesUseCase(FakeFavoritesRepository(Ok(expected))),
			coroutineScope = this,
		)

		viewModel.loadFavorites()
		advanceUntilIdle()

		assertEquals(expected, viewModel.recipes.toList())
		assertNull(viewModel.errorMessage)
		assertFalse(viewModel.isLoading)
	}

	@Test
	fun loadFavoritesExposesError() = runTest {
		val viewModel = FavoritesViewModel(
			getFavoriteRecipes = GetFavoriteRecipesUseCase(
				FakeFavoritesRepository(Err(Failure.ServerError("Favorites failed"))),
			),
			coroutineScope = this,
		)

		viewModel.loadFavorites()
		advanceUntilIdle()

		assertEquals("Favorites failed", viewModel.errorMessage)
		assertEquals(emptyList(), viewModel.recipes.toList())
		assertFalse(viewModel.isLoading)
	}

	private class FakeFavoritesRepository(
		private val result: Outcome<List<RecipeSummary>>,
	) : FavoritesRepository {

		override suspend fun addFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)

		override suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>> = result

		override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)
	}
}
