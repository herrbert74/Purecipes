package app.purecipes.feature.cooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.model.asHandledException
import app.purecipes.feature.analytics.domain.model.toAnalyticsErrorKind
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
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
import kotlin.time.TimeSource

@AssistedInject
class StepByStepCookingViewModel(
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val trackEvent: TrackEventUseCase,
	private val logBreadcrumb: LogBreadcrumbUseCase,
	private val sendHandledException: SendHandledExceptionUseCase,
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

	private val monotonicTimeSource = TimeSource.Monotonic

	private var cookingStartedMark = monotonicTimeSource.markNow()

	private var hasTrackedCookingCompleted = false

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
			trackCookingStepViewed()
		}
	}

	fun nextStep() {
		val details = recipeDetails ?: return
		if (currentStepIndex < details.steps.lastIndex) {
			currentStepIndex += 1
			trackCookingStepViewed()
			trackCookingCompletedIfNeeded()
		}
	}

	fun setCurrentStep(stepIndex: Int) {
		val details = recipeDetails ?: return
		val newIndex = stepIndex.coerceIn(0, details.steps.lastIndex)
		if (newIndex == currentStepIndex) {
			return
		}
		currentStepIndex = newIndex
		trackCookingStepViewed()
		trackCookingCompletedIfNeeded()
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
			hasTrackedCookingCompleted = false

			val outcome = getRecipeDetails(recipeId)
			baseRecipeDetails = outcome.get()
			applyMeasurementPreferences()
			val details = recipeDetails ?: baseRecipeDetails
			if (details != null) {
				cookingStartedMark = monotonicTimeSource.markNow()
				logBreadcrumb(CrashBreadcrumb.cookingStarted(recipeId))
				trackEvent(
					AnalyticsEvent.CookingStarted(
						recipeId = recipeId,
						origin = AnalyticsOrigin.RECIPE_DETAILS,
						stepCount = details.steps.size,
					),
				)
			}
			val error = outcome.getError()
			if (error != null) {
				sendHandledException(error.asHandledException())
				trackEvent(
					AnalyticsEvent.RecipeLoadFailed(
						recipeId = recipeId,
						errorKind = error.toAnalyticsErrorKind(),
					),
				)
			}
			errorMessage = error?.message
			currentStepIndex = 0
			isLoading = false
		}
	}

	private fun trackCookingStepViewed() {
		val details = recipeDetails ?: baseRecipeDetails ?: return
		logBreadcrumb(CrashBreadcrumb.cookingStepAdvanced(recipeId, currentStepIndex))
		trackEvent(
			AnalyticsEvent.CookingStepViewed(
				recipeId = recipeId,
				stepIndex = currentStepIndex,
				stepCount = details.steps.size,
			),
		)
	}

	private fun trackCookingCompletedIfNeeded() {
		val details = recipeDetails ?: baseRecipeDetails ?: return
		if (hasTrackedCookingCompleted || currentStepIndex != details.steps.lastIndex) {
			return
		}
		hasTrackedCookingCompleted = true
		trackEvent(
			AnalyticsEvent.CookingCompleted(
				recipeId = recipeId,
				durationSeconds = cookingStartedMark.elapsedNow().inWholeSeconds,
			),
		)
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(recipeId: Int): StepByStepCookingViewModel
	}
}
