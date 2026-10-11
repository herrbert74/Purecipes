package app.purecipes.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.ui.GoPremiumSettingsPanel
import app.purecipes.feature.subscription.ui.MonetisationDebugOverridesPanel
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.NotificationPreferences
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

private const val PRIVACY_POLICY_URL = "https://purecipes.app/privacy"
private const val TERMS_OF_SERVICE_URL = "https://purecipes.app/terms"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
	onBack: () -> Unit,
	onOpenPaywall: () -> Unit,
	onOpenAbout: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = metroViewModel(),
) {
	val notificationPreferences by viewModel.notificationPreferences.collectAsState(
		initial = viewModel.notificationPreferences.retainedValue(NotificationPreferences()),
	)
	val searchPreferences by viewModel.searchPreferences.collectAsState(
		initial = viewModel.searchPreferences.retainedValue(SearchPreferences()),
	)
	val measurementPreferences by viewModel.measurementPreferences.collectAsState(
		initial = viewModel.measurementPreferences.retainedValue(
			MeasurementPreferences(preferredSystem = MeasurementSystem.METRIC),
		),
	)
	val monetisationDebugOverrides by viewModel.monetisationDebugOverrides.collectAsState(
		initial = viewModel.monetisationDebugOverrides.retainedValue(MonetisationDebugOverrides()),
	)
	val uriHandler = LocalUriHandler.current
	val scrollState = rememberScrollState(viewModel.settingsScrollOffset)
	LaunchedEffect(scrollState) {
		val targetOffset = viewModel.settingsScrollOffset
		snapshotFlow { scrollState.maxValue }
			.first { maxValue -> maxValue != Int.MAX_VALUE && maxValue > 0 }
		val target = targetOffset.coerceAtMost(scrollState.maxValue)
		if (scrollState.value != target) {
			scrollState.scrollTo(target)
		}
		snapshotFlow { scrollState.value to scrollState.maxValue }
			.collect { (offset, maxValue) ->
				if (maxValue > 0) {
					viewModel.onSettingsScrollOffsetChange(offset)
				}
			}
	}

	val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
		rememberTopAppBarState(),
	)
	Scaffold(
		modifier = modifier
			.fillMaxSize()
			.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
		topBar = {
			TopAppBar(
				title = { Text(text = "Settings") },
				scrollBehavior = topAppBarScrollBehavior,
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = AppIcons.ArrowBack,
							contentDescription = "Back",
						)
					}
				},
			)
		},
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(scrollState)
				.padding(innerPadding)
				.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			GoPremiumSettingsPanel(onOpenPaywall = onOpenPaywall)
			if (viewModel.showMonetisationDebugOverrides) {
				MonetisationDebugOverridesPanel(
					overrides = monetisationDebugOverrides,
					onPremiumStatusChange = viewModel::onPremiumStatusOverrideChange,
					onAdsDisplayChange = viewModel::onAdsDisplayOverrideChange,
				)
			}
			MeasurementPreferencesSection(
				preferences = measurementPreferences,
				onPreferencesChange = viewModel::onMeasurementPreferencesChange,
				onReset = viewModel::onResetMeasurementPreferences,
			)
			SearchPreferencesSection(
				preferences = searchPreferences,
				onPreferencesChange = viewModel::onSearchPreferencesChange,
			)
			NotificationPreferencesSection(
				preferences = notificationPreferences,
				onPreferencesChange = viewModel::onNotificationPreferencesChange,
				onSendTestNotification = viewModel::onSendTestNotification,
			)
			AboutSettingsPanel(onOpenAbout = onOpenAbout)
			LegalSettingsPanel(
				onOpenPrivacyPolicy = { uriHandler.openUri(PRIVACY_POLICY_URL) },
				onOpenTermsOfService = { uriHandler.openUri(TERMS_OF_SERVICE_URL) },
			)
		}
	}
}

private fun <T> Flow<T>.retainedValue(fallback: T): T = (this as? StateFlow<T>)?.value ?: fallback

@Composable
private fun NotificationPreferencesSection(
	preferences: NotificationPreferences,
	onPreferencesChange: (NotificationPreferences) -> Unit,
	onSendTestNotification: () -> Unit,
	modifier: Modifier = Modifier,
) {
	NotificationPreferencesPanel(
		preferences = preferences,
		onPreferencesChange = onPreferencesChange,
		onSendTestNotification = onSendTestNotification,
		modifier = modifier,
	)
}

@Composable
private fun SearchPreferencesSection(
	preferences: SearchPreferences,
	onPreferencesChange: (SearchPreferences) -> Unit,
	modifier: Modifier = Modifier,
) {
	SearchPreferencesPanel(
		preferences = preferences,
		onPreferencesChange = onPreferencesChange,
		modifier = modifier,
	)
}

@Composable
private fun MeasurementPreferencesSection(
	preferences: MeasurementPreferences,
	onPreferencesChange: (MeasurementPreferences) -> Unit,
	onReset: () -> Unit,
	modifier: Modifier = Modifier,
) {
	MeasurementPreferencesPanel(
		preferences = preferences,
		onPreferencesChange = onPreferencesChange,
		onReset = onReset,
		modifier = modifier,
	)
}
