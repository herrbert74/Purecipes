package app.purecipes.feature.sharing.data.datasource

expect class SharePlatformDataSource() {

	fun shareText(text: String, title: String?)
}
