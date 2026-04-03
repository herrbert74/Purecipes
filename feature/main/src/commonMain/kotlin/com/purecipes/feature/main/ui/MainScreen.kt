package com.purecipes.feature.main.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import com.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import com.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import com.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.analytics.ui.ConsentPreferencesScreen
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.purecipes.feature.auth.ui.AuthenticationScreen
import com.purecipes.feature.cooking.ui.StepByStepCookingRoute
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.favorites.ui.FavoritesScreen
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.feature.newrecipe.ui.CreateRecipeScreen
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
	observeConsentState: ObserveConsentStateUseCase,
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	refreshConsent: RefreshConsentUseCase,
	setAnalyticsUserId: SetAnalyticsUserIdUseCase,
	showConsentForm: ShowConsentFormUseCase,
	signInWithEmail: SignInWithEmailUseCase,
	registerWithEmail: RegisterWithEmailUseCase,
	signInWithExternalProvider: SignInWithExternalProviderUseCase,
	signInWithGoogle: SignInWithGoogleUseCase,
	signOut: SignOutUseCase,
	addFavoriteRecipe: AddFavoriteRecipeUseCase,
	getCreatedRecipes: GetCreatedRecipesUseCase,
	getFavoriteRecipes: GetFavoriteRecipesUseCase,
	searchRecipes: SearchRecipesUseCase,
	getRecipeDetails: GetRecipeDetailsUseCase,
	googleWebClientId: String?,
	removeFavoriteRecipe: RemoveFavoriteRecipeUseCase,
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
						RecipeSearchScreen(
							modifier = Modifier.fillMaxSize(),
							searchRecipes = searchRecipes,
							trackEvent = trackEvent,
							onRecipeSelect = { recipeId -> viewModel.onRecipeSelected(backStack, recipeId) },
						)
					}
					entry<RecipeDetailsDestination> { destination ->
						RecipeDetailsRoute(
							recipeId = destination.recipeId,
							addFavoriteRecipe = addFavoriteRecipe,
							canManageFavorites = canManageFavorites,
							getRecipeDetails = getRecipeDetails,
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
							trackEvent = trackEvent,
							onBack = { viewModel.onBack(backStack) },
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<FavoritesDestination> {
						FavoritesScreen(
							getFavoriteRecipes = getFavoriteRecipes,
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
					entry<PrivacyDestination> {
						ConsentPreferencesScreen(
							observeConsentState = observeConsentState,
							showConsentForm = showConsentForm,
							modifier = Modifier.fillMaxSize(),
						)
					}
					entry<AccountDestination> {
						AuthenticationScreen(
							observeAuthenticationState = observeAuthenticationState,
							signInWithEmail = signInWithEmail,
							registerWithEmail = registerWithEmail,
							signInWithExternalProvider = signInWithExternalProvider,
							signInWithGoogle = signInWithGoogle,
							signOut = signOut,
							googleWebClientId = googleWebClientId,
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
						subclass(PrivacyDestination.serializer())
					subclass(AccountDestination.serializer())
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
internal object PrivacyDestination : MainDestination

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
		destination = PrivacyDestination,
		label = "Privacy",
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
		PrivacyDestination -> Icons.Filled.Lock
		AccountDestination -> Icons.Filled.Person
		is RecipeDetailsDestination -> Icons.Filled.Favorite
		is RecipeCookingDestination -> Icons.Filled.Favorite
	}
