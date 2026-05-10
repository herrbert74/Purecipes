package com.purecipes.shared.data.network

import com.purecipes.shared.data.config.PurecipesBuildType

private const val STAGING_BACKEND_BASE_URL = "http://staging.purecipes.com/"
private const val RELEASE_BACKEND_BASE_URL = "http://185.132.41.132" // "http://www.purecipes.app/"

fun backendBaseUrl(buildType: PurecipesBuildType): String {
	return when (buildType) {
		PurecipesBuildType.DEBUG -> localBackendBaseUrl()
		PurecipesBuildType.STAGING -> STAGING_BACKEND_BASE_URL
		PurecipesBuildType.RELEASE -> RELEASE_BACKEND_BASE_URL
	}
}

expect fun localBackendBaseUrl(): String
