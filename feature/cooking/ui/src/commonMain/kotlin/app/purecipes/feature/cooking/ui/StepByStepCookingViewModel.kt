package app.purecipes.feature.cooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AssistedInject
class StepByStepCookingViewModel(
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val trackEvent: TrackEventUseCase,
	@Assisted private val recipeId: Int,
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

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(recipeId: Int): StepByStepCookingViewModel
	}
}
