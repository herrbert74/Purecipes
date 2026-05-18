package app.purecipes.umbrella

import androidx.compose.ui.window.ComposeUIViewController
import app.purecipes.feature.main.ui.MainScreen
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIViewController

class RecipeSearchViewControllerFactory {

	fun make(): UIViewController {
		val graph = createGraph<IosAppGraph>()
		return ComposeUIViewController(
			configure = {
				enforceStrictPlistSanityCheck = false
			}
		) {
			MainScreen(
				observeConsentState = graph.observeConsentStateUseCase,
				observeAuthenticationState = graph.observeAuthenticationStateUseCase,
				observeMeasurementPreferences = graph.observeMeasurementPreferencesUseCase,
				observeNotificationPreferences = graph.observeNotificationPreferencesUseCase,
				refreshConsent = graph.refreshConsentUseCase,
				setAnalyticsUserId = graph.setAnalyticsUserIdUseCase,
				showConsentForm = graph.showConsentFormUseCase,
				signInWithEmail = graph.signInWithEmailUseCase,
				registerWithEmail = graph.registerWithEmailUseCase,
				resendEmailVerification = graph.resendEmailVerificationUseCase,
				sendPasswordResetEmail = graph.sendPasswordResetEmailUseCase,
				signInWithExternalProvider = graph.signInWithExternalProviderUseCase,
				signInWithGoogle = graph.signInWithGoogleUseCase,
				deleteAccount = graph.deleteAccountUseCase,
				signOut = graph.signOutUseCase,
				addFavoriteRecipe = graph.addFavoriteRecipeUseCase,
				filterRecipesForMeasurementPreferences = graph.filterRecipesForMeasurementPreferencesUseCase,
				getCreatedRecipes = graph.getCreatedRecipesUseCase,
				getFavoriteRecipesPage = graph.getFavoriteRecipesPageUseCase,
				getCookbooksPage = graph.getCookbooksPageUseCase,
				createCookbook = graph.createCookbookUseCase,
				deleteCookbook = graph.deleteCookbookUseCase,
				getCookbookRecipesPage = graph.getCookbookRecipesPageUseCase,
				getCookbookCoverImageUrl = graph.getCookbookCoverImageUrlUseCase,
				getRecipeCookbooks = graph.getRecipeCookbooksUseCase,
				addRecipeToCookbook = graph.addRecipeToCookbookUseCase,
				getMeasurementPreferences = graph.getMeasurementPreferencesUseCase,
				searchRecipes = graph.searchRecipesUseCase,
				getRecipeDetails = graph.getRecipeDetailsUseCase,
				googleWebClientId = graph.purecipesConfig.googleWebClientId(),
				markMeasurementMismatchSeen = graph.markMeasurementMismatchSeenUseCase,
				processRecipeDetailsForMeasurementPreferences =
					graph.processRecipeDetailsForMeasurementPreferencesUseCase,
				removeFavoriteRecipe = graph.removeFavoriteRecipeUseCase,
				resetMeasurementPreferences = graph.resetMeasurementPreferencesUseCase,
				saveMeasurementPreferences = graph.saveMeasurementPreferencesUseCase,
				saveNotificationPreferences = graph.saveNotificationPreferencesUseCase,
				sendTestNotification = graph.sendTestNotificationUseCase,
				saveCreatedRecipe = graph.saveCreatedRecipeUseCase,
				trackEvent = graph.trackEventUseCase,
				getSearchFilters = graph.getSearchFiltersUseCase,
				saveSearchFilters = graph.saveSearchFiltersUseCase,
				getUserPantry = graph.getUserPantryUseCase,
				updateUserPantry = graph.updateUserPantryUseCase,
			)
		}
	}
}
