package app.purecipes.feature.newrecipe.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.PurecipesTextButton

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
			PurecipesButton(
				text = confirmLabel,
				onClick = onConfirm,
				modifier = Modifier.testTag(CLEAR_FORM_DIALOG_CONFIRM_TAG),
			)
		},
		dismissButton = {
			PurecipesTextButton(
				text = "Cancel",
				onClick = onDismiss,
			)
		},
		title = { Text(text = title) },
		text = { Text(text = message) },
	)
}
