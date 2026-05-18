package app.purecipes.feature.auth.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.shared.ui.component.PurecipesTextButton
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun SignedInContent(
	consentState: ConsentState,
	user: AuthUser,
	isBusy: Boolean,
	onManagePrivacySettings: () -> Unit,
	onSignOut: () -> Unit,
	onDeleteAccount: () -> Unit,
) {
	var showDeleteAccountDialog by remember { mutableStateOf(false) }
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
		PurecipesTextButton(
			text = "Delete account",
			onClick = { showDeleteAccountDialog = true },
			enabled = !isBusy,
			modifier = Modifier
				.fillMaxWidth()
				.testTag(DELETE_ACCOUNT_BUTTON_TAG),
		)
	}
	if (showDeleteAccountDialog) {
		AlertDialog(
			modifier = Modifier.testTag(DELETE_ACCOUNT_DIALOG_TAG),
			onDismissRequest = { showDeleteAccountDialog = false },
			confirmButton = {
				Button(
					onClick = {
						showDeleteAccountDialog = false
						onDeleteAccount()
					},
					enabled = !isBusy,
				) {
					Text(text = "Delete")
				}
			},
			dismissButton = {
				TextButton(onClick = { showDeleteAccountDialog = false }) {
					Text(text = "Cancel")
				}
			},
			title = { Text(text = "Delete account?") },
			text = {
				Text(
					text = "This permanently removes your account and signs you out. This cannot be undone.",
				)
			},
		)
	}
}
