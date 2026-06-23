package app.purecipes.backend.feature.ingredient

internal object IngredientMatchRepositorySql {

	const val RECIPE_INGREDIENT_LINES_SQL = """
		SELECT r.id AS recipe_id, i.ingredient
		FROM recipes r
		JOIN ingredient_groups ig ON ig.recipe_id = r.id
		JOIN ingredients i ON i.ingredient_group_id = ig.id
		WHERE i.ingredient IS NOT NULL
		ORDER BY r.id, ig.order_index, i.order_index
	"""
}
