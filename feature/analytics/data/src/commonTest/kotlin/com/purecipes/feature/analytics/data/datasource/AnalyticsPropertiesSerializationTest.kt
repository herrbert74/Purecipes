package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyticsPropertiesSerializationTest {

	@Test
	fun `serializes flat analytics properties to json`() {
		val properties = linkedMapOf(
			"query" to AnalyticsValue.TextValue("pasta"),
			"result_count" to AnalyticsValue.NumberValue(4),
			"is_favorite" to AnalyticsValue.BooleanValue(true),
		)

		assertEquals(
			expected = "{\"query\":\"pasta\",\"result_count\":4,\"is_favorite\":true}",
			actual = properties.toAnalyticsJson(),
		)
	}

	@Test
	fun `escapes quotes slashes and control characters in text values`() {
		val properties = mapOf(
			"text" to AnalyticsValue.TextValue("He said \"hi\"\\next\nline\tindent"),
		)

		assertEquals(
			expected = "{\"text\":\"He said \\\"hi\\\"\\\\next\\nline\\tindent\"}",
			actual = properties.toAnalyticsJson(),
		)
	}

	@Test
	fun `escapes quotes in property names`() {
		val properties = mapOf(
			"recipe\"id" to AnalyticsValue.NumberValue(42),
		)

		assertEquals(
			expected = "{\"recipe\\\"id\":42}",
			actual = properties.toAnalyticsJson(),
		)
	}
}