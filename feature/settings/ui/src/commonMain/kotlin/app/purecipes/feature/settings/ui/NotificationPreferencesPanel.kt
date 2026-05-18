package app.purecipes.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.NotificationPreferences
import app.purecipes.shared.ui.component.PurecipesOutlinedButton
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun NotificationPreferencesPanel(
	preferences: NotificationPreferences,
	onPreferencesChange: (NotificationPreferences) -> Unit,
	onSendTestNotification: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			SectionHeader(
				title = "Notifications",
				subtitle = "Manage push notification settings across devices.",
			)

			NotificationToggleRow(
				label = "Enable All Notifications",
				description = "Turn all notifications on or off globally",
				checked = preferences.enableAll,
				onCheckedChange = { checked ->
					onPreferencesChange(preferences.copy(enableAll = checked))
				},
			)

			if (preferences.enableAll) {
				HorizontalDivider()

				NotificationToggleRow(
					label = "Cooking Timers",
					description = "Alerts when timers complete or steps change",
					checked = preferences.enableTimers,
					onCheckedChange = { checked ->
						onPreferencesChange(preferences.copy(enableTimers = checked))
					},
				)

				NotificationToggleRow(
					label = "Social Engagement",
					description = "Comments and community interactions",
					checked = preferences.enableSocial,
					onCheckedChange = { checked ->
						onPreferencesChange(preferences.copy(enableSocial = checked))
					},
				)

				NotificationToggleRow(
					label = "Recipe Updates",
					description = "New features, updates and suggestions",
					checked = preferences.enableUpdates,
					onCheckedChange = { checked ->
						onPreferencesChange(preferences.copy(enableUpdates = checked))
					},
				)
				HorizontalDivider()

				PurecipesOutlinedButton(
					text = "Send Test Notification",
					onClick = onSendTestNotification,
				)
			}
		}
	}
}

@Composable
private fun NotificationToggleRow(
	label: String,
	description: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = PurecipesTheme.space.xs),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Column(modifier = Modifier.weight(1f).padding(end = PurecipesTheme.space.m)) {
			Text(
				text = label,
				style = PurecipesTheme.typography.bodyMedium,
				fontWeight = FontWeight.Medium,
			)
			Text(
				text = description,
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
		Switch(
			checked = checked,
			onCheckedChange = onCheckedChange,
		)
	}
}

private val previewNotificationPreferences = NotificationPreferences(
	enableAll = true,
	enableTimers = true,
	enableSocial = false,
	enableUpdates = true,
)

@Preview(
	name = "Notification preferences light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun NotificationPreferencesPanelLightPreview() {
	PurecipesTheme(darkTheme = false) {
		NotificationPreferencesPanel(
			preferences = previewNotificationPreferences,
			onPreferencesChange = {},
			onSendTestNotification = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "Notification preferences dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun NotificationPreferencesPanelDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		NotificationPreferencesPanel(
			preferences = previewNotificationPreferences,
			onPreferencesChange = {},
			onSendTestNotification = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}
