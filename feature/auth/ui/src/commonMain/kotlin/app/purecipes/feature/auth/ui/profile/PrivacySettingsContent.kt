package app.purecipes.feature.auth.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.model.toDisplayText
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun PrivacySettingsContent(
	consentState: ConsentState,
	onManagePrivacySettings: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Text(
			text = "Privacy",
			style = PurecipesTheme.typography.titleLarge,
		)
		Text(
			text = consentState.toDisplayText(),
			style = PurecipesTheme.typography.bodyLarge,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		OutlinedButton(
			modifier = Modifier.fillMaxWidth(),
			onClick = onManagePrivacySettings,
		) {
			Text(text = "Manage privacy settings")
		}
	}
}
