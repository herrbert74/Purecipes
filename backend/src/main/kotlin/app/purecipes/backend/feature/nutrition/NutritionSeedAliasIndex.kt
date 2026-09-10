package app.purecipes.backend.feature.nutrition

internal object NutritionSeedAliasIndex {

	fun merge(
		foods: Collection<NutritionFoodRecord>,
		storedAliases: Map<String, Int>,
	): Map<String, Int> {
		val foodIdByDisplayName = foods.associate { food -> food.displayName to food.id }
		val seedAliases = buildMap {
			NutritionSeedAliases.aliases.forEach { seedAlias ->
				val foodId = seedAlias.preferredDescriptions.firstNotNullOfOrNull { description ->
					foodIdByDisplayName[description]
				} ?: return@forEach
				val key = NutritionNameNormalizer.normalize(seedAlias.alias)
				if (key.isNotBlank() && key !in this) {
					put(key, foodId)
				}
			}
		}
		return storedAliases + seedAliases
	}
}
