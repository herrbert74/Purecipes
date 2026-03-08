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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.requestFocus
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.search.model.RecipeSummary
import com.purecipes.feature.search.repository.RecipeSearchRepository
import com.purecipes.shared.ui.component.BodyText
import com.purecipes.shared.ui.component.TitleText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
	repository: RecipeSearchRepository,
	modifier: Modifier = Modifier,
	closeScreen: () -> Unit = {},
) {
	val coroutineScope = rememberCoroutineScope()

	var searchQuery by rememberSaveable { mutableStateOf("") }
	var isSearching by remember { mutableStateOf(false) }
	var isSearchBarActive by rememberSaveable { mutableStateOf(false) }
	var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
	val recipes = rememberSaveable { mutableStateListOf<RecipeSummary>() }

	fun searchNow() {
		coroutineScope.launch {
			isSearching = true
			errorMessage = null
			val outcome = repository.search(searchQuery)
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
			inputField = {
				SearchBarDefaults.InputField(
					query = searchQuery,
					onQueryChange = { searchQuery = it },
					onSearch = { searchNow() },
					expanded = true,
					onExpandedChange = {
						isSearchBarActive = it
						if (!isSearchBarActive) {
							closeScreen()
						}
					},
					modifier = Modifier
						.semantics(mergeDescendants = true) {
							contentDescription = "Searchbar"
							requestFocus { false }
						},
					placeholder = { Text("Search recipes") },
					leadingIcon = {
						Text("🔎")
					},
				)
			},
			expanded = true,
			onExpandedChange = {},
			modifier = Modifier.fillMaxWidth(),
		) {
			when {
				isSearching -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
					CircularProgressIndicator()
				}

				errorMessage != null -> Text(
					text = errorMessage ?: "Unknown error",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error,
				)

				else -> LazyColumn(
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
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.size(56.dp)
					.clip(RoundedCornerShape(8.dp))
					.background(MaterialTheme.colorScheme.secondaryContainer),
				contentScale = ContentScale.Crop,
			)

			Column(modifier = Modifier.weight(1f)) {
				TitleText(
					text = recipe.title
				)
				Spacer(modifier = Modifier.height(2.dp))
				BodyText(
					text = recipe.cuisine ?: "Unknown cuisine",
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}
