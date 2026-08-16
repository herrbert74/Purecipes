package app.purecipes.shared.data.session

import android.content.Context
import androidx.startup.Initializer
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsInitializer
import com.russhwolf.settings.SharedPreferencesSettings

private var appContext: Context? = null

internal actual fun createAuthSessionSettings(): Settings {
	val context = checkNotNull(appContext) {
		"AuthSessionSettingsInitializer has not created an application Context"
	}
	val delegate = context.getSharedPreferences(
		AUTH_SESSION_PREFERENCES_NAME,
		Context.MODE_PRIVATE,
	)
	return SharedPreferencesSettings(delegate)
}

internal actual fun protectAuthSessionSettingsFromBackup() = Unit

class AuthSessionSettingsInitializer : Initializer<Context> {

	override fun create(context: Context): Context = context.applicationContext.also { appContext = it }

	override fun dependencies(): List<Class<out Initializer<*>>> = listOf(SettingsInitializer::class.java)
}
