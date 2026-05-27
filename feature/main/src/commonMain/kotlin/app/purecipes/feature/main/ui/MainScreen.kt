package app.purecipes.feature.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.ui.navigation.installAuthFlow
import app.purecipes.feature.cooking.ui.navigation.installCookingFlow
import app.purecipes.feature.favorites.ui.navigation.installFavoritesFlow
import app.purecipes.feature.newrecipe.ui.navigation.installCreateFlow
import app.purecipes.feature.recipedetails.ui.navigation.installRecipeDetailsFlow
import app.purecipes.feature.search.ui.navigation.installSearchFlow
import app.purecipes.feature.settings.ui.navigation.installSettingsFlow
import app.purecipes.shared.ui.component.HandleSystemBack
import app.purecipes.shared.ui.navigation.PostLoginAction
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

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
						installSearchFlow(
							isSignedIn = authenticationState is AuthenticationState.SignedIn,
							sessionKey = sessionKey,
							onRecipeSelect = viewModel::onRecipeSelected,
							onRequestLogInForFilters = {
								viewModel.requestLoginForPostLoginAction(PostLoginAction.OpenSearchFilters)
							},
						)
						installRecipeDetailsFlow(
							navigator = viewModel.navigator,
							canManageFavorites = canManageFavorites,
							sessionKey = sessionKey,
							onFavoriteChange = { favoritesRefreshSignal += 1 },
							onStartCooking = viewModel::onStartCooking,
							onOpenMeasurementPreferences = viewModel::onOpenSettings,
						)
						installCookingFlow(
							navigator = viewModel.navigator,
						)
						installFavoritesFlow(
							refreshSignal = favoritesRefreshSignal,
							sessionKey = sessionKey,
							onRecipeSelect = viewModel::onRecipeSelected,
						)
						installCreateFlow(
							canUploadRecipes = canManageFavorites,
						)
						installAuthFlow(
							navigator = viewModel.navigator,
							googleWebClientId = viewModel.googleWebClientId,
							onOpenSettings = viewModel::onOpenSettings,
							onNavigateToEmailRegistration = viewModel::onOpenEmailRegistration,
							onNavigateToSignIn = viewModel::onOpenEmailSignIn,
							onRegistrationSuccess = viewModel::onRegistrationSuccess,
						)
						installSettingsFlow(
							navigator = viewModel.navigator,
						)
					},
				)
			}
		}
	}
}
