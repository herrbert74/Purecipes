package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val CREATE_FEATURE_REQUEST_TITLE_INPUT_TAG = "createFeatureRequestTitleInput"

internal const val CREATE_FEATURE_REQUEST_DESCRIPTION_INPUT_TAG = "createFeatureRequestDescriptionInput"

private val CREATE_FEATURE_REQUEST_PROGRESS_SIZE = 24.dp

private const val CREATE_FEATURE_REQUEST_DESCRIPTION_LINES = 4

@Composable
internal fun CreateFeatureRequestDialog(
	isLoading: Boolean,
	errorMessage: String?,
	onDismiss: () -> Unit,
	onConfirm: (title: String, description: String) -> Unit,
) {
	var titleField by remember { mutableStateOf("") }
	var descriptionField by remember { mutableStateOf("") }
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = { onConfirm(titleField, descriptionField) },
				enabled = !isLoading && titleField.trim().isNotEmpty() && descriptionField.trim().isNotEmpty(),
			) {
				Text(text = "Submit")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = "Request a feature") },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				OutlinedTextField(
					value = titleField,
					onValueChange = { titleField = it },
					modifier = Modifier
						.fillMaxWidth()
						.testTag(CREATE_FEATURE_REQUEST_TITLE_INPUT_TAG),
					label = { Text(text = "Title") },
					singleLine = true,
				)
				OutlinedTextField(
					value = descriptionField,
					onValueChange = { descriptionField = it },
					modifier = Modifier
						.fillMaxWidth()
						.testTag(CREATE_FEATURE_REQUEST_DESCRIPTION_INPUT_TAG),
					label = { Text(text = "Description") },
					minLines = CREATE_FEATURE_REQUEST_DESCRIPTION_LINES,
				)
				errorMessage?.let { ErrorText(text = it) }
				if (isLoading) {
					CircularProgressIndicator(
						modifier = Modifier.size(CREATE_FEATURE_REQUEST_PROGRESS_SIZE),
					)
				}
			}
		},
	)
}
