package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIContentSizeCategoryDidChangeNotification

@Composable
internal actual fun ProvideDynamicTypeFontScale(content: @Composable () -> Unit) {
	var contentSizeCategory by remember {
		mutableStateOf(
			UIApplication.sharedApplication.preferredContentSizeCategory.orEmpty(),
		)
	}

	DisposableEffect(Unit) {
		val notificationCenter = NSNotificationCenter.defaultCenter
		val observer = notificationCenter.addObserverForName(
			name = UIContentSizeCategoryDidChangeNotification,
			`object` = null,
			queue = NSOperationQueue.mainQueue,
		) { _ ->
			contentSizeCategory =
				UIApplication.sharedApplication.preferredContentSizeCategory.orEmpty()
		}
		onDispose {
			notificationCenter.removeObserver(observer)
		}
	}

	val density = LocalDensity.current
	val fontScale = fontScaleForContentSizeCategory(
		category = contentSizeCategory,
		fallback = density.fontScale,
	)
	CompositionLocalProvider(
		LocalDensity provides Density(density = density.density, fontScale = fontScale),
		content = content,
	)
}
