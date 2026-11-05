package app.purecipes.adaptive

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import app.purecipes.feature.search.ui.RecipeSearchScreenContent
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

private data class FormFactorMainTab(
	val label: String,
	val icon: ImageVector,
)

private val formFactorMainTabs = listOf(
	FormFactorMainTab(label = "Home", icon = AppIcons.Home),
	FormFactorMainTab(label = "Library", icon = AppIcons.LibraryBooks),
	FormFactorMainTab(label = "Create", icon = AppIcons.Add),
	FormFactorMainTab(label = "Account", icon = AppIcons.Person),
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
		NavigationSuiteScaffold(
			modifier = modifier.fillMaxSize(),
			navigationItems = {
				formFactorMainTabs.forEachIndexed { index, tab ->
					NavigationSuiteItem(
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
			},
		) {
			RecipeSearchScreenContent(
				darkTheme = darkTheme,
				isSearchExpanded = false,
				searchQuery = "",
				hasActiveFilters = false,
				isSearching = false,
				errorMessage = null,
				totalMatches = totalMatches,
				recipes = recipes,
			)
		}
	}
}
