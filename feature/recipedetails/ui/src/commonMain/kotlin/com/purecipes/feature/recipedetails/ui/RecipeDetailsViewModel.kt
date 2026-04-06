package com.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.analytics.domain.model.AnalyticsEvent
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeFormatHandling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class RecipeDetailsViewModel(
	private val recipeId: Int,
	private val addFavoriteRecipe: AddFavoriteRecipeUseCase,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val markMeasurementMismatchSeen: MarkMeasurementMismatchSeenUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	private val trackEvent: TrackEventUseCase,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	var isLoading by mutableStateOf(true)
		private set

	var recipeDetails by mutableStateOf<RecipeDetails?>(null)
		private set

	var isRecipeConverted by mutableStateOf(false)
		private set

	var showMeasurementMismatchDialog by mutableStateOf(false)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var favoriteErrorMessage by mutableStateOf<String?>(null)
		private set

	var isFavoriteUpdating by mutableStateOf(false)
		private set

	var favoriteChangeCount by mutableIntStateOf(0)
		private set

	private var baseRecipeDetails: RecipeDetails? = null

	private var measurementPreferences: MeasurementPreferences? = null

	init {
		loadRecipe()
	}

	fun retry() {
		loadRecipe()
	}

	fun dismissMeasurementMismatchDialog() {
		showMeasurementMismatchDialog = false
		scope.launch {
			markMeasurementMismatchSeen(recipeId)
		}
	}

	fun convertCurrentRecipe() {
		val rawRecipe = baseRecipeDetails ?: return
		val preferences = measurementPreferences ?: return
		val processed = processRecipeDetailsForMeasurementPreferences(
			recipe = rawRecipe,
			preferences = preferences.copy(formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED),
		)
		recipeDetails = processed.recipe
		isRecipeConverted = processed.isConverted
		showMeasurementMismatchDialog = false
		scope.launch {
			markMeasurementMismatchSeen(recipeId)
		}
	}

	fun toggleFavorite() {
		val currentRecipe = recipeDetails ?: return
		if (isFavoriteUpdating) return
		isFavoriteUpdating = true
		favoriteErrorMessage = null

		scope.launch {
			val outcome = if (currentRecipe.isFavorite) {
				removeFavoriteRecipe(currentRecipe.id)
			} else {
				addFavoriteRecipe(currentRecipe.id)
			}

			if (outcome.getError() == null) {
					baseRecipeDetails = baseRecipeDetails?.copy(isFavorite = !currentRecipe.isFavorite)
				recipeDetails = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)
				favoriteChangeCount += 1
				trackEvent(
					AnalyticsEvent.FavoriteChanged(
						recipeId = currentRecipe.id,
						isFavorite = !currentRecipe.isFavorite,
					),
				)
			} else {
				favoriteErrorMessage = outcome.getError()?.message
			}
			isFavoriteUpdating = false
		}
	}

	private fun loadRecipe() {
		scope.launch {
			isLoading = true
			errorMessage = null
			favoriteErrorMessage = null
			recipeDetails = null
				showMeasurementMismatchDialog = false

				measurementPreferences = getMeasurementPreferences()
			val outcome = getRecipeDetails(recipeId)
				baseRecipeDetails = outcome.get()
				val processedRecipe = baseRecipeDetails?.let { loadedRecipe ->
					processRecipeDetailsForMeasurementPreferences(
						recipe = loadedRecipe,
						preferences = measurementPreferences ?: return@let null,
					)
				}
				recipeDetails = processedRecipe?.recipe
				isRecipeConverted = processedRecipe?.isConverted == true
				showMeasurementMismatchDialog = processedRecipe?.shouldShowMismatchNotification == true
			if (recipeDetails != null) {
				trackEvent(AnalyticsEvent.RecipeViewed(recipeId))
			}
			errorMessage = outcome.getError()?.message
			isLoading = false
		}
	}

	override fun onCleared() {
		if (ownsCoroutineScope) {
			scope.cancel()
		}
	}
}

@Composable
internal fun recipeDetailsViewModel(
	recipeId: Int,
	addFavoriteRecipe: AddFavoriteRecipeUseCase,
	getRecipeDetails: GetRecipeDetailsUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	markMeasurementMismatchSeen: MarkMeasurementMismatchSeenUseCase,
	processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	trackEvent: TrackEventUseCase,
	sessionKey: String?,
): RecipeDetailsViewModel {
	val viewModelKey =
		buildString {
			append("RecipeDetailsViewModel:")
			append(recipeId)
			append(':')
			append(addFavoriteRecipe.hashCode())
			append(':')
			append(getRecipeDetails.hashCode())
			append(':')
			append(getMeasurementPreferences.hashCode())
			append(':')
			append(removeFavoriteRecipe.hashCode())
			append(':')
			append(trackEvent.hashCode())
			append(':')
			append(sessionKey ?: "signed-out")
		}
	return viewModel(
		key = viewModelKey,
		factory = viewModelFactory {
			initializer {
				RecipeDetailsViewModel(
					recipeId = recipeId,
					addFavoriteRecipe = addFavoriteRecipe,
					getRecipeDetails = getRecipeDetails,
					getMeasurementPreferences = getMeasurementPreferences,
					markMeasurementMismatchSeen = markMeasurementMismatchSeen,
					processRecipeDetailsForMeasurementPreferences = processRecipeDetailsForMeasurementPreferences,
					removeFavoriteRecipe = removeFavoriteRecipe,
					trackEvent = trackEvent,
				)
			}
		},
	)
}
