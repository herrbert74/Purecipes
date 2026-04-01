package com.purecipes.shared.domain.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CuisineTest {

	@Test
	fun cuisineSerializerUsesDisplayName() {
		assertEquals("\"Italian\"", Json.encodeToString(Cuisine.ITALIAN))
	}

	@Test
	fun cuisineParserAcceptsDisplayNameAndEnumName() {
		assertEquals(Cuisine.MIDDLE_EASTERN, Cuisine.fromRawValue("Middle Eastern"))
		assertEquals(Cuisine.MIDDLE_EASTERN, Cuisine.fromRawValue("middle_eastern"))
	}

	@Test
	fun cuisineParserReturnsNullForUnknownValue() {
		assertNull(Cuisine.fromRawValue("Martian"))
	}
}
