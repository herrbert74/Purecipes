package app.purecipes.backend.feature.nutrition

internal data class NutritionSeedAlias(
	val alias: String,
	val preferredDescriptions: List<String>,
) {

	constructor(alias: String, preferredDescription: String, vararg fallbacks: String) : this(
		alias = alias,
		preferredDescriptions = listOf(preferredDescription, *fallbacks),
	)
}

internal object NutritionSeedAliases {

	val aliases: List<NutritionSeedAlias> =
		NutritionSeedAliasesPart1.aliases + NutritionSeedAliasesPart2.aliases +
			NutritionSeedAliasesPart3.aliases + NutritionSeedAliasesPart4.aliases
}
