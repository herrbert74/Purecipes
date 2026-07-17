package app.purecipes.shared.data.config

interface PurecipesConfig {

	fun buildType(): PurecipesBuildType

	fun versionName(): String

	fun versionCode(): Long

	fun environment(): String = buildType().environmentName()

	// Needed for Wi-Fi debugging on Android or any debugging on iOS/WASM
	fun debugBackendHostOverride(): String? = null

	fun googleWebClientId(): String? = null

	fun gaMeasurementId(): String? = null

	fun mixpanelProjectToken(): String? = null

	fun usercentricsSettingsId(): String? = null

	fun revenueCatApiKey(): String? = null

	fun showMonetisationDebugOverrides(): Boolean = buildType() != PurecipesBuildType.RELEASE

	fun adMobAppId(): String? = null

	fun adMobBannerAdUnitId(): String? = null

	fun adMobInterstitialAdUnitId(): String? = null
}

enum class PurecipesBuildType {
	DEBUG,
	STAGING,
	RELEASE,
	;

	fun environmentName(): String = name.lowercase()
}

fun purecipesBuildType(name: String): PurecipesBuildType {
	return when (name.lowercase()) {
		"staging" -> PurecipesBuildType.STAGING
		"release" -> PurecipesBuildType.RELEASE
		else -> PurecipesBuildType.DEBUG
	}
}
