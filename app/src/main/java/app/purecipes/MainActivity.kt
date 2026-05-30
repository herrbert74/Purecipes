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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
		val splashScreen = installSplashScreen()
		var firstFrameDrawn = false
		splashScreen.setKeepOnScreenCondition { !firstFrameDrawn }

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
			LaunchedEffect(Unit) {
				withFrameNanos { }
				firstFrameDrawn = true
			}
			MainScreen(
				onDeliverPendingIncomingLink = { deliverDeepLinkFromIntent(intent) },
				metroViewModelFactory = graph.metroViewModelFactory,
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
