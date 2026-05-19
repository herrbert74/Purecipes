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
		val parseResult = FdcFoodDataJsonParser.parse(fdcJsonFile)
		val parsedFoods = parseResult.foods
		if (parsedFoods.isEmpty()) {
			return emptyResult(parseResult.dataset, seedCatalogueAliases)
		}

		if (dryRun) {
			return dryRunImport(
				dataset = parseResult.dataset,
				parsedFoods = parsedFoods,
				seedCatalogueAliases = seedCatalogueAliases,
			)
		}

		if (replaceExisting) {
			repository.replaceSeedData()
		}

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
				sourceMetadata = parseResult.dataset.sourceMetadata,
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

		return NutritionSeedImportResult(
			dataset = parseResult.dataset,
			foodsImported = foodsImported,
			foodsSkipped = foodsSkipped,
			measuresImported = measuresImported,
			catalogueAliasesImported = aliasResult.catalogueAliasesImported,
			extraAliasesImported = aliasResult.extraAliasesImported,
			unmatchedCatalogueNames = aliasResult.unmatchedCatalogueNames,
		)
	}

	private fun dryRunImport(
		dataset: FdcFoodDataset,
		parsedFoods: List<FdcFoundationFood>,
		seedCatalogueAliases: Boolean,
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
