package app.purecipes.feature.sharing.data.datasource

import dev.zacsweers.metro.Inject

@Inject
actual class WebLaunchLinkPlatformDataSource actual constructor() {

	actual fun readLaunchUrl(): String? = wasmReadWebLaunchUrl()
}

@JsFun(
	"""
	() => {
		if (typeof window === 'undefined' || !window.location) {
			return null;
		}
		const href = window.location.href;
		const path = window.location.pathname;
		if (href && href !== path) {
			return href;
		}
		return path || null;
	}
""",
)
private external fun wasmReadWebLaunchUrl(): String?
