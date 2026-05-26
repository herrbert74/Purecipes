package app.purecipes.feature.sharing.data.datasource

import platform.Foundation.NSArray
import platform.Foundation.arrayWithObject
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

internal actual class SharePlatformDataSource actual constructor() {

	actual fun shareText(text: String, title: String?) {
		val activityItems = NSArray.arrayWithObject(text)
		val controller = UIActivityViewController(activityItems, null)
		var root = UIApplication.sharedApplication.keyWindow?.rootViewController
		while (root?.presentedViewController != null) {
			root = root.presentedViewController
		}
		root?.presentViewController(controller, animated = true, completion = null)
	}
}
