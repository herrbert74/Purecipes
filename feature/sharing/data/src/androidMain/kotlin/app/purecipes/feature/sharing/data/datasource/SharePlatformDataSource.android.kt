package app.purecipes.feature.sharing.data.datasource

import app.purecipes.feature.sharing.data.runtime.SharingAndroidRuntime
import dev.zacsweers.metro.Inject

@Inject
actual class SharePlatformDataSource actual constructor() {

	actual fun shareText(text: String, title: String?) {
		SharingAndroidRuntime.shareText(text, title)
	}
}
