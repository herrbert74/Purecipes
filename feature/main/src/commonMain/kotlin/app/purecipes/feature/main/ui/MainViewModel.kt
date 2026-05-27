package app.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.auth.ui.navigation.EmailRegistrationDestination
import app.purecipes.feature.auth.ui.navigation.EmailSignInDestination
import app.purecipes.feature.cooking.ui.navigation.RecipeCookingDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.feature.settings.ui.navigation.AccountSettingsDestination
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.usecase.ObserveIncomingLinksUseCase
import app.purecipes.feature.sharing.domain.usecase.PublishWebLaunchLinkUseCase
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.ui.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AssistedInject
class MainViewModel(
	private val observeAuthenticationState: ObserveAuthenticationStateUseCase,
	private val refreshConsent: RefreshConsentUseCase,
	private val setAnalyticsUserId: SetAnalyticsUserIdUseCase,
	private val observeIncomingLinks: ObserveIncomingLinksUseCase,
	private val publishWebLaunchLink: PublishWebLaunchLinkUseCase,
	private val purecipesConfig: PurecipesConfig,
	@Assisted private val onDeliverPendingIncomingLink: () -> Unit,
) : ViewModel() {

	private var backStack: NavBackStack<NavKey> = NavBackStack(SearchDestination)

	private var pendingPostLoginOrigin: PostLoginNavOrigin? = null
	private var pendingOpenSearchFiltersAfterLogin: Boolean = false
	private var pendingCookbookShareToken: String? = null

	private var previousAuthenticationState: AuthenticationState? = null
	private var previousSessionKey: String? = null
	private var incomingLinksCollectionJob: Job? = null
	private var isStarted = false

	var authenticationState by mutableStateOf(observeAuthenticationState().value)
		private set

	val googleWebClientId: String?
		get() = purecipesConfig.googleWebClientId()

	private val navigatorImpl: Navigator = object : Navigator {
		override fun push(destination: NavKey) {
			backStack += destination
		}

		override fun replaceTabRoot(destination: NavKey) {
			backStack.clear()
			backStack += destination
		}

		override fun popTo(destination: NavKey) {
			while (backStack.isNotEmpty() && backStack.lastOrNull() != destination) {
				backStack.removeAt(backStack.lastIndex)
			}
			if (backStack.lastOrNull() != destination) {
				backStack.clear()
				backStack += destination
			}
		}

		override fun back(): Boolean {
			if (backStack.size > 1) {
				backStack.removeAt(backStack.lastIndex)
				return true
			}
			return backStack.firstOrNull() != SearchDestination
		}
	}

	internal val navigator: Navigator get() = navigatorImpl

	fun start() {
		if (isStarted) {
			return
		}
		isStarted = true
		viewModelScope.launch {
			refreshConsent()
		}
		viewModelScope.launch {
			publishWebLaunchLink()
		}
		viewModelScope.launch {
			observeAuthenticationState().collect { state ->
				authenticationState = state
				handleAuthenticationStateTransition(state)
				updateSessionScopedWork(state)
			}
		}
	}

	fun clearPostLoginNavigationState() {
		pendingPostLoginOrigin = null
		pendingOpenSearchFiltersAfterLogin = false
	}

	internal fun requestLoginForPostLoginAction(origin: PostLoginNavOrigin) {
		pendingPostLoginOrigin = origin
		onTabSelected(mainTabs.first { it.destination == AccountDestination })
	}

	@Composable
	internal fun mainBackStack(): NavBackStack<NavKey> {
		val stack = rememberNavBackStack(
			configuration = remember {
				SavedStateConfiguration {
					serializersModule = mainNavigationSerializersModule()
				}
			},
			SearchDestination,
		)
		backStack = stack
		return stack
	}

	internal fun takePostLoginOriginAfterSignIn(): PostLoginNavOrigin? {
		val origin = pendingPostLoginOrigin
		pendingPostLoginOrigin = null
		return origin
	}

	internal fun markPendingOpenSearchFiltersAfterLogin() {
		pendingOpenSearchFiltersAfterLogin = true
	}

	internal fun takePendingOpenSearchFilters(): Boolean {
		if (!pendingOpenSearchFiltersAfterLogin) return false
		pendingOpenSearchFiltersAfterLogin = false
		return true
	}

	fun shouldExit(): Boolean = backStack.size == 1 && backStack.firstOrNull() == SearchDestination

	internal fun peekBackStack(): List<NavKey> = backStack.toList()

	internal fun onTabSelected(tab: MainTab) {
		if (tab.destination !is AccountDestination) {
			pendingPostLoginOrigin = null
			if (tab.destination != SearchDestination) {
				pendingOpenSearchFiltersAfterLogin = false
			}
		}
		if (backStack.size != 1 || backStack.firstOrNull() != tab.destination) {
			navigator.replaceTabRoot(tab.destination)
		}
	}

	fun onRecipeSelected(recipeId: Int) {
		navigator.push(RecipeDetailsDestination(recipeId))
	}

	fun onDeepLink(link: PurecipesLink) {
		when (link) {
			is PurecipesLink.Recipe -> navigateToRecipe(link.id)
			is PurecipesLink.CookbookShare -> navigateToCookbookShare(link.token)
		}
	}

	fun stageCookbookShareImport(token: String) {
		pendingCookbookShareToken = token
	}

	internal fun takePendingCookbookShareToken(): String? {
		val token = pendingCookbookShareToken
		pendingCookbookShareToken = null
		return token
	}

	private fun navigateToRecipe(recipeId: Int) {
		pendingCookbookShareToken = null
		if (backStack.firstOrNull() != SearchDestination) {
			backStack.clear()
			backStack += SearchDestination
		}
		while (
			backStack.lastOrNull() is RecipeDetailsDestination ||
				backStack.lastOrNull() is RecipeCookingDestination
		) {
			backStack.removeAt(backStack.lastIndex)
		}
		backStack += RecipeDetailsDestination(recipeId)
	}

	private fun navigateToCookbookShare(token: String) {
		stageCookbookShareImport(token)
		onTabSelected(mainTabs.first { it.destination == FavoritesDestination })
	}

	fun onStartCooking(recipeId: Int) {
		navigator.push(RecipeCookingDestination(recipeId))
	}

	fun onOpenSettings() {
		if (backStack.firstOrNull() != AccountDestination) {
			navigator.replaceTabRoot(AccountDestination)
		}
		if (backStack.lastOrNull() != AccountSettingsDestination) {
			navigator.push(AccountSettingsDestination)
		}
	}

	fun onOpenEmailRegistration() {
		ensureAccountRoot()
		if (backStack.lastOrNull() != EmailRegistrationDestination) {
			navigator.push(EmailRegistrationDestination)
		}
	}

	fun onOpenEmailSignIn(
		prefilledEmail: String = "",
		showRegistrationSuccessMessage: Boolean = false,
	) {
		ensureAccountRoot()
		val destination = EmailSignInDestination(
			prefilledEmail = prefilledEmail,
			showRegistrationSuccessMessage = showRegistrationSuccessMessage,
		)
		if (backStack.lastOrNull() == destination) {
			return
		}
		navigator.push(destination)
	}

	fun onRegistrationSuccess(email: String) {
		if (backStack.lastOrNull() == EmailRegistrationDestination) {
			backStack.removeAt(backStack.lastIndex)
		}
		onOpenEmailSignIn(prefilledEmail = email, showRegistrationSuccessMessage = true)
	}

	private fun ensureAccountRoot() {
		if (backStack.firstOrNull() != AccountDestination) {
			backStack.clear()
			backStack += AccountDestination
		}
	}

	fun onBack(): Boolean = navigator.back()

	fun onAuthenticationSucceeded() {
		while (backStack.lastOrNull().isAccountAuthFlowDestination()) {
			backStack.removeAt(backStack.lastIndex)
		}
	}

	private fun handleAuthenticationStateTransition(state: AuthenticationState) {
		val previous = previousAuthenticationState
		previousAuthenticationState = state
		if (previous is AuthenticationState.SignedIn && state is AuthenticationState.SignedOut) {
			clearPostLoginNavigationState()
		} else if (previous is AuthenticationState.SignedOut && state is AuthenticationState.SignedIn) {
			onAuthenticationSucceeded()
			when (takePostLoginOriginAfterSignIn()) {
				PostLoginNavOrigin.RECIPE_SEARCH_FILTERS -> {
					markPendingOpenSearchFiltersAfterLogin()
					onTabSelected(mainTabs.first { it.destination == SearchDestination })
				}

				PostLoginNavOrigin.COOKBOOK_SHARE_IMPORT ->
					onTabSelected(mainTabs.first { it.destination == FavoritesDestination })

				null -> Unit
			}
		}
	}

	private fun updateSessionScopedWork(state: AuthenticationState) {
		val sessionKey = when (state) {
			is AuthenticationState.SignedIn -> state.user.id
			AuthenticationState.SignedOut -> null
		}
		if (sessionKey == previousSessionKey && incomingLinksCollectionJob != null) {
			return
		}
		previousSessionKey = sessionKey
		setAnalyticsUserId(sessionKey)
		onDeliverPendingIncomingLink()
		incomingLinksCollectionJob?.cancel()
		val isSignedIn = sessionKey != null
		incomingLinksCollectionJob = viewModelScope.launch {
			observeIncomingLinks().collect { link ->
				if (link is PurecipesLink.CookbookShare && !isSignedIn) {
					stageCookbookShareImport(link.token)
					requestLoginForPostLoginAction(PostLoginNavOrigin.COOKBOOK_SHARE_IMPORT)
				} else {
					onDeepLink(link)
				}
			}
		}
	}

	private fun NavKey?.isAccountAuthFlowDestination(): Boolean {
		return this is EmailSignInDestination || this is EmailRegistrationDestination
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(onDeliverPendingIncomingLink: () -> Unit): MainViewModel
	}
}
