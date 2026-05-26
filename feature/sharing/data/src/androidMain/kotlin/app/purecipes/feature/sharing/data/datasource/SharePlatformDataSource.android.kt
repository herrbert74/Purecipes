package app.purecipes.feature.sharing.data.datasource

import app.purecipes.feature.sharing.data.runtime.SharingAndroidRuntime

internal actual class SharePlatformDataSource actual constructor() {

	actual fun shareText(text: String, title: String?) {
		SharingAndroidRuntime.shareText(text, title)
	}
}
