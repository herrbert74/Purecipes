package com.purecipes.feature.favorites.ui

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
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
import com.purecipes.shared.domain.model.RecipeSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class FavoritesViewModel(
	private val getFavoriteRecipes: GetFavoriteRecipesUseCase,
	coroutineScope: CoroutineScope? = null,
) : ViewModel() {

	private val ownsCoroutineScope = coroutineScope == null
	private val scope = coroutineScope ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	var isLoading by mutableStateOf(false)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	val recipes = mutableStateListOf<RecipeSummary>()

	fun loadFavorites() {
		scope.launch {
			isLoading = true
			errorMessage = null
			val outcome = getFavoriteRecipes()
			recipes.clear()
			recipes.addAll(outcome.get() ?: emptyList())
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
internal fun favoritesViewModel(
	getFavoriteRecipes: GetFavoriteRecipesUseCase,
	sessionKey: String?,
): FavoritesViewModel {
	return viewModel(
		key = "FavoritesViewModel:${getFavoriteRecipes.hashCode()}:${sessionKey ?: "signed-out"}",
		factory = viewModelFactory {
			initializer {
				FavoritesViewModel(getFavoriteRecipes = getFavoriteRecipes)
			}
		},
	)
}
