package app.purecipes.shared.domain.ingredient

object IngredientAliasGroups {

	val groups: List<Set<String>> = listOf(
		setOf("chile", "chili", "chilli"),
		setOf("cilantro", "coriander", "corainder"),
		setOf("yogurt", "yoghurt", "jogurt", "greek yogurt", "greek yoghurt"),
		setOf("creme fraiche", "crème fraiche", "crème fraîche"),
		setOf("petit pois", "pea", "peas"),
		setOf("bay leaf", "bay leaves", "bayleaves"),
		setOf("cornstarch", "cornflour"),
		setOf("corn", "sweetcorn"),
		setOf("ice", "water"),
		setOf("oil", "vegetable oil", "cooking spray"),
		setOf("prawn", "prawns", "shrimp"),
		setOf("scallion", "scallions", "spring onion", "green onion"),
		setOf("courgette", "zucchini"),
		setOf("aubergine", "eggplant"),
		setOf("arugula", "rocket"),
	)
}
