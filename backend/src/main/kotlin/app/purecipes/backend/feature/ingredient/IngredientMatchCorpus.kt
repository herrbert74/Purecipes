package app.purecipes.backend.feature.ingredient

data class IngredientMatchCorpus(
	val recipeIngredients: Map<Int, List<String>>,
	val vocabulary: Set<String>,
)
