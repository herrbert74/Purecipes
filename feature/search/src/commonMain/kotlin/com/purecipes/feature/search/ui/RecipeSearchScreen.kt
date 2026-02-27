package com.purecipes.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.search.model.RecipeSummary
import com.purecipes.feature.search.repository.RecipeSearchRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
	modifier: Modifier = Modifier,
	repository: RecipeSearchRepository,
) {
	val coroutineScope = rememberCoroutineScope()

	var query by remember { mutableStateOf("") }
	var isSearching by remember { mutableStateOf(false) }
	var isSearchBarActive by remember { mutableStateOf(false) }
	var errorMessage by remember { mutableStateOf<String?>(null) }
	val recipes = remember { mutableStateListOf<RecipeSummary>() }

	fun searchNow() {
		coroutineScope.launch {
			isSearching = true
			errorMessage = null
			val outcome = repository.search(query)
			recipes.clear()
			recipes.addAll(outcome.get() ?: emptyList())
			errorMessage = outcome.getError()?.message
			isSearching = false
			isSearchBarActive = false
		}
	}

	LaunchedEffect(Unit) {
		searchNow()
	}

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		SearchBar(
			query = query,
			onQueryChange = { query = it },
			onSearch = { searchNow() },
			active = isSearchBarActive,
			onActiveChange = { isSearchBarActive = it },
			placeholder = { Text("Search recipes") },
			leadingIcon = {
				Text("🔎")
			},
			modifier = Modifier.fillMaxWidth(),
		) {}

		when {
			isSearching -> {
				Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
					CircularProgressIndicator()
				}
			}

			errorMessage != null -> {
				Text(
					text = errorMessage ?: "Unknown error",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error,
				)
			}

			else -> {
				LazyColumn(
					verticalArrangement = Arrangement.spacedBy(8.dp),
					contentPadding = PaddingValues(bottom = 16.dp),
				) {
					items(recipes, key = { it.id }) { recipe ->
						RecipeRow(recipe = recipe)
					}
				}
			}
		}
	}
}

@Composable
private fun RecipeRow(recipe: RecipeSummary) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(12.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Box(
				modifier = Modifier
					.size(56.dp)
					.clip(MaterialTheme.shapes.medium)
					.background(MaterialTheme.colorScheme.secondaryContainer),
				contentAlignment = Alignment.Center,
			) {
				Text(
					text = "IMG",
					style = MaterialTheme.typography.labelMedium,
				)
			}

			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = recipe.title,
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
				Spacer(modifier = Modifier.height(2.dp))
				Text(
					text = recipe.cuisine ?: "Unknown cuisine",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}
