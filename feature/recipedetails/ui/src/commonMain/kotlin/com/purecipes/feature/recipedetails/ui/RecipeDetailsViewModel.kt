package com.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class RecipeDetailsViewModel(
	private val recipeId: Int,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
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

	init {
		loadRecipe()
	}

	fun retry() {
		loadRecipe()
	}

	private fun loadRecipe() {
		scope.launch {
			isLoading = true
			errorMessage = null
			recipeDetails = null

			val outcome = getRecipeDetails(recipeId)
			recipeDetails = outcome.get()
			errorMessage = outcome.getError()?.message
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
internal fun recipeDetailsViewModel(
	recipeId: Int,
	getRecipeDetails: GetRecipeDetailsUseCase,
): RecipeDetailsViewModel {
	return viewModel(
		key = "RecipeDetailsViewModel:$recipeId:${getRecipeDetails.hashCode()}",
		factory = viewModelFactory {
			initializer {
				RecipeDetailsViewModel(
					recipeId = recipeId,
					getRecipeDetails = getRecipeDetails,
				)
			}
		},
	)
}
