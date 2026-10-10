package app.purecipes.feature.main.ui

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.analytics.domain.model.AnalyticsActiveTab
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.home.ui.navigation.HomeDestination
import app.purecipes.feature.library.ui.navigation.LibraryDestination
import app.purecipes.feature.newrecipe.ui.navigation.CreateDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.shared.ui.icon.AppIcons

internal enum class MainTabStackId {
	Home,
	Search,
	Library,
	Create,
	Account,
}

internal val MainTab.stackId: MainTabStackId
	get() = when (destination) {
		is HomeDestination -> MainTabStackId.Home
		is SearchDestination -> MainTabStackId.Search
		is LibraryDestination -> MainTabStackId.Library
		CreateDestination -> MainTabStackId.Create
		AccountDestination -> MainTabStackId.Account
		else -> error("$destination is not a tab destination")
	}

internal val MainTabStackId.saveStateKey: String
	get() = when (this) {
		MainTabStackId.Home -> "main_tab_back_stack_Search"
		MainTabStackId.Search -> "main_tab_back_stack_RecipeSearch"
		else -> "main_tab_back_stack_$name"
	}

internal fun MainTabStackId.toAnalyticsActiveTab(): String = when (this) {
	MainTabStackId.Home -> AnalyticsActiveTab.HOME
	MainTabStackId.Search -> AnalyticsActiveTab.SEARCH
	MainTabStackId.Library -> AnalyticsActiveTab.FAVORITES
	MainTabStackId.Create -> AnalyticsActiveTab.CREATE
	MainTabStackId.Account -> AnalyticsActiveTab.ACCOUNT
}

internal data class MainTab(
	val destination: NavKey,
	val label: String,
)

internal val mainTabs = listOf(
	MainTab(
		destination = HomeDestination,
		label = "Home",
	),
	MainTab(
		destination = SearchDestination(),
		label = "Search",
	),
	MainTab(
		destination = LibraryDestination(),
		label = "Library",
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
	is HomeDestination -> rootDestination is HomeDestination
	is SearchDestination -> rootDestination is SearchDestination
	is LibraryDestination -> rootDestination is LibraryDestination
	else -> rootDestination == destination
}

internal val MainTab.icon: ImageVector
	get() = when (destination) {
		is HomeDestination -> AppIcons.Home
		is SearchDestination -> AppIcons.Search
		is LibraryDestination -> AppIcons.LibraryBooks
		CreateDestination -> AppIcons.Add
		AccountDestination -> AppIcons.Person
		else -> error("$destination is not a tab destination")
	}
