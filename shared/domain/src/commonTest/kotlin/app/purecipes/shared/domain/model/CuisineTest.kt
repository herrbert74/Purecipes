package app.purecipes.shared.domain.model

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlin.test.Test

class CuisineTest {

	@Test
	fun cuisineSerializerUsesDisplayName() {
		Json.encodeToString(Cuisine.ITALIAN) shouldBe "\"Italian\""
	}

	@Test
	fun cuisineParserAcceptsDisplayNameAndEnumName() {
		Cuisine.fromRawValue("Middle Eastern") shouldBe Cuisine.MIDDLE_EASTERN
		Cuisine.fromRawValue("middle_eastern") shouldBe Cuisine.MIDDLE_EASTERN
	}

	@Test
	fun cuisineParserReturnsNullForUnknownValue() {
		Cuisine.fromRawValue("Martian") shouldBe null
	}
}
