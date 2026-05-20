package app.purecipes.backend.feature.nutrition

internal data class NutritionSeedAlias(
	val alias: String,
	val preferredDescription: String,
)

internal object NutritionSeedAliases {
	private const val EGG_DESCRIPTION = "Eggs, Grade A, Large, egg whole"
	private const val SUGAR_DESCRIPTION = "Sugars, granulated"
	private const val FLOUR_DESCRIPTION = "Flour, wheat, all-purpose, enriched, unbleached"
	private const val CHICKEN_BREAST_DESCRIPTION = "Chicken, breast, boneless, skinless, raw"
	private const val GARLIC_DESCRIPTION = "Garlic, raw"

	val aliases: List<NutritionSeedAlias> = listOf(
		NutritionSeedAlias(alias = "egg", preferredDescription = EGG_DESCRIPTION),
		NutritionSeedAlias(alias = "eggs", preferredDescription = EGG_DESCRIPTION),
		NutritionSeedAlias(alias = "caster sugar", preferredDescription = SUGAR_DESCRIPTION),
		NutritionSeedAlias(alias = "granulated sugar", preferredDescription = SUGAR_DESCRIPTION),
		NutritionSeedAlias(alias = "plain flour", preferredDescription = FLOUR_DESCRIPTION),
		NutritionSeedAlias(alias = "all-purpose flour", preferredDescription = FLOUR_DESCRIPTION),
		NutritionSeedAlias(alias = "chicken breast", preferredDescription = CHICKEN_BREAST_DESCRIPTION),
		NutritionSeedAlias(alias = "clove garlic", preferredDescription = GARLIC_DESCRIPTION),
		NutritionSeedAlias(alias = "garlic clove", preferredDescription = GARLIC_DESCRIPTION),
	)
}
