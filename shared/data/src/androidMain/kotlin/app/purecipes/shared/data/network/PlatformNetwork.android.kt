package app.purecipes.shared.data.network

actual fun localBackendBaseUrl(debugBackendHostOverride: String?): String {
	val host = debugBackendHostOverride?.trim()?.takeIf { it.isNotEmpty() }
		?: defaultAndroidDebugBackendHost()
	return formatDebugBackendBaseUrl(host)
}
