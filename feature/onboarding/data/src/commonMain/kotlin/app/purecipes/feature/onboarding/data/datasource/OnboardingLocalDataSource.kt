package app.purecipes.feature.onboarding.data.datasource

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class OnboardingLocalDataSource(
	private val settings: Settings = Settings(),
	private val preferencesKey: String = DEFAULT_PREFERENCES_KEY,
) : OnboardingDataSource.Local {

	override fun isOnboardingCompleted(): Boolean =
		settings.getBoolean(preferencesKey, defaultValue = false)

	override fun setOnboardingCompleted(completed: Boolean) {
		settings[preferencesKey] = completed
	}

	private companion object {

		const val DEFAULT_PREFERENCES_KEY = "purecipes.onboarding.completed"
	}
}
