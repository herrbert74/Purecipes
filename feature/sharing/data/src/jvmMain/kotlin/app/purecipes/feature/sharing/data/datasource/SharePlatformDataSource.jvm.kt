package app.purecipes.feature.sharing.data.datasource

import dev.zacsweers.metro.Inject

@Inject
actual class SharePlatformDataSource actual constructor() {

	actual fun shareText(text: String, title: String?) = Unit
}
