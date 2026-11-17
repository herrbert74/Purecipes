package app.purecipes.shared.ui.component

internal fun fontScaleForContentSizeCategory(
	category: String,
	fallback: Float = DEFAULT_FONT_SCALE,
): Float = CONTENT_SIZE_CATEGORY_TO_FONT_SCALE[category] ?: fallback

internal const val DEFAULT_FONT_SCALE = 1f

internal val CONTENT_SIZE_CATEGORY_TO_FONT_SCALE = mapOf(
	"UICTContentSizeCategoryXS" to 0.8f,
	"UICTContentSizeCategoryS" to 0.85f,
	"UICTContentSizeCategoryM" to 0.9f,
	"UICTContentSizeCategoryL" to 1f,
	"UICTContentSizeCategoryXL" to 1.1f,
	"UICTContentSizeCategoryXXL" to 1.2f,
	"UICTContentSizeCategoryXXXL" to 1.3f,
	"UICTContentSizeCategoryAccessibilityM" to 1.4f,
	"UICTContentSizeCategoryAccessibilityL" to 1.5f,
	"UICTContentSizeCategoryAccessibilityXL" to 1.6f,
	"UICTContentSizeCategoryAccessibilityXXL" to 1.7f,
	"UICTContentSizeCategoryAccessibilityXXXL" to 1.8f,
)
