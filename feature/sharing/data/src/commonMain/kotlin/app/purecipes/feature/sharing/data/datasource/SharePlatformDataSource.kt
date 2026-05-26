package app.purecipes.feature.sharing.data.datasource

internal expect class SharePlatformDataSource() {

	fun shareText(text: String, title: String?)
}
