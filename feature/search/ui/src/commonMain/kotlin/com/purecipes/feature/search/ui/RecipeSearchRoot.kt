package com.purecipes.feature.search.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository

@Composable
fun RecipeSearchRoot(repository: RecipeSearchRepository) {
	MaterialTheme {
		RecipeSearchScreen(repository = repository)
	}
}
