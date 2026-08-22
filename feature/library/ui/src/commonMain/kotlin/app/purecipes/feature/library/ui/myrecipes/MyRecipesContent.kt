package app.purecipes.feature.library.ui.myrecipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.component.VerticalScrollbar
import app.purecipes.shared.ui.component.paging.AdaptiveGridDefaults
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun MyRecipesContent(
	isLoading: Boolean,
	errorMessage: String?,
	recipes: ImmutableList<RecipeSummary>,
	onCreateRecipe: () -> Unit,
	onRecipeSelect: (Int) -> Unit,
	onEditRecipe: (Int) -> Unit,
	onDeleteRecipe: (RecipeSummary) -> Unit,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var pendingDeleteRecipe by remember { mutableStateOf<RecipeSummary?>(null) }

	Box(modifier = modifier.fillMaxSize()) {
		when {
			isLoading && recipes.isEmpty() -> Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}

			errorMessage != null && recipes.isEmpty() -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
				) {
					ErrorText(text = errorMessage, textAlign = TextAlign.Center)
					TextButton(onClick = onRetry) {
						Text(text = "Retry")
					}
				}
			}

			recipes.isEmpty() -> EmptyStateContent(
				icon = Icons.Filled.Add,
				iconContentDescription = "My recipes",
				title = "No recipes uploaded yet",
				description = "Create your own recipes, then edit them any time from here.",
				action = {
					Button(onClick = onCreateRecipe) {
						Text(text = "Create recipe")
					}
				},
			)

			else -> Column(modifier = Modifier.fillMaxSize()) {
				errorMessage?.let { message ->
					ErrorText(
						text = message,
						textAlign = TextAlign.Center,
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.s),
					)
				}
				val gridState = rememberLazyGridState()
				Box(modifier = Modifier.weight(1f)) {
					LazyVerticalGrid(
						columns = GridCells.Adaptive(AdaptiveGridDefaults.MinItemWidth),
						modifier = Modifier.fillMaxSize(),
						state = gridState,
						contentPadding = PaddingValues(PurecipesTheme.space.m),
						verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
						horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
					) {
						item(span = { GridItemSpan(maxLineSpan) }) {
							Text(
								text = if (recipes.size == 1) {
									"1 recipe"
								} else {
									"${recipes.size} recipes"
								},
								style = PurecipesTheme.typography.labelMedium,
								color = PurecipesTheme.colorScheme.onSurfaceVariant,
							)
						}
						items(recipes, key = RecipeSummary::id) { recipe ->
							RecipeCard(
								recipe = recipe,
								onClick = { onRecipeSelect(recipe.id) },
								onEditClick = { onEditRecipe(recipe.id) },
								onDeleteClick = { pendingDeleteRecipe = recipe },
							)
						}
					}
					VerticalScrollbar(
						state = gridState,
						modifier = Modifier.align(Alignment.CenterEnd),
					)
				}
			}
		}

		pendingDeleteRecipe?.let { recipe ->
			DeleteCreatedRecipeDialog(
				recipeName = recipe.title,
				onDismiss = { pendingDeleteRecipe = null },
				onConfirm = {
					onDeleteRecipe(recipe)
					pendingDeleteRecipe = null
				},
			)
		}
	}
}
