package app.purecipes.backend.tools

import app.purecipes.backend.db.Db

private const val ARG_OUTPUT = "--output"

fun main(args: Array<String>) {
	val outputPath = args.toList().windowed(size = 2, step = 1)
		.firstOrNull { it.first() == ARG_OUTPUT }
		?.last()

	val reporter = IngredientFoodMatchReporter(Db.create().dataSource)
	val report = reporter.format(reporter.collect())
	println(report)

	if (outputPath != null) {
		java.io.File(outputPath).writeText(report)
		println("Saved report to $outputPath")
	}
}
