package app.purecipes.marketing

import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun MarketingCoilPreview(content: @Composable () -> Unit) {
	val context = LocalContext.current
	val previewHandler = remember(context) {
		AsyncImagePreviewHandler { request ->
			val key = request.data.toString()
			val drawable = ContextCompat.getDrawable(context, MarketingImages.drawableRes(key))
				?: error("Missing marketing drawable for '$key'")
			val bitmap = if (drawable is BitmapDrawable && drawable.bitmap != null) {
				drawable.bitmap
			} else {
				drawable.toBitmap()
			}
			bitmap.asImage()
		}
	}
	CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
		content()
	}
}
