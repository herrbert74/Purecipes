package app.purecipes.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.NotificationPreferences
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun SettingsScreen(
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = metroViewModel(),
) {
	val notificationPreferences by viewModel.notificationPreferences.collectAsState(
		initial = NotificationPreferences(),
	)
	val measurementPreferences by viewModel.measurementPreferences.collectAsState(initial = null)

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Settings") },
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
				.verticalScroll(rememberScrollState())
				.padding(innerPadding)
				.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			measurementPreferences?.let { preferences ->
				MeasurementPreferencesSection(
					preferences = preferences,
					onPreferencesChange = viewModel::onMeasurementPreferencesChange,
					onReset = viewModel::onResetMeasurementPreferences,
				)
			}
			NotificationPreferencesSection(
				preferences = notificationPreferences,
				onPreferencesChange = viewModel::onNotificationPreferencesChange,
				onSendTestNotification = viewModel::onSendTestNotification,
			)
		}
	}
}

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
