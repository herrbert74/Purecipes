package com.purecipes.umbrella

import com.purecipes.shared.data.config.PurecipesConfig
import com.purecipes.shared.data.config.purecipesBuildType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface WasmPurecipesConfigModule {

	@Provides
	fun providePurecipesConfig(): PurecipesConfig {
		return object : PurecipesConfig {
			override fun buildType() = purecipesBuildType(BuildKonfig.purecipesBuildType)

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
