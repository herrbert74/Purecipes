package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CreatedRecipesTabContent(
	onCreateRecipe: () -> Unit,
	onEditRecipe: (Int) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: CreatedRecipesViewModel = metroViewModel(),
) {
	LaunchedEffect(Unit) {
		viewModel.reload()
	}

	CreatedRecipesTabContent(
		isLoading = viewModel.isLoading,
		errorMessage = viewModel.errorMessage,
		recipes = viewModel.recipes.toImmutableList(),
		onCreateRecipe = onCreateRecipe,
		onEditRecipe = onEditRecipe,
		onRetry = viewModel::retry,
		modifier = modifier,
	)
}

@Composable
internal fun CreatedRecipesTabContent(
	isLoading: Boolean,
	errorMessage: String?,
	recipes: ImmutableList<RecipeSummary>,
	onCreateRecipe: () -> Unit,
	onEditRecipe: (Int) -> Unit,
	onRetry: () -> Unit,
	modifier: Modifier = Modifier,
) {
	when {
		isLoading && recipes.isEmpty() -> Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center,
		) {
			CircularProgressIndicator()
		}

		errorMessage != null && recipes.isEmpty() -> Box(
			modifier = modifier
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
			modifier = modifier,
			action = {
				Button(onClick = onCreateRecipe) {
					Text(text = "Create recipe")
				}
			},
		)

		else -> LazyColumn(
			modifier = modifier.fillMaxSize(),
			contentPadding = PaddingValues(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			item {
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
					onClick = { onEditRecipe(recipe.id) },
				)
			}
		}
	}
}

@Preview(
	name = "My recipes empty light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CreatedRecipesTabContentEmptyLightPreview() {
	PurecipesTheme(darkTheme = false) {
		CreatedRecipesTabContent(
			isLoading = false,
			errorMessage = null,
			recipes = persistentListOf(),
			onCreateRecipe = {},
			onEditRecipe = {},
			onRetry = {},
		)
	}
}

@Preview(
	name = "My recipes list light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CreatedRecipesTabContentListLightPreview() {
	PurecipesTheme(darkTheme = false) {
		CreatedRecipesTabContent(
			isLoading = false,
			errorMessage = null,
			recipes = persistentListOf(
				RecipeSummary(
					id = 1,
					title = "Tomato Pasta",
					cuisine = Cuisine.ITALIAN,
					imageUrl = null,
					totalTime = 25,
				),
			),
			onCreateRecipe = {},
			onEditRecipe = {},
			onRetry = {},
		)
	}
}
