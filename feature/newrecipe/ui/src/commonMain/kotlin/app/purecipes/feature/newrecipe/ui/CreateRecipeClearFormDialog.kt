package app.purecipes.feature.newrecipe.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

internal const val CLEAR_FORM_DIALOG_CONFIRM_TAG = "createRecipeClearFormDialogConfirm"

@Composable
internal fun CreateRecipeClearFormDialog(
	isEditing: Boolean,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
) {
	val title = if (isEditing) {
		"Start new recipe?"
	} else {
		"Clear form?"
	}
	val message = if (isEditing) {
		"Are you sure you want to start a new recipe? Unsaved changes will be lost."
	} else {
		"Are you sure you want to clear this recipe? All entered data will be lost."
	}
	val confirmLabel = if (isEditing) {
		"Start new"
	} else {
		"Clear form"
	}

	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = onConfirm,
				modifier = Modifier.testTag(CLEAR_FORM_DIALOG_CONFIRM_TAG),
			) {
				Text(text = confirmLabel)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = title) },
		text = { Text(text = message) },
	)
}
