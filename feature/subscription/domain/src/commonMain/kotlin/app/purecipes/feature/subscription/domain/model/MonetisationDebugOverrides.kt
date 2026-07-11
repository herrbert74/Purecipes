package app.purecipes.feature.subscription.domain.model

data class MonetisationDebugOverrides(
	val premiumStatus: PremiumStatusOverride = PremiumStatusOverride.AUTO,
	val adsDisplay: AdsDisplayOverride = AdsDisplayOverride.AUTO,
)
