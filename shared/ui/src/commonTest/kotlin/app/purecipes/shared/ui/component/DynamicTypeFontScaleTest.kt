package app.purecipes.shared.ui.component

import kotlin.test.Test
import kotlin.test.assertEquals

class DynamicTypeFontScaleTest {

	@Test
	fun mapsStandardAndAccessibilityContentSizeCategories() {
		assertEquals(0.8f, fontScaleForContentSizeCategory("UICTContentSizeCategoryXS"))
		assertEquals(1f, fontScaleForContentSizeCategory("UICTContentSizeCategoryL"))
		assertEquals(1.8f, fontScaleForContentSizeCategory("UICTContentSizeCategoryAccessibilityXXXL"))
	}

	@Test
	fun fallsBackToDefaultForUnknownCategory() {
		assertEquals(DEFAULT_FONT_SCALE, fontScaleForContentSizeCategory("unknown"))
	}
}
