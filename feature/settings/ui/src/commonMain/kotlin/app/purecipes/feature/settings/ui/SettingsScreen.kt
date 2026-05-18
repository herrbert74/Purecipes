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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
import app.purecipes.shared.domain.model.NotificationPreferences
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
	observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	resetMeasurementPreferences: ResetMeasurementPreferencesUseCase,
	saveMeasurementPreferences: SaveMeasurementPreferencesUseCase,
	observeNotificationPreferences: ObserveNotificationPreferencesUseCase,
	saveNotificationPreferences: SaveNotificationPreferencesUseCase,
	sendTestNotification: SendTestNotificationUseCase,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
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
			MeasurementPreferencesSection(
				observeMeasurementPreferences = observeMeasurementPreferences,
				resetMeasurementPreferences = resetMeasurementPreferences,
				saveMeasurementPreferences = saveMeasurementPreferences,
			)
			NotificationPreferencesSection(
				observeNotificationPreferences = observeNotificationPreferences,
				saveNotificationPreferences = saveNotificationPreferences,
				sendTestNotification = sendTestNotification,
			)
		}
	}
}

@Composable
private fun NotificationPreferencesSection(
	observeNotificationPreferences: ObserveNotificationPreferencesUseCase,
	saveNotificationPreferences: SaveNotificationPreferencesUseCase,
	sendTestNotification: SendTestNotificationUseCase,
	modifier: Modifier = Modifier,
) {
	val preferences by observeNotificationPreferences().collectAsState(initial = NotificationPreferences())
	val scope = rememberCoroutineScope()

	NotificationPreferencesPanel(
		preferences = preferences,
		onPreferencesChange = { updatedPreferences ->
			scope.launch {
				saveNotificationPreferences(updatedPreferences)
			}
		},
		onSendTestNotification = {
			sendTestNotification("Testing 1 2 3", "Push notifications are working!")
		},
		modifier = modifier,
	)
}

@Composable
private fun MeasurementPreferencesSection(
	observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	resetMeasurementPreferences: ResetMeasurementPreferencesUseCase,
	saveMeasurementPreferences: SaveMeasurementPreferencesUseCase,
	modifier: Modifier = Modifier,
) {
	val preferences by observeMeasurementPreferences().collectAsState(initial = null)
	val scope = rememberCoroutineScope()
	val currentPreferences = preferences ?: return

	MeasurementPreferencesPanel(
		preferences = currentPreferences,
		onPreferencesChange = { updatedPreferences ->
			scope.launch { saveMeasurementPreferences(updatedPreferences) }
		},
		onReset = { scope.launch { resetMeasurementPreferences() } },
		modifier = modifier,
	)
}
