package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val SAVE_BUTTON_TAG = "createRecipeSaveButton"

@Composable
internal fun CreateRecipeSaveBar(
	isEditing: Boolean,
	isSaving: Boolean,
	isImportingImage: Boolean,
	onSaveClick: () -> Unit,
	onClearClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(modifier = modifier.fillMaxWidth()) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			HorizontalDivider()
			Button(
				onClick = onSaveClick,
				enabled = !isSaving && !isImportingImage,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(SAVE_BUTTON_TAG),
			) {
				Text(text = if (isEditing) "Update recipe" else "Upload recipe")
			}
			TextButton(
				onClick = onClearClick,
				enabled = !isSaving && !isImportingImage,
			) {
				Text(text = if (isEditing) "Start new" else "Clear form")
			}
		}
	}
}
