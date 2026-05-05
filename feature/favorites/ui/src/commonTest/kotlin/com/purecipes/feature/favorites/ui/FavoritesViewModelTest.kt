package com.purecipes.feature.favorites.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import com.purecipes.shared.domain.model.CookbookListPage
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchResultsPage
import com.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import com.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

	private val getCookbookCoverImageUrl =
		com.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase(
		repository = object : CookbookCoverRepository {
			override fun getCookbookCoverImageUrl(
				cookbookId: Int,
				candidateImageUrls: List<String>,
				nowMillis: Long,
				random: kotlin.random.Random,
			): String? = candidateImageUrls.firstOrNull()
		},
	)

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
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			coroutineScope = this,
		)

		viewModel.loadFavorites()
		advanceUntilIdle()

		kotlin.test.assertEquals(expected, viewModel.savedRecipes.toList())
		kotlin.test.assertEquals(null, viewModel.savedErrorMessage)
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
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			coroutineScope = this,
		)

		viewModel.loadFavorites()
		advanceUntilIdle()

		kotlin.test.assertEquals("Favorites failed", viewModel.savedErrorMessage)
		kotlin.test.assertEquals(emptyList(), viewModel.savedRecipes.toList())
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
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			coroutineScope = this,
		)
		viewModel.loadFavorites()
		advanceUntilIdle()

		var created = true
		viewModel.createCookbookFromName("  weeknight dinners ") { ok ->
			created = ok
		}
		advanceUntilIdle()

		kotlin.test.assertEquals(false, created)
		kotlin.test.assertEquals("Cookbook already exists", viewModel.createCookbookError)
		kotlin.test.assertEquals(0, cookbooksRepository.createCookbookCallCount)
	}
}
