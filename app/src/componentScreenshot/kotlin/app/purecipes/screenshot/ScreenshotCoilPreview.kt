package app.purecipes.screenshot

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler

private const val PLACEHOLDER_WIDTH_PX = 320
private const val PLACEHOLDER_HEIGHT_PX = 180
private const val PLACEHOLDER_ACCENT_LEFT_PX = 40f
private const val PLACEHOLDER_ACCENT_TOP_PX = 30f
private const val PLACEHOLDER_ACCENT_RIGHT_PX = 280f
private const val PLACEHOLDER_ACCENT_BOTTOM_PX = 150f
private const val PLACEHOLDER_BACKGROUND_COLOR = 0xFFE8DED2.toInt()
private const val PLACEHOLDER_ACCENT_COLOR = 0xFFC59B76.toInt()

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun ScreenshotCoilPreview(
	content: @Composable () -> Unit,
) {
	val previewHandler = remember {
		val placeholder = createDeterministicPlaceholderImage()
		AsyncImagePreviewHandler {
			placeholder
		}
	}
	CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
		content()
	}
}

private fun createDeterministicPlaceholderImage() =
	Bitmap.createBitmap(PLACEHOLDER_WIDTH_PX, PLACEHOLDER_HEIGHT_PX, Bitmap.Config.ARGB_8888).apply {
		val canvas = Canvas(this)
		canvas.drawColor(PLACEHOLDER_BACKGROUND_COLOR)
		val paint = Paint().apply {
			color = Color.argb(
				Color.alpha(PLACEHOLDER_ACCENT_COLOR),
				Color.red(PLACEHOLDER_ACCENT_COLOR),
				Color.green(PLACEHOLDER_ACCENT_COLOR),
				Color.blue(PLACEHOLDER_ACCENT_COLOR),
			)
		}
		canvas.drawRect(
			PLACEHOLDER_ACCENT_LEFT_PX,
			PLACEHOLDER_ACCENT_TOP_PX,
			PLACEHOLDER_ACCENT_RIGHT_PX,
			PLACEHOLDER_ACCENT_BOTTOM_PX,
			paint,
		)
	}.asImage()
