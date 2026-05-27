package app.purecipes.feature.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.ui.authentication.AuthenticationScreen
import app.purecipes.feature.auth.ui.registration.RegistrationScreen
import app.purecipes.feature.auth.ui.signin.SignInScreen
import app.purecipes.feature.cooking.ui.StepByStepCookingRoute
import app.purecipes.feature.favorites.ui.FavoritesScreen
import app.purecipes.feature.newrecipe.ui.CreateRecipeScreen
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.settings.ui.SettingsScreen
import app.purecipes.shared.ui.component.HandleSystemBack
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.serialization.Serializable

@Composable
fun MainScreen(
	metroViewModelFactory: MetroViewModelFactory,
	modifier: Modifier = Modifier,
	onExitRequest: () -> Unit = {},
	onDeliverPendingIncomingLink: () -> Unit = {},
) {
	MainScreenContent(
		metroViewModelFactory = metroViewModelFactory,
		modifier = modifier,
		onExitRequest = onExitRequest,
		onDeliverPendingIncomingLink = onDeliverPendingIncomingLink,
	)
}

@Composable
private fun MainScreenContent(
	metroViewModelFactory: MetroViewModelFactory,
	modifier: Modifier = Modifier,
	onExitRequest: () -> Unit = {},
	onDeliverPendingIncomingLink: () -> Unit = {},
	viewModel: MainViewModel = assistedMetroViewModel<MainViewModel, MainViewModel.Factory> {
		create(onDeliverPendingIncomingLink = onDeliverPendingIncomingLink)
	},
) {
	PurecipesTheme {
		CompositionLocalProvider(LocalMetroViewModelFactory provides metroViewModelFactory) {
			LaunchedEffect(viewModel) {
				viewModel.start()
			}
			val backStack = viewModel.mainBackStack()
			val rootDestination = backStack.firstOrNull()
			val authenticationState = viewModel.authenticationState
			var favoritesRefreshSignal by remember { mutableIntStateOf(0) }
			val sessionKey = when (authenticationState) {
				is AuthenticationState.SignedIn -> authenticationState.user.id
				AuthenticationState.SignedOut -> null
			}
			val canManageFavorites = authenticationState is AuthenticationState.SignedIn
			HandleSystemBack(
				enabled = true,
				onBack = {
					if (!viewModel.onBack()) {
						onExitRequest()
					}
				},
			)

			Scaffold(
				modifier = modifier.fillMaxSize(),
				bottomBar = {
					NavigationBar {
						mainTabs.forEach { tab ->
							NavigationBarItem(
								selected = tab.isSelected(rootDestination),
								onClick = { viewModel.onTabSelected(tab) },
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
					onBack = {
						viewModel.onBack()
					},
					entryProvider = entryProvider {
						entry<SearchDestination> {
							val initialShowFilterSheet = remember(sessionKey) {
								viewModel.takePendingOpenSearchFilters()
							}
							RecipeSearchScreen(
								initialShowFilterSheet = initialShowFilterSheet,
								isSignedIn = authenticationState is AuthenticationState.SignedIn,
								modifier = Modifier.fillMaxSize(),
								onRecipeSelect = { recipeId -> viewModel.onRecipeSelected(recipeId) },
								onRequestLogInForFilters = {
									viewModel.requestLoginForPostLoginAction(
										PostLoginNavOrigin.RECIPE_SEARCH_FILTERS,
									)
								},
								sessionKey = sessionKey,
							)
						}
						entry<RecipeDetailsDestination> { destination ->
							RecipeDetailsScreen(
								recipeId = destination.recipeId,
								canManageFavorites = canManageFavorites,
								onOpenMeasurementPreferences = { viewModel.onOpenSettings() },
								onBack = { viewModel.onBack() },
								onFavoriteChange = { favoritesRefreshSignal += 1 },
								onStartCooking = { recipeId -> viewModel.onStartCooking(recipeId) },
								sessionKey = sessionKey,
								modifier = Modifier.fillMaxSize(),
							)
						}
						entry<RecipeCookingDestination> { destination ->
							StepByStepCookingRoute(
								recipeId = destination.recipeId,
								onBack = { viewModel.onBack() },
								modifier = Modifier.fillMaxSize(),
							)
						}
						entry<FavoritesDestination> {
							val initialCookbookShareToken = remember {
								viewModel.takePendingCookbookShareToken()
							}
							FavoritesScreen(
								refreshSignal = favoritesRefreshSignal,
								modifier = Modifier.fillMaxSize(),
								sessionKey = sessionKey,
								initialCookbookShareToken = initialCookbookShareToken,
								onRecipeSelect = { recipeId -> viewModel.onRecipeSelected(recipeId) },
							)
						}
						entry<CreateDestination> {
							CreateRecipeScreen(
								canUploadRecipes = canManageFavorites,
								modifier = Modifier.fillMaxSize(),
							)
						}
						entry<AccountDestination> {
							AuthenticationScreen(
								onOpenSettings = { viewModel.onOpenSettings() },
								onNavigateToEmailRegistration = { viewModel.onOpenEmailRegistration() },
								onNavigateToSignIn = { viewModel.onOpenEmailSignIn() },
								googleWebClientId = viewModel.googleWebClientId,
								modifier = Modifier.fillMaxSize(),
							)
						}
						entry<EmailRegistrationDestination> {
							RegistrationScreen(
								onBack = { viewModel.onBack() },
								onRegistrationSuccess = { email -> viewModel.onRegistrationSuccess(email) },
								modifier = Modifier.fillMaxSize(),
							)
						}
						entry<EmailSignInDestination> { destination ->
							SignInScreen(
								initialEmail = destination.prefilledEmail,
								showRegistrationSuccessMessage = destination.showRegistrationSuccessMessage,
								onBack = { viewModel.onBack() },
								modifier = Modifier.fillMaxSize(),
							)
						}
						entry<AccountSettingsDestination> {
							SettingsScreen(
								onBack = {
									viewModel.onBack()
								},
								modifier = Modifier.fillMaxSize(),
							)
						}
					},
				)
			}
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

@Serializable
internal object EmailRegistrationDestination : MainDestination

@Serializable
internal data class EmailSignInDestination(
	val prefilledEmail: String = "",
	val showRegistrationSuccessMessage: Boolean = false,
) : MainDestination

@Serializable
internal object AccountSettingsDestination : MainDestination

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

internal fun MainTab.isSelected(rootDestination: NavKey?): Boolean = rootDestination == destination

private val MainTab.icon: ImageVector
	get() = when (destination) {
		SearchDestination -> Icons.Filled.Search
		FavoritesDestination -> Icons.Filled.Favorite
		CreateDestination -> Icons.Filled.Add
		AccountDestination -> Icons.Filled.Person
		EmailRegistrationDestination -> error("EmailRegistrationDestination is not a tab destination")
		is EmailSignInDestination -> error("EmailSignInDestination is not a tab destination")
		AccountSettingsDestination -> error("AccountSettingsDestination is not a tab destination")
		is RecipeDetailsDestination -> error("RecipeDetailsDestination is not a tab destination")
		is RecipeCookingDestination -> error("RecipeCookingDestination is not a tab destination")
	}
