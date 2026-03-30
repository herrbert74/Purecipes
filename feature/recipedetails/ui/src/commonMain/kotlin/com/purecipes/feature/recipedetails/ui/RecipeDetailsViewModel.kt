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
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class RecipeDetailsViewModel(
	private val recipeId: Int,
	private val addFavoriteRecipe: AddFavoriteRecipeUseCase,
	private val getRecipeDetails: GetRecipeDetailsUseCase,
	private val removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
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

	var favoriteErrorMessage by mutableStateOf<String?>(null)
		private set

	var isFavoriteUpdating by mutableStateOf(false)
		private set

	var favoriteChangeCount by mutableIntStateOf(0)
		private set

	init {
		loadRecipe()
	}

	fun retry() {
		loadRecipe()
	}

	fun toggleFavorite() {
		val currentRecipe = recipeDetails ?: return
		if (isFavoriteUpdating) return

		scope.launch {
			isFavoriteUpdating = true
			favoriteErrorMessage = null
			val outcome = if (currentRecipe.isFavorite) {
				removeFavoriteRecipe(currentRecipe.id)
			} else {
				addFavoriteRecipe(currentRecipe.id)
			}

			if (outcome.getError() == null) {
				recipeDetails = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)
				favoriteChangeCount += 1
			} else {
				favoriteErrorMessage = outcome.getError()?.message
			}
			isFavoriteUpdating = false
		}
	}

	private fun loadRecipe() {
		scope.launch {
			isLoading = true
			errorMessage = null
			favoriteErrorMessage = null
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
	addFavoriteRecipe: AddFavoriteRecipeUseCase,
	getRecipeDetails: GetRecipeDetailsUseCase,
	removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	sessionKey: String?,
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
			append(removeFavoriteRecipe.hashCode())
			append(':')
			append(sessionKey ?: "signed-out")
		}
	return viewModel(
		key = viewModelKey,
		factory = viewModelFactory {
			initializer {
				RecipeDetailsViewModel(
					recipeId = recipeId,
					addFavoriteRecipe = addFavoriteRecipe,
					getRecipeDetails = getRecipeDetails,
					removeFavoriteRecipe = removeFavoriteRecipe,
				)
			}
		},
	)
}
