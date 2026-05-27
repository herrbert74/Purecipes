package app.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
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
import kotlinx.coroutines.launch

private const val COOKBOOK_PICKER_PAGE_SIZE = 100

@AssistedInject
class RecipeDetailsViewModel(
	private val addFavoriteRecipe: AddFavoriteRecipeUseCase,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val markMeasurementMismatchSeen: MarkMeasurementMismatchSeenUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	private val trackEvent: TrackEventUseCase,
	private val getRecipeCookbooks: GetRecipeCookbooksUseCase,
	private val getCookbooksPage: GetCookbooksPageUseCase,
	private val createCookbook: CreateCookbookUseCase,
	private val addRecipeToCookbook: AddRecipeToCookbookUseCase,
	private val shareRecipe: ShareRecipeUseCase,
	@Assisted private val recipeId: Int,
	@Assisted private val sessionKey: String?,
) : ViewModel() {

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

	val recipeCookbooks = mutableStateListOf<CookbookRef>()

	val sheetCookbooks = mutableStateListOf<CookbookSummary>()

	var cookbookActionError by mutableStateOf<String?>(null)
		private set

	var isCookbookActionInFlight by mutableStateOf(false)
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
				favoriteChangeCount += 1
				trackEvent(
					AnalyticsEvent.FavoriteChanged(
						recipeId = currentRecipe.id,
						isFavorite = !currentRecipe.isFavorite,
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
				refreshCookbookMembership()
			}
			isCookbookActionInFlight = false
			onDone(err)
		}
	}

	fun createCookbookAndAdd(name: String, onDone: (String?) -> Unit) {
		val recipe = recipeDetails ?: return onDone(null)
		val trimmed = name.trim()
		if (trimmed.isEmpty()) {
			onDone(null)
			return
		}
		if (sheetCookbooks.any { it.name.trim().equals(trimmed, ignoreCase = true) }) {
			val duplicateMessage = "Cookbook already exists"
			cookbookActionError = duplicateMessage
			onDone(duplicateMessage)
			return
		}
		viewModelScope.launch {
			isCookbookActionInFlight = true
			cookbookActionError = null
			val createOutcome = createCookbook(trimmed)
			val created = createOutcome.get()
			if (created == null) {
				isCookbookActionInFlight = false
				onDone(createOutcome.getError()?.message)
				return@launch
			}
			val addOutcome = addRecipeToCookbook(created.id, recipe.id)
			val err = addOutcome.getError()?.message
			if (err == null) {
				refreshCookbookMembership()
			}
			isCookbookActionInFlight = false
			onDone(err)
		}
	}

	fun shareCurrentRecipe() {
		shareRecipe(
			recipeId = recipeId,
			title = recipeDetails?.title,
		)
	}

	private fun refreshCookbookMembership() {
		viewModelScope.launch {
			cookbookActionError = null
			if (sessionKey == null) {
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

	private fun loadRecipe() {
		viewModelScope.launch {
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
			refreshCookbookMembership()
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(recipeId: Int, sessionKey: String?): RecipeDetailsViewModel
	}
}
