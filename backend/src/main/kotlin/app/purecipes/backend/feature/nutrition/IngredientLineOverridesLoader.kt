package app.purecipes.backend.feature.nutrition

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines
import kotlin.io.path.writeText

internal data class IngredientLineOverrideKey(
	val sourceUrl: String,
	val rawText: String,
)

internal data class IngredientLineOverride(
	val sourceUrl: String,
	val rawText: String,
	val kind: String = "",
	val groupName: String = "",
	val quantity: String = "",
	val unit: String = "",
	val lookupName: String = "",
)

internal object IngredientLineOverridesLoader {

	const val LINE_OVERRIDES_FILE_NAME = "line-overrides.tsv"
	const val LINE_OVERRIDES_HEADER =
		"source_url\traw_text\tkind\tgroup_name\tquantity\tunit\tlookup_name"

	private val defaultCandidatePaths = listOf(
		Path.of("backend/data/ingredients/$LINE_OVERRIDES_FILE_NAME"),
		Path.of("data/ingredients/$LINE_OVERRIDES_FILE_NAME"),
	)

	private const val COL_SOURCE_URL = 0
	private const val COL_RAW_TEXT = 1
	private const val COL_KIND = 2
	private const val COL_GROUP_NAME = 3
	private const val COL_QUANTITY = 4
	private const val COL_UNIT = 5
	private const val COL_LOOKUP_NAME = 6

	fun resolveDefaultOverridesPath(): Path? =
		defaultCandidatePaths.firstOrNull { path -> path.exists() && path.isRegularFile() }

	fun loadDefaultOverrides(): Map<IngredientLineOverrideKey, IngredientLineOverride> =
		resolveDefaultOverridesPath()?.let(::loadOverrides).orEmpty()

	fun ensureFileExists(outputDir: Path): Path {
		Files.createDirectories(outputDir)
		val file = outputDir.resolve(LINE_OVERRIDES_FILE_NAME)
		if (!file.exists()) {
			file.writeText("$LINE_OVERRIDES_HEADER\n")
		}
		return file
	}

	fun loadOverrides(path: Path): Map<IngredientLineOverrideKey, IngredientLineOverride> {
		if (!path.exists() || !path.isRegularFile()) {
			return emptyMap()
		}
		val overrides = linkedMapOf<IngredientLineOverrideKey, IngredientLineOverride>()
		path.readLines()
			.drop(1)
			.filter(String::isNotBlank)
			.forEach { line ->
				val cols = line.split('\t')
				val sourceUrl = cols.getOrNull(COL_SOURCE_URL)?.trim().orEmpty()
				val rawText = cols.getOrNull(COL_RAW_TEXT)?.trim().orEmpty()
				if (sourceUrl.isNotEmpty() && rawText.isNotEmpty()) {
					val key = IngredientLineOverrideKey(sourceUrl = sourceUrl, rawText = rawText)
					overrides[key] = IngredientLineOverride(
						sourceUrl = sourceUrl,
						rawText = rawText,
						kind = cols.getOrNull(COL_KIND)?.trim()?.lowercase().orEmpty(),
						groupName = cols.getOrNull(COL_GROUP_NAME)?.trim().orEmpty(),
						quantity = cols.getOrNull(COL_QUANTITY)?.trim().orEmpty(),
						unit = cols.getOrNull(COL_UNIT)?.trim().orEmpty(),
						lookupName = cols.getOrNull(COL_LOOKUP_NAME)?.trim().orEmpty(),
					)
				}
			}
		return overrides
	}
}
