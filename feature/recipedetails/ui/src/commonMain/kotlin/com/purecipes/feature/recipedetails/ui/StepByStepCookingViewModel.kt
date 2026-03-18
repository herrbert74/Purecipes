package com.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class StepByStepCookingViewModel(
	private val recipeId: Int,
	private val repository: RecipeDetailsRepository,
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

	private fun loadRecipe() {
		scope.launch {
			isLoading = true
			errorMessage = null
			recipeDetails = null

			val outcome = repository.getRecipeDetails(recipeId)
			recipeDetails = outcome.get()
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
}

@Composable
internal fun stepByStepCookingViewModel(
	recipeId: Int,
	repository: RecipeDetailsRepository,
): StepByStepCookingViewModel {
	return viewModel(
		key = "StepByStepCookingViewModel:$recipeId:${repository.hashCode()}",
		factory = viewModelFactory {
			initializer {
				StepByStepCookingViewModel(
					recipeId = recipeId,
					repository = repository,
				)
			}
		},
	)
}
