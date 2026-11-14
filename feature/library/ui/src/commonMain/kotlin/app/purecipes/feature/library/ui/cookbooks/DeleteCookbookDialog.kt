package app.purecipes.feature.library.ui.cookbooks

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.PurecipesTextButton

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
			PurecipesButton(
				text = "Delete",
				onClick = onConfirm,
				modifier = Modifier.testTag(DELETE_COOKBOOK_DIALOG_CONFIRM_TAG),
			)
		},
		dismissButton = {
			PurecipesTextButton(
				text = "Cancel",
				onClick = onDismiss,
			)
		},
		title = { Text(text = "Delete cookbook?") },
		text = { Text(text = "Are you sure you want to delete $cookbookName?") },
	)
}
