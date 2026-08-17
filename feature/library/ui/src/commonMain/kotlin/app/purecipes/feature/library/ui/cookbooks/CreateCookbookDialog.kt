package app.purecipes.feature.library.ui.cookbooks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import app.purecipes.feature.library.domain.CookbookNameSuggestions
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList

internal const val CREATE_COOKBOOK_DIALOG_INPUT_TAG = "createCookbookDialogInput"

@Composable
internal fun CreateCookbookDialog(
	existingCookbookNames: ImmutableList<String>,
	isLoading: Boolean,
	errorMessage: String?,
	onDismiss: () -> Unit,
	onConfirm: (String) -> Unit,
) {
	var nameField by remember { mutableStateOf("") }
	val existingCookbookNamesNormalized = remember(existingCookbookNames) {
		existingCookbookNames
			.map { it.trim().lowercase() }
			.toSet()
	}
	val suggestionNames = remember(existingCookbookNamesNormalized) {
		CookbookNameSuggestions.values.filter { suggestion ->
			suggestion.trim().lowercase() !in existingCookbookNamesNormalized
		}
	}
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = { onConfirm(nameField) },
				enabled = !isLoading && nameField.trim().isNotEmpty(),
			) {
				Text(text = "Create")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = "New cookbook") },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				LazyRow(
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					modifier = Modifier.fillMaxWidth(),
				) {
					items(suggestionNames, key = { it }) { suggestion ->
						FilterChip(
							selected = false,
							onClick = { nameField = suggestion },
							label = { Text(text = suggestion) },
						)
					}
				}
				OutlinedTextField(
					value = nameField,
					onValueChange = { nameField = it },
					modifier = Modifier
						.fillMaxWidth()
						.testTag(CREATE_COOKBOOK_DIALOG_INPUT_TAG),
					label = { Text(text = "Name") },
					singleLine = true,
				)
				errorMessage?.let { ErrorText(text = it) }
				if (isLoading) {
					CircularProgressIndicator(modifier = Modifier.size(24.dp))
				}
			}
		},
	)
}
