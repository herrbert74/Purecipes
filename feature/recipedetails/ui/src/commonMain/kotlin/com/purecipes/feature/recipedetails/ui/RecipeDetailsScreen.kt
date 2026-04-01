package com.purecipes.feature.recipedetails.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.ui.component.BackNavigationButton

@Composable
fun RecipeDetailsRoute(
	recipeId: Int,
	addFavoriteRecipe: AddFavoriteRecipeUseCase,
	canManageFavorites: Boolean,
	getRecipeDetails: GetRecipeDetailsUseCase,
	onBack: () -> Unit,
	onFavoriteChange: () -> Unit,
	onStartCooking: (Int) -> Unit,
	removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	sessionKey: String?,
	modifier: Modifier = Modifier,
) {
	val viewModel = recipeDetailsViewModel(
		recipeId = recipeId,
		addFavoriteRecipe = addFavoriteRecipe,
		getRecipeDetails = getRecipeDetails,
		removeFavoriteRecipe = removeFavoriteRecipe,
		sessionKey = sessionKey,
	)
	val currentOnFavoriteChange by rememberUpdatedState(onFavoriteChange)

	LaunchedEffect(viewModel.favoriteChangeCount) {
		if (viewModel.favoriteChangeCount > 0) {
			currentOnFavoriteChange()
		}
	}

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Recipe details") },
				actions = {
					IconButton(
						onClick = viewModel::toggleFavorite,
						enabled = canManageFavorites && viewModel.recipeDetails != null && !viewModel.isFavoriteUpdating,
					) {
						Icon(
							imageVector = if (viewModel.recipeDetails?.isFavorite == true) {
								Icons.Filled.Favorite
							} else {
								Icons.Outlined.FavoriteBorder
							},
							contentDescription = if (viewModel.recipeDetails?.isFavorite == true) {
								"Remove from favorites"
							} else {
								"Add to favorites"
							},
							tint = if (viewModel.recipeDetails?.isFavorite == true) {
								MaterialTheme.colorScheme.primary
							} else {
								MaterialTheme.colorScheme.onSurfaceVariant
							},
						)
					}
				},
				navigationIcon = {
					BackNavigationButton(onBack = onBack)
				},
			)
		},
	) { innerPadding ->
		when {
			viewModel.isLoading -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}

			viewModel.errorMessage != null -> RecipeDetailsMessageScreen(
				message = viewModel.errorMessage ?: "Unknown error",
				onBack = onBack,
				modifier = Modifier.padding(innerPadding),
			)

			viewModel.recipeDetails != null -> RecipeDetailsScreen(
				canManageFavorites = canManageFavorites,
				favoriteErrorMessage = viewModel.favoriteErrorMessage,
				isFavoriteUpdating = viewModel.isFavoriteUpdating,
				recipe = viewModel.recipeDetails ?: return@Scaffold,
				onStartCooking = { onStartCooking(recipeId) },
				onToggleFavorite = viewModel::toggleFavorite,
				modifier = Modifier.padding(innerPadding),
			)

			else -> RecipeDetailsMessageScreen(
				message = "Recipe not found",
				onBack = onBack,
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

@Composable
private fun RecipeDetailsScreen(
	canManageFavorites: Boolean,
	favoriteErrorMessage: String?,
	isFavoriteUpdating: Boolean,
	recipe: RecipeDetails,
	onStartCooking: () -> Unit,
	onToggleFavorite: () -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(
		modifier = modifier.fillMaxSize(),
		contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		item {
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.fillMaxWidth()
					.height(240.dp)
					.clip(RoundedCornerShape(24.dp))
					.background(MaterialTheme.colorScheme.surfaceContainerLow),
				contentScale = ContentScale.Crop,
			)
		}

		item {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				Text(
					text = recipe.title,
					style = MaterialTheme.typography.headlineMedium,
				)
				Text(
					text = recipe.description,
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}

		item {
			RecipeMetadataRow(recipe = recipe)
		}

		item {
			Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
				Button(
					onClick = onStartCooking,
					enabled = recipe.steps.isNotEmpty(),
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(text = "Start cooking")
				}
				Button(
					onClick = onToggleFavorite,
					enabled = canManageFavorites && !isFavoriteUpdating,
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(
						text = if (!canManageFavorites) {
							"Sign in to save favorites"
						} else if (recipe.isFavorite) {
							"Remove from favorites"
						} else {
							"Add to favorites"
						},
					)
				}
				favoriteErrorMessage?.let {
					Text(
						text = it,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.error,
					)
				}
			}
		}

		item {
			Text(
				text = "Ingredients",
				style = MaterialTheme.typography.titleLarge,
			)
		}

		items(recipe.ingredientGroups) { group ->
			IngredientGroupCard(group = group)
		}

		item {
			Text(
				text = "Steps",
				style = MaterialTheme.typography.titleLarge,
			)
		}

		items(recipe.steps.indices.toList()) { stepIndex ->
			StepCard(
				stepNumber = stepIndex + 1,
				step = recipe.steps[stepIndex],
			)
		}
	}
}

@Composable
private fun RecipeMetadataRow(recipe: RecipeDetails) {
	val items = listOfNotNull(
		recipe.cuisine?.displayName,
		recipe.totalTime?.let { "$it min" },
		recipe.yields?.takeIf { it.isNotBlank() },
	)

	if (items.isEmpty()) return

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		items.forEach { item ->
			Surface(
				shape = RoundedCornerShape(999.dp),
				color = MaterialTheme.colorScheme.secondaryContainer,
			) {
				Text(
					text = item,
					modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
					style = MaterialTheme.typography.labelLarge,
				)
			}
		}
	}
}

@Composable
private fun IngredientGroupCard(group: IngredientGroup) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			group.name?.takeIf { it.isNotBlank() }?.let {
				Text(
					text = it,
					style = MaterialTheme.typography.titleMedium,
				)
			}

			group.ingredients.forEach { ingredient ->
				Text(
					text = "- $ingredient",
					style = MaterialTheme.typography.bodyLarge,
				)
			}
		}
	}
}

@Composable
private fun StepCard(stepNumber: Int, step: String) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Surface(
				modifier = Modifier.size(36.dp),
				shape = RoundedCornerShape(18.dp),
				color = MaterialTheme.colorScheme.primaryContainer,
			) {
				Box(contentAlignment = Alignment.Center) {
					Text(
						text = stepNumber.toString(),
						style = MaterialTheme.typography.titleMedium,
					)
				}
			}

			Text(
				text = step,
				modifier = Modifier.weight(1f),
				style = MaterialTheme.typography.bodyLarge,
			)
		}
	}
}

@Composable
private fun RecipeDetailsMessageScreen(
	message: String,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
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
			Text(
				text = message,
				style = MaterialTheme.typography.bodyLarge,
			)
			Spacer(modifier = Modifier.height(4.dp))
			TextButton(onClick = onBack) {
				Text(text = "Back to search")
			}
		}
	}
}
