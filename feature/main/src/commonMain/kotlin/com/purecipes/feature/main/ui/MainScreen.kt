package com.purecipes.feature.main.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.feature.recipedetails.ui.RecipeDetailsRoute
import com.purecipes.feature.recipedetails.ui.StepByStepCookingRoute
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.ui.RecipeSearchScreen
import com.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Composable
fun MainScreen(
	recipeSearchRepository: RecipeSearchRepository,
	recipeDetailsRepository: RecipeDetailsRepository,
	modifier: Modifier = Modifier,
) {
	PurecipesTheme {
		val backStack = rememberMainBackStack()
		val currentDestination = backStack.lastOrNull()

		Scaffold(
			modifier = modifier.fillMaxSize(),
			bottomBar = {
				NavigationBar {
					mainTabs.forEach { tab ->
						NavigationBarItem(
							selected = tab.isSelected(currentDestination),
							onClick = {
								if (!tab.isSelected(currentDestination) || backStack.lastOrNull() != tab.destination) {
									backStack.clear()
									backStack += tab.destination
								}
							},
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
			NavDisplay(
				backStack = backStack,
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				entryProvider = entryProvider {
					entry<SearchDestination> {
						RecipeSearchScreen(
							modifier = Modifier.fillMaxSize(),
							repository = recipeSearchRepository,
							onRecipeSelect = { recipeId ->
								backStack += RecipeDetailsDestination(recipeId)
							},
						)
					}
					entry<RecipeDetailsDestination> { destination ->
						RecipeDetailsRoute(
							recipeId = destination.recipeId,
							repository = recipeDetailsRepository,
							onBack = { backStack.popLast() },
							onStartCooking = { recipeId ->
								backStack += RecipeCookingDestination(recipeId)
							},
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<RecipeCookingDestination> { destination ->
						StepByStepCookingRoute(
							recipeId = destination.recipeId,
							repository = recipeDetailsRepository,
							onBack = { backStack.popLast() },
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<FavoritesDestination> {
						PlaceholderScreen(
							title = "Favorites",
							subtitle = "Not implemented yet",
							icon = Icons.Filled.Favorite,
						)
					}
					entry<CreateDestination> {
						PlaceholderScreen(
							title = "Create",
							subtitle = "Not implemented yet",
							icon = Icons.Filled.Add,
						)
					}
					entry<AccountDestination> {
						PlaceholderScreen(
							title = "Account",
							subtitle = "Not implemented yet",
							icon = Icons.Filled.Person,
						)
					}
				},
			)
		}
	}
}

@Composable
private fun rememberMainBackStack() = rememberNavBackStack(
	configuration = remember {
		SavedStateConfiguration {
			serializersModule = SerializersModule {
				polymorphic(baseClass = NavKey::class) {
					subclass(SearchDestination.serializer())
					subclass(RecipeDetailsDestination.serializer())
					subclass(RecipeCookingDestination.serializer())
					subclass(FavoritesDestination.serializer())
					subclass(CreateDestination.serializer())
					subclass(AccountDestination.serializer())
				}
			}
		}
	},
	SearchDestination,
)

@Composable
private fun PlaceholderScreen(
	title: String,
	subtitle: String,
	icon: ImageVector,
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Icon(
				imageVector = icon,
				contentDescription = title,
				modifier = Modifier.size(64.dp),
			)
			Text(
				text = title,
				style = MaterialTheme.typography.headlineSmall,
			)
			Text(
				text = subtitle,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center,
			)
		}
	}
}

private sealed interface MainDestination : NavKey

private sealed interface SearchFlowDestination : MainDestination

@Serializable
private object SearchDestination : SearchFlowDestination

@Serializable
private data class RecipeDetailsDestination(val recipeId: Int) : SearchFlowDestination

@Serializable
private data class RecipeCookingDestination(val recipeId: Int) : SearchFlowDestination

@Serializable
private object FavoritesDestination : MainDestination

@Serializable
private object CreateDestination : MainDestination

@Serializable
private object AccountDestination : MainDestination

private data class MainTab(
	val destination: MainDestination,
	val label: String,
	val icon: ImageVector,
)

private val mainTabs = listOf(
	MainTab(
		destination = SearchDestination,
		label = "Search",
		icon = Icons.Filled.Search,
	),
	MainTab(
		destination = FavoritesDestination,
		label = "Favorites",
		icon = Icons.Filled.Favorite,
	),
	MainTab(
		destination = CreateDestination,
		label = "Create",
		icon = Icons.Filled.Add,
	),
	MainTab(
		destination = AccountDestination,
		label = "Account",
		icon = Icons.Filled.Person,
	),
)

private fun MainTab.isSelected(currentDestination: NavKey?): Boolean = when (destination) {
	SearchDestination -> currentDestination is SearchFlowDestination
	else -> currentDestination == destination
}

private fun MutableList<NavKey>.popLast() {
	if (size > 1) {
		removeAt(lastIndex)
	}
}
