package com.purecipes.feature.favorites.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.CookbookListPage
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchResultsPage
import com.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import com.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

	@Test
	fun `load favorites populates recipes`() = runTest {
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
		val favoritesRepo = FakeFavoritesRepository(
			getFavoriteRecipesPageResult = Ok(
				SearchResultsPage(
					items = expected,
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = FavoritesViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
			getCookbooksPage = GetCookbooksPageUseCase(FakeCookbooksRepository()),
			createCookbook = CreateCookbookUseCase(FakeCookbooksRepository()),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
			coroutineScope = this,
		)

		viewModel.loadFavorites()
		advanceUntilIdle()

		viewModel.savedRecipes.toList() shouldBe expected
		viewModel.savedErrorMessage shouldBe null
	}

	@Test
	fun `load favorites exposes error`() = runTest {
		val viewModel = FavoritesViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(
				FakeFavoritesRepository(
					getFavoriteRecipesPageResult = Err(Failure.ServerError("Favorites failed")),
				),
			),
			getCookbooksPage = GetCookbooksPageUseCase(FakeCookbooksRepository()),
			createCookbook = CreateCookbookUseCase(FakeCookbooksRepository()),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
			coroutineScope = this,
		)

		viewModel.loadFavorites()
		advanceUntilIdle()

		viewModel.savedErrorMessage shouldBe "Favorites failed"
		viewModel.savedRecipes.toList() shouldBe emptyList()
	}

	@Test
	fun `create cookbook from name rejects duplicate name`() = runTest {
		val existingCookbook = CookbookSummary(
			id = 10,
			name = "Weeknight Dinners",
			recipeCount = 0,
			updatedAtEpochMillis = 0L,
		)
		val cookbooksRepository = FakeCookbooksRepository(
			cookbooksPageResult = Ok(
				CookbookListPage(
					items = listOf(existingCookbook),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = FavoritesViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(FakeFavoritesRepository()),
			getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
			createCookbook = CreateCookbookUseCase(cookbooksRepository),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
			coroutineScope = this,
		)
		viewModel.loadFavorites()
		advanceUntilIdle()

		var created = true
		viewModel.createCookbookFromName("  weeknight dinners ") { ok ->
			created = ok
		}
		advanceUntilIdle()

		created shouldBe false
		viewModel.createCookbookError shouldBe "Cookbook already exists"
		cookbooksRepository.createCookbookCallCount shouldBe 0
	}
}
