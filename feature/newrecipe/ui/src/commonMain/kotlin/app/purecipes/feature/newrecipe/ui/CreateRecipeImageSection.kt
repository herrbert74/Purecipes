package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
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
	var showUrlField by remember(imageUrlInput) {
		mutableStateOf(imageUrlInput.isRemoteImageUrl())
	}
	val shape = RoundedCornerShape(PurecipesTheme.space.m)
	val canInteract = !isSaving && !isImportingImage

	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		when {
			isImportingImage -> ImageImportPlaceholder(shape = shape)

			imageUrlInput.isNotBlank() -> Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(IMAGE_DROPZONE_HEIGHT)
					.clip(shape),
			) {
				AsyncImage(
					model = imagePreviewModel(imageUrlInput),
					contentDescription = titleInput.ifBlank { "Recipe image preview" },
					modifier = Modifier.fillMaxSize(),
					contentScale = ContentScale.Crop,
				)
				Row(
					modifier = Modifier
						.align(Alignment.BottomEnd)
						.padding(PurecipesTheme.space.s),
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
				) {
					if (onPickImageClick != null) {
						Surface(
							shape = RoundedCornerShape(PurecipesTheme.space.s),
							color = PurecipesTheme.colorScheme.surface.copy(alpha = 0.92f),
							modifier = Modifier
								.testTag(IMAGE_PICK_BUTTON_TAG)
								.clickable(enabled = canInteract, onClick = onPickImageClick),
						) {
							Text(
								text = "Change",
								modifier = Modifier.padding(
									horizontal = PurecipesTheme.space.m,
									vertical = PurecipesTheme.space.s,
								),
								style = PurecipesTheme.typography.labelLarge,
							)
						}
					}
					Surface(
						shape = RoundedCornerShape(PurecipesTheme.space.s),
						color = PurecipesTheme.colorScheme.surface.copy(alpha = 0.92f),
						modifier = Modifier
							.testTag(IMAGE_CLEAR_BUTTON_TAG)
							.clickable(enabled = canInteract, onClick = onClearImageClick),
					) {
						Text(
							text = "Remove",
							modifier = Modifier.padding(
								horizontal = PurecipesTheme.space.m,
								vertical = PurecipesTheme.space.s,
							),
							style = PurecipesTheme.typography.labelLarge,
						)
					}
				}
			}

			else -> EmptyImageDropzone(
				enabled = canInteract && onPickImageClick != null,
				shape = shape,
				onClick = { onPickImageClick?.invoke() },
			)
		}

		ImageImportStatus(
			imageUrlInput = imageUrlInput,
			isImportingImage = isImportingImage,
		)

		if (!showUrlField) {
			TextButton(
				onClick = { showUrlField = true },
				enabled = canInteract,
				modifier = Modifier.testTag(IMAGE_URL_TOGGLE_TAG),
			) {
				Text(text = "Paste image URL")
			}
		} else {
			OutlinedTextField(
				value = imageUrlInput,
				onValueChange = onImageUrlChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(IMAGE_FIELD_TAG),
				label = { Text(text = "Image URL") },
				singleLine = true,
				enabled = canInteract,
			)
			if (imageUrlInput.isBlank()) {
				TextButton(
					onClick = { showUrlField = false },
					enabled = canInteract,
				) {
					Text(text = "Hide URL field")
				}
			}
		}

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
private fun EmptyImageDropzone(
	enabled: Boolean,
	shape: RoundedCornerShape,
	onClick: () -> Unit,
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(IMAGE_DROPZONE_HEIGHT)
			.clip(shape)
			.border(
				width = 1.dp,
				color = PurecipesTheme.colorScheme.outlineVariant,
				shape = shape,
			)
			.background(PurecipesTheme.colorScheme.surfaceContainerHighest)
			.testTag(IMAGE_PICK_BUTTON_TAG)
			.clickable(enabled = enabled, onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			modifier = Modifier.padding(PurecipesTheme.space.m),
		) {
			Icon(
				imageVector = Icons.Filled.AddAPhoto,
				contentDescription = null,
				modifier = Modifier.size(40.dp),
				tint = PurecipesTheme.colorScheme.primary,
			)
			Text(
				text = if (enabled) "Add a photo" else "Photo picker unavailable",
				style = PurecipesTheme.typography.titleMedium,
				textAlign = TextAlign.Center,
			)
			Text(
				text = "Photos help your recipe stand out",
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center,
			)
		}
	}
}

@Composable
private fun ImageImportPlaceholder(shape: RoundedCornerShape) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(IMAGE_DROPZONE_HEIGHT)
			.clip(shape)
			.background(PurecipesTheme.colorScheme.surfaceContainerHighest),
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
			Text(
				text = "Importing image",
				style = PurecipesTheme.typography.labelLarge,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
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

private val IMAGE_DROPZONE_HEIGHT = 200.dp

internal const val IMAGE_PICK_BUTTON_TAG = "createRecipeImagePickButton"
internal const val IMAGE_CLEAR_BUTTON_TAG = "createRecipeImageClearButton"
internal const val IMAGE_FIELD_TAG = "createRecipeImageField"
internal const val IMAGE_URL_TOGGLE_TAG = "createRecipeImageUrlToggle"
