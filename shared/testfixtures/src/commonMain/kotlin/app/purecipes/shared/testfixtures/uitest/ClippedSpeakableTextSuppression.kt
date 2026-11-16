package app.purecipes.shared.testfixtures.uitest

internal const val CLIPPED_SPEAKABLE_TEXT_MAX_HEIGHT_PX = 8

internal fun shouldSuppressClippedSpeakableText(
	checkClassName: String,
	visibleHeightPx: Int?,
): Boolean {
	return checkClassName == "SpeakableTextPresentCheck" &&
		visibleHeightPx != null &&
		visibleHeightPx < CLIPPED_SPEAKABLE_TEXT_MAX_HEIGHT_PX
}
