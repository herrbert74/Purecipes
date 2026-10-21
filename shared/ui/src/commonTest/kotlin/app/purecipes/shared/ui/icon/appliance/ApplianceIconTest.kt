package app.purecipes.shared.ui.icon.appliance

import app.purecipes.shared.domain.model.Appliance
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ApplianceIconTest {

	@Test
	fun allAppliancesHaveDistinctIcons() {
		for (appliance in Appliance.entries) {
			val icon = appliance.icon
			icon.shouldNotBeNull()
			icon.defaultWidth.value shouldBe 24f
			icon.defaultHeight.value shouldBe 24f
		}
	}
}
