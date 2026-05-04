package com.purecipes.feature.recipedetails.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.SearchResultsPage
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import com.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import com.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import com.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsViewModelTest {

	private val fakeCookbooksRepository = FakeCookbooksRepository()

	@Test
	fun detailsViewModelLoadsRecipeDetails() = runTest {
		val recipe = fakeRecipeDetails()
		val repository = FakeRecipeDetailsRepository(Ok(recipe))
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			sessionKey = null,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			coroutineScope = this,
		)

		advanceUntilIdle()

		viewModel.recipeDetails shouldBe recipe
		viewModel.errorMessage shouldBe null
		viewModel.isLoading shouldBe false
	}

	@Test
	fun detailsViewModelExposesRepositoryError() = runTest {
		val repository = FakeRecipeDetailsRepository(Err(Failure.ServerError("Recipe failed")))
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			sessionKey = null,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			coroutineScope = this,
		)

		advanceUntilIdle()

		viewModel.errorMessage shouldBe "Recipe failed"
		viewModel.recipeDetails shouldBe null
		viewModel.isLoading shouldBe false
	}

	@Test
	fun toggleFavoriteUpdatesRecipeState() = runTest {
		val repository = FakeRecipeDetailsRepository(Ok(fakeRecipeDetails()))
		val favoritesRepository = FakeFavoritesRepository()
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			sessionKey = null,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()
		advanceUntilIdle()

		true shouldBe viewModel.recipeDetails?.isFavorite
		viewModel.favoriteChangeCount shouldBe 1
		viewModel.favoriteErrorMessage shouldBe null
	}

	@Test
	fun toggleFavoriteMarksUpdatingSynchronously() = runTest {
		val repository = FakeRecipeDetailsRepository(Ok(fakeRecipeDetails()))
		val favoriteStarted = CompletableDeferred<Unit>()
		val finishFavorite = CompletableDeferred<Unit>()
		val favoritesRepository = BlockingFavoritesRepository(favoriteStarted, finishFavorite)
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			sessionKey = null,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()

		viewModel.isFavoriteUpdating shouldBe true
		viewModel.favoriteErrorMessage shouldBe null
		favoriteStarted.isCompleted shouldBe false

		advanceUntilIdle()
		favoriteStarted.isCompleted shouldBe true
		viewModel.isFavoriteUpdating shouldBe true

		finishFavorite.complete(Unit)
		advanceUntilIdle()

		viewModel.isFavoriteUpdating shouldBe false
		true shouldBe viewModel.recipeDetails?.isFavorite
	}

	private class BlockingFavoritesRepository(
		private val favoriteStarted: CompletableDeferred<Unit>,
		private val finishFavorite: CompletableDeferred<Unit>,
	) : com.purecipes.feature.favorites.domain.repository.FavoritesRepository {

		override suspend fun addFavorite(recipeId: Int): Outcome<Unit> {
			favoriteStarted.complete(Unit)
			finishFavorite.await()
			return Ok(Unit)
		}

		override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int) = Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = 0,
			),
		)

		override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)
	}

}
