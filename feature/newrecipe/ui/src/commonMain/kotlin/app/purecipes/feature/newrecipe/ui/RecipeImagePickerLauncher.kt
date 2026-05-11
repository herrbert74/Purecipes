package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.Composable

interface RecipeImagePickerLauncher {
	fun launch()
}

@Composable
expect fun rememberRecipeImagePicker(
	onImageSelect: (String) -> Unit,
	onImportStateChange: (Boolean) -> Unit,
	onPickerError: (String) -> Unit,
): RecipeImagePickerLauncher?
