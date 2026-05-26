package app.purecipes.feature.sharing.domain.repository

interface WebLaunchLinkRepository {

	fun readLaunchUrl(): String?
}
