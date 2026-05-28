package app.purecipes.feature.main.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import dev.zacsweers.metro.createGraph
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class MainScreenMetroIntegrationTest {

	@get:Rule
	val composeRule = createAndroidComposeRule<ComponentActivity>()

	@Before
	fun setUp() {
		AnalyticsAndroidRuntime.initialize(composeRule.activity.application)
	}

	@Test
	fun mainScreenWithMetroViewModelFactoryComposesSearchFlow() {
		val graph = createGraph<DeviceTestAppGraph>()
		composeRule.setContent {
			MainScreen(metroViewModelFactory = graph.metroViewModelFactory)
		}
		composeRule.waitUntil(timeoutMillis = 10_000) {
			composeRule.onAllNodesWithContentDescription("Search in recipe titles")
				.fetchSemanticsNodes()
				.isNotEmpty()
		}
	}
}
