package app.purecipes.shared.data.session

import com.russhwolf.settings.Settings

internal const val AUTH_SESSION_PREFERENCES_NAME = "purecipes_auth_session"

internal expect fun createAuthSessionSettings(): Settings

internal expect fun protectAuthSessionSettingsFromBackup()
