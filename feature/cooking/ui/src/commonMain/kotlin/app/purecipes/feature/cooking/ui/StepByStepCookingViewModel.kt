package app.purecipes.feature.cooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.RecipeDetails
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AssistedInject
class StepByStepCookingViewModel(
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val trackEvent: TrackEventUseCase,
	@Assisted private val recipeId: Int,
) : ViewModel() {

	var isLoading by mutableStateOf(true)
		private set

	var recipeDetails by mutableStateOf<RecipeDetails?>(null)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var currentStepIndex by mutableIntStateOf(0)
		private set

	private var baseRecipeDetails: RecipeDetails? = null

	private var measurementPreferences: MeasurementPreferences? = null

	init {
		viewModelScope.launch {
			observeMeasurementPreferences().collectLatest { preferences ->
				measurementPreferences = preferences
				applyMeasurementPreferences()
			}
		}
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

	private fun applyMeasurementPreferences() {
		val rawRecipe = baseRecipeDetails ?: return
		val preferences = measurementPreferences ?: return
		recipeDetails = processRecipeDetailsForMeasurementPreferences(rawRecipe, preferences).recipe
	}

	private fun loadRecipe() {
		viewModelScope.launch {
			isLoading = true
			errorMessage = null
			recipeDetails = null

			val outcome = getRecipeDetails(recipeId)
			baseRecipeDetails = outcome.get()
			applyMeasurementPreferences()
			if (recipeDetails != null) {
				trackEvent(AnalyticsEvent.CookingStarted(recipeId))
			}
			errorMessage = outcome.getError()?.message
			currentStepIndex = 0
			isLoading = false
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(recipeId: Int): StepByStepCookingViewModel
	}
}
