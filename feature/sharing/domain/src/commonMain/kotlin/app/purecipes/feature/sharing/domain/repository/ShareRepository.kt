package app.purecipes.feature.sharing.domain.repository

interface ShareRepository {

	fun shareText(text: String, title: String?)
}
