package app.purecipes.store.screenshots

enum class StoreOutputSize(
	val directoryName: String,
	val widthPx: Int,
	val heightPx: Int,
) {
	PHONE(directoryName = "phone", widthPx = 1080, heightPx = 1920),
	TABLET_7(directoryName = "tablet-7", widthPx = 1200, heightPx = 1920),
	TABLET_10(directoryName = "tablet-10", widthPx = 1600, heightPx = 2560),
	FEATURE_GRAPHIC(directoryName = "feature-graphic", widthPx = 1024, heightPx = 500),
}
