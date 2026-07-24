package app.purecipes.shared.data.network

import app.purecipes.shared.data.config.PurecipesBuildType

internal const val DEBUG_BACKEND_PORT = 9090

private const val STAGING_BACKEND_BASE_URL = "https://staging.purecipes.app/"
private const val RELEASE_BACKEND_BASE_URL = "https://purecipes.app/"

fun backendBaseUrl(
	buildType: PurecipesBuildType,
	debugBackendHostOverride: String? = null,
): String {
	return when (buildType) {
		PurecipesBuildType.DEBUG -> localBackendBaseUrl(debugBackendHostOverride)
		PurecipesBuildType.STAGING -> STAGING_BACKEND_BASE_URL
		PurecipesBuildType.RELEASE -> RELEASE_BACKEND_BASE_URL
	}
}

internal fun formatDebugBackendBaseUrl(host: String): String {
	return "http://${host.trim()}:$DEBUG_BACKEND_PORT/"
}

expect fun localBackendBaseUrl(debugBackendHostOverride: String?): String
