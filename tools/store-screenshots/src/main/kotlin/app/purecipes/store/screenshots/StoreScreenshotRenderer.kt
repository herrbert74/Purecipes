package app.purecipes.store.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.use
import kotlinx.coroutines.Dispatchers
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import java.io.File

object StoreScreenshotRenderer {

	fun loadScreenshot(file: File): ImageBitmap {
		require(file.isFile) { "Raw screenshot not found: ${file.absolutePath}" }
		return Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
	}

	fun render(
		slide: MarketingSlide,
		screenshot: ImageBitmap,
		outputSize: StoreOutputSize,
		fontsDirectory: File,
		outputFile: File,
	) {
		outputFile.parentFile.mkdirs()
		ImageComposeScene(
			width = outputSize.widthPx,
			height = outputSize.heightPx,
			density = Density(density = 1f),
			coroutineContext = Dispatchers.Unconfined,
		) {
			val fontFamily = rememberCabinFontFamily(fontsDirectory)
			MarketingFrame(
				title = slide.title,
				subtitle = slide.subtitle,
				screenshot = screenshot,
				outputSize = outputSize,
				fontFamily = fontFamily,
				theme = slide.theme,
				modifier = Modifier.fillMaxSize(),
			)
		}.use { scene ->
			val image = scene.render()
			val encoded = image.encodeToData(EncodedImageFormat.PNG)
				?: error("Failed to encode PNG for ${outputFile.name}")
			outputFile.writeBytes(encoded.bytes)
		}
	}
}
