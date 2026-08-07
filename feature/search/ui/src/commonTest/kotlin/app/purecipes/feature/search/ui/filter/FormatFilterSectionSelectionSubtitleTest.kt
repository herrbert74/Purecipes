package app.purecipes.feature.search.ui.filter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FormatFilterSectionSelectionSubtitleTest {

	@Test
	fun emptyLabelsReturnsNull() {
		assertNull(formatFilterSectionSelectionSubtitle(emptyList()))
	}

	@Test
	fun upToThreeLabelsAreJoined() {
		assertEquals("A", formatFilterSectionSelectionSubtitle(listOf("A")))
		assertEquals("A, B", formatFilterSectionSelectionSubtitle(listOf("A", "B")))
		assertEquals("A, B, C", formatFilterSectionSelectionSubtitle(listOf("A", "B", "C")))
	}

	@Test
	fun moreThanThreeShowsTwoAndOverflowCount() {
		assertEquals(
			"A, B +2",
			formatFilterSectionSelectionSubtitle(listOf("A", "B", "C", "D")),
		)
		assertEquals(
			"A, B +3",
			formatFilterSectionSelectionSubtitle(listOf("A", "B", "C", "D", "E")),
		)
	}
}
