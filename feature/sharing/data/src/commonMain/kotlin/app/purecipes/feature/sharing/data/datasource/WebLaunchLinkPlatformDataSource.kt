package app.purecipes.feature.sharing.data.datasource

expect class WebLaunchLinkPlatformDataSource() {

	fun readLaunchUrl(): String?
}
