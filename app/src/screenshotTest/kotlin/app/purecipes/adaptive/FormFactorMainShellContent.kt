package app.purecipes.adaptive

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import app.purecipes.feature.search.ui.RecipeSearchScreenContent
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

private data class FormFactorMainTab(
	val label: String,
	val icon: ImageVector,
)

private val formFactorMainTabs = listOf(
	FormFactorMainTab(label = "Home", icon = Icons.Filled.Home),
	FormFactorMainTab(label = "Library", icon = Icons.AutoMirrored.Filled.LibraryBooks),
	FormFactorMainTab(label = "Create", icon = Icons.Filled.Add),
	FormFactorMainTab(label = "Account", icon = Icons.Filled.Person),
)

@Composable
internal fun FormFactorMainShellContent(
	darkTheme: Boolean,
	recipes: ImmutableList<RecipeSummary>,
	totalMatches: Int,
	modifier: Modifier = Modifier,
	selectedTabIndex: Int = 0,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Scaffold(
			modifier = modifier.fillMaxSize(),
			bottomBar = {
				NavigationBar {
					formFactorMainTabs.forEachIndexed { index, tab ->
						NavigationBarItem(
							selected = index == selectedTabIndex,
							onClick = {},
							icon = {
								Icon(
									imageVector = tab.icon,
									contentDescription = tab.label,
								)
							},
							label = { Text(text = tab.label) },
						)
					}
				}
			},
		) { innerPadding ->
			RecipeSearchScreenContent(
				darkTheme = darkTheme,
				isSearchExpanded = false,
				searchQuery = "",
				hasActiveFilters = false,
				isSearching = false,
				errorMessage = null,
				totalMatches = totalMatches,
				recipes = recipes,
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}
