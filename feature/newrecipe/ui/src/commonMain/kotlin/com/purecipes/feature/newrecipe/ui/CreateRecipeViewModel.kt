package com.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class CreateRecipeViewModel(
	private val getCreatedRecipes: GetCreatedRecipesUseCase,
	private val saveCreatedRecipe: SaveCreatedRecipeUseCase,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	var titleInput by mutableStateOf("")
		private set

	var descriptionInput by mutableStateOf("")
		private set

	var imageUrlInput by mutableStateOf("")
		private set

	var ingredientsInput by mutableStateOf("")
		private set

	var stepsInput by mutableStateOf("")
		private set

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
	}

	fun onStepsChange(value: String) {
		stepsInput = value
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
		ingredientsInput = recipe.ingredientGroups.flatMap { it.ingredients }.joinToString(separator = "\n")
		stepsInput = recipe.steps.joinToString(separator = "\n")
		totalTimeInput = recipe.totalTime?.toString().orEmpty()
		yieldsInput = recipe.yields.orEmpty()
		selectedCuisine = recipe.cuisine
		formErrorMessage = null
		successMessage = null
	}

	fun startNewRecipe() {
		editingRecipeId = null
		titleInput = ""
		descriptionInput = ""
		imageUrlInput = ""
		ingredientsInput = ""
		stepsInput = ""
		totalTimeInput = ""
		yieldsInput = ""
		selectedCuisine = null
		formErrorMessage = null
		successMessage = null
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

		scope.launch {
			val outcome = saveCreatedRecipe(
				SaveCreatedRecipeRequest(
					recipeId = editingRecipeId,
					title = titleInput,
					description = descriptionInput,
					imageUrl = imageUrlInput,
					ingredients = ingredientsInput.lineSequence().map(String::trim).filter(String::isNotEmpty).toList(),
					steps = stepsInput.lineSequence().map(String::trim).filter(String::isNotEmpty).toList(),
					totalTime = totalTimeInput.trim().takeIf { it.isNotEmpty() }?.toIntOrNull(),
					yields = yieldsInput,
					cuisine = selectedCuisine,
				),
			)

			val savedRecipe = outcome.get()
			if (savedRecipe != null) {
				replaceRecipe(savedRecipe)
				editRecipe(savedRecipe)
				successMessage = if (wasEditing) {
					"Recipe updated."
				} else {
					"Recipe uploaded."
				}
			} else {
				formErrorMessage = outcome.getError()?.message
			}
			isSaving = false
		}
	}

	private fun loadRecipes() {
		scope.launch {
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
				stepsInput.lineSequence().map(String::trim).none(String::isNotEmpty)
			},
			"Total time must be a whole number.".takeIf {
				totalTimeInput.isNotBlank() && totalTimeInput.trim().toIntOrNull() == null
			},
		).firstOrNull()
	}

	override fun onCleared() {
		if (ownsCoroutineScope) {
			scope.cancel()
		}
	}
}

@Composable
internal fun createRecipeViewModel(
	getCreatedRecipes: GetCreatedRecipesUseCase,
	saveCreatedRecipe: SaveCreatedRecipeUseCase,
): CreateRecipeViewModel {
	return viewModel(
		key = "CreateRecipeViewModel:${getCreatedRecipes.hashCode()}:${saveCreatedRecipe.hashCode()}",
		factory = viewModelFactory {
			initializer {
				CreateRecipeViewModel(
					getCreatedRecipes = getCreatedRecipes,
					saveCreatedRecipe = saveCreatedRecipe,
				)
			}
		},
	)
}
