package app.purecipes.backend.feature.nutrition

import app.purecipes.shared.domain.model.IngredientCatalogue
import java.io.File

internal data class NutritionSeedImportResult(
	val dataset: FdcFoodDataset,
	val foodsImported: Int,
	val foodsSkipped: Int,
	val measuresImported: Int,
	val catalogueAliasesImported: Int,
	val extraAliasesImported: Int,
	val unmatchedCatalogueNames: List<String>,
	val foodsScanned: Int = foodsImported + foodsSkipped,
	val neededNameMatches: Map<String, String> = emptyMap(),
)

internal class NutritionSeedImporter(
	private val repository: NutritionFoodSeedRepository,
) {

	fun importFdcJson(
		fdcJsonFile: File,
		replaceExisting: Boolean,
		dryRun: Boolean,
		seedCatalogueAliases: Boolean,
	): NutritionSeedImportResult {
		val dataset = FdcFoodDataJsonParser.peekDataset(fdcJsonFile)
		if (dataset == FdcFoodDataset.BRANDED && replaceExisting) {
			error("Do not use replace when importing Branded Foods; that would delete Foundation and SR Legacy foods.")
		}
		if (dataset == FdcFoodDataset.SURVEY && replaceExisting) {
			error("Do not use replace when importing Survey foods; that would delete Foundation and SR Legacy foods.")
		}
		if (dataset == FdcFoodDataset.BRANDED && !dryRun) {
			repository.deleteBrandedFoods()
		}
		val neededQueries = if (dataset == FdcFoodDataset.BRANDED) {
			BrandedFoodNeedCollector.collect(repository)
		} else {
			null
		}
		if (dataset == FdcFoodDataset.BRANDED && neededQueries.isNullOrEmpty()) {
			return NutritionSeedImportResult(
				dataset = dataset,
				foodsImported = 0,
				foodsSkipped = 0,
				measuresImported = 0,
				catalogueAliasesImported = 0,
				extraAliasesImported = 0,
				unmatchedCatalogueNames = emptyList(),
				foodsScanned = 0,
			)
		}
		val parseResult = FdcFoodDataJsonParser.parse(fdcJsonFile, neededQueries)
		val parsedFoods = parseResult.foods
		val shouldSeedCatalogueAliases = seedCatalogueAliases && dataset != FdcFoodDataset.BRANDED
		return when {
			parsedFoods.isEmpty() && dataset != FdcFoodDataset.BRANDED ->
				emptyResult(parseResult.dataset, shouldSeedCatalogueAliases)

			dryRun -> dryRunImport(
				dataset = parseResult.dataset,
				parsedFoods = parsedFoods,
				seedCatalogueAliases = shouldSeedCatalogueAliases,
				foodsScanned = parseResult.foodsScanned,
				neededNameMatches = parseResult.neededNameMatches,
			)

			else -> importParsedFoods(
				dataset = parseResult.dataset,
				parsedFoods = parsedFoods,
				replaceExisting = replaceExisting,
				seedCatalogueAliases = shouldSeedCatalogueAliases,
				foodsScanned = parseResult.foodsScanned,
				neededNameMatches = parseResult.neededNameMatches,
			)
		}
	}

	private fun importParsedFoods(
		dataset: FdcFoodDataset,
		parsedFoods: List<FdcFoundationFood>,
		replaceExisting: Boolean,
		seedCatalogueAliases: Boolean,
		foodsScanned: Int,
		neededNameMatches: Map<String, String>,
	): NutritionSeedImportResult {
		if (replaceExisting) {
			repository.replaceSeedData()
		}
		repository.deleteUndeterminedMeasures()

		var foodsImported = 0
		var foodsSkipped = 0
		var measuresImported = 0

		parsedFoods.forEach { food ->
			val nutrients = food.nutrientsPer100g()
			if (nutrients == null) {
				foodsSkipped++
				return@forEach
			}
			val foodId = repository.upsertFood(
				food = food,
				nutrients = nutrients,
				sourceMetadata = dataset.sourceMetadata,
			)
			foodsImported++

			val measureNames = mutableSetOf<String>()
			food.portions.forEach { portion ->
				if (measureNames.add(portion.measureName)) {
					repository.upsertMeasure(foodId, portion.measureName, portion.gramsPerMeasure)
					measuresImported++
				}
			}
			NutritionSupplementalMeasures.measuresByFdcId[food.fdcId].orEmpty().forEach { supplementalMeasure ->
				if (measureNames.add(supplementalMeasure.measureName)) {
					repository.upsertMeasure(
						foodId = foodId,
						measureName = supplementalMeasure.measureName,
						gramsPerMeasure = supplementalMeasure.gramsPerMeasure,
					)
					measuresImported++
				}
			}
		}

		val aliasResult = if (seedCatalogueAliases) {
			seedCatalogueAliases(parsedFoods = parsedFoods, persist = true)
		} else {
			AliasSeedResult.empty()
		}
		if (dataset == FdcFoodDataset.BRANDED) {
			seedNeededNameAliases(parsedFoods = parsedFoods, neededNameMatches = neededNameMatches)
		}

		return NutritionSeedImportResult(
			dataset = dataset,
			foodsImported = foodsImported,
			foodsSkipped = foodsSkipped,
			measuresImported = measuresImported,
			catalogueAliasesImported = aliasResult.catalogueAliasesImported,
			extraAliasesImported = aliasResult.extraAliasesImported,
			unmatchedCatalogueNames = aliasResult.unmatchedCatalogueNames,
			foodsScanned = foodsScanned,
			neededNameMatches = neededNameMatches,
		)
	}

	private fun dryRunImport(
		dataset: FdcFoodDataset,
		parsedFoods: List<FdcFoundationFood>,
		seedCatalogueAliases: Boolean,
		foodsScanned: Int,
		neededNameMatches: Map<String, String>,
	): NutritionSeedImportResult {
		val foodsWithNutrients = parsedFoods.count { it.nutrientsPer100g() != null }
		val aliasResult = if (seedCatalogueAliases) {
			seedCatalogueAliases(parsedFoods = parsedFoods, persist = false)
		} else {
			AliasSeedResult.empty()
		}
		return NutritionSeedImportResult(
			dataset = dataset,
			foodsImported = foodsWithNutrients,
			foodsSkipped = parsedFoods.size - foodsWithNutrients,
			measuresImported = parsedFoods.sumOf { food ->
				food.portions.size + NutritionSupplementalMeasures.measuresByFdcId[food.fdcId].orEmpty().size
			},
			catalogueAliasesImported = aliasResult.catalogueAliasesImported,
			extraAliasesImported = aliasResult.extraAliasesImported,
			unmatchedCatalogueNames = aliasResult.unmatchedCatalogueNames,
			foodsScanned = foodsScanned,
			neededNameMatches = neededNameMatches,
		)
	}

	private fun seedCatalogueAliases(
		parsedFoods: List<FdcFoundationFood>,
		persist: Boolean,
	): AliasSeedResult {
		val foodsForMatching = FdcFoodMatchingSupport.mergeForMatching(
			storedFoods = repository.loadFoodsForMatching(),
			importedFoods = parsedFoods,
		)

		var catalogueAliasesImported = 0
		val unmatchedCatalogueNames = mutableListOf<String>()
		IngredientCatalogue.allItems.sorted().forEach { catalogueName ->
			val matchedFood = FdcFoodMatcher.matchCatalogueName(catalogueName, foodsForMatching)
			if (matchedFood == null) {
				unmatchedCatalogueNames += catalogueName
				return@forEach
			}
			if (persist) {
				val foodId = repository.findFoodId(matchedFood.sourceName, matchedFood.fdcId)
				if (foodId == null) {
					unmatchedCatalogueNames += catalogueName
					return@forEach
				}
				repository.upsertAlias(foodId = foodId, alias = catalogueName)
			}
			catalogueAliasesImported++
		}

		var extraAliasesImported = 0
		NutritionSeedAliases.aliases.forEach { seedAlias ->
			val matchedFood = FdcFoodMatcher.matchAlias(seedAlias.alias, foodsForMatching) ?: return@forEach
			if (persist) {
				val foodId = repository.findFoodId(matchedFood.sourceName, matchedFood.fdcId) ?: return@forEach
				repository.upsertAlias(foodId = foodId, alias = seedAlias.alias)
			}
			extraAliasesImported++
		}

		return AliasSeedResult(
			catalogueAliasesImported = catalogueAliasesImported,
			extraAliasesImported = extraAliasesImported,
			unmatchedCatalogueNames = unmatchedCatalogueNames,
		)
	}

	private fun seedNeededNameAliases(
		parsedFoods: List<FdcFoundationFood>,
		neededNameMatches: Map<String, String>,
	) {
		val foodByDescription = parsedFoods.associateBy { food -> food.description }
		neededNameMatches.forEach { (query, description) ->
			val matchedFood = foodByDescription[description] ?: return@forEach
			val foodId = repository.findFoodId(matchedFood.sourceName, matchedFood.fdcId) ?: return@forEach
			repository.upsertAlias(foodId = foodId, alias = query)
		}
	}

	private fun emptyResult(dataset: FdcFoodDataset, seedCatalogueAliases: Boolean): NutritionSeedImportResult =
		NutritionSeedImportResult(
			dataset = dataset,
			foodsImported = 0,
			foodsSkipped = 0,
			measuresImported = 0,
			catalogueAliasesImported = 0,
			extraAliasesImported = 0,
			unmatchedCatalogueNames = if (seedCatalogueAliases) {
				IngredientCatalogue.allItems.sorted()
			} else {
				emptyList()
			},
		)

	private data class AliasSeedResult(
		val catalogueAliasesImported: Int,
		val extraAliasesImported: Int,
		val unmatchedCatalogueNames: List<String>,
	) {

		companion object {

			fun empty(): AliasSeedResult =
				AliasSeedResult(
					catalogueAliasesImported = 0,
					extraAliasesImported = 0,
					unmatchedCatalogueNames = emptyList(),
				)
		}
	}
}
