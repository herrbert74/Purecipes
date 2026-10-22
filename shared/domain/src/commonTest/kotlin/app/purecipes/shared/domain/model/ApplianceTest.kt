package app.purecipes.shared.domain.model

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ApplianceTest {

	@Test
	fun applianceSerializesToEnumName() {
		Json.encodeToString(Appliance.OVEN) shouldBe "\"OVEN\""
		Json.decodeFromString<Appliance>("\"OVEN\"") shouldBe Appliance.OVEN
	}

	@Test
	fun allAppliancesHaveNonEmptyDisplayName() {
		Appliance.entries.forEach { appliance ->
			appliance.displayName.isNotEmpty() shouldBe true
		}
	}
}
