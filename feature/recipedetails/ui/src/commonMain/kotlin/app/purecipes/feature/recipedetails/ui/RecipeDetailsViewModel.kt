package app.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.getValue
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
import app.purecipes.feature.library.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.library.domain.usecase.ObserveCookbookMembershipEventsUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeFormatHandling
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

private const val COOKBOOK_PICKER_PAGE_SIZE = 100

@AssistedInject
class RecipeDetailsViewModel(
	private val addFavoriteRecipe: AddFavoriteRecipeUseCase,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	private val markMeasurementMismatchSeen: MarkMeasurementMismatchSeenUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	private val observeFavoriteEvents: ObserveFavoriteEventsUseCase,
	private val observeCookbookMembershipEvents: ObserveCookbookMembershipEventsUseCase,
	private val trackEvent: TrackEventUseCase,
	private val logBreadcrumb: LogBreadcrumbUseCase,
	private val sendHandledException: SendHandledExceptionUseCase,
	private val getRecipeCookbooks: GetRecipeCookbooksUseCase,
	private val getCookbooksPage: GetCookbooksPageUseCase,
	private val createCookbook: CreateCookbookUseCase,
	private val addRecipeToCookbook: AddRecipeToCookbookUseCase,
	private val shareRecipe: ShareRecipeUseCase,
	@Assisted private val recipeId: Int,
	@Assisted sessionKey: String?,
	@Assisted private val origin: String,
) : ViewModel() {

	private var activeSessionKey: String? = sessionKey

	private val analyticsOrigin: AnalyticsOrigin =
		AnalyticsOrigin.fromValue(origin) ?: AnalyticsOrigin.SEARCH

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

	val recipeCookbooks = mutableStateListOf<CookbookRef>()

	val sheetCookbooks = mutableStateListOf<CookbookSummary>()

	var cookbookActionError by mutableStateOf<String?>(null)
		private set

	var isCookbookActionInFlight by mutableStateOf(false)
		private set

	private var baseRecipeDetails: RecipeDetails? = null

	private var measurementPreferences: MeasurementPreferences? = null

	private var favoriteEventsJob: Job? = null

	private var cookbookMembershipEventsJob: Job? = null

	init {
		viewModelScope.launch {
			observeMeasurementPreferences().collectLatest { preferences ->
				measurementPreferences = preferences
				applyMeasurementPreferences()
			}
		}
		loadRecipe()
		startFavoriteEventsCollection(sessionKey)
		startCookbookMembershipEventsCollection(sessionKey)
	}

	fun retry() {
		loadRecipe()
	}

	fun onScreenVisible() {
		if (recipeDetails != null && !isLoading) {
			refreshCookbookMembership()
		}
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == activeSessionKey) {
			return
		}
		activeSessionKey = sessionKey
		startFavoriteEventsCollection(sessionKey)
		startCookbookMembershipEventsCollection(sessionKey)
		if (sessionKey == null) {
			baseRecipeDetails = baseRecipeDetails?.copy(isFavorite = false)
			recipeDetails = recipeDetails?.copy(isFavorite = false)
			recipeCookbooks.clear()
		} else {
			loadRecipe(shouldTrackAnalytics = false)
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

	private fun startCookbookMembershipEventsCollection(sessionKey: String?) {
		cookbookMembershipEventsJob?.cancel()
		cookbookMembershipEventsJob = null
		if (sessionKey == null) {
			return
		}
		cookbookMembershipEventsJob = viewModelScope.launch {
			observeCookbookMembershipEvents().collect { event ->
				if (event.recipeId == recipeId) {
					refreshCookbookMembership()
				}
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
		refreshCookbookMembership()
	}

	fun dismissMeasurementMismatchDialog() {
		showMeasurementMismatchDialog = false
		viewModelScope.launch {
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
		viewModelScope.launch {
			markMeasurementMismatchSeen(recipeId)
		}
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
						origin = AnalyticsOrigin.RECIPE_DETAILS,
						isPrivate = currentRecipe.isPrivate,
					),
				)
				refreshCookbookMembership()
			} else {
				favoriteErrorMessage = outcome.getError()?.message
			}
			isFavoriteUpdating = false
		}
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
						origin = AnalyticsOrigin.RECIPE_DETAILS,
						isPrivate = recipe.isPrivate,
					),
				)
				refreshCookbookMembership()
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
							origin = AnalyticsOrigin.RECIPE_DETAILS,
							isPrivate = recipe.isPrivate,
						),
					)
					refreshCookbookMembership()
				}
				isCookbookActionInFlight = false
				onDone(err)
			}
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
				origin = analyticsOrigin,
				isPrivate = recipeDetails?.isPrivate == true,
				shareType = AnalyticsShareType.RECIPE,
			),
		)
	}

	private fun refreshCookbookMembership() {
		viewModelScope.launch {
			cookbookActionError = null
			if (activeSessionKey == null) {
				recipeCookbooks.clear()
				return@launch
			}
			val r = recipeDetails
			if (r == null || !r.isFavorite) {
				recipeCookbooks.clear()
				return@launch
			}
			val outcome = getRecipeCookbooks(r.id)
			recipeCookbooks.clear()
			recipeCookbooks.addAll(outcome.get().orEmpty())
		}
	}

	private fun applyMeasurementPreferences() {
		val rawRecipe = baseRecipeDetails ?: return
		val preferences = measurementPreferences ?: return
		val processedRecipe = processRecipeDetailsForMeasurementPreferences(
			recipe = rawRecipe,
			preferences = preferences,
		)
		recipeDetails = processedRecipe.recipe
		isRecipeConverted = processedRecipe.isConverted
		if (!isLoading) {
			showMeasurementMismatchDialog = processedRecipe.shouldShowMismatchNotification
		}
	}

	private fun loadRecipe(shouldTrackAnalytics: Boolean = true) {
		viewModelScope.launch {
			isLoading = true
			errorMessage = null
			favoriteErrorMessage = null
			recipeDetails = null
			showMeasurementMismatchDialog = false

			val outcome = getRecipeDetails(recipeId)
			baseRecipeDetails = outcome.get()
			applyMeasurementPreferences()
			val loadedRecipe = baseRecipeDetails
			if (loadedRecipe != null && shouldTrackAnalytics) {
				logBreadcrumb(CrashBreadcrumb.recipeOpened(recipeId))
				trackEvent(
					AnalyticsEvent.RecipeViewed(
						recipeId = recipeId,
						recipeName = loadedRecipe.title,
						origin = analyticsOrigin,
						isPrivate = loadedRecipe.isPrivate,
					),
				)
			}
			val error = outcome.getError()
			if (error != null && shouldTrackAnalytics) {
				sendHandledException(error.asHandledException())
				trackEvent(
					AnalyticsEvent.RecipeLoadFailed(
						recipeId = recipeId,
						errorKind = error.toAnalyticsErrorKind(),
						recipeName = loadedRecipe?.title,
						isPrivate = loadedRecipe?.isPrivate,
					),
				)
			}
			errorMessage = error?.message
			isLoading = false
			refreshCookbookMembership()
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(recipeId: Int, sessionKey: String?, origin: String): RecipeDetailsViewModel
	}
}
