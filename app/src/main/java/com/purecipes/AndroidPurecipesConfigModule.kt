package com.purecipes

import com.purecipes.shared.data.config.PurecipesConfig
import com.purecipes.shared.data.config.purecipesBuildType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AndroidPurecipesConfigModule {

	@Provides
	fun providePurecipesConfig(): PurecipesConfig {
		return object : PurecipesConfig {
			override fun buildType() = purecipesBuildType(BuildConfig.BUILD_TYPE)

			override fun googleWebClientId(): String? {
				return BuildConfig.PURECIPES_GOOGLE_WEB_CLIENT_ID.takeIf { it.isNotBlank() }
			}
		}
	}
}
