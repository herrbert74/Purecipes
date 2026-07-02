package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

@Composable
internal fun CreateRecipeImageSection(
	titleInput: String,
	imageUrlInput: String,
	isImportingImage: Boolean,
	isSaving: Boolean,
	imagePickerErrorMessage: String?,
	onClearImageClick: () -> Unit,
	onImageUrlChange: (String) -> Unit,
	onPickImageClick: (() -> Unit)?,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		OutlinedTextField(
		value = imageUrlInput,
		onValueChange = onImageUrlChange,
		modifier = Modifier
			.fillMaxWidth()
			.testTag(IMAGE_FIELD_TAG),
		label = { Text(text = "Image URL or local file path") },
		singleLine = true,
	)

	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		if (onPickImageClick != null) {
			FilledTonalButton(
				onClick = onPickImageClick,
				enabled = !isSaving && !isImportingImage,
				modifier = Modifier
					.weight(1f)
					.testTag(IMAGE_PICK_BUTTON_TAG),
			) {
				if (isImportingImage) {
					CircularProgressIndicator(
						modifier = Modifier.size(PurecipesTheme.space.m),
						strokeWidth = PurecipesTheme.space.quark,
					)
					Text(text = "Importing image")
				} else {
					Text(text = "Choose image")
				}
			}
		}
		if (imageUrlInput.isNotBlank()) {
			TextButton(
				onClick = onClearImageClick,
				enabled = !isSaving && !isImportingImage,
				modifier = Modifier.testTag(IMAGE_CLEAR_BUTTON_TAG),
			) {
				Text(text = "Clear image")
			}
		}
	}

	if (isImportingImage) {
		ImageImportPlaceholder()
	}

	if (imageUrlInput.isNotBlank() && !isImportingImage) {
		AsyncImage(
			model = imagePreviewModel(imageUrlInput),
			contentDescription = titleInput.ifBlank { "Recipe image preview" },
			modifier = Modifier
				.fillMaxWidth()
				.height(180.dp)
				.clip(RoundedCornerShape(PurecipesTheme.space.m)),
			contentScale = ContentScale.Crop,
		)
	}

	ImageImportStatus(
		imageUrlInput = imageUrlInput,
		isImportingImage = isImportingImage,
	)

	imagePickerErrorMessage?.let {
		Text(
			text = it,
			style = PurecipesTheme.typography.bodyMedium,
			color = PurecipesTheme.colorScheme.error,
		)
	}
	}
}

@Composable
private fun ImageImportPlaceholder() {
	Card(
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerHighest),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(180.dp),
			contentAlignment = Alignment.Center,
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				CircularProgressIndicator()
				Text(
					text = "Preparing image preview...",
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

@Composable
private fun ImageImportStatus(imageUrlInput: String, isImportingImage: Boolean) {
	val statusText = when {
		isImportingImage -> "Importing the selected image before upload."
		imageUrlInput.isBlank() -> null
		imageUrlInput.isRemoteImageUrl() -> "This image will be used from its remote URL."
		else -> "This local image will upload with the recipe when you save it."
	}

	statusText?.let {
		Text(
			text = it,
			style = PurecipesTheme.typography.bodyMedium,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
	}
}

private fun imagePreviewModel(imageUrlInput: String): String {
	val trimmedInput = imageUrlInput.trim()
	return if (trimmedInput.isRemoteImageUrl() ||
		trimmedInput.startsWith("file://", ignoreCase = true)
	) {
		trimmedInput
	} else {
		"file://$trimmedInput"
	}
}

private fun String.isRemoteImageUrl(): Boolean {
	return startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)
}

private const val IMAGE_PICK_BUTTON_TAG = "createRecipeImagePickButton"
private const val IMAGE_CLEAR_BUTTON_TAG = "createRecipeImageClearButton"
private const val IMAGE_FIELD_TAG = "createRecipeImageField"
