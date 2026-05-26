package app.purecipes.feature.sharing.data.datasource

internal expect class WebLaunchLinkPlatformDataSource() {

	fun readLaunchUrl(): String?
}
