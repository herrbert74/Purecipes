package app.purecipes.store.screenshots

enum class PlayImageType(
	val directoryName: String,
	val apiValue: String,
) {
	PHONE(directoryName = "phone", apiValue = "phoneScreenshots"),
	TABLET_7(directoryName = "tablet-7", apiValue = "sevenInchScreenshots"),
	TABLET_10(directoryName = "tablet-10", apiValue = "tenInchScreenshots"),
	FEATURE_GRAPHIC(directoryName = "feature-graphic", apiValue = "featureGraphic"),
}
