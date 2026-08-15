package app.purecipes.feature.library.ui.cookbooks

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

internal const val DELETE_COOKBOOK_DIALOG_CONFIRM_TAG = "deleteCookbookDialogConfirm"

@Composable
internal fun DeleteCookbookDialog(
	cookbookName: String,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = onConfirm,
				modifier = Modifier.testTag(DELETE_COOKBOOK_DIALOG_CONFIRM_TAG),
			) {
				Text(text = "Delete")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = "Delete cookbook?") },
		text = { Text(text = "Are you sure you want to delete $cookbookName?") },
	)
}
