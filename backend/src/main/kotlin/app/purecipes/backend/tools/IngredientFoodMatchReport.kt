package app.purecipes.backend.tools

import app.purecipes.backend.db.Db

private const val ARG_INCLUDE_SCRAPED = "--include-scraped"
private const val ARG_OUTPUT = "--output"

fun main(args: Array<String>) {
	val argList = args.toList()
	val outputPath = argList.windowed(size = 2, step = 1)
		.firstOrNull { it.first() == ARG_OUTPUT }
		?.last()
	val includeScraped = argList.contains(ARG_INCLUDE_SCRAPED)

	val reporter = IngredientFoodMatchReporter(Db.create().dataSource)
	val report = reporter.format(reporter.collect(), includeScrapedRecipes = includeScraped)
	println(report)

	if (outputPath != null) {
		java.io.File(outputPath).writeText(report)
		println("Saved report to $outputPath")
	}
}
