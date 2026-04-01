package com.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.pathExtension
import platform.Photos.PHPhotoLibrary
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
actual fun rememberRecipeImagePicker(
	onImageSelect: (String) -> Unit,
	onImportStateChange: (Boolean) -> Unit,
	onPickerError: (String) -> Unit,
): RecipeImagePickerLauncher? {
	val onImageSelectState = rememberUpdatedState(onImageSelect)
	val onImportStateChangeState = rememberUpdatedState(onImportStateChange)
	val onPickerErrorState = rememberUpdatedState(onPickerError)
	val delegate = remember {
		RecipeImagePickerDelegate(
			onImageSelect = { path -> onImageSelectState.value(path) },
			onImportStateChange = { isImporting -> onImportStateChangeState.value(isImporting) },
			onPickerError = { message -> onPickerErrorState.value(message) },
		)
	}

	return remember(delegate) {
		object : RecipeImagePickerLauncher {
			override fun launch() {
				val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
				if (viewController == null) {
					onImportStateChangeState.value(false)
					onPickerErrorState.value("Could not open the image picker.")
					return
				}
				onImportStateChangeState.value(true)
				delegate.present(viewController)
			}
		}
	}
}

private class RecipeImagePickerDelegate(
	private val onImageSelect: (String) -> Unit,
	private val onImportStateChange: (Boolean) -> Unit,
	private val onPickerError: (String) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

	private var pickerViewController: PHPickerViewController? = null

	fun present(viewController: UIViewController) {
		val configuration = PHPickerConfiguration(PHPhotoLibrary.sharedPhotoLibrary())
		configuration.selectionLimit = 1
		configuration.filter = PHPickerFilter.imagesFilter()
		val picker = PHPickerViewController(configuration).apply {
			delegate = this@RecipeImagePickerDelegate
		}
		pickerViewController = picker
		viewController.presentViewController(picker, true, null)
	}

	override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
		picker.dismissViewControllerAnimated(true, null)
		pickerViewController = null
		val result = didFinishPicking.firstOrNull() as? PHPickerResult
		if (result == null) {
			onImportStateChange(false)
			return
		}

		result.itemProvider.loadFileRepresentationForTypeIdentifier("public.image") { url, error ->
			if (url == null || error != null) {
				onImportStateChange(false)
				onPickerError("Could not import the selected image.")
				return@loadFileRepresentationForTypeIdentifier
			}

			val copiedPath = copyImageToTemporaryDirectory(url)
			if (copiedPath == null) {
				onImportStateChange(false)
				onPickerError("Could not import the selected image.")
			} else {
				onImportStateChange(false)
				onImageSelect(copiedPath)
			}
		}
	}
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
private fun copyImageToTemporaryDirectory(sourceUrl: NSURL): String? {
	val temporaryDirectory = NSTemporaryDirectory()
	val extension = sourceUrl.pathExtension?.takeIf { it.isNotBlank() } ?: "jpg"
	val fileName = NSUUID().UUIDString + "." + extension
	val destinationPath = temporaryDirectory + fileName
	val destinationUrl = NSURL.fileURLWithPath(destinationPath)
	NSFileManager.defaultManager.removeItemAtURL(destinationUrl, null)
	val copied = NSFileManager.defaultManager.copyItemAtURL(sourceUrl, destinationUrl, null)
	return destinationUrl.path?.takeIf { copied }
}
