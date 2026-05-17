package app.purecipes.feature.auth.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun SignedInContent(
	consentState: ConsentState,
	user: AuthUser,
	isBusy: Boolean,
	onManagePrivacySettings: () -> Unit,
	onSignOut: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m)) {
		ProfileHeader(user = user)
		HorizontalDivider()
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
			AssistChip(
				onClick = {},
				label = { Text(text = user.provider.name.lowercase().replaceFirstChar { it.titlecase() }) },
			)
		}
		HorizontalDivider()
		PrivacySettingsContent(
			consentState = consentState,
			onManagePrivacySettings = onManagePrivacySettings,
		)
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = onSignOut,
			enabled = !isBusy,
		) {
			if (isBusy) {
				CircularProgressIndicator(
					modifier = Modifier.size(PurecipesTheme.space.m),
					strokeWidth = PurecipesTheme.space.quark,
				)
			} else {
				Text(text = "Sign out")
			}
		}
	}
}
