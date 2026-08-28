package app.purecipes.feature.main.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.ui.navigation.installAuthFlow
import app.purecipes.feature.cooking.ui.navigation.RecipeCookingDestination
import app.purecipes.feature.cooking.ui.navigation.installCookingFlow
import app.purecipes.feature.library.ui.navigation.installCookbookDetailFlow
import app.purecipes.feature.library.ui.navigation.installLibraryFlow
import app.purecipes.feature.main.ui.analytics.TrackActiveScreenViews
import app.purecipes.feature.newrecipe.ui.navigation.installCreateFlow
import app.purecipes.feature.onboarding.ui.OnboardingScreen
import app.purecipes.feature.recipedetails.ui.navigation.installRecipeDetailsFlow
import app.purecipes.feature.search.ui.navigation.installSearchFlow
import app.purecipes.feature.settings.ui.navigation.installSettingsFlow
import app.purecipes.feature.subscription.ui.navigation.PaywallDestination
import app.purecipes.feature.subscription.ui.navigation.installSubscriptionFlow
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.ui.component.NavigationBackHandler
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
	onPlatformSplashExitStart: () -> Unit = {},
) {
	CompositionLocalProvider(LocalMetroViewModelFactory provides metroViewModelFactory) {
		MainScreenContent(
			modifier = modifier,
			onExitRequest = onExitRequest,
			onDeliverPendingIncomingLink = onDeliverPendingIncomingLink,
			onPlatformSplashExitStart = onPlatformSplashExitStart,
		)
	}
}

@Composable
private fun MainScreenContent(
	modifier: Modifier = Modifier,
	onExitRequest: () -> Unit = {},
	onDeliverPendingIncomingLink: () -> Unit = {},
	onPlatformSplashExitStart: () -> Unit = {},
	viewModel: MainViewModel = assistedMetroViewModel<MainViewModel, MainViewModel.Factory> {
		create(onDeliverPendingIncomingLink = onDeliverPendingIncomingLink)
	},
) {
	PurecipesTheme {
		var mainContentReady by remember { mutableStateOf(false) }
		LaunchedEffect(viewModel, mainContentReady) {
			if (mainContentReady) {
				viewModel.start()
			}
		}
		val isAppReady by viewModel.isContentReady.collectAsState()
		val mainContent: @Composable () -> Unit = {
			if (mainContentReady) {
				val tabBackStack = viewModel.rememberActiveTabBackStack()
				TrackActiveScreenViews(
					selectedTab = viewModel.selectedTab,
					backStack = tabBackStack,
					screenViewTracker = viewModel.screenViewTracker,
				)
				val authenticationState = viewModel.authenticationState
				val sessionKey = when (authenticationState) {
					is AuthenticationState.SignedIn -> authenticationState.user.id
					AuthenticationState.SignedOut -> null
				}
				val canManageFavorites = authenticationState is AuthenticationState.SignedIn
				val listDetailSceneStrategy = rememberMainListDetailSceneStrategy()
				NavigationBackHandler(
					enabled = true,
					backStackDepth = tabBackStack.size,
					onBack = {
						if (!viewModel.onBack() && viewModel.shouldExit()) {
							onExitRequest()
						}
					},
				)

				val isNavBarVisible = tabBackStack.lastOrNull() !is RecipeCookingDestination
				MainNavigationSuiteScaffold(
					selectedTab = viewModel.selectedTab,
					onTabSelect = viewModel::onTabSelected,
					isNavBarVisible = isNavBarVisible,
				) {
					key(viewModel.selectedTab.stackId) {
						NavDisplay(
							backStack = tabBackStack,
							modifier = Modifier.fillMaxSize(),
							sceneStrategies = listOf(listDetailSceneStrategy),
							entryProvider = entryProvider {
								installSearchFlow(
									isSignedIn = authenticationState is AuthenticationState.SignedIn,
									sessionKey = sessionKey,
									onRecipeSelect = viewModel::onRecipeSelected,
									onRequestLogInForFilters = {
										viewModel.requestLoginForPostLoginAction(PostLoginAction.OpenSearchFilters)
									},
									onOpenPaywall = { feature ->
										viewModel.navigator.push(
											PaywallDestination(
												feature = feature,
												origin = AnalyticsOrigin.SEARCH.value,
											),
										)
									},
								)
								installRecipeDetailsFlow(
									navigator = viewModel.navigator,
									canManageFavorites = canManageFavorites,
									sessionKey = sessionKey,
									onStartCooking = viewModel::onStartCooking,
									onOpenMeasurementPreferences = viewModel::onOpenSettings,
								)
								installCookingFlow(
									navigator = viewModel.navigator,
									canManageFavorites = canManageFavorites,
									sessionKey = sessionKey,
									onFindMoreRecipes = viewModel.cookingFlowNavigator::findMoreRecipes,
								)
								installLibraryFlow(
									sessionKey = sessionKey,
									onRecipeSelect = viewModel::onRecipeSelected,
									onCookbookSelect = viewModel.libraryTabNavigator::openCookbook,
									onCookbookImportSuccess = { cookbookId, name, recipeCount ->
										viewModel.libraryTabNavigator.openCookbook(
											CookbookSummary(
												id = cookbookId,
												name = name,
												recipeCount = recipeCount,
												updatedAtEpochMillis = 0L,
											),
										)
									},
									onCreateRecipe = viewModel.createRecipeTabNavigator::openNewRecipe,
									onEditCreatedRecipe = viewModel.createRecipeTabNavigator::openEditor,
									onRequestLogIn = {
										viewModel.requestLoginForPostLoginAction(
											PostLoginAction.OpenFavoritesMyRecipes,
										)
									},
								)
								installCookbookDetailFlow(
									sessionKey = sessionKey,
									onRecipeSelect = viewModel::onRecipeSelected,
									onBack = viewModel::onBack,
								)
								installCreateFlow(
									navigator = viewModel.navigator,
									canUploadRecipes = canManageFavorites,
									onSaveSuccess = viewModel.createRecipeTabNavigator::onRecipeSaveSuccess,
									onRequestLogIn = {
										viewModel.requestLoginForPostLoginAction(PostLoginAction.OpenCreate)
									},
									onOpenPaywall = { feature ->
										viewModel.navigator.push(
											PaywallDestination(
												feature = feature,
												origin = AnalyticsOrigin.CREATE_RECIPE.value,
											),
										)
									},
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
								installSubscriptionFlow(
									navigator = viewModel.navigator,
								)
							},
						)
					}
				}
			}
		}
		PlatformSplash(
			isAppReady = isAppReady,
			onSplashExitStart = onPlatformSplashExitStart,
			onMainContentStart = { mainContentReady = true },
			modifier = modifier,
		) {
			Box(modifier = Modifier.fillMaxSize()) {
				mainContent()
			if (viewModel.onboardingGate.isVisible) {
				OnboardingScreen(onFinish = viewModel.onboardingGate::onFinished)
			}
			}
		}
	}
}
