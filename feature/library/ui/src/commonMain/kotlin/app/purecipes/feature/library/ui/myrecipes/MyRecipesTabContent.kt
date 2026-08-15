package app.purecipes.feature.library.ui.myrecipes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MyRecipesTabContent(
	onCreateRecipe: () -> Unit,
	onRecipeSelect: (Int) -> Unit,
	onEditRecipe: (Int) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: MyRecipesViewModel = metroViewModel(),
) {
	LaunchedEffect(Unit) {
		viewModel.reload()
	}

	MyRecipesContent(
		isLoading = viewModel.isLoading,
		errorMessage = viewModel.errorMessage,
		recipes = viewModel.recipes.toImmutableList(),
		onCreateRecipe = onCreateRecipe,
		onRecipeSelect = onRecipeSelect,
		onEditRecipe = onEditRecipe,
		onDeleteRecipe = { recipe -> viewModel.deleteRecipe(recipe) },
		onRetry = viewModel::retry,
		modifier = modifier,
	)
}

@Preview(
	name = "My recipes empty light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun MyRecipesTabContentEmptyLightPreview() {
	PurecipesTheme(darkTheme = false) {
		MyRecipesContent(
			isLoading = false,
			errorMessage = null,
			recipes = persistentListOf(),
			onCreateRecipe = {},
			onRecipeSelect = {},
			onEditRecipe = {},
			onDeleteRecipe = {},
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
private fun MyRecipesTabContentListLightPreview() {
	PurecipesTheme(darkTheme = false) {
		MyRecipesContent(
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
			onRecipeSelect = {},
			onEditRecipe = {},
			onDeleteRecipe = {},
			onRetry = {},
		)
	}
}
