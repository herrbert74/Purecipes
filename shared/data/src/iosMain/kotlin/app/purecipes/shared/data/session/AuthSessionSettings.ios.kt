package app.purecipes.shared.data.session

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLibraryDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

internal actual fun createAuthSessionSettings(): Settings {
	return NSUserDefaultsSettings(NSUserDefaults(suiteName = AUTH_SESSION_PREFERENCES_NAME))
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun protectAuthSessionSettingsFromBackup() {
	val libraryPath = NSSearchPathForDirectoriesInDomains(
		NSLibraryDirectory,
		NSUserDomainMask,
		true,
	).firstOrNull() as? String ?: return
	val preferencesUrl = NSURL.fileURLWithPath(
		"$libraryPath/Preferences/$AUTH_SESSION_PREFERENCES_NAME.plist",
	)
	preferencesUrl.setResourceValue(
		true,
		forKey = NSURLIsExcludedFromBackupKey,
		error = null,
	)
}
