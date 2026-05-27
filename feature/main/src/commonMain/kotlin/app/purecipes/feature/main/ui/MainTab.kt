package app.purecipes.feature.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.newrecipe.ui.navigation.CreateDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination

internal data class MainTab(
	val destination: NavKey,
	val label: String,
)

internal val mainTabs = listOf(
	MainTab(
		destination = SearchDestination(),
		label = "Search",
	),
	MainTab(
		destination = FavoritesDestination(),
		label = "Favorites",
	),
	MainTab(
		destination = CreateDestination,
		label = "Create",
	),
	MainTab(
		destination = AccountDestination,
		label = "Account",
	),
)

internal fun MainTab.isSelected(rootDestination: NavKey?): Boolean = when (destination) {
	is SearchDestination -> rootDestination is SearchDestination
	is FavoritesDestination -> rootDestination is FavoritesDestination
	else -> rootDestination == destination
}

internal val MainTab.icon: ImageVector
	get() = when (destination) {
		is SearchDestination -> Icons.Filled.Search
		is FavoritesDestination -> Icons.Filled.Favorite
		CreateDestination -> Icons.Filled.Add
		AccountDestination -> Icons.Filled.Person
		else -> error("$destination is not a tab destination")
	}
