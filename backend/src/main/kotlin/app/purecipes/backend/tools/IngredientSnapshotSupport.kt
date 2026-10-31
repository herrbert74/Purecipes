package app.purecipes.backend.tools

import app.purecipes.backend.feature.nutrition.NutritionNameNormalizer
import app.purecipes.backend.feature.search.IngredientVocabulary
import app.purecipes.shared.domain.ingredient.IngredientLookup
import app.purecipes.shared.domain.ingredient.IngredientNameMatching
import app.purecipes.shared.domain.model.IngredientCatalogue

internal data class IngredientNameSnapshotRow(
	val lookupName: String,
	val status: String,
	val pantryItem: String,
	val fdcSource: String,
	val fdcId: String,
	val fdcDescription: String,
	val matchSource: String,
	val lineCount: Int,
)

internal data class IngredientLineSnapshotRow(
	val sourceUrl: String,
	val groupIndex: Int,
	val lineIndex: Int,
	val rawText: String,
	val kind: String,
	val groupName: String,
	val quantity: String,
	val unit: String,
	val lookupName: String,
	val requirement: String,
	val alternativeGroupKey: String,
) {

	fun key(): IngredientLineKey = IngredientLineKey(
		sourceUrl = sourceUrl,
		groupIndex = groupIndex,
		lineIndex = lineIndex,
	)
}

internal data class IngredientLineKey(
	val sourceUrl: String,
	val groupIndex: Int,
	val lineIndex: Int,
) : Comparable<IngredientLineKey> {

	override fun compareTo(other: IngredientLineKey): Int =
		compareValuesBy(
			this,
			other,
			IngredientLineKey::sourceUrl,
			IngredientLineKey::groupIndex,
			IngredientLineKey::lineIndex
		)
}

internal data class IngredientSnapshotData(
	val names: List<IngredientNameSnapshotRow>,
	val lines: List<IngredientLineSnapshotRow>,
)

internal data class IngredientNameChange(
	val before: IngredientNameSnapshotRow,
	val after: IngredientNameSnapshotRow,
)

internal data class IngredientLineChange(
	val before: IngredientLineSnapshotRow,
	val after: IngredientLineSnapshotRow,
)

internal data class IngredientSnapshotDiff(
	val addedNames: List<IngredientNameSnapshotRow>,
	val removedNames: List<IngredientNameSnapshotRow>,
	val changedNames: List<IngredientNameChange>,
	val addedLines: List<IngredientLineSnapshotRow>,
	val removedLines: List<IngredientLineSnapshotRow>,
	val changedLines: List<IngredientLineChange>,
) {

	val isEmpty: Boolean
		get() = addedNames.isEmpty() &&
			removedNames.isEmpty() &&
			changedNames.isEmpty() &&
			addedLines.isEmpty() &&
			removedLines.isEmpty() &&
			changedLines.isEmpty()

	fun formatSummary(maxSamples: Int = DEFAULT_MAX_SAMPLES): String = buildString {
		appendLine("Ingredient snapshot diff")
		appendLine(
			"Names: +${addedNames.size} added, -${removedNames.size} removed, ~${changedNames.size} changed",
		)
		appendLine(
			"Lines: +${addedLines.size} added, -${removedLines.size} removed, ~${changedLines.size} changed",
		)
		appendNameChanges(maxSamples)
		appendLineChanges(maxSamples)
	}

	private fun StringBuilder.appendNameChanges(maxSamples: Int) {
		if (addedNames.isNotEmpty()) {
			appendLine()
			appendLine("Added names (${addedNames.size}):")
			addedNames.take(maxSamples).forEach { row ->
				val foodSummary = "${row.fdcSource}:${row.fdcId} (${row.fdcDescription})"
				appendLine("  + ${row.lookupName} -> pantry=${row.pantryItem} fdc=$foodSummary")
			}
		}
		if (removedNames.isNotEmpty()) {
			appendLine()
			appendLine("Removed names (${removedNames.size}):")
			removedNames.take(maxSamples).forEach { row ->
				appendLine("  - ${row.lookupName}")
			}
		}
		if (changedNames.isNotEmpty()) {
			appendLine()
			appendLine("Changed names (${changedNames.size}):")
			changedNames.take(maxSamples).forEach { change ->
				val beforeSummary = formatNameState(change.before)
				val afterSummary = formatNameState(change.after)
				appendLine("  ~ ${change.after.lookupName}: $beforeSummary -> $afterSummary")
			}
		}
	}

	private fun formatNameState(row: IngredientNameSnapshotRow): String =
		"[pantry=${row.pantryItem} fdc=${row.fdcSource}:${row.fdcId} " +
			"(${row.fdcDescription}) src=${row.matchSource} n=${row.lineCount}]"

	private fun StringBuilder.appendLineChanges(maxSamples: Int) {
		if (changedLines.isNotEmpty()) {
			appendLine()
			appendLine("Changed lines (${changedLines.size}):")
			changedLines.take(maxSamples).forEach { change ->
				val b = change.before
				val a = change.after
				appendLine(
					"  ~ ${a.sourceUrl} [${a.groupIndex}:${a.lineIndex}] \"${a.rawText}\": " +
						"[kind=${b.kind} qty=${b.quantity} unit=${b.unit} name=${b.lookupName}] -> " +
						"[kind=${a.kind} qty=${a.quantity} unit=${a.unit} name=${a.lookupName}]",
				)
			}
		}
	}

	private companion object {

		const val DEFAULT_MAX_SAMPLES = 25
	}
}

