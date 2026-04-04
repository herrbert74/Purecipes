package com.purecipes.shared.data.config

interface PurecipesConfig {
	fun buildType(): PurecipesBuildType

	fun googleWebClientId(): String? = null

	fun gaMeasurementId(): String? = null

	fun mixpanelProjectToken(): String? = null

	fun usercentricsSettingsId(): String? = null
}

enum class PurecipesBuildType {
	DEBUG,
	STAGING,
	RELEASE,
}

fun purecipesBuildType(name: String): PurecipesBuildType {
	return when (name.lowercase()) {
		"staging" -> PurecipesBuildType.STAGING
		"release" -> PurecipesBuildType.RELEASE
		else -> PurecipesBuildType.DEBUG
	}
}
