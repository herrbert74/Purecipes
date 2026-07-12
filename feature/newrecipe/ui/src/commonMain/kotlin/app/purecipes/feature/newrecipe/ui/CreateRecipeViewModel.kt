package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.model.asHandledException
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.usecase.EstimateRecipeNutritionUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
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
) : ViewModel() {

	private var nutritionEstimateJob: Job? = null

	var titleInput by mutableStateOf("")
		private set

	var descriptionInput by mutableStateOf("")
		private set

	var imageUrlInput by mutableStateOf("")
		private set

	var ingredientsInput by mutableStateOf("")
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

	var isLoading by mutableStateOf(true)
		private set

	var isSaving by mutableStateOf(false)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var formErrorMessage by mutableStateOf<String?>(null)
		private set

	var successMessage by mutableStateOf<String?>(null)
		private set

	var editingRecipeId by mutableStateOf<Int?>(null)
		private set

	val recipes = mutableStateListOf<RecipeDetails>()

	val isEditing: Boolean
		get() = editingRecipeId != null

	init {
		loadRecipes()
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

	fun onIngredientsChange(value: String) {
		ingredientsInput = value
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

	fun editRecipe(recipe: RecipeDetails) {
		editingRecipeId = recipe.id
		titleInput = recipe.title
		descriptionInput = recipe.description
		imageUrlInput = recipe.imageUrl.orEmpty()
		ingredientsInput = recipe.ingredientGroups
			.flatMap { it.ingredients }
			.let(IngredientLineParser::toEditableLines)
			.joinToString(separator = "\n")
		stepInputs.clear()
		stepInputs.addAll(recipe.steps.ifEmpty { listOf("") })
		totalTimeInput = recipe.totalTime?.toString().orEmpty()
		yieldsInput = recipe.yields.orEmpty()
		selectedCuisine = recipe.cuisine
		nutritionEstimate = recipe.nutrition?.recipeTotals
		formErrorMessage = null
		successMessage = null
		scheduleNutritionEstimate()
	}

	fun startNewRecipe() {
		editingRecipeId = null
		titleInput = ""
		descriptionInput = ""
		imageUrlInput = ""
		ingredientsInput = ""
		stepInputs.clear()
		stepInputs.add("")
		totalTimeInput = ""
		yieldsInput = ""
		selectedCuisine = null
		nutritionEstimate = null
		isNutritionEstimateLoading = false
		formErrorMessage = null
		successMessage = null
		nutritionEstimateJob?.cancel()
	}

	fun retry() {
		loadRecipes()
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
		errorMessage = null
		val wasEditing = isEditing

		viewModelScope.launch {
			val ingredients = IngredientLineParser.parseLines(
				ingredientsInput
					.lineSequence()
					.map(String::trim)
					.filter(String::isNotEmpty)
					.toList(),
			)
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
				),
			)

			val savedRecipe = outcome.get()
			if (savedRecipe != null) {
				replaceRecipe(savedRecipe)
				editRecipe(savedRecipe)
				trackEvent(
					AnalyticsEvent.RecipeSaved(
						recipeId = savedRecipe.id,
						isEditing = wasEditing,
						hasPhoto = imageUrlInput.isNotBlank(),
						ingredientCount = ingredients.size,
						stepCount = steps.size,
					),
				)
				successMessage = if (wasEditing) {
					"Recipe updated."
				} else {
					"Recipe uploaded."
				}
			} else {
				outcome.getError()?.let { error ->
					sendHandledException(error.asHandledException())
					formErrorMessage = error.message
				}
			}
			isSaving = false
		}
	}

	private fun scheduleNutritionEstimate() {
		nutritionEstimateJob?.cancel()
		nutritionEstimateJob = viewModelScope.launch {
			val ingredients = IngredientLineParser.parseLines(
				ingredientsInput
					.lineSequence()
					.map(String::trim)
					.filter(String::isNotEmpty)
					.toList(),
			)
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

	private fun loadRecipes() {
		viewModelScope.launch {
			isLoading = true
			errorMessage = null
			val outcome = getCreatedRecipes()
			recipes.clear()
			recipes.addAll(outcome.get() ?: emptyList())
			errorMessage = outcome.getError()?.message
			isLoading = false
		}
	}

	private fun replaceRecipe(recipe: RecipeDetails) {
		recipes.removeAll { it.id == recipe.id }
		recipes.add(index = 0, element = recipe)
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
