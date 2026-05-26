package app.purecipes.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.sharing.domain.model.PurecipesLink

internal class MainViewModel : ViewModel() {

	private var pendingPostLoginOrigin: PostLoginNavOrigin? = null
	private var pendingOpenSearchFiltersAfterLogin: Boolean = false
	private var pendingCookbookShareToken: String? = null

	fun clearPostLoginNavigationState() {
		pendingPostLoginOrigin = null
		pendingOpenSearchFiltersAfterLogin = false
	}

	fun requestLoginForPostLoginAction(origin: PostLoginNavOrigin, backStack: MutableList<NavKey>) {
		pendingPostLoginOrigin = origin
		onTabSelected(backStack, mainTabs.first { it.destination == AccountDestination })
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

	fun shouldExit(backStack: List<NavKey>): Boolean =
		backStack.size == 1 && backStack.firstOrNull() == SearchDestination

	fun onTabSelected(backStack: MutableList<NavKey>, tab: MainTab) {
		if (tab.destination !is AccountDestination) {
			pendingPostLoginOrigin = null
			if (tab.destination != SearchDestination) {
				pendingOpenSearchFiltersAfterLogin = false
			}
		}
		if (backStack.size != 1 || backStack.firstOrNull() != tab.destination) {
			backStack.clear()
			backStack += tab.destination
		}
	}

	fun onRecipeSelected(backStack: MutableList<NavKey>, recipeId: Int) {
		backStack += RecipeDetailsDestination(recipeId)
	}

	fun onDeepLink(backStack: MutableList<NavKey>, link: PurecipesLink) {
		when (link) {
			is PurecipesLink.Recipe -> navigateToRecipe(backStack, link.id)
			is PurecipesLink.CookbookShare -> navigateToCookbookShare(backStack, link.token)
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

	private fun navigateToRecipe(backStack: MutableList<NavKey>, recipeId: Int) {
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

	private fun navigateToCookbookShare(backStack: MutableList<NavKey>, token: String) {
		stageCookbookShareImport(token)
		onTabSelected(backStack, mainTabs.first { it.destination == FavoritesDestination })
	}

	fun onStartCooking(backStack: MutableList<NavKey>, recipeId: Int) {
		backStack += RecipeCookingDestination(recipeId)
	}

	fun onOpenSettings(backStack: MutableList<NavKey>) {
		if (backStack.firstOrNull() != AccountDestination) {
			backStack.clear()
			backStack += AccountDestination
		}
		if (backStack.lastOrNull() != AccountSettingsDestination) {
			backStack += AccountSettingsDestination
		}
	}

	fun onOpenEmailRegistration(backStack: MutableList<NavKey>) {
		ensureAccountRoot(backStack)
		if (backStack.lastOrNull() != EmailRegistrationDestination) {
			backStack += EmailRegistrationDestination
		}
	}

	fun onOpenEmailSignIn(
		backStack: MutableList<NavKey>,
		prefilledEmail: String = "",
		showRegistrationSuccessMessage: Boolean = false,
	) {
		ensureAccountRoot(backStack)
		val destination = EmailSignInDestination(
			prefilledEmail = prefilledEmail,
			showRegistrationSuccessMessage = showRegistrationSuccessMessage,
		)
		if (backStack.lastOrNull() == destination) {
			return
		}
		backStack += destination
	}

	fun onRegistrationSuccess(backStack: MutableList<NavKey>, email: String) {
		if (backStack.lastOrNull() == EmailRegistrationDestination) {
			backStack.removeAt(backStack.lastIndex)
		}
		onOpenEmailSignIn(
			backStack = backStack,
			prefilledEmail = email,
			showRegistrationSuccessMessage = true,
		)
	}

	private fun ensureAccountRoot(backStack: MutableList<NavKey>) {
		if (backStack.firstOrNull() != AccountDestination) {
			backStack.clear()
			backStack += AccountDestination
		}
	}

	fun onBack(backStack: MutableList<NavKey>) {
		if (backStack.size > 1) {
			backStack.removeAt(backStack.lastIndex)
		}
	}

	fun onAuthenticationSucceeded(backStack: MutableList<NavKey>) {
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
