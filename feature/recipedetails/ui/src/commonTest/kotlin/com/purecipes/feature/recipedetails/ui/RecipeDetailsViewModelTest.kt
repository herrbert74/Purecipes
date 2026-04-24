package com.purecipes.feature.recipedetails.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import com.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import com.purecipes.shared.testfixtures.fake.fakeRecipeDetails
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
			coroutineScope = this,
		)

		advanceUntilIdle()

		assertEquals("Recipe failed", viewModel.errorMessage)
		assertNull(viewModel.recipeDetails)
		assertFalse(viewModel.isLoading)
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
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()
		advanceUntilIdle()

		assertEquals(viewModel.recipeDetails?.isFavorite, true)
		assertEquals(1, viewModel.favoriteChangeCount)
		assertNull(viewModel.favoriteErrorMessage)
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
		assertEquals(viewModel.recipeDetails?.isFavorite, true)
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

		override suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>> = Ok(emptyList())

		override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)
	}

}
