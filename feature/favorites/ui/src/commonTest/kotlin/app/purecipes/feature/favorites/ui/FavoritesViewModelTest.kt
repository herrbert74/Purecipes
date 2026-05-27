package app.purecipes.feature.favorites.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

	private val getCookbookCoverImageUrl =
		GetCookbookCoverImageUrlUseCase(
			repository = object : CookbookCoverRepository {
				override fun getCookbookCoverImageUrl(
					cookbookId: Int,
					candidateImageUrls: List<String>,
					nowMillis: Long,
					random: Random,
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
			deleteCookbookUseCase = DeleteCookbookUseCase(FakeCookbooksRepository()),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			shareCookbook = unusedShareCookbookUseCase(),
			sessionKey = "session",
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
			deleteCookbookUseCase = DeleteCookbookUseCase(FakeCookbooksRepository()),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			shareCookbook = unusedShareCookbookUseCase(),
			sessionKey = "session",
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
			deleteCookbookUseCase = DeleteCookbookUseCase(cookbooksRepository),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			shareCookbook = unusedShareCookbookUseCase(),
			sessionKey = "session",
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

	@Test
	fun `delete cookbook rejects non-empty cookbook`() = runTest {
		val cookbooksRepository = FakeCookbooksRepository()
		val viewModel = FavoritesViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(FakeFavoritesRepository()),
			getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
			createCookbook = CreateCookbookUseCase(cookbooksRepository),
			deleteCookbookUseCase = DeleteCookbookUseCase(cookbooksRepository),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			shareCookbook = unusedShareCookbookUseCase(),
			sessionKey = "session",
			coroutineScope = this,
		)
		var deleted = true

		viewModel.deleteCookbook(
			CookbookSummary(
				id = 10,
				name = "Weeknight Dinners",
				recipeCount = 1,
				updatedAtEpochMillis = 0L,
			),
		) { ok ->
			deleted = ok
		}

		kotlin.test.assertEquals(false, deleted)
		kotlin.test.assertEquals("Only empty cookbooks can be deleted", viewModel.deleteCookbookError)
		kotlin.test.assertEquals(0, cookbooksRepository.deleteCookbookCallCount)
	}

	@Test
	fun `delete cookbook succeeds for empty cookbook`() = runTest {
		val cookbooksRepository = FakeCookbooksRepository()
		val viewModel = FavoritesViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(FakeFavoritesRepository()),
			getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
			createCookbook = CreateCookbookUseCase(cookbooksRepository),
			deleteCookbookUseCase = DeleteCookbookUseCase(cookbooksRepository),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			shareCookbook = unusedShareCookbookUseCase(),
			sessionKey = "session",
			coroutineScope = this,
		)
		var deleted = false

		viewModel.deleteCookbook(
			CookbookSummary(
				id = 11,
				name = "Empty Cookbook",
				recipeCount = 0,
				updatedAtEpochMillis = 0L,
			),
		) { ok ->
			deleted = ok
		}
		advanceUntilIdle()

		kotlin.test.assertEquals(true, deleted)
		kotlin.test.assertEquals(null, viewModel.deleteCookbookError)
		kotlin.test.assertEquals(1, cookbooksRepository.deleteCookbookCallCount)
	}
}
