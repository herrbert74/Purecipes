package app.purecipes.feature.main.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.newrecipe.ui.navigation.CreateDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination

internal enum class MainTabStackId {
	Search,
	Favorites,
	Create,
	Account,
}

internal val MainTab.stackId: MainTabStackId
	get() = when (destination) {
		is SearchDestination -> MainTabStackId.Search
		is FavoritesDestination -> MainTabStackId.Favorites
		CreateDestination -> MainTabStackId.Create
		AccountDestination -> MainTabStackId.Account
		else -> error("$destination is not a tab destination")
	}

internal val MainTabStackId.saveStateKey: String
	get() = "main_tab_back_stack_$name"

internal data class MainTab(
	val destination: NavKey,
	val label: String,
)

internal val mainTabs = listOf(
	MainTab(
		destination = SearchDestination(),
		label = "Home",
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
		is SearchDestination -> Icons.Filled.Home
		is FavoritesDestination -> Icons.Filled.Favorite
		CreateDestination -> Icons.Filled.Add
		AccountDestination -> Icons.Filled.Person
		else -> error("$destination is not a tab destination")
	}
