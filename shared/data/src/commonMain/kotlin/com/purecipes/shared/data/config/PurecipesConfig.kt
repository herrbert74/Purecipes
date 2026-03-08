package com.purecipes.shared.data.config

interface PurecipesConfig {
	fun buildType(): PurecipesBuildType
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
