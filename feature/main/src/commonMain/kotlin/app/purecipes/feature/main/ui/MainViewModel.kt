package app.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import app.purecipes.feature.ads.domain.usecase.DecidePreCookInterstitialUseCase
import app.purecipes.feature.ads.domain.usecase.ShowInterstitialAdUseCase
import app.purecipes.feature.analytics.domain.model.AnalyticsActiveTab
import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumStatus
import app.purecipes.feature.analytics.domain.model.AnalyticsUserState
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.SetCrashCustomValueUseCase
import app.purecipes.feature.analytics.domain.usecase.SetCrashUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.SetGlobalPropertiesUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackScreenViewUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.ValidateSessionUseCase
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.auth.ui.navigation.EmailRegistrationDestination
import app.purecipes.feature.auth.ui.navigation.EmailSignInDestination
import app.purecipes.feature.cooking.ui.navigation.RecipeCookingDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.main.ui.analytics.ScreenViewTracker
import app.purecipes.feature.newrecipe.ui.navigation.CreateDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.feature.settings.ui.navigation.AccountSettingsDestination
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.usecase.ObserveIncomingLinksUseCase
import app.purecipes.feature.sharing.domain.usecase.PublishWebLaunchLinkUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.feature.subscription.domain.usecase.SyncSubscriptionUserIdUseCase
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.ui.navigation.Navigator
import app.purecipes.shared.ui.navigation.PostLoginAction
import app.purecipes.shared.ui.navigation.PostLoginNavigationTarget
import app.purecipes.shared.ui.navigation.resolvePostLoginNavigationTarget
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AssistedInject
class MainViewModel(
	private val observeAuthenticationState: ObserveAuthenticationStateUseCase,
	private val validateSession: ValidateSessionUseCase,
	private val refreshConsent: RefreshConsentUseCase,
	private val setAnalyticsUserId: SetAnalyticsUserIdUseCase,
	private val setCrashUserId: SetCrashUserIdUseCase,
	private val setCrashCustomValue: SetCrashCustomValueUseCase,
	private val setGlobalProperties: SetGlobalPropertiesUseCase,
	trackScreenView: TrackScreenViewUseCase,
	private val syncSubscriptionUserId: SyncSubscriptionUserIdUseCase,
	private val observePremiumStatus: ObservePremiumStatusUseCase,
	private val observeIncomingLinks: ObserveIncomingLinksUseCase,
	private val publishWebLaunchLink: PublishWebLaunchLinkUseCase,
	private val decidePreCookInterstitial: DecidePreCookInterstitialUseCase,
	private val showInterstitialAd: ShowInterstitialAdUseCase,
	private val purecipesConfig: PurecipesConfig,
	searchReadiness: SearchReadinessCoordinator,
	@Assisted private val onDeliverPendingIncomingLink: () -> Unit,
) : ViewModel() {

	val isContentReady: StateFlow<Boolean> = searchReadiness.isReady

	internal val screenViewTracker = ScreenViewTracker(trackScreenView)

	private val tabBackStacks = mutableMapOf<MainTabStackId, NavBackStack<NavKey>>()

	internal var selectedTab by mutableStateOf(mainTabs.first { it.stackId == MainTabStackId.Search })
		private set

	private var pendingPostLoginAction: PostLoginAction? = null

	private var previousAuthenticationState: AuthenticationState? = null
	private var previousSessionKey: String? = null
	private var incomingLinksCollectionJob: Job? = null
	private var isStarted = false

	var authenticationState by mutableStateOf(observeAuthenticationState().value)
		private set

	val googleWebClientId: String?
		get() = purecipesConfig.googleWebClientId()

	private val activeStack: NavBackStack<NavKey>
		get() = stackFor(selectedTab.stackId)

	private val navigatorImpl: Navigator = object : Navigator {
		override fun push(destination: NavKey) {
			activeStack += destination
		}

		override fun replaceTabRoot(destination: NavKey) {
			replaceStackRoot(destination)
		}

		override fun popTo(destination: NavKey) {
			val stack = activeStack
			while (stack.isNotEmpty() && stack.lastOrNull() != destination) {
				stack.removeAt(stack.lastIndex)
			}
			if (stack.lastOrNull() != destination) {
				stack.clear()
				stack += destination
			}
		}

		override fun back(): Boolean {
			val stack = activeStack
			return when {
				stack.size > 1 -> {
					stack.removeAt(stack.lastIndex)
					true
				}

				selectedTab.stackId != MainTabStackId.Search -> {
					selectTab(MainTabStackId.Search)
					true
				}

				else -> false
			}
		}
	}

	internal val navigator: Navigator get() = navigatorImpl

	internal val createRecipeTabNavigator = CreateRecipeTabNavigator(
		stackFor = ::stackFor,
		selectTab = ::selectTab,
		replaceTabRoot = navigatorImpl::replaceTabRoot,
	)

	fun start() {
		if (isStarted) {
			return
		}
		isStarted = true
		setCrashCustomValue(AnalyticsGlobalProperty.ENVIRONMENT, purecipesConfig.environment())
		setActiveTabContext(AnalyticsActiveTab.SEARCH)
		viewModelScope.launch {
			observePremiumStatus().collectLatest { premium ->
				val premiumStatus = if (premium) {
					AnalyticsPremiumStatus.PREMIUM
				} else {
					AnalyticsPremiumStatus.FREE
				}
				setGlobalProperties(
					mapOf(
						AnalyticsGlobalProperty.PREMIUM_STATUS to AnalyticsValue.TextValue(premiumStatus),
					),
				)
			}
		}
		viewModelScope.launch {
			refreshConsent()
		}
		viewModelScope.launch {
			publishWebLaunchLink()
		}
		viewModelScope.launch {
			validateSession()
			observeAuthenticationState().collect { state ->
				authenticationState = state
				handleAuthenticationStateTransition(state)
				updateSessionScopedWork(state)
			}
		}
	}

	fun clearPostLoginNavigationState() {
		pendingPostLoginAction = null
	}

	internal fun requestLoginForPostLoginAction(action: PostLoginAction) {
		pendingPostLoginAction = action
		onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Account })
	}

	@Composable
	internal fun rememberActiveTabBackStack(): NavBackStack<NavKey> {
		val configuration = remember {
			SavedStateConfiguration {
				serializersModule = mainNavigationSerializersModule()
			}
		}
		val searchStack = rememberMainTabNavBackStack(
			saveStateKey = MainTabStackId.Search.saveStateKey,
			configuration = configuration,
			root = tabRootForStack(MainTabStackId.Search),
		)
		val favoritesStack = rememberMainTabNavBackStack(
			saveStateKey = MainTabStackId.Favorites.saveStateKey,
			configuration = configuration,
			root = tabRootForStack(MainTabStackId.Favorites),
		)
		val createStack = rememberMainTabNavBackStack(
			saveStateKey = MainTabStackId.Create.saveStateKey,
			configuration = configuration,
			root = tabRootForStack(MainTabStackId.Create),
		)
		val accountStack = rememberMainTabNavBackStack(
			saveStateKey = MainTabStackId.Account.saveStateKey,
			configuration = configuration,
			root = tabRootForStack(MainTabStackId.Account),
		)
		SideEffect {
			tabBackStacks[MainTabStackId.Search] = searchStack
			tabBackStacks[MainTabStackId.Favorites] = favoritesStack
			tabBackStacks[MainTabStackId.Create] = createStack
			tabBackStacks[MainTabStackId.Account] = accountStack
		}
		return when (selectedTab.stackId) {
			MainTabStackId.Search -> searchStack
			MainTabStackId.Favorites -> favoritesStack
			MainTabStackId.Create -> createStack
			MainTabStackId.Account -> accountStack
		}
	}

	internal fun takePendingPostLoginAction(): PostLoginAction? {
		val action = pendingPostLoginAction
		pendingPostLoginAction = null
		return action
	}

	fun shouldExit(): Boolean =
		selectedTab.stackId == MainTabStackId.Search && activeStack.size == 1

	internal fun peekBackStack(): List<NavKey> = activeStack.toList()

	internal fun initializeTabBackStacksForTest() {
		MainTabStackId.entries.forEach { stackId ->
			tabBackStacks[stackId] = NavBackStack(tabRootForStack(stackId))
		}
	}

	internal fun onTabSelected(tab: MainTab) {
		if (tab.destination !is AccountDestination) {
			pendingPostLoginAction = null
		}
		val root = tabRootDestination(tab)
		if (tab.stackId == selectedTab.stackId) {
			val stack = stackFor(tab.stackId)
			if (stack.size != 1 || !stack.firstOrNull().isSameTabRoot(root)) {
				replaceStackRoot(root)
			}
			return
		}
		selectTab(tab.stackId)
	}

	fun onRecipeSelected(recipeId: Int) {
		openRecipeDetails(recipeId = recipeId, origin = analyticsOriginForSelectedTab())
	}

	fun onDeepLink(link: PurecipesLink) {
		when (link) {
			is PurecipesLink.Recipe -> navigateToRecipe(link.id)
			is PurecipesLink.CookbookShare -> navigateToCookbookShare(link.token)
		}
	}

	private fun navigateToRecipe(recipeId: Int) {
		selectTab(MainTabStackId.Search)
		openRecipeDetails(
			recipeId = recipeId,
			stack = stackFor(MainTabStackId.Search),
			origin = AnalyticsOrigin.DEEP_LINK,
		)
	}

	private fun openRecipeDetails(
		recipeId: Int,
		stack: NavBackStack<NavKey> = activeStack,
		origin: AnalyticsOrigin,
	) {
		while (
			stack.lastOrNull() is RecipeDetailsDestination ||
			stack.lastOrNull() is RecipeCookingDestination
		) {
			stack.removeAt(stack.lastIndex)
		}
		stack += RecipeDetailsDestination(recipeId = recipeId, origin = origin.value)
	}

	private fun navigateToCookbookShare(token: String) {
		navigator.replaceTabRoot(FavoritesDestination(cookbookShareToken = token))
	}

	fun onStartCooking(recipeId: Int) {
		viewModelScope.launch {
			if (decidePreCookInterstitial()) {
				showInterstitialAd {
					navigator.push(RecipeCookingDestination(recipeId))
				}
			} else {
				navigator.push(RecipeCookingDestination(recipeId))
			}
		}
	}

	fun onOpenSettings() {
		ensureAccountRoot()
		val stack = stackFor(MainTabStackId.Account)
		if (stack.lastOrNull() != AccountSettingsDestination) {
			stack += AccountSettingsDestination
		}
	}

	fun onOpenEmailRegistration() {
		ensureAccountRoot()
		val stack = stackFor(MainTabStackId.Account)
		if (stack.lastOrNull() != EmailRegistrationDestination) {
			stack += EmailRegistrationDestination
		}
	}

	fun onOpenEmailSignIn(
		prefilledEmail: String = "",
		showRegistrationSuccessMessage: Boolean = false,
	) {
		ensureAccountRoot()
		val stack = stackFor(MainTabStackId.Account)
		val destination = EmailSignInDestination(
			prefilledEmail = prefilledEmail,
			showRegistrationSuccessMessage = showRegistrationSuccessMessage,
		)
		if (stack.lastOrNull() == destination) {
			return
		}
		stack += destination
	}

	fun onRegistrationSuccess(email: String) {
		val stack = stackFor(MainTabStackId.Account)
		if (stack.lastOrNull() == EmailRegistrationDestination) {
			stack.removeAt(stack.lastIndex)
		}
		onOpenEmailSignIn(prefilledEmail = email, showRegistrationSuccessMessage = true)
	}

	private fun ensureAccountRoot() {
		selectTab(MainTabStackId.Account)
		val stack = stackFor(MainTabStackId.Account)
		if (stack.firstOrNull() != AccountDestination) {
			stack.clear()
			stack += AccountDestination
		}
	}

	fun onBack(): Boolean = navigator.back()

	fun onAuthenticationSucceeded() {
		val stack = stackFor(MainTabStackId.Account)
		while (stack.lastOrNull().isAccountAuthFlowDestination()) {
			stack.removeAt(stack.lastIndex)
		}
	}

	private fun handleAuthenticationStateTransition(state: AuthenticationState) {
		val previous = previousAuthenticationState
		previousAuthenticationState = state
		if (previous is AuthenticationState.SignedIn && state is AuthenticationState.SignedOut) {
			clearPostLoginNavigationState()
		} else if (previous is AuthenticationState.SignedOut && state is AuthenticationState.SignedIn) {
			onAuthenticationSucceeded()
			takePendingPostLoginAction()?.let(::applyPostLoginNavigation)
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
		setCrashUserId(sessionKey)
		val userState = if (sessionKey != null) {
			AnalyticsUserState.LOGGED_IN
		} else {
			AnalyticsUserState.ANONYMOUS
		}
		setGlobalProperties(
			mapOf(
				AnalyticsGlobalProperty.USER_STATE to AnalyticsValue.TextValue(userState),
			),
		)
		setCrashCustomValue(AnalyticsGlobalProperty.USER_STATE, userState)
		viewModelScope.launch {
			syncSubscriptionUserId(sessionKey)
		}
		onDeliverPendingIncomingLink()
		incomingLinksCollectionJob?.cancel()
		val isSignedIn = sessionKey != null
		incomingLinksCollectionJob = viewModelScope.launch {
			observeIncomingLinks().collect { link ->
				if (link is PurecipesLink.CookbookShare && !isSignedIn) {
					requestLoginForPostLoginAction(PostLoginAction.ImportCookbookShare(link.token))
				} else {
					onDeepLink(link)
				}
			}
		}
	}

	private fun applyPostLoginNavigation(action: PostLoginAction) {
		when (val target = resolvePostLoginNavigationTarget(action)) {
			PostLoginNavigationTarget.OpenSearchWithFilters ->
				navigator.replaceTabRoot(SearchDestination(openFiltersOnStart = true))

			PostLoginNavigationTarget.OpenCreate ->
				navigator.replaceTabRoot(CreateDestination)

			PostLoginNavigationTarget.OpenFavoritesMyRecipes ->
				navigator.replaceTabRoot(FavoritesDestination(openMyRecipes = true))

			is PostLoginNavigationTarget.OpenFavoritesWithCookbookShare ->
				navigator.replaceTabRoot(FavoritesDestination(cookbookShareToken = target.token))
		}
	}

	private fun replaceStackRoot(destination: NavKey) {
		val stackId = tabStackIdForRoot(destination)
		val stack = stackFor(stackId)
		val root = tabRootForDestination(destination, stackId)
		stack.clear()
		stack += root
		selectTab(stackId)
	}

	private fun stackFor(stackId: MainTabStackId): NavBackStack<NavKey> =
		tabBackStacks[stackId] ?: error("Tab back stack for $stackId is not bound yet")

	private fun selectTab(stackId: MainTabStackId) {
		selectedTab = mainTabs.first { it.stackId == stackId }
		setActiveTabContext(stackId.toAnalyticsActiveTab())
	}

	private fun setActiveTabContext(activeTab: String) {
		setGlobalProperties(
			mapOf(
				AnalyticsGlobalProperty.ACTIVE_TAB to AnalyticsValue.TextValue(activeTab),
			),
		)
		setCrashCustomValue(AnalyticsGlobalProperty.ACTIVE_TAB, activeTab)
	}

	private fun analyticsOriginForSelectedTab(): AnalyticsOrigin = when (selectedTab.stackId) {
		MainTabStackId.Search -> AnalyticsOrigin.SEARCH
		MainTabStackId.Favorites -> AnalyticsOrigin.FAVORITES
		MainTabStackId.Create -> AnalyticsOrigin.CREATE_RECIPE
		MainTabStackId.Account -> AnalyticsOrigin.ACCOUNT
	}

	private fun tabRootDestination(tab: MainTab): NavKey = tabRootForStack(tab.stackId)

	private fun tabRootForStack(stackId: MainTabStackId): NavKey = when (stackId) {
		MainTabStackId.Search -> SearchDestination()
		MainTabStackId.Favorites -> FavoritesDestination()
		MainTabStackId.Create -> CreateDestination
		MainTabStackId.Account -> AccountDestination
	}

	private fun tabRootForDestination(destination: NavKey, stackId: MainTabStackId): NavKey =
		when (stackId) {
			MainTabStackId.Search -> when (destination) {
				is SearchDestination -> destination
				else -> SearchDestination()
			}

			MainTabStackId.Favorites -> when (destination) {
				is FavoritesDestination -> destination
				else -> FavoritesDestination()
			}

			else -> destination
		}

	private fun tabStackIdForRoot(destination: NavKey): MainTabStackId = when (destination) {
		is SearchDestination -> MainTabStackId.Search
		is FavoritesDestination -> MainTabStackId.Favorites
		CreateDestination -> MainTabStackId.Create
		AccountDestination -> MainTabStackId.Account
		else -> error("$destination is not a tab root destination")
	}

	private fun NavKey?.isSameTabRoot(root: NavKey): Boolean = when (root) {
		is SearchDestination -> this is SearchDestination
		is FavoritesDestination -> this is FavoritesDestination
		else -> this == root
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
