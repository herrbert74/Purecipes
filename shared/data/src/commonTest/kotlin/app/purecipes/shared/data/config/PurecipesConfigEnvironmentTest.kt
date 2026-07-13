package app.purecipes.shared.data.config

import kotlin.test.Test
import kotlin.test.assertEquals

class PurecipesConfigEnvironmentTest {

	@Test
	fun environmentNameMatchesBuildType() {
		assertEquals("debug", PurecipesBuildType.DEBUG.environmentName())
		assertEquals("staging", PurecipesBuildType.STAGING.environmentName())
		assertEquals("release", PurecipesBuildType.RELEASE.environmentName())
	}

	@Test
	fun environmentDerivesFromBuildType() {
		val config = object : PurecipesConfig {
			override fun buildType(): PurecipesBuildType = PurecipesBuildType.STAGING

			override fun versionName(): String = "1.0.0"

			override fun versionCode(): Long = 1
		}

		assertEquals("staging", config.environment())
	}
}
