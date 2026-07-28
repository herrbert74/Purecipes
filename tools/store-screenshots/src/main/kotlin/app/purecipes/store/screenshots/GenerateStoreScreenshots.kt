package app.purecipes.store.screenshots

import java.io.File

private const val DEFAULT_LANGUAGE = "en-GB"
private const val RAW_REFERENCE_DIR =
	"app/src/screenshotTestDebug/reference/app/purecipes/marketing/MarketingScreenshotsKt"

fun main(args: Array<String>) {
	val options = parseArgs(args)
	val fontsDirectory = File(options.projectRoot, "tools/store-screenshots/src/main/resources/fonts")
	require(fontsDirectory.isDirectory) {
		"Fonts directory missing: ${fontsDirectory.absolutePath}"
	}

	val outputRoot = options.outputDirectory
	var renderedCount = 0
	for (slide in MarketingSlides.all) {
		val rawFile = resolveRawScreenshot(options.projectRoot, slide.rawScreenshotNamePrefix)
		val screenshot = StoreScreenshotRenderer.loadScreenshot(rawFile)
		for (size in StoreOutputSize.entries) {
			if (size == StoreOutputSize.FEATURE_GRAPHIC && slide != MarketingSlides.all.first()) {
				continue
			}
			val outputFile = File(
				outputRoot,
				"${size.directoryName}/${options.language}/${slide.fileName}",
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

internal fun resolveRawScreenshot(projectRoot: File, namePrefix: String): File {
	val directory = File(projectRoot, RAW_REFERENCE_DIR)
	require(directory.isDirectory) {
		"Raw screenshot directory missing: ${directory.absolutePath}. " +
			"Run ./gradlew :app:updateDebugScreenshotTest first."
	}
	val matches = directory.listFiles()
		?.filter { file ->
			file.isFile &&
				file.extension.equals("png", ignoreCase = true) &&
				file.name.startsWith(namePrefix)
		}
		.orEmpty()
		.sortedBy { it.name }
	require(matches.size == 1) {
		"Expected exactly one raw screenshot starting with '$namePrefix' in ${directory.absolutePath}, " +
			"found ${matches.size}: ${matches.map { it.name }}"
	}
	return matches.first()
}

private data class CliOptions(
	val projectRoot: File,
	val outputDirectory: File,
	val language: String,
)

private fun parseArgs(args: Array<String>): CliOptions {
	var projectRoot = File(".").canonicalFile
	var outputDirectory = File(projectRoot, "store-listing")
	var language = DEFAULT_LANGUAGE
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

			"--language" -> {
				language = requireNext(args, index, arg)
				index += 2
			}

			else -> error("Unknown argument: $arg")
		}
	}
	return CliOptions(
		projectRoot = projectRoot,
		outputDirectory = outputDirectory,
		language = language,
	)
}

private fun requireNext(args: Array<String>, index: Int, flag: String): String {
	require(index + 1 < args.size) { "Missing value for $flag" }
	return args[index + 1]
}
