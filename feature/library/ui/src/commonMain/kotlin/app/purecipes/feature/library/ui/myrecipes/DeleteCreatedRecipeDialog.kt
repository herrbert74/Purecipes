package app.purecipes.feature.library.ui.myrecipes

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

internal const val DELETE_CREATED_RECIPE_DIALOG_CONFIRM_TAG = "deleteCreatedRecipeDialogConfirm"

@Composable
internal fun DeleteCreatedRecipeDialog(
	recipeName: String,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = onConfirm,
				modifier = Modifier.testTag(DELETE_CREATED_RECIPE_DIALOG_CONFIRM_TAG),
			) {
				Text(text = "Delete")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = "Delete recipe?") },
		text = { Text(text = "Are you sure you want to delete $recipeName?") },
	)
}
