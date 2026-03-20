package com.purecipes.feature.search.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase

@Composable
fun RecipeSearchRoot(searchRecipes: SearchRecipesUseCase) {
	MaterialTheme {
		RecipeSearchScreen(searchRecipes = searchRecipes)
	}
}
