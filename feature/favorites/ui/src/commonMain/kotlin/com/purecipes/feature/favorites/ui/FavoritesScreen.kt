package com.purecipes.feature.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.ui.component.BodyText
import com.purecipes.shared.ui.component.TitleText

@Composable
fun FavoritesScreen(
	getFavoriteRecipes: GetFavoriteRecipesUseCase,
	refreshSignal: Int,
	sessionKey: String?,
	modifier: Modifier = Modifier,
	onRecipeSelect: (Int) -> Unit = {},
) {
	val viewModel = favoritesViewModel(
		getFavoriteRecipes = getFavoriteRecipes,
		sessionKey = sessionKey,
	)

	LaunchedEffect(refreshSignal, sessionKey) {
		if (sessionKey != null) {
			viewModel.loadFavorites()
		}
	}

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Favorites") },
			)
		},
	) { innerPadding ->
		if (sessionKey == null) {
			FavoritesSignedOutContent(modifier = Modifier.padding(innerPadding))
			return@Scaffold
		}

		FavoritesContent(
			isLoading = viewModel.isLoading,
			errorMessage = viewModel.errorMessage,
			recipes = viewModel.recipes,
			modifier = Modifier.padding(innerPadding),
			onRecipeSelect = onRecipeSelect,
		)
	}
}

@Composable
private fun FavoritesSignedOutContent(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Icon(
				imageVector = Icons.Filled.Favorite,
				contentDescription = "Favorites",
				modifier = Modifier.size(56.dp),
				tint = MaterialTheme.colorScheme.primary,
			)
			Text(
				text = "Sign in to view favorites",
				style = MaterialTheme.typography.headlineSmall,
			)
			Text(
				text = "Favorites are tied to your session, so each account keeps its own saved recipes.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center,
			)
		}
	}
}

@Composable
private fun FavoritesContent(
	isLoading: Boolean,
	errorMessage: String?,
	recipes: SnapshotStateList<RecipeSummary>,
	modifier: Modifier = Modifier,
	onRecipeSelect: (Int) -> Unit,
) {
	when {
		isLoading -> Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center,
		) {
			CircularProgressIndicator()
		}

		errorMessage != null -> Box(
			modifier = modifier
				.fillMaxSize()
				.padding(24.dp),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = errorMessage,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.error,
				textAlign = TextAlign.Center,
			)
		}

		recipes.isEmpty() -> Box(
			modifier = modifier
				.fillMaxSize()
				.padding(24.dp),
			contentAlignment = Alignment.Center,
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Icon(
					imageVector = Icons.Filled.Favorite,
					contentDescription = "Favorites",
					modifier = Modifier.size(56.dp),
					tint = MaterialTheme.colorScheme.primary,
				)
				Text(
					text = "No favorites yet",
					style = MaterialTheme.typography.headlineSmall,
				)
				Text(
					text = "Add recipes from the details screen and they will appear here.",
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					textAlign = TextAlign.Center,
				)
			}
		}

		else -> LazyColumn(
			modifier = modifier.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(16.dp),
		) {
			items(recipes, key = { it.id }) { recipe ->
				FavoriteRecipeRow(
					recipe = recipe,
					onClick = { onRecipeSelect(recipe.id) },
				)
			}
		}
	}
}

@Composable
private fun FavoriteRecipeRow(recipe: RecipeSummary, onClick: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
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
				TitleText(text = recipe.title)
				BodyText(
					text = listOfNotNull(
						recipe.cuisine ?: "Unknown cuisine",
						recipe.totalTime?.let { "$it min" },
					).joinToString(separator = " • "),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}
