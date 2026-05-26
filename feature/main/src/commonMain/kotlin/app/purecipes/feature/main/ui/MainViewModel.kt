package app.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import app.purecipes.feature.main.ui.navigation.Navigator
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

internal class MainViewModel : ViewModel() {

	private var backStack: NavBackStack<NavKey> = NavBackStack(SearchDestination)

	private var pendingPostLoginOrigin: PostLoginNavOrigin? = null
	private var pendingOpenSearchFiltersAfterLogin: Boolean = false
	private var pendingCookbookShareToken: String? = null

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

	fun clearPostLoginNavigationState() {
		pendingPostLoginOrigin = null
		pendingOpenSearchFiltersAfterLogin = false
	}

	fun requestLoginForPostLoginAction(origin: PostLoginNavOrigin) {
		pendingPostLoginOrigin = origin
		onTabSelected(mainTabs.first { it.destination == AccountDestination })
	}

	@Composable
	internal fun mainBackStack(): NavBackStack<NavKey> {
		val stack = rememberNavBackStack(
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
		backStack = stack
		return stack
	}

	fun takePostLoginOriginAfterSignIn(): PostLoginNavOrigin? {
		val origin = pendingPostLoginOrigin
		pendingPostLoginOrigin = null
		return origin
	}

	fun markPendingOpenSearchFiltersAfterLogin() {
		pendingOpenSearchFiltersAfterLogin = true
	}

	fun takePendingOpenSearchFilters(): Boolean {
		if (!pendingOpenSearchFiltersAfterLogin) return false
		pendingOpenSearchFiltersAfterLogin = false
		return true
	}

	fun shouldExit(): Boolean = backStack.size == 1 && backStack.firstOrNull() == SearchDestination

	internal fun peekBackStack(): List<NavKey> = backStack.toList()

	fun onTabSelected(tab: MainTab) {
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

	fun takePendingCookbookShareToken(): String? {
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

	private fun NavKey?.isAccountAuthFlowDestination(): Boolean {
		return this is EmailSignInDestination || this is EmailRegistrationDestination
	}
}

@Composable
internal fun mainViewModel(): MainViewModel = viewModel(
	factory = viewModelFactory {
		initializer { MainViewModel() }
	},
)