internal object IngredientSnapshotClassifier {

	const val KIND_INGREDIENT = "ingredient"
	const val KIND_HEADING = "heading"
	const val KIND_NOTE = "note"
	const val KIND_EQUIPMENT = "equipment"

	fun classifyLineKind(rawText: String, parsedName: String?): String {
		val labelSource = parsedName?.trim().orEmpty().ifBlank { rawText }
		return when {
			IngredientVocabulary.isEquipmentLine(rawText) -> KIND_EQUIPMENT
			IngredientVocabulary.isSectionHeadingLine(rawText) -> KIND_HEADING
			IngredientVocabulary.isIgnorableIngredientLine(rawText) -> KIND_NOTE
			!NutritionNameNormalizer.hasMeaningfulFoodName(labelSource) -> KIND_NOTE
			else -> KIND_INGREDIENT
		}
	}
}

internal object PantryItemResolver {

	private data class IndexedCatalogueItem(
		val item: String,
		val normalizedItem: String,
		val variants: List<String>,
	)

	private data class CandidateMatch(
		val item: String,
		val isExactPhrase: Boolean,
		val variantWordCount: Int,
		val variantLength: Int,
		val itemLength: Int,
	)

	private val indexedCatalogue: List<IndexedCatalogueItem> by lazy {
		IngredientCatalogue.allItems
			.map { item ->
				IndexedCatalogueItem(
					item = item,
					normalizedItem = IngredientNameMatching.normalizeIngredientName(item),
					variants = IngredientNameMatching.ingredientVariants(item).toList(),
				)
			}
			.sortedWith(
				compareByDescending<IndexedCatalogueItem> { it.normalizedItem.split(' ').size }
					.thenByDescending { it.normalizedItem.length }
					.thenBy { it.item },
			)
	}

	private val sortedCatalogueNames: List<String> by lazy {
		indexedCatalogue.map(IndexedCatalogueItem::item)
	}

	private val defaultPantryFallbacks: List<Pair<String, String>> = listOf(
		"bicarbonate of soda" to "Baking Soda",
		"baking soda" to "Baking Soda",
		"vegetable oil" to "Vegetable Oil",
		"water" to "Water",
		"salt" to "Salt",
	)

	fun resolve(lookupName: String, fallbackNames: Collection<String> = emptyList()): String {
		val direct = resolveSingleText(lookupName)
		if (direct.isNotEmpty()) {
			return direct
		}
		return fallbackNames
			.asSequence()
			.map(::resolveSingleText)
			.filter(String::isNotEmpty)
			.groupingBy { it }
			.eachCount()
			.entries
			.minWithOrNull(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
			?.key
			.orEmpty()
	}

	private fun resolveSingleText(text: String): String {
		val trimmed = text.trim()
		return if (trimmed.isEmpty()) {
			""
		} else {
			IngredientLookup.resolveCatalogueIngredient(trimmed, sortedCatalogueNames)
				?: bestPhraseMatch(trimmed)
				?: resolveDefaultPantryFallback(trimmed)
		}
	}

	private fun bestPhraseMatch(text: String): String? {
		val normalizedText = IngredientNameMatching.normalizeIngredientText(text)
		if (normalizedText.isBlank()) {
			return null
		}
		val paddedText = " $normalizedText "
		val candidates = indexedCatalogue.mapNotNull { entry ->
			if (!IngredientNameMatching.matchesAnyIngredient(text, listOf(entry.item))) {
				return@mapNotNull null
			}
			val bestVariant = entry.variants
				.filter { variant -> IngredientNameMatching.matchesAnyIngredient(text, listOf(variant)) }
				.maxWithOrNull(
					compareBy<String> { variant -> paddedText.contains(" $variant ") }
						.thenBy { variant -> variant.split(' ').size }
						.thenBy { variant -> variant.length },
				) ?: entry.normalizedItem
			val isExactPhrase = paddedText.contains(" $bestVariant ")
			CandidateMatch(
				item = entry.item,
				isExactPhrase = isExactPhrase,
				variantWordCount = bestVariant.split(' ').size,
				variantLength = bestVariant.length,
				itemLength = entry.normalizedItem.length,
			)
		}
		return candidates.minWithOrNull(
			compareByDescending<CandidateMatch> { it.isExactPhrase }
				.thenByDescending { it.variantWordCount }
				.thenByDescending { it.variantLength }
				.thenByDescending { it.itemLength }
				.thenBy { it.item },
		)?.item
	}

	private fun resolveDefaultPantryFallback(text: String): String =
		defaultPantryFallbacks.firstOrNull { (phrase, _) ->
			IngredientNameMatching.matchesAnyIngredient(text, listOf(phrase))
		}?.second.orEmpty()
}
