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
		}
	}
}
