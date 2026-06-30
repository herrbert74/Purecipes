package app.purecipes.feature.settings.ui.about

import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.runViewModelTest
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AboutViewModelTest {

	@Test
	fun `version text includes version name and code`() = runViewModelTest {
		val viewModel = AboutViewModel(
			purecipesConfig = fakePurecipesConfig(
				versionName = "1.2.3",
				versionCode = 42L,
			),
		)

		viewModel.versionText shouldBe "Version 1.2.3 (42)"
	}

	private fun fakePurecipesConfig(
		versionName: String = "0.0.0",
		versionCode: Long = 0L,
	): PurecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

		override fun versionName(): String = versionName

		override fun versionCode(): Long = versionCode
	}
}
