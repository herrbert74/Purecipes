package com.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.CookbookRef
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeFormatHandling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val COOKBOOK_PICKER_PAGE_SIZE = 100

internal class RecipeDetailsViewModel(
	private val recipeId: Int,
	private val addFavoriteRecipe: AddFavoriteRecipeUseCase,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	private val markMeasurementMismatchSeen: MarkMeasurementMismatchSeenUseCase,
	private val processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	private val removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	private val trackEvent: TrackEventUseCase,
	private val sessionKey: String?,
	private val getRecipeCookbooks: GetRecipeCookbooksUseCase,
	private val getCookbooksPage: GetCookbooksPageUseCase,
	private val createCookbook: CreateCookbookUseCase,
	private val addRecipeToCookbook: AddRecipeToCookbookUseCase,
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
				refreshCookbookMembership()
			} else {
				favoriteErrorMessage = outcome.getError()?.message
			}
			isFavoriteUpdating = false
		}
	}

	fun prepareCookbookPicker() {
		scope.launch {
			sheetCookbooks.clear()
			val page = getCookbooksPage(1, COOKBOOK_PICKER_PAGE_SIZE).get() ?: return@launch
			sheetCookbooks.addAll(page.items)
		}
	}

	fun addRecipeToCookbookId(cookbookId: Int, onDone: (String?) -> Unit) {
		val recipe = recipeDetails ?: return onDone(null)
		scope.launch {
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
		scope.launch {
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

	private fun refreshCookbookMembership() {
		scope.launch {
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
			refreshCookbookMembership()
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
	getRecipeCookbooks: GetRecipeCookbooksUseCase,
	getCookbooksPage: GetCookbooksPageUseCase,
	createCookbook: CreateCookbookUseCase,
	addRecipeToCookbook: AddRecipeToCookbookUseCase,
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
			append(':')
			append(getRecipeCookbooks.hashCode())
			append(':')
			append(getCookbooksPage.hashCode())
			append(':')
			append(createCookbook.hashCode())
			append(':')
			append(addRecipeToCookbook.hashCode())
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
					sessionKey = sessionKey,
					getRecipeCookbooks = getRecipeCookbooks,
					getCookbooksPage = getCookbooksPage,
					createCookbook = createCookbook,
					addRecipeToCookbook = addRecipeToCookbook,
				)
			}
		},
	)
}
