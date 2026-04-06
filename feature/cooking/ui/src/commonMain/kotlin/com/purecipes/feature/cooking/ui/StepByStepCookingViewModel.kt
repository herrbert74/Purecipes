package com.purecipes.feature.cooking.ui

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
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class StepByStepCookingViewModel(
	private val recipeId: Int,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val trackEvent: TrackEventUseCase,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	var isLoading by mutableStateOf(true)
		private set

	var recipeDetails by mutableStateOf<RecipeDetails?>(null)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var currentStepIndex by mutableIntStateOf(0)
		private set

	init {
		loadRecipe()
	}

	fun previousStep() {
		if (currentStepIndex > 0) {
			currentStepIndex -= 1
		}
	}

	fun nextStep() {
		val details = recipeDetails ?: return
		if (currentStepIndex < details.steps.lastIndex) {
			currentStepIndex += 1
		}
	}

	fun setCurrentStep(stepIndex: Int) {
		val lastIndex = recipeDetails?.steps?.lastIndex ?: return
		currentStepIndex = stepIndex.coerceIn(0, lastIndex)
	}

	private fun loadRecipe() {
		scope.launch {
			isLoading = true
			errorMessage = null
			recipeDetails = null

				val preferences = getMeasurementPreferences()
			val outcome = getRecipeDetails(recipeId)
				recipeDetails = outcome.get()?.let { recipe ->
					processRecipeDetailsForMeasurementPreferences(recipe, preferences).recipe
				}
			if (recipeDetails != null) {
				trackEvent(AnalyticsEvent.CookingStarted(recipeId))
			}
			errorMessage = outcome.getError()?.message
			currentStepIndex = 0
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
internal fun stepByStepCookingViewModel(
	recipeId: Int,
	getRecipeDetails: GetRecipeDetailsUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	trackEvent: TrackEventUseCase,
): StepByStepCookingViewModel {
	val viewModelKey = buildString {
		append("StepByStepCookingViewModel:")
		append(recipeId)
		append(':')
		append(getRecipeDetails.hashCode())
		append(':')
		append(getMeasurementPreferences.hashCode())
		append(':')
		append(trackEvent.hashCode())
	}
	return viewModel(
		key = viewModelKey,
		factory = viewModelFactory {
			initializer {
				StepByStepCookingViewModel(
					recipeId = recipeId,
					getRecipeDetails = getRecipeDetails,
					getMeasurementPreferences = getMeasurementPreferences,
					processRecipeDetailsForMeasurementPreferences = processRecipeDetailsForMeasurementPreferences,
					trackEvent = trackEvent,
				)
			}
		},
	)
}
