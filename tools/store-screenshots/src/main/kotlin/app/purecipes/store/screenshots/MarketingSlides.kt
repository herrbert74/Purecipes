package app.purecipes.store.screenshots

object MarketingSlides {
	val all: List<MarketingSlide> = listOf(
		MarketingSlide(
			fileName = "01.png",
			title = "Find recipes you'll actually cook",
			subtitle = "Browse cuisines and discover your next favorite meal",
			rawScreenshotNamePrefix = "SearchMarketingScreenshot_search",
			theme = MarketingTheme.ROSE,
		),
		MarketingSlide(
			fileName = "02.png",
			title = "Everything before you start",
			subtitle = "Ingredients, timing, and one-tap cooking mode",
			rawScreenshotNamePrefix = "DetailsMarketingScreenshot_details",
			theme = MarketingTheme.GOLD,
		),
		MarketingSlide(
			fileName = "03.png",
			title = "Cook one step at a time",
			subtitle = "Swipe through clear instructions while you cook",
			rawScreenshotNamePrefix = "CookingMarketingScreenshot_cooking",
			theme = MarketingTheme.DEEP,
		),
		MarketingSlide(
			fileName = "04.png",
			title = "Your kitchen, your units",
			subtitle = "Filter by metric or imperial — no mental math",
			rawScreenshotNamePrefix = "UnitsMarketingScreenshot_units",
			theme = MarketingTheme.ROSE,
		),
	)
}
