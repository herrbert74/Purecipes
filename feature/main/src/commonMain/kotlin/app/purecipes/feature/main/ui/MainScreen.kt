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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.ui.authentication.AuthenticationScreen
import app.purecipes.feature.auth.ui.registration.RegistrationScreen
import app.purecipes.feature.auth.ui.signin.SignInScreen
import app.purecipes.feature.cooking.ui.StepByStepCookingRoute
import app.purecipes.feature.favorites.ui.FavoritesScreen
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import app.purecipes.feature.newrecipe.ui.CreateRecipeScreen
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
import app.purecipes.feature.settings.ui.SettingsScreen
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.usecase.ObserveIncomingLinksUseCase
import app.purecipes.feature.sharing.domain.usecase.PublishWebLaunchLinkUseCase
import app.purecipes.shared.ui.component.HandleSystemBack
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable

@Composable
fun MainScreen(
	observeConsentState: ObserveConsentStateUseCase,
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	observeNotificationPreferences: ObserveNotificationPreferencesUseCase,
	refreshConsent: RefreshConsentUseCase,
	setAnalyticsUserId: SetAnalyticsUserIdUseCase,
	showConsentForm: ShowConsentFormUseCase,
	googleWebClientId: String?,
	resetMeasurementPreferences: ResetMeasurementPreferencesUseCase,
	saveMeasurementPreferences: SaveMeasurementPreferencesUseCase,
	saveNotificationPreferences: SaveNotificationPreferencesUseCase,
	sendTestNotification: SendTestNotificationUseCase,
	observeIncomingLinks: ObserveIncomingLinksUseCase,
	publishWebLaunchLink: PublishWebLaunchLinkUseCase,
	metroViewModelFactory: MetroViewModelFactory,
	modifier: Modifier = Modifier,
	onExitRequest: () -> Unit = {},
	onDeliverPendingIncomingLink: () -> Unit = {},
) {
	PurecipesTheme {
		CompositionLocalProvider(LocalMetroViewModelFactory provides metroViewModelFactory) {
			val viewModel = mainViewModel()
			val backStack = viewModel.mainBackStack()
			val rootDestination = backStack.firstOrNull()
			val authenticationState by observeAuthenticationState().collectAsState()
			var favoritesRefreshSignal by remember { mutableIntStateOf(0) }
			val sessionKey = when (val state = authenticationState) {
				is AuthenticationState.SignedIn -> state.user.id
				AuthenticationState.SignedOut -> null
			}
			val currentOnDeliverPendingIncomingLink by rememberUpdatedState(onDeliverPendingIncomingLink)
			val currentObserveIncomingLinks by rememberUpdatedState(observeIncomingLinks)
			LaunchedEffect(Unit) {
				refreshConsent()
			}
			LaunchedEffect(Unit) {
				publishWebLaunchLink()
			}
			LaunchedEffect(sessionKey) {
				currentOnDeliverPendingIncomingLink()
				val isSignedIn = sessionKey != null
				currentObserveIncomingLinks().collectLatest { link ->
					if (link is PurecipesLink.CookbookShare && !isSignedIn) {
						viewModel.stageCookbookShareImport(link.token)
						viewModel.requestLoginForPostLoginAction(PostLoginNavOrigin.COOKBOOK_SHARE_IMPORT)
					} else {
						viewModel.onDeepLink(link)
					}
				}
			}
			LaunchedEffect(sessionKey) {
				setAnalyticsUserId(sessionKey)
			}
			var previousAuthenticationState by remember { mutableStateOf<AuthenticationState?>(null) }
			LaunchedEffect(authenticationState) {
				val previous = previousAuthenticationState
				previousAuthenticationState = authenticationState
				if (previous is AuthenticationState.SignedIn && authenticationState is AuthenticationState.SignedOut) {
					viewModel.clearPostLoginNavigationState()
				} else if (previous is AuthenticationState.SignedOut &&
					authenticationState is AuthenticationState.SignedIn
				) {
					viewModel.onAuthenticationSucceeded()
					when (viewModel.takePostLoginOriginAfterSignIn()) {
						PostLoginNavOrigin.RECIPE_SEARCH_FILTERS -> {
							viewModel.markPendingOpenSearchFiltersAfterLogin()
							viewModel.onTabSelected(mainTabs.first { it.destination == SearchDestination })
						}

						PostLoginNavOrigin.COOKBOOK_SHARE_IMPORT ->
							viewModel.onTabSelected(mainTabs.first { it.destination == FavoritesDestination })

						null -> Unit
					}
				}
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
								observeConsentState = observeConsentState,
								showConsentForm = showConsentForm,
								onOpenSettings = { viewModel.onOpenSettings() },
								onNavigateToEmailRegistration = { viewModel.onOpenEmailRegistration() },
								onNavigateToSignIn = { viewModel.onOpenEmailSignIn() },
								googleWebClientId = googleWebClientId,
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
								observeMeasurementPreferences = observeMeasurementPreferences,
								resetMeasurementPreferences = resetMeasurementPreferences,
								saveMeasurementPreferences = saveMeasurementPreferences,
								saveNotificationPreferences = saveNotificationPreferences,
								observeNotificationPreferences = observeNotificationPreferences,
								sendTestNotification = sendTestNotification,
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
