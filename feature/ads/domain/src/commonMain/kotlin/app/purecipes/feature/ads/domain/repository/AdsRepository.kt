package app.purecipes.feature.ads.domain.repository

interface AdsRepository {

	fun initialize()

	fun showInterstitial(onDismissed: () -> Unit)
}
