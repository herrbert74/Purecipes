package app.purecipes.backend.feature.nutrition

internal object FdcFoodMatcher {

	fun matchCatalogueName(
		catalogueName: String,
		foods: List<FdcFoundationFood>,
	): FdcFoundationFood? {
		val preferredDescription = NutritionSeedOverrides.preferredDescriptionByCatalogueName[catalogueName]
		if (preferredDescription != null) {
			return foods.firstOrNull { it.description == preferredDescription }
		}
		return matchSearchTerm(catalogueName, foods)
	}

	fun matchAlias(
		alias: String,
		foods: List<FdcFoundationFood>,
	): FdcFoundationFood? {
		val normalizedAlias = NutritionNameNormalizer.normalize(alias)
		val preferredDescriptions = NutritionSeedAliases.aliases
			.firstOrNull { NutritionNameNormalizer.normalize(it.alias) == normalizedAlias }
			?.preferredDescriptions
		if (preferredDescriptions != null) {
			val preferredFood = preferredDescriptions.firstNotNullOfOrNull { description ->
				foods.firstOrNull { food -> food.description == description }
			}
			if (preferredFood != null) {
				return preferredFood
			}
		}
		return matchSearchTerm(alias, foods)
	}

	private fun matchSearchTerm(
		searchTerm: String,
		foods: List<FdcFoundationFood>,
	): FdcFoundationFood? {
		val queryNormalized = NutritionNameNormalizer.forLookup(searchTerm)
		if (queryNormalized.isBlank()) {
			return null
		}
		return foods
			.mapNotNull { food ->
				val scored = NutritionFoodNameScorer.score(queryNormalized, food.normalizedDescription)
					?: return@mapNotNull null
				scored to food
			}
			.maxWithOrNull(
				compareByDescending<Pair<NutritionFoodNameScore, FdcFoundationFood>> { it.first.score }
					.thenBy { FdcFoodMatchingSupport.sourcePriority(it.second.sourceName) }
					.thenBy { it.first.extraTokenCount }
					.thenBy { it.second.description.length },
			)
			?.second
	}
}
