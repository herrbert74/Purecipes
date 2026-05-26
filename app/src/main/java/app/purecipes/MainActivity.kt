package app.purecipes

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import app.purecipes.feature.main.ui.MainScreen
import app.purecipes.feature.sharing.data.runtime.SharingAndroidRuntime
import app.purecipes.feature.sharing.domain.usecase.DeliverIncomingLinkUseCase
import com.mmk.kmpauth.core.KMPAuth
import com.mmk.kmpauth.facebook.handleFacebookActivityResult
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.zacsweers.metro.createGraph
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

	private lateinit var deliverIncomingLinkUseCase: DeliverIncomingLinkUseCase

	private val requestNotificationPermission =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NotifierManager.onCreateOrOnNewIntent(intent)
		enableEdgeToEdge()

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
		}

		val graph = createGraph<PurecipesAppGraph>()
		deliverIncomingLinkUseCase = graph.deliverIncomingLinkUseCase

		lifecycleScope.launch { graph.initializeNotificationsUseCase() }

		setContent {
			MainScreen(
				onDeliverPendingIncomingLink = { deliverDeepLinkFromIntent(intent) },
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
				estimateRecipeNutrition = graph.estimateRecipeNutritionUseCase,
				trackEvent = graph.trackEventUseCase,
				getSearchFilters = graph.getSearchFiltersUseCase,
				saveSearchFilters = graph.saveSearchFiltersUseCase,
				getUserPantry = graph.getUserPantryUseCase,
				updateUserPantry = graph.updateUserPantryUseCase,
				observeIncomingLinks = graph.observeIncomingLinksUseCase,
				publishWebLaunchLink = graph.publishWebLaunchLinkUseCase,
				shareRecipe = graph.shareRecipeUseCase,
				shareCookbook = graph.shareCookbookUseCase,
				importCookbookShare = graph.importCookbookShareUseCase,
				onExitRequest = ::finish,
			)
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		setIntent(intent)
		NotifierManager.onCreateOrOnNewIntent(intent)
		deliverDeepLinkFromIntent(intent)
	}

	private fun deliverDeepLinkFromIntent(intent: Intent?) {
		val data: Uri = intent?.data ?: return
		deliverIncomingLinkUseCase(data.toString())
	}

	override fun onStart() {
		super.onStart()
		AnalyticsAndroidRuntime.onActivityStarted(this)
		SharingAndroidRuntime.onActivityStarted(this)
	}

	override fun onStop() {
		AnalyticsAndroidRuntime.onActivityStopped(this)
		SharingAndroidRuntime.onActivityStopped(this)
		super.onStop()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		KMPAuth.handleFacebookActivityResult(requestCode, resultCode, data)
		super.onActivityResult(requestCode, resultCode, data)
	}
}
