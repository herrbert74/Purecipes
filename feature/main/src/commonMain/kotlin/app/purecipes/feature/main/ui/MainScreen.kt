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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.DeleteAccountUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SendPasswordResetEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.feature.auth.ui.authentication.AuthenticationScreen
import app.purecipes.feature.auth.ui.registration.RegistrationScreen
import app.purecipes.feature.auth.ui.signin.SignInScreen
import app.purecipes.feature.cooking.ui.StepByStepCookingRoute
import app.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.favorites.ui.FavoritesScreen
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.feature.newrecipe.ui.CreateRecipeScreen
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
import app.purecipes.feature.settings.ui.SettingsScreen
import app.purecipes.shared.ui.component.HandleSystemBack
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Composable
fun MainScreen(
	observeConsentState: ObserveConsentStateUseCase,
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	observeNotificationPreferences: ObserveNotificationPreferencesUseCase,
	refreshConsent: RefreshConsentUseCase,
	setAnalyticsUserId: SetAnalyticsUserIdUseCase,
	showConsentForm: ShowConsentFormUseCase,
	signInWithEmail: SignInWithEmailUseCase,
	registerWithEmail: RegisterWithEmailUseCase,
	resendEmailVerification: ResendEmailVerificationUseCase,
	sendPasswordResetEmail: SendPasswordResetEmailUseCase,
	signInWithExternalProvider: SignInWithExternalProviderUseCase,
	signInWithGoogle: SignInWithGoogleUseCase,
	deleteAccount: DeleteAccountUseCase,
	signOut: SignOutUseCase,
	addFavoriteRecipe: AddFavoriteRecipeUseCase,
	filterRecipesForMeasurementPreferences: FilterRecipesForMeasurementPreferencesUseCase,
	getCreatedRecipes: GetCreatedRecipesUseCase,
	getFavoriteRecipesPage: GetFavoriteRecipesPageUseCase,
	getCookbooksPage: GetCookbooksPageUseCase,
	createCookbook: CreateCookbookUseCase,
	deleteCookbook: DeleteCookbookUseCase,
	getCookbookRecipesPage: GetCookbookRecipesPageUseCase,
	getCookbookCoverImageUrl: GetCookbookCoverImageUrlUseCase,
	getRecipeCookbooks: GetRecipeCookbooksUseCase,
	addRecipeToCookbook: AddRecipeToCookbookUseCase,
	getMeasurementPreferences: GetMeasurementPreferencesUseCase,
	searchRecipes: SearchRecipesUseCase,
	getSearchFilters: GetSearchFiltersUseCase,
	saveSearchFilters: SaveSearchFiltersUseCase,
	getUserPantry: GetUserPantryUseCase,
	updateUserPantry: UpdateUserPantryUseCase,
	getRecipeDetails: GetRecipeDetailsUseCase,
	googleWebClientId: String?,
	markMeasurementMismatchSeen: MarkMeasurementMismatchSeenUseCase,
	processRecipeDetailsForMeasurementPreferences: ProcessRecipeDetailsForMeasurementPreferencesUseCase,
	removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
	resetMeasurementPreferences: ResetMeasurementPreferencesUseCase,
	saveMeasurementPreferences: SaveMeasurementPreferencesUseCase,
	saveNotificationPreferences: SaveNotificationPreferencesUseCase,
	sendTestNotification: SendTestNotificationUseCase,
	saveCreatedRecipe: SaveCreatedRecipeUseCase,
	trackEvent: TrackEventUseCase,
	modifier: Modifier = Modifier,
	onExitRequest: () -> Unit = {},
) {
	PurecipesTheme {
		val viewModel = mainViewModel()
		val backStack = rememberMainBackStack()
		val rootDestination = backStack.firstOrNull()
		val authenticationState by observeAuthenticationState().collectAsState()
		var favoritesRefreshSignal by remember { mutableIntStateOf(0) }
		val sessionKey = when (val state = authenticationState) {
			is AuthenticationState.SignedIn -> state.user.id
			AuthenticationState.SignedOut -> null
		}
		LaunchedEffect(Unit) {
			refreshConsent()
		}
		LaunchedEffect(sessionKey) {
			setAnalyticsUserId(sessionKey)
		}
		var previousAuthenticationState by remember { mutableStateOf<AuthenticationState?>(null) }
		LaunchedEffect(authenticationState) {
			val previous = previousAuthenticationState
			previousAuthenticationState = authenticationState
			when {
				previous is AuthenticationState.SignedIn && authenticationState is AuthenticationState.SignedOut ->
					viewModel.clearPostLoginNavigationState()

				previous is AuthenticationState.SignedOut && authenticationState is AuthenticationState.SignedIn -> {
					viewModel.onAuthenticationSucceeded(backStack)
					when (viewModel.takePostLoginOriginAfterSignIn()) {
						PostLoginNavOrigin.RECIPE_SEARCH_FILTERS -> {
							viewModel.markPendingOpenSearchFiltersAfterLogin()
							viewModel.onTabSelected(
								backStack,
								mainTabs.first { it.destination == SearchDestination },
							)
						}

						null -> Unit
					}
				}
			}
		}
		val canManageFavorites = authenticationState is AuthenticationState.SignedIn
		HandleSystemBack(
			enabled = viewModel.shouldExit(backStack),
			onBack = onExitRequest,
		)

		Scaffold(
			modifier = modifier.fillMaxSize(),
			bottomBar = {
				NavigationBar {
					mainTabs.forEach { tab ->
						NavigationBarItem(
							selected = tab.isSelected(rootDestination),
							onClick = { viewModel.onTabSelected(backStack, tab) },
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
						val initialShowFilterSheet = remember(sessionKey) {
							viewModel.takePendingOpenSearchFilters()
						}
						RecipeSearchScreen(
							filterRecipesForMeasurementPreferences = filterRecipesForMeasurementPreferences,
							getMeasurementPreferences = getMeasurementPreferences,
							getSearchFilters = getSearchFilters,
							getUserPantry = getUserPantry,
							initialShowFilterSheet = initialShowFilterSheet,
							isSignedIn = authenticationState is AuthenticationState.SignedIn,
							modifier = Modifier.fillMaxSize(),
							onRecipeSelect = { recipeId -> viewModel.onRecipeSelected(backStack, recipeId) },
							onRequestLogInForFilters = {
								viewModel.requestLoginForPostLoginAction(
									PostLoginNavOrigin.RECIPE_SEARCH_FILTERS,
									backStack,
								)
							},
							saveSearchFilters = saveSearchFilters,
							searchRecipes = searchRecipes,
							sessionKey = sessionKey,
							trackEvent = trackEvent,
							updateUserPantry = updateUserPantry,
						)
					}
					entry<RecipeDetailsDestination> { destination ->
						RecipeDetailsScreen(
							recipeId = destination.recipeId,
							addFavoriteRecipe = addFavoriteRecipe,
							addRecipeToCookbook = addRecipeToCookbook,
							canManageFavorites = canManageFavorites,
							createCookbook = createCookbook,
							getCookbooksPage = getCookbooksPage,
							getRecipeCookbooks = getRecipeCookbooks,
							getRecipeDetails = getRecipeDetails,
							getMeasurementPreferences = getMeasurementPreferences,
							markMeasurementMismatchSeen = markMeasurementMismatchSeen,
							onOpenMeasurementPreferences = { viewModel.onOpenSettings(backStack) },
							processRecipeDetailsForMeasurementPreferences =
								processRecipeDetailsForMeasurementPreferences,
							trackEvent = trackEvent,
							onBack = { viewModel.onBack(backStack) },
							onFavoriteChange = { favoritesRefreshSignal += 1 },
							onStartCooking = { recipeId -> viewModel.onStartCooking(backStack, recipeId) },
							removeFavoriteRecipe = removeFavoriteRecipe,
							sessionKey = sessionKey,
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<RecipeCookingDestination> { destination ->
						StepByStepCookingRoute(
							recipeId = destination.recipeId,
							getRecipeDetails = getRecipeDetails,
							getMeasurementPreferences = getMeasurementPreferences,
							processRecipeDetailsForMeasurementPreferences =
								processRecipeDetailsForMeasurementPreferences,
							trackEvent = trackEvent,
							onBack = { viewModel.onBack(backStack) },
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<FavoritesDestination> {
						FavoritesScreen(
							getFavoriteRecipesPage = getFavoriteRecipesPage,
							getCookbooksPage = getCookbooksPage,
							createCookbook = createCookbook,
							deleteCookbook = deleteCookbook,
							getCookbookRecipesPage = getCookbookRecipesPage,
							getCookbookCoverImageUrl = getCookbookCoverImageUrl,
							refreshSignal = favoritesRefreshSignal,
							sessionKey = sessionKey,
							modifier = Modifier.fillMaxSize(),
							onRecipeSelect = { recipeId -> viewModel.onRecipeSelected(backStack, recipeId) },
						)
					}
					entry<CreateDestination> {
						CreateRecipeScreen(
							canUploadRecipes = canManageFavorites,
							getCreatedRecipes = getCreatedRecipes,
							saveCreatedRecipe = saveCreatedRecipe,
							trackEvent = trackEvent,
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<AccountDestination> {
						AuthenticationScreen(
							observeConsentState = observeConsentState,
							observeAuthenticationState = observeAuthenticationState,
							signInWithExternalProvider = signInWithExternalProvider,
							signInWithGoogle = signInWithGoogle,
							showConsentForm = showConsentForm,
							deleteAccount = deleteAccount,
							signOut = signOut,
							onOpenSettings = { viewModel.onOpenSettings(backStack) },
							onNavigateToEmailRegistration = { viewModel.onOpenEmailRegistration(backStack) },
							onNavigateToSignIn = { viewModel.onOpenEmailSignIn(backStack) },
							googleWebClientId = googleWebClientId,
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<EmailRegistrationDestination> {
						RegistrationScreen(
							registerWithEmail = registerWithEmail,
							onBack = { viewModel.onBack(backStack) },
							onRegistrationSuccess = { email ->
								viewModel.onRegistrationSuccess(backStack, email)
							},
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<EmailSignInDestination> { destination ->
						SignInScreen(
							signInWithEmail = signInWithEmail,
							resendEmailVerification = resendEmailVerification,
							sendPasswordResetEmail = sendPasswordResetEmail,
							initialEmail = destination.prefilledEmail,
							showRegistrationSuccessMessage = destination.showRegistrationSuccessMessage,
							onBack = { viewModel.onBack(backStack) },
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
							onBack = { viewModel.onBack(backStack) },
							modifier = Modifier.fillMaxSize(),
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
					subclass(EmailRegistrationDestination.serializer())
					subclass(EmailSignInDestination.serializer())
					subclass(AccountSettingsDestination.serializer())
				}
			}
		}
	},
	SearchDestination,
)

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
