package com.purecipes.feature.favorites.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

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

		viewModel.recipes.toList() shouldBe expected
		viewModel.errorMessage shouldBe null
		viewModel.isLoading shouldBe false
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

		viewModel.errorMessage shouldBe "Favorites failed"
		viewModel.recipes.toList() shouldBe emptyList()
		viewModel.isLoading shouldBe false
	}

}
