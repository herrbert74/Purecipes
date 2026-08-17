package app.purecipes.store.screenshots

data class MarketingSlide(
	val fileName: String,
	val title: String,
	val subtitle: String?,
	val rawScreenshotNamePrefix: String,
	val theme: MarketingTheme = MarketingTheme.ROSE,
)
