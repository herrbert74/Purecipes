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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import app.purecipes.feature.ads.data.runtime.AdsAndroidRuntime
import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import app.purecipes.feature.main.ui.MainScreen
import app.purecipes.feature.sharing.data.runtime.SharingAndroidRuntime
import app.purecipes.feature.sharing.domain.usecase.DeliverIncomingLinkUseCase
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import dev.zacsweers.metro.createGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

	private val graphState = mutableStateOf<PurecipesAppGraph?>(null)
	private val osSplashBridge = AndroidOsSplashBridge()

	private var appGraph: PurecipesAppGraph? = null
	private var graphLoadStarted = false

	private val requestNotificationPermission =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		var keepOsSplashOnScreen = true
		splashScreen.setKeepOnScreenCondition { keepOsSplashOnScreen }
		osSplashBridge.install(splashScreen)

		super.onCreate(savedInstanceState)
		KMPNotifier.onCreateOrOnNewIntent(intent)
		enableEdgeToEdge()
		startGraphLoadIfNeeded()
		requestNotificationPermissionIfNeeded()

		appGraph = graphState.value

		setContent {
			val readyGraph by graphState
			readyGraph?.let { graph ->
				MainScreen(
					onDeliverPendingIncomingLink = { deliverDeepLinkFromIntent(intent) },
					metroViewModelFactory = graph.metroViewModelFactory,
					onExitRequest = ::finish,
					onPlatformSplashExitStart = osSplashBridge::dismiss,
				)
			}
		}

		keepOsSplashOnScreen = false
	}

	private fun startGraphLoadIfNeeded() {
		if (graphLoadStarted) {
			return
		}
		graphLoadStarted = true
		lifecycleScope.launch {
			val graph = withContext(Dispatchers.Default) {
				createGraph<PurecipesAppGraph>()
			}
			appGraph = graph
			graphState.value = graph
			launch {
				graph.initializeNotificationsUseCase()
			}
			graph.initializeSubscriptionsUseCase()
			graph.initializeAdsUseCase()
		}
	}

	private fun requestNotificationPermissionIfNeeded() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
		}
	}

	override fun onNewIntent(intent: Intent) {
		super.onNewIntent(intent)
		setIntent(intent)
		KMPNotifier.onCreateOrOnNewIntent(intent)
		deliverDeepLinkFromIntent(intent)
	}

	private fun deliverDeepLinkFromIntent(intent: Intent?) {
		val useCase: DeliverIncomingLinkUseCase = appGraph?.deliverIncomingLinkUseCase ?: return
		val data: Uri = intent?.data ?: return
		useCase(data.toString())
	}

	override fun onStart() {
		super.onStart()
		AnalyticsAndroidRuntime.onActivityStarted(this)
		AdsAndroidRuntime.onActivityStarted(this)
		SharingAndroidRuntime.onActivityStarted(this)
	}

	override fun onStop() {
		AnalyticsAndroidRuntime.onActivityStopped(this)
		AdsAndroidRuntime.onActivityStopped(this)
		SharingAndroidRuntime.onActivityStopped(this)
		super.onStop()
	}
}
