package app.purecipes.feature.newrecipe.ui

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

@Composable
actual fun rememberRecipeImagePicker(
	onImageSelect: (String) -> Unit,
	onImportStateChange: (Boolean) -> Unit,
	onPickerError: (String) -> Unit,
): RecipeImagePickerLauncher? {
	val context = LocalContext.current
	val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri == null) {
			onImportStateChange(false)
			return@rememberLauncherForActivityResult
		}

		runCatching {
			copyImageToCache(context, uri)
		}.onSuccess {
			onImportStateChange(false)
			onImageSelect(it)
		}
			.onFailure {
				onImportStateChange(false)
				onPickerError("Could not import the selected image.")
			}
	}

	return remember(launcher) {
		object : RecipeImagePickerLauncher {
			override fun launch() {
				onImportStateChange(true)
				launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
			}
		}
	}
}

private fun copyImageToCache(context: Context, uri: Uri): String {
	val contentResolver = context.contentResolver
	val mimeType = contentResolver.getType(uri).orEmpty()
	val sourceFileName = readDisplayName(context, uri)
	val extension = fileExtension(sourceFileName, mimeType)
	val outputFile = File(context.cacheDir, "${UUID.randomUUID()}.$extension")
	contentResolver.openInputStream(uri)?.use { inputStream ->
		outputFile.outputStream().use { outputStream ->
			inputStream.copyTo(outputStream)
		}
	} ?: error("Unable to open the selected image.")
	return outputFile.absolutePath
}

private fun readDisplayName(context: Context, uri: Uri): String? {
	context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
		val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
		val hasDisplayName = cursor.moveToFirst() && index >= 0
		return cursor.getString(index).takeIf { hasDisplayName }
	}
	return null
}

private fun fileExtension(fileName: String?, mimeType: String): String {
	val nameExtension = fileName
		?.substringAfterLast('.', missingDelimiterValue = "")
		?.lowercase()
		?.takeIf(String::isNotBlank)
	if (nameExtension != null) {
		return nameExtension
	}

	return when (mimeType.lowercase()) {
		"image/png" -> "png"
		"image/webp" -> "webp"
		"image/gif" -> "gif"
		else -> "jpg"
	}
}
