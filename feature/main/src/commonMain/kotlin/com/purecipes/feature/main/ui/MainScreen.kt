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
import com.purecipes.feature.cooking.ui.StepByStepCookingRoute
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.feature.recipedetails.ui.RecipeDetailsRoute
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.feature.search.ui.RecipeSearchScreen
import com.purecipes.shared.ui.component.HandleSystemBack
import com.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Composable
fun MainScreen(
	searchRecipes: SearchRecipesUseCase,
	getRecipeDetails: GetRecipeDetailsUseCase,
	modifier: Modifier = Modifier,
	onExitRequest: () -> Unit = {},
) {
	PurecipesTheme {
		val viewModel = mainViewModel()
		val backStack = rememberMainBackStack()
		val currentDestination = backStack.lastOrNull()
		HandleSystemBack(
			enabled = viewModel.shouldExit(currentDestination),
			onBack = onExitRequest,
		)

		Scaffold(
			modifier = modifier.fillMaxSize(),
			bottomBar = {
				NavigationBar {
					mainTabs.forEach { tab ->
						NavigationBarItem(
							selected = tab.isSelected(currentDestination),
							onClick = { viewModel.onTabSelected(backStack, currentDestination, tab) },
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
							searchRecipes = searchRecipes,
							onRecipeSelect = { recipeId -> viewModel.onRecipeSelected(backStack, recipeId) },
						)
					}
					entry<RecipeDetailsDestination> { destination ->
						RecipeDetailsRoute(
							recipeId = destination.recipeId,
							getRecipeDetails = getRecipeDetails,
							onBack = { viewModel.onBack(backStack) },
							onStartCooking = { recipeId -> viewModel.onStartCooking(backStack, recipeId) },
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<RecipeCookingDestination> { destination ->
						StepByStepCookingRoute(
							recipeId = destination.recipeId,
							getRecipeDetails = getRecipeDetails,
							onBack = { viewModel.onBack(backStack) },
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

internal sealed interface MainDestination : NavKey

internal sealed interface SearchFlowDestination : MainDestination

@Serializable
internal object SearchDestination : SearchFlowDestination

@Serializable
internal data class RecipeDetailsDestination(val recipeId: Int) : SearchFlowDestination

@Serializable
internal data class RecipeCookingDestination(val recipeId: Int) : SearchFlowDestination

@Serializable
internal object FavoritesDestination : MainDestination

@Serializable
internal object CreateDestination : MainDestination

@Serializable
internal object AccountDestination : MainDestination

internal data class MainTab(
	val destination: MainDestination,
	val label: String,
)

internal val mainTabs = listOf(
	MainTab(
		destination = SearchDestination,
		label = "Search",
	),
	MainTab(
		destination = FavoritesDestination,
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

internal fun MainTab.isSelected(currentDestination: NavKey?): Boolean = when (destination) {
	SearchDestination -> currentDestination is SearchFlowDestination
	else -> currentDestination == destination
}

private val MainTab.icon: ImageVector
	get() = when (destination) {
		SearchDestination -> Icons.Filled.Search
		FavoritesDestination -> Icons.Filled.Favorite
		CreateDestination -> Icons.Filled.Add
		AccountDestination -> Icons.Filled.Person
		is RecipeDetailsDestination -> Icons.Filled.Favorite
		is RecipeCookingDestination -> Icons.Filled.Favorite
	}
