package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.Composable

@Composable
actual fun rememberRecipeImagePicker(
	onImageSelect: (String) -> Unit,
	onImportStateChange: (Boolean) -> Unit,
	onPickerError: (String) -> Unit,
): RecipeImagePickerLauncher? = null
