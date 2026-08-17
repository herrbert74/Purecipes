package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumFeature
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.model.asHandledException
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.usecase.EstimateRecipeNutritionUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.domain.ingredient.IngredientLineParser
import app.purecipes.shared.domain.ingredient.nutritionIngredientTexts
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeDetails
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class CreateRecipeViewModel(
	private val getCreatedRecipes: GetCreatedRecipesUseCase,
	private val saveCreatedRecipe: SaveCreatedRecipeUseCase,
	private val estimateRecipeNutrition: EstimateRecipeNutritionUseCase,
	private val trackEvent: TrackEventUseCase,
	private val logBreadcrumb: LogBreadcrumbUseCase,
	private val sendHandledException: SendHandledExceptionUseCase,
	private val observePremiumStatus: ObservePremiumStatusUseCase,
) : ViewModel() {

	private var nutritionEstimateJob: Job? = null
	private var loadRecipeJob: Job? = null
	private var loadedIsPrivate = false

	val ingredientsEditor = CreateRecipeIngredientsEditor()

	var titleInput by mutableStateOf("")
		private set

	var descriptionInput by mutableStateOf("")
		private set

	var imageUrlInput by mutableStateOf("")
		private set

	var nutritionEstimate by mutableStateOf<NutritionSummary?>(null)
		private set

	var isNutritionEstimateLoading by mutableStateOf(false)
		private set

	val stepInputs = mutableStateListOf("")

	var totalTimeInput by mutableStateOf("")
		private set

	var yieldsInput by mutableStateOf("")
		private set

	var selectedCuisine by mutableStateOf<Cuisine?>(null)
		private set

	var isPrivate by mutableStateOf(false)
		private set

	var isPremium by mutableStateOf(false)
		private set

	var isLoadingRecipe by mutableStateOf(false)
		private set

	var isSaving by mutableStateOf(false)
		private set

	var loadErrorMessage by mutableStateOf<String?>(null)
		private set

	var formErrorMessage by mutableStateOf<String?>(null)
		private set

	var successMessage by mutableStateOf<String?>(null)
		private set

	var editingRecipeId by mutableStateOf<Int?>(null)
		private set

	var saveCompletedEvent by mutableIntStateOf(0)
		private set

	val isEditing: Boolean
		get() = editingRecipeId != null

	val canMakePrivate: Boolean
		get() = isPremium || isPrivate

	init {
		viewModelScope.launch {
			observePremiumStatus().collect { premium ->
				isPremium = premium
			}
		}
	}

	fun onTitleChange(value: String) {
		titleInput = value
	}

	fun onDescriptionChange(value: String) {
		descriptionInput = value
	}

	fun onImageUrlChange(value: String) {
		imageUrlInput = value
	}

	fun onIngredientsEdited() {
		scheduleNutritionEstimate()
	}

	fun onStepChange(index: Int, value: String) {
		stepInputs[index] = value
	}

	fun addStep() {
		stepInputs.add("")
	}

	fun removeStep(index: Int) {
		if (stepInputs.size == 1) {
			stepInputs[0] = ""
		} else {
			stepInputs.removeAt(index)
		}
	}

	fun moveStep(fromIndex: Int, toIndex: Int) {
		if (
			fromIndex == toIndex ||
			fromIndex !in stepInputs.indices ||
			toIndex !in stepInputs.indices
		) {
			return
		}

		val step = stepInputs.removeAt(fromIndex)
		stepInputs.add(index = toIndex, element = step)
	}

	fun moveStepUp(index: Int) {
		if (index <= 0 || index >= stepInputs.size) {
			return
		}
		moveStep(fromIndex = index, toIndex = index - 1)
	}

	fun moveStepDown(index: Int) {
		if (index < 0 || index >= stepInputs.lastIndex) {
			return
		}
		moveStep(fromIndex = index, toIndex = index + 1)
	}

	fun onTotalTimeChange(value: String) {
		totalTimeInput = value
	}

	fun onYieldsChange(value: String) {
		yieldsInput = value
	}

	fun onCuisineChange(value: Cuisine?) {
		selectedCuisine = value
	}

	fun onIsPrivateChange(value: Boolean) {
		if (value && !canMakePrivate) {
			trackEvent(
				AnalyticsEvent.PremiumFeatureBlocked(
					feature = AnalyticsPremiumFeature.PRIVATE_RECIPES,
					origin = AnalyticsOrigin.CREATE_RECIPE,
				),
			)
			return
		}
		isPrivate = value
	}

	fun onRecipeIdChanged(recipeId: Int?) {
		saveCompletedEvent = 0
		if (recipeId != null) {
			loadRecipe(recipeId)
		} else if (isEditing) {
			startNewRecipe()
		}
	}

	fun loadRecipe(recipeId: Int) {
		if (editingRecipeId == recipeId && !isLoadingRecipe) {
			return
		}
		loadRecipeJob?.cancel()
		isLoadingRecipe = true
		loadErrorMessage = null
		loadRecipeJob = viewModelScope.launch {
			val outcome = getCreatedRecipes()
			val recipe = outcome.get()?.firstOrNull { it.id == recipeId }
			if (recipe != null) {
				populateForm(recipe)
			} else {
				loadErrorMessage = outcome.getError()?.message
					?: "Could not find that recipe."
			}
			isLoadingRecipe = false
		}
	}

	fun startNewRecipe() {
		loadRecipeJob?.cancel()
		editingRecipeId = null
		titleInput = ""
		descriptionInput = ""
		imageUrlInput = ""
		ingredientsEditor.reset()
		stepInputs.clear()
		stepInputs.add("")
		totalTimeInput = ""
		yieldsInput = ""
		selectedCuisine = null
		isPrivate = false
		loadedIsPrivate = false
		nutritionEstimate = null
		isNutritionEstimateLoading = false
		isLoadingRecipe = false
		loadErrorMessage = null
		formErrorMessage = null
		successMessage = null
		saveCompletedEvent = 0
		nutritionEstimateJob?.cancel()
	}

	fun saveRecipe() {
		val validationMessage = validate()
		if (validationMessage != null || isSaving) {
			formErrorMessage = validationMessage
			return
		}

		isSaving = true
		formErrorMessage = null
		successMessage = null
		val wasEditing = isEditing

		viewModelScope.launch {
			val ingredients = IngredientLineParser.parseLines(ingredientsEditor.toLines())
			val steps = stepInputs.map(String::trim).filter(String::isNotEmpty)
			logBreadcrumb(CrashBreadcrumb.RECIPE_SAVE_ATTEMPTED)
			val outcome = saveCreatedRecipe(
				SaveCreatedRecipeRequest(
					recipeId = editingRecipeId,
					title = titleInput,
					description = descriptionInput,
					imageUrl = imageUrlInput,
					ingredients = ingredients,
					steps = steps,
					totalTime = totalTimeInput.trim().takeIf { it.isNotEmpty() }?.toIntOrNull(),
					yields = yieldsInput,
					cuisine = selectedCuisine,
					isPrivate = isPrivate,
				),
			)

			val savedRecipe = outcome.get()
			if (savedRecipe != null) {
				trackSavedRecipe(
					savedRecipe = savedRecipe,
					wasEditing = wasEditing,
					hasPhoto = imageUrlInput.isNotBlank(),
					ingredientCount = ingredients.size,
					stepCount = steps.size,
				)
				successMessage = if (wasEditing) {
					"Recipe updated."
				} else {
					"Recipe uploaded."
				}
				saveCompletedEvent += 1
			} else {
				outcome.getError()?.let { error ->
					sendHandledException(error.asHandledException())
					formErrorMessage = error.message
				}
			}
			isSaving = false
		}
	}

	private fun populateForm(recipe: RecipeDetails) {
		editingRecipeId = recipe.id
		titleInput = recipe.title
		descriptionInput = recipe.description
		imageUrlInput = recipe.imageUrl.orEmpty()
		ingredientsEditor.replaceFromEditableLines(
			recipe.ingredientGroups
				.flatMap { it.ingredients }
				.let(IngredientLineParser::toEditableLines),
		)
		stepInputs.clear()
		stepInputs.addAll(recipe.steps.ifEmpty { listOf("") })
		totalTimeInput = recipe.totalTime?.toString().orEmpty()
		yieldsInput = recipe.yields.orEmpty()
		selectedCuisine = recipe.cuisine
		isPrivate = recipe.isPrivate
		loadedIsPrivate = recipe.isPrivate
		nutritionEstimate = recipe.nutrition?.recipeTotals
		formErrorMessage = null
		successMessage = null
		scheduleNutritionEstimate()
	}

	private fun trackSavedRecipe(
		savedRecipe: RecipeDetails,
		wasEditing: Boolean,
		hasPhoto: Boolean,
		ingredientCount: Int,
		stepCount: Int,
	) {
		trackEvent(
			AnalyticsEvent.RecipeSaved(
				recipeId = savedRecipe.id,
				recipeName = savedRecipe.title,
				isEditing = wasEditing,
				hasPhoto = hasPhoto,
				ingredientCount = ingredientCount,
				stepCount = stepCount,
				isPrivate = savedRecipe.isPrivate,
			),
		)
		if (savedRecipe.isPrivate != loadedIsPrivate) {
			trackEvent(
				AnalyticsEvent.RecipePrivacyChanged(
					recipeId = savedRecipe.id,
					recipeName = savedRecipe.title,
					isPrivate = savedRecipe.isPrivate,
					isEditing = wasEditing,
				),
			)
		}
		loadedIsPrivate = savedRecipe.isPrivate
	}

	private fun scheduleNutritionEstimate() {
		nutritionEstimateJob?.cancel()
		nutritionEstimateJob = viewModelScope.launch {
			val ingredients = IngredientLineParser.parseLines(ingredientsEditor.toLines())
				.filter { it.requirement != IngredientRequirement.OPTIONAL }
				.let(::nutritionIngredientTexts)
			if (ingredients.isEmpty()) {
				nutritionEstimate = null
				isNutritionEstimateLoading = false
				return@launch
			}

			delay(NUTRITION_ESTIMATE_DEBOUNCE_MS)
			isNutritionEstimateLoading = true
			nutritionEstimate = estimateRecipeNutrition(ingredients).get()
			isNutritionEstimateLoading = false
		}
	}

	private fun validate(): String? {
		return listOfNotNull(
			"Add a recipe title.".takeIf { titleInput.isBlank() },
			"Add a recipe description.".takeIf { descriptionInput.isBlank() },
			"Add at least one cooking step.".takeIf {
				stepInputs.map(String::trim).none(String::isNotEmpty)
			},
			"Total time must be a whole number.".takeIf {
				totalTimeInput.isNotBlank() && totalTimeInput.trim().toIntOrNull() == null
			},
		).firstOrNull()
	}

	private companion object {

		const val NUTRITION_ESTIMATE_DEBOUNCE_MS = 400L
	}
}
