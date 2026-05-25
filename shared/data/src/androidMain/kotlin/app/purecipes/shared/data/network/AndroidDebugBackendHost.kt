package app.purecipes.shared.data.network

import android.os.Build

internal fun defaultAndroidDebugBackendHost(): String {
	return if (isAndroidEmulator()) {
		"10.0.2.2"
	} else {
		"127.0.0.1"
	}
}

private fun isAndroidEmulator(): Boolean {
	return Build.FINGERPRINT.startsWith("generic") ||
		Build.FINGERPRINT.startsWith("unknown") ||
		Build.MODEL.contains("google_sdk") ||
		Build.MODEL.contains("Emulator") ||
		Build.MODEL.contains("Android SDK built for x86") ||
		Build.MANUFACTURER.contains("Genymotion") ||
		(Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
		Build.PRODUCT == "google_sdk"
}
