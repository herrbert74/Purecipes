#!/usr/bin/env kotlin
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
@file:Import("EditorialCollections.kt")

fun assertEquals(description: String, expected: String?, actual: String?) {
	if (expected != actual) {
		error("FAIL [$description]: expected '$expected', got '$actual'")
	}
}

fun assertThrows(description: String, block: () -> Unit) {
	try {
		block()
		error("FAIL [$description]: expected an error")
	} catch (_: EditorialInputException) {
		Unit
	}
}

fun checkSlugs() {
	assertEquals("slug", "this-week", validateSlug("  this-week  "))
	assertEquals("single letter", "a", validateSlug("a"))
	assertEquals("digits", "week-2", validateSlug("week-2"))
	assertEquals("max length", "a".repeat(64), validateSlug("a".repeat(64)))
	assertThrows("uppercase") { validateSlug("This-Week") }
	assertThrows("leading hyphen") { validateSlug("-week") }
	assertThrows("trailing hyphen") { validateSlug("week-") }
	assertThrows("double hyphen") { validateSlug("this--week") }
	assertThrows("empty slug") { validateSlug(" ") }
	assertThrows("too long") { validateSlug("a".repeat(65)) }
}

fun checkSourceAndSort() {
	assertEquals("source", EDITORIAL_SOURCE, validateSource("Editorial"))
	assertEquals("query source", QUERY_SOURCE, validateSource("query"))
	assertThrows("bad source") { validateSource("saved") }
	assertEquals("sort", NEWEST_SORT, validateSort("Newest"))
	assertEquals("catalog sort", CATALOG_SORT, validateSort("catalog"))
	assertThrows("bad sort") { validateSort("rank") }
}

fun checkTimestamps() {
	assertEquals("date", "2026-10-12 00:00:00", normalizeTimestamp("2026-10-12"))
	assertEquals("date time", "2026-10-12 09:30:00", normalizeTimestamp(" 2026-10-12 09:30:00 "))
	assertEquals("bad month", null, normalizeTimestamp("2026-13-01"))
	assertEquals("iso t", null, normalizeTimestamp("2026-10-12T00:00:00"))
}

fun checkFilters() {
	val italian = """{"cuisines":["Italian"],"mealTypes":["DINNER"]}"""
	assertEquals("empty filter", "", validateFilterPayload("  "))
	assertEquals("object filter", "{}", validateFilterPayload("  {}  "))
	assertEquals("italian filter", italian, validateFilterPayload(italian))
	assertThrows("array filter") { validateFilterPayload("""["Italian"]""") }
	assertThrows("unknown field") { validateFilterPayload("""{"cuisine":["Italian"]}""") }
	assertThrows("number value") { validateFilterPayload("""{"mealTypes":[1]}""") }
	assertThrows("line break") { validateFilterPayload("{\n}") }
}

fun checkRenderedSql() {
	val rendered = renderEditorialSql(
		"WHERE title ILIKE :'pattern' ESCAPE '\\'' AND slug = :'slug' AND position = :position AND enabled = :enabled",
		mapOf(
			"pattern" to likeContains("100%"),
			"slug" to "o'brien",
			"position" to "4",
			"enabled" to "TRUE",
		),
	)
	assertEquals(
		"rendered sql",
		"WHERE title ILIKE '%100\\%%' ESCAPE '\\'' AND slug = 'o''brien' AND position = 4 AND enabled = TRUE",
		rendered,
	)
	val cast = renderEditorialSql("SELECT id::text", emptyMap())
	assertEquals("cast stays", "SELECT id::text", cast)
}

fun checkLikePattern() {
	assertEquals("percent", "%100\\%%", likeContains("100%"))
	assertEquals("underscore", "%a\\_b%", likeContains("a_b"))
	assertEquals("backslash", "%a\\\\b%", likeContains("a\\b"))
}

checkSlugs()
checkSourceAndSort()
checkTimestamps()
checkFilters()
checkRenderedSql()
checkLikePattern()
println("Editorial collection checks passed.")
