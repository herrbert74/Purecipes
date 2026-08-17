package app.purecipes.feature.cooking.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsShareType
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.model.asHandledException
import app.purecipes.feature.analytics.domain.model.toAnalyticsErrorKind
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.library.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.domain.model.CookbookSummary
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

private const val COOKBOOK_PICKER_PAGE_SIZE = 100

@AssistedInject
class StepByStepCookingViewModel(
	private val addFavoriteRecipe: AddFavoriteRecipeUseCase,
	private val addRecipeToCookbook: AddRecipeToCookbookUseCase,
	private val createCookbook: CreateCookbookUseCase,
	private val getCookbooksPage: GetCookbooksPageUseCase,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	private val shareRecipe: ShareRecipeUseCase,
	private val trackEvent: TrackEventUseCase,
	private val logBreadcrumb: LogBreadcrumbUseCase,
	private val sendHandledException: SendHandledExceptionUseCase,
	@Assisted private val recipeId: Int,
	@Assisted sessionKey: String?,
) : ViewModel() {

	private var activeSessionKey: String? = sessionKey

	var isLoading by mutableStateOf(true)
		private set

	var recipeDetails by mutableStateOf<RecipeDetails?>(null)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var favoriteErrorMessage by mutableStateOf<String?>(null)
		private set

	var isFavoriteUpdating by mutableStateOf(false)
		private set

	val sheetCookbooks = mutableStateListOf<CookbookSummary>()

	var cookbookActionError by mutableStateOf<String?>(null)
		private set

	var isCookbookActionInFlight by mutableStateOf(false)
		private set

	var currentPageIndex by mutableIntStateOf(0)
		private set

	private var baseRecipeDetails: RecipeDetails? = null

	private var measurementPreferences: MeasurementPreferences? = null

	private val monotonicTimeSource = TimeSource.Monotonic

	private var cookingStartedMark = monotonicTimeSource.markNow()

	private var hasTrackedCookingCompleted = false

	private var hasTrackedCookingAbandoned = false

	private var favoriteEventsJob: Job? = null

	init {
		viewModelScope.launch {
			observeMeasurementPreferences().collectLatest { preferences ->
				measurementPreferences = preferences
				applyMeasurementPreferences()
			}
		}
		loadRecipe()
		startFavoriteEventsCollection(sessionKey)
	}

	override fun onCleared() {
		trackCookingAbandonedIfNeeded()
		super.onCleared()
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == activeSessionKey) {
			return
		}
		activeSessionKey = sessionKey
		startFavoriteEventsCollection(sessionKey)
		if (sessionKey == null) {
			baseRecipeDetails = baseRecipeDetails?.copy(isFavorite = false)
			recipeDetails = recipeDetails?.copy(isFavorite = false)
			sheetCookbooks.clear()
		} else {
			refreshFavoriteState()
		}
	}

	fun previousStep() {
		if (currentPageIndex > 0) {
			currentPageIndex -= 1
			onPageChanged()
		}
	}

	fun nextStep() {
		val details = recipeDetails ?: return
		if (currentPageIndex < finishPageIndex(details)) {
			currentPageIndex += 1
			onPageChanged()
		}
	}

	fun setCurrentPage(pageIndex: Int) {
		val details = recipeDetails ?: return
		val newIndex = pageIndex.coerceIn(0, finishPageIndex(details))
		if (newIndex == currentPageIndex) {
			return
		}
		currentPageIndex = newIndex
		onPageChanged()
	}

	fun toggleFavorite() {
		val currentRecipe = recipeDetails ?: return
		if (isFavoriteUpdating) return
		isFavoriteUpdating = true
		favoriteErrorMessage = null

		viewModelScope.launch {
			val outcome = if (currentRecipe.isFavorite) {
				removeFavoriteRecipe(currentRecipe.id)
			} else {
				addFavoriteRecipe(currentRecipe.id)
			}

			if (outcome.getError() == null) {
				baseRecipeDetails = baseRecipeDetails?.copy(isFavorite = !currentRecipe.isFavorite)
				recipeDetails = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)
				trackEvent(
					AnalyticsEvent.FavoriteChanged(
						recipeId = currentRecipe.id,
						recipeName = currentRecipe.title,
						isFavorite = !currentRecipe.isFavorite,
						origin = AnalyticsOrigin.COOKING,
						isPrivate = currentRecipe.isPrivate,
					),
				)
			} else {
				favoriteErrorMessage = outcome.getError()?.message
			}
			isFavoriteUpdating = false
		}
	}

	fun shareCurrentRecipe() {
		if (recipeDetails?.isPrivate == true) {
			return
		}
		shareRecipe(
			recipeId = recipeId,
			title = recipeDetails?.title,
		)
		trackEvent(
			AnalyticsEvent.RecipeShared(
				recipeId = recipeId,
				recipeName = recipeDetails?.title.orEmpty(),
				origin = AnalyticsOrigin.COOKING,
				isPrivate = recipeDetails?.isPrivate == true,
				shareType = AnalyticsShareType.RECIPE,
			),
		)
	}

	fun prepareCookbookPicker() {
		viewModelScope.launch {
			sheetCookbooks.clear()
			val page = getCookbooksPage(1, COOKBOOK_PICKER_PAGE_SIZE).get() ?: return@launch
			sheetCookbooks.addAll(page.items)
		}
	}

	fun addRecipeToCookbookId(cookbookId: Int, onDone: (String?) -> Unit) {
		val recipe = recipeDetails ?: return onDone(null)
		viewModelScope.launch {
			isCookbookActionInFlight = true
			cookbookActionError = null
			val outcome = addRecipeToCookbook(cookbookId, recipe.id)
			val err = outcome.getError()?.message
			if (err == null) {
				val cookbookName = sheetCookbooks.firstOrNull { it.id == cookbookId }?.name
				trackEvent(
					AnalyticsEvent.RecipeAddedToCookbook(
						recipeId = recipe.id,
						recipeName = recipe.title,
						cookbookId = cookbookId,
						cookbookName = cookbookName,
						origin = AnalyticsOrigin.COOKING,
						isPrivate = recipe.isPrivate,
					),
				)
			}
			isCookbookActionInFlight = false
			onDone(err)
		}
	}

	fun createCookbookAndAdd(name: String, onDone: (String?) -> Unit) {
		val recipe = recipeDetails
		val trimmed = name.trim()
		when {
			recipe == null || trimmed.isEmpty() -> onDone(null)
			sheetCookbooks.any { it.name.trim().equals(trimmed, ignoreCase = true) } -> {
				val duplicateMessage = "Cookbook already exists"
				cookbookActionError = duplicateMessage
				onDone(duplicateMessage)
			}

			else -> viewModelScope.launch {
				isCookbookActionInFlight = true
				cookbookActionError = null
				val createOutcome = createCookbook(trimmed)
				val created = createOutcome.get()
				if (created == null) {
					isCookbookActionInFlight = false
					onDone(createOutcome.getError()?.message)
					return@launch
				}
				trackEvent(
					AnalyticsEvent.CookbookCreated(
						cookbookId = created.id,
						cookbookName = created.name,
					),
				)
				val addOutcome = addRecipeToCookbook(created.id, recipe.id)
				val err = addOutcome.getError()?.message
				if (err == null) {
					trackEvent(
						AnalyticsEvent.RecipeAddedToCookbook(
							recipeId = recipe.id,
							recipeName = recipe.title,
							cookbookId = created.id,
							cookbookName = created.name,
							origin = AnalyticsOrigin.COOKING,
							isPrivate = recipe.isPrivate,
						),
					)
				}
				isCookbookActionInFlight = false
				onDone(err)
			}
		}
	}

	private fun startFavoriteEventsCollection(sessionKey: String?) {
		favoriteEventsJob?.cancel()
		favoriteEventsJob = null
		if (sessionKey == null) {
			return
		}
		favoriteEventsJob = viewModelScope.launch {
			observeFavoriteEvents().collect { event ->
				applyFavoriteEvent(event)
			}
		}
	}

	private fun applyFavoriteEvent(event: FavoriteEvent) {
		if (event.recipeId != recipeId) {
			return
		}
		val isFavorite = when (event) {
			is FavoriteEvent.Added -> true
			is FavoriteEvent.Removed -> false
		}
		if (baseRecipeDetails?.isFavorite == isFavorite && recipeDetails?.isFavorite == isFavorite) {
			return
		}
		baseRecipeDetails = baseRecipeDetails?.copy(isFavorite = isFavorite)
		recipeDetails = recipeDetails?.copy(isFavorite = isFavorite)
	}

	private fun refreshFavoriteState() {
		viewModelScope.launch {
			val loaded = getRecipeDetails(recipeId).get() ?: return@launch
			baseRecipeDetails = baseRecipeDetails?.copy(isFavorite = loaded.isFavorite)
			recipeDetails = recipeDetails?.copy(isFavorite = loaded.isFavorite)
		}
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
			favoriteErrorMessage = null
			recipeDetails = null
			hasTrackedCookingCompleted = false

			val outcome = getRecipeDetails(recipeId)
			baseRecipeDetails = outcome.get()
			applyMeasurementPreferences()
			val details = recipeDetails ?: baseRecipeDetails
			if (details != null) {
				cookingStartedMark = monotonicTimeSource.markNow()
				hasTrackedCookingAbandoned = false
				logBreadcrumb(CrashBreadcrumb.cookingStarted(recipeId))
				trackEvent(
					AnalyticsEvent.CookingStarted(
						recipeId = recipeId,
						recipeName = details.title,
						origin = AnalyticsOrigin.RECIPE_DETAILS,
						stepCount = details.steps.size,
						isPrivate = details.isPrivate,
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
						recipeName = details?.title,
						isPrivate = details?.isPrivate,
					),
				)
			}
			errorMessage = error?.message
			currentPageIndex = 0
			isLoading = false
		}
	}

	private fun onPageChanged() {
		if (!isOnFinishPage()) {
			trackCookingStepViewed()
		}
		trackCookingCompletedIfNeeded()
	}

	private fun isOnFinishPage(): Boolean {
		val details = recipeDetails ?: baseRecipeDetails ?: return false
		return currentPageIndex >= details.steps.size
	}

	private fun finishPageIndex(details: RecipeDetails): Int = details.steps.size

	private fun trackCookingStepViewed() {
		val details = recipeDetails ?: baseRecipeDetails ?: return
		logBreadcrumb(CrashBreadcrumb.cookingStepAdvanced(recipeId, currentPageIndex))
		trackEvent(
			AnalyticsEvent.CookingStepViewed(
				recipeId = recipeId,
				recipeName = details.title,
				stepIndex = currentPageIndex,
				stepCount = details.steps.size,
				isPrivate = details.isPrivate,
			),
		)
	}

	private fun trackCookingCompletedIfNeeded() {
		val details = recipeDetails ?: baseRecipeDetails ?: return
		if (hasTrackedCookingCompleted || currentPageIndex < details.steps.lastIndex) {
			return
		}
		hasTrackedCookingCompleted = true
		trackEvent(
			AnalyticsEvent.CookingCompleted(
				recipeId = recipeId,
				recipeName = details.title,
				durationSeconds = cookingStartedMark.elapsedNow().inWholeSeconds,
				stepCount = details.steps.size,
				origin = AnalyticsOrigin.RECIPE_DETAILS,
				isPrivate = details.isPrivate,
			),
		)
	}

	internal fun trackCookingAbandonedIfNeeded() {
		val details = recipeDetails ?: baseRecipeDetails ?: return
		if (hasTrackedCookingCompleted || hasTrackedCookingAbandoned || details.steps.isEmpty()) {
			return
		}
		hasTrackedCookingAbandoned = true
		trackEvent(
			AnalyticsEvent.CookingAbandoned(
				recipeId = recipeId,
				recipeName = details.title,
				lastStepIndex = currentPageIndex.coerceAtMost(details.steps.lastIndex),
				stepCount = details.steps.size,
				durationSeconds = cookingStartedMark.elapsedNow().inWholeSeconds,
				isPrivate = details.isPrivate,
			),
		)
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(recipeId: Int, sessionKey: String?): StepByStepCookingViewModel
	}
}
