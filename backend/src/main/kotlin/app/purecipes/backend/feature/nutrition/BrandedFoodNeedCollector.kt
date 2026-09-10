package app.purecipes.backend.feature.nutrition

internal object BrandedFoodNeedCollector {

	fun collect(repository: NutritionFoodSeedRepository): Set<String> {
		val storedFoods = repository.loadFoodsForMatching()
		return repository.loadMeasurableUnmatchedParsedNames()
			.map { parsedName -> NutritionNameNormalizer.forLookup(parsedName) }
			.filter(::isUsefulBrandedQuery)
			.filter { query -> FdcFoodMatcher.matchAlias(query, storedFoods) == null }
			.toSet()
	}

	private fun isUsefulBrandedQuery(query: String): Boolean {
		val tokens = NutritionNameNormalizer.tokens(query)
		return query.isNotBlank() &&
			tokens.isNotEmpty() &&
			tokens.any { token -> token.any(Char::isLetter) } &&
			tokens !in rejectedQueryTokenSets
	}

	private val rejectedQueryTokenSets = setOf(
		listOf("taste"),
		listOf("teaspoon"),
		listOf("tablespoon"),
		listOf("thinly", "sliced"),
		listOf("with", "knife"),
	)
}
