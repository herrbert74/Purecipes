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
		val ownForms: Set<String>,
		val wordCount: Int,
	)

	private val trailingFormWords = setOf(
		"breast",
		"breasts",
		"bulb",
		"bulbs",
		"cheese",
		"chunk",
		"chunks",
		"clove",
		"cloves",
		"cube",
		"cubes",
		"fillet",
		"fillets",
		"floret",
		"florets",
		"half",
		"halves",
		"head",
		"heads",
		"kernel",
		"kernels",
		"leaf",
		"leaves",
		"leg",
		"legs",
		"piece",
		"pieces",
		"quarter",
		"quarters",
		"ring",
		"rings",
		"slice",
		"slices",
		"spear",
		"spears",
		"sprig",
		"sprigs",
		"stalk",
		"stalks",
		"strip",
		"strips",
		"thigh",
		"thighs",
		"wedge",
		"wedges",
		"wing",
		"wings",
	)

	private val nonDairyModifiers = setOf(
		"almond",
		"almonds",
		"apple",
		"cashew",
		"cashews",
		"cocoa",
		"coconut",
		"hazelnut",
		"hazelnuts",
		"oat",
		"oats",
		"peanut",
		"peanuts",
		"pistachio",
		"pistachios",
		"pumpkin",
		"rice",
		"sesame",
		"shea",
		"soy",
		"soya",
		"sunflower",
		"tartar",
	)

	private val singleWordDairyItems = setOf("Butter", "Cream", "Milk")
	private val genericUmbrellaItems = setOf("Cheese", "Fish")

	private val indexedCatalogue: List<IndexedCatalogueItem> by lazy {
		IngredientCatalogue.allItems
			.map { item ->
				val normalized = IngredientNameMatching.normalizeIngredientName(item)
				val singular = singularizeLastWord(normalized)
				val plural = pluralizeLastWord(singular)
				IndexedCatalogueItem(
					item = item,
					normalizedItem = normalized,
					ownForms = setOf(normalized, singular, plural).filter(String::isNotBlank).toSet(),
					wordCount = normalized.split(' ').size,
				)
			}
			.sortedWith(
				compareBy<IndexedCatalogueItem> { it.item in genericUmbrellaItems }
					.thenByDescending { it.wordCount }
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

	fun resolve(
		lookupName: String,
		pinnedPantryByLookupName: Map<String, String> = emptyMap(),
	): String {
		val pinned = pinnedPantryByLookupName[lookupName]?.trim().orEmpty()
		return pinned.ifEmpty { resolveSingleText(lookupName) }
	}

	private fun resolveSingleText(text: String): String {
		val trimmed = text.trim()
		if (trimmed.isEmpty()) {
			return ""
		}
		return resolveOwnCatalogueMatch(trimmed)
			?: IngredientLookup.resolveCatalogueIngredient(trimmed, sortedCatalogueNames)
			?: bestTrailingHeadMatch(trimmed)
			?: resolveDefaultPantryFallback(trimmed)
	}

	private fun resolveOwnCatalogueMatch(text: String): String? {
		val normalized = IngredientNameMatching.normalizeIngredientName(text)
		if (normalized.isBlank()) {
			return null
		}
		val singular = singularizeLastWord(normalized)
		return indexedCatalogue.firstOrNull { entry ->
			normalized in entry.ownForms || singular in entry.ownForms
		}?.item
	}

	private fun bestTrailingHeadMatch(text: String): String? {
		val normalizedText = IngredientNameMatching.normalizeIngredientText(text)
		if (normalizedText.isBlank()) {
			return null
		}
		val strippedText = stripTrailingFormWords(normalizedText)
		return if (strippedText != normalizedText) {
			findTrailingHeadMatch(strippedText) ?: findTrailingHeadMatch(normalizedText)
		} else {
			findTrailingHeadMatch(normalizedText)
		}
	}

	private fun findTrailingHeadMatch(phrase: String): String? {
		if (phrase.isBlank()) {
			return null
		}
		return indexedCatalogue.firstOrNull { entry ->
			matchesTrailingOwnForm(phrase, entry)
		}?.item
	}

	private fun stripTrailingFormWords(normalizedText: String): String {
		val words = normalizedText.split(' ').filter(String::isNotBlank).toMutableList()
		while (words.size > 1 && words.last() in trailingFormWords) {
			words.removeAt(words.lastIndex)
		}
		return words.joinToString(" ")
	}

	private fun matchesTrailingOwnForm(phrase: String, entry: IndexedCatalogueItem): Boolean {
		val matchedForm = entry.ownForms.firstOrNull { form ->
			phrase == form || phrase.endsWith(" $form")
		} ?: return false
		val hasNonDairyModifier = entry.item in singleWordDairyItems &&
			phrase != matchedForm &&
			phrase.removeSuffix(matchedForm).trim().split(' ').any { word -> word in nonDairyModifiers }
		return !hasNonDairyModifier
	}

	private fun resolveDefaultPantryFallback(text: String): String =
		defaultPantryFallbacks.firstOrNull { (phrase, _) ->
			val normalized = IngredientNameMatching.normalizeIngredientText(text)
			normalized == phrase || normalized.endsWith(" $phrase")
		}?.second.orEmpty()

	private const val IES_SUFFIX_LENGTH = 3
	private const val S_SUFFIX_LENGTH = 1
	private const val SES_DROP_LENGTH = 2

	private fun singularizeLastWord(value: String): String {
		val words = value.split(' ').filter(String::isNotBlank)
		if (words.isEmpty()) {
			return value
		}
		val lastWord = words.last()
		val singularLastWord = when {
			lastWord.endsWith("ies") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(IES_SUFFIX_LENGTH) + "y"

			lastWord.endsWith("ses") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(SES_DROP_LENGTH)

			lastWord.endsWith("oes") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(IES_SUFFIX_LENGTH) + "o"

			lastWord.endsWith("ves") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(IES_SUFFIX_LENGTH) + "f"

			lastWord.endsWith("s") && !lastWord.endsWith("ss") && lastWord.length > S_SUFFIX_LENGTH ->
				lastWord.dropLast(S_SUFFIX_LENGTH)

			else -> lastWord
		}
		return (words.dropLast(1) + singularLastWord).joinToString(separator = " ")
	}

	private fun pluralizeLastWord(value: String): String {
		val words = value.split(' ').filter(String::isNotBlank)
		if (words.isEmpty()) {
			return value
		}
		val lastWord = words.last()
		val pluralLastWord = when {
			lastWord.endsWith("y") && lastWord.length > 1 && lastWord[lastWord.length - 2] !in "aeiou" ->
				lastWord.dropLast(1) + "ies"

			lastWord.endsWith("s") || lastWord.endsWith("sh") || lastWord.endsWith("ch") || lastWord.endsWith("x") ->
				"${lastWord}es"

			else -> "${lastWord}s"
		}
		return (words.dropLast(1) + pluralLastWord).joinToString(separator = " ")
	}
}
