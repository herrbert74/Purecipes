package com.purecipes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mmk.kmpauth.core.KMPAuth
import com.mmk.kmpauth.facebook.handleFacebookActivityResult
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import com.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import com.purecipes.feature.main.ui.MainScreen
import dev.zacsweers.metro.createGraph

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NotifierManager.onCreateOrOnNewIntent(intent)
		enableEdgeToEdge()

		val graph = createGraph<PurecipesAppGraph>()

		setContent {
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
				signInWithExternalProvider = graph.signInWithExternalProviderUseCase,
				signInWithGoogle = graph.signInWithGoogleUseCase,
				signOut = graph.signOutUseCase,
				addFavoriteRecipe = graph.addFavoriteRecipeUseCase,
				filterRecipesForMeasurementPreferences = graph.filterRecipesForMeasurementPreferencesUseCase,
				getCreatedRecipes = graph.getCreatedRecipesUseCase,
				getFavoriteRecipes = graph.getFavoriteRecipesUseCase,
				getMeasurementPreferences = graph.getMeasurementPreferencesUseCase,
				searchRecipes = graph.searchRecipesUseCase,
				getRecipeDetails = graph.getRecipeDetailsUseCase,
				googleWebClientId = graph.purecipesConfig.googleWebClientId(),
				markMeasurementMismatchSeen = graph.markMeasurementMismatchSeenUseCase,
				processRecipeDetailsForMeasurementPreferences = graph.processRecipeDetailsForMeasurementPreferencesUseCase,
				removeFavoriteRecipe = graph.removeFavoriteRecipeUseCase,
				resetMeasurementPreferences = graph.resetMeasurementPreferencesUseCase,
				saveMeasurementPreferences = graph.saveMeasurementPreferencesUseCase,
				saveNotificationPreferences = graph.saveNotificationPreferencesUseCase,
				sendTestNotification = graph.sendTestNotificationUseCase,
				saveCreatedRecipe = graph.saveCreatedRecipeUseCase,
				trackEvent = graph.trackEventUseCase,
				onExitRequest = ::finish,
			)
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		NotifierManager.onCreateOrOnNewIntent(intent)
	}

	override fun onStart() {
		super.onStart()
		AnalyticsAndroidRuntime.onActivityStarted(this)
	}

	override fun onStop() {
		AnalyticsAndroidRuntime.onActivityStopped(this)
		super.onStop()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		KMPAuth.handleFacebookActivityResult(requestCode, resultCode, data)
		super.onActivityResult(requestCode, resultCode, data)
	}
}
