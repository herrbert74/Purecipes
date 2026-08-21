package app.purecipes.feature.library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.library.ui.favorites.FavoritesTabContent
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun LibraryScreenContent(
	darkTheme: Boolean,
	recipes: ImmutableList<RecipeSummary>,
	totalMatches: Int,
	modifier: Modifier = Modifier,
	selectedTabIndex: Int = 0,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Scaffold(
			modifier = modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = "Library") },
				)
			},
		) { innerPadding ->
			Column(modifier = Modifier.padding(innerPadding)) {
				PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
					Tab(
						selected = selectedTabIndex == 0,
						onClick = {},
						text = { Text(text = "Favorites") },
					)
					Tab(
						selected = selectedTabIndex == 1,
						onClick = {},
						text = { Text(text = "Cookbooks") },
					)
					Tab(
						selected = selectedTabIndex == 2,
						onClick = {},
						text = { Text(text = "My recipes") },
					)
				}
				val recipeList = remember(recipes) {
					mutableStateListOf<RecipeSummary>().apply { addAll(recipes) }
				}
				val paginationState = remember(recipes) {
					PaginationState<Int, RecipeSummary>(
						initialPageKey = 1,
						onRequestPage = { },
					).apply {
						if (recipes.isNotEmpty()) {
							appendPage(1, recipes, nextPageKey = 2, isLastPage = true)
						}
					}
				}
				FavoritesTabContent(
					errorMessage = null,
					paginationState = paginationState,
					recipes = recipeList,
					totalMatches = totalMatches,
					onRecipeSelect = {},
					modifier = Modifier.weight(1f),
				)
			}
		}
	}
}

@Preview(
	name = "Library favorites light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun LibraryScreenContentLightPreview() {
	LibraryScreenContent(
		darkTheme = false,
		recipes = persistentListOf(
			RecipeSummary(
				id = 1,
				title = "Tomato Pasta",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 25,
			),
			RecipeSummary(
				id = 2,
				title = "Miso Ramen",
				cuisine = Cuisine.JAPANESE,
				imageUrl = null,
				totalTime = 45,
			),
		),
		totalMatches = 2,
	)
}
