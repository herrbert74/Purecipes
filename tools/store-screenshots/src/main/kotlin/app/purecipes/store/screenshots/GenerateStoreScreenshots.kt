package app.purecipes.store.screenshots

import java.io.File

private const val DEFAULT_LOCALE = "en-US"

fun main(args: Array<String>) {
	val options = parseArgs(args)
	val fontsDirectory = File(options.projectRoot, "tools/store-screenshots/src/main/resources/fonts")
	require(fontsDirectory.isDirectory) {
		"Fonts directory missing: ${fontsDirectory.absolutePath}"
	}

	val outputRoot = options.outputDirectory
	var renderedCount = 0
	for (slide in MarketingSlides.all) {
		val rawFile = File(options.projectRoot, slide.rawScreenshotRelativePath)
		val screenshot = StoreScreenshotRenderer.loadScreenshot(rawFile)
		for (size in StoreOutputSize.entries) {
			val outputFile = File(
				outputRoot,
				"${size.directoryName}/$DEFAULT_LOCALE/${slide.fileName}",
			)
			StoreScreenshotRenderer.render(
				slide = slide,
				screenshot = screenshot,
				outputSize = size,
				fontsDirectory = fontsDirectory,
				outputFile = outputFile,
			)
			renderedCount += 1
			println("Wrote ${outputFile.relativeTo(options.projectRoot)}")
		}
	}
	println("Generated $renderedCount store screenshots under ${outputRoot.absolutePath}")
}

private data class CliOptions(
	val projectRoot: File,
	val outputDirectory: File,
)

private fun parseArgs(args: Array<String>): CliOptions {
	var projectRoot = File(".").canonicalFile
	var outputDirectory = File(projectRoot, "store-listing")
	var index = 0
	while (index < args.size) {
		when (val arg = args[index]) {
			"--project-root" -> {
				projectRoot = File(requireNext(args, index, arg)).canonicalFile
				index += 2
			}

			"--output" -> {
				outputDirectory = File(requireNext(args, index, arg))
				index += 2
			}

			else -> error("Unknown argument: $arg")
		}
	}
	return CliOptions(projectRoot = projectRoot, outputDirectory = outputDirectory)
}

private fun requireNext(args: Array<String>, index: Int, flag: String): String {
	require(index + 1 < args.size) { "Missing value for $flag" }
	return args[index + 1]
}
