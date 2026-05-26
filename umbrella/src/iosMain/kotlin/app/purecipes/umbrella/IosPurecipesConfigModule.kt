package app.purecipes.umbrella

import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.data.config.purecipesBuildType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface IosPurecipesConfigModule {

	@Provides
	fun providePurecipesConfig(): PurecipesConfig {
		return object : PurecipesConfig {
			override fun buildType() = purecipesBuildType(BuildKonfig.purecipesBuildType)

			override fun debugBackendHostOverride(): String? {
				return BuildKonfig.purecipesDebugBackendHost.takeIf { it.isNotBlank() }
			}

			override fun googleWebClientId(): String? {
				return BuildKonfig.purecipesGoogleWebClientId.takeIf { it.isNotBlank() }
			}

			override fun gaMeasurementId(): String? {
				return BuildKonfig.purecipesGaMeasurementId.takeIf { it.isNotBlank() }
			}

			override fun mixpanelProjectToken(): String? {
				return BuildKonfig.purecipesMixpanelProjectToken.takeIf { it.isNotBlank() }
			}

			override fun usercentricsSettingsId(): String? {
				return BuildKonfig.purecipesUsercentricsSettingsId.takeIf { it.isNotBlank() }
			}
		}
	}
}
