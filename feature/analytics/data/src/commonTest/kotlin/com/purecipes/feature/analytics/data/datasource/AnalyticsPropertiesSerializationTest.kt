package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AnalyticsPropertiesSerializationTest {

	@Test
	fun `serializes flat analytics properties to json`() {
		val properties = linkedMapOf(
			"query" to AnalyticsValue.TextValue("pasta"),
			"result_count" to AnalyticsValue.NumberValue(4),
			"is_favorite" to AnalyticsValue.BooleanValue(true),
		)

		properties.toAnalyticsJson() shouldBe """{"query":"pasta","result_count":4,"is_favorite":true}"""
	}

	@Test
	fun `escapes quotes slashes and control characters in text values`() {
		val properties = mapOf(
			"text" to AnalyticsValue.TextValue("""He said \"hi\"\\next\nline\tindent"""),
		)

		properties.toAnalyticsJson() shouldBe """{"text":"He said \\\"hi\\\"\\\\next\\nline\\tindent"}"""
	}

	@Test
	fun `escapes quotes in property names`() {
		val properties = mapOf(
			"recipe\"id" to AnalyticsValue.NumberValue(42),
		)

		properties.toAnalyticsJson() shouldBe """{"recipe\"id":42}"""
	}

	@Test
	fun `serializes empty map to empty json object`() {
		val properties = emptyMap<String, AnalyticsValue>()

		properties.toAnalyticsJson() shouldBe "{}"
	}

	@Test
	fun `escapes carriage return in text values`() {
		val properties = mapOf(
			"text" to AnalyticsValue.TextValue("line1\rline2"),
		)

		properties.toAnalyticsJson() shouldBe """{"text":"line1\rline2"}"""
	}
}
