package app.purecipes.backend.feature.recipe

import kotlin.text.RegexOption

internal object RecipeRepositorySql {

	const val SEARCH_WITH_FILTERS_MAX_LIMIT = 200

	const val FIRST_PARAMETER_INDEX = 1

	const val SECOND_PARAMETER_INDEX = 2

	const val THIRD_PARAMETER_INDEX = 3

	const val FOURTH_PARAMETER_INDEX = 4

	const val FIFTH_PARAMETER_INDEX = 5

	const val SIXTH_PARAMETER_INDEX = 6

	const val SEVENTH_PARAMETER_INDEX = 7

	const val EIGHTH_PARAMETER_INDEX = 8

	const val NINTH_PARAMETER_INDEX = 9

	const val ADD_FAVORITE_SQL = """
		INSERT INTO favorites (user_id, recipe_id)
		VALUES (?, ?)
		ON CONFLICT (user_id, recipe_id) DO NOTHING
	"""

	const val FAVORITES_COUNT_SQL = """
		SELECT COUNT(*)
		FROM favorites
		WHERE user_id = ?
	"""

	const val FAVORITES_PAGE_SQL = """
		SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time, r.measurement_system
		FROM favorites f
		INNER JOIN recipes r ON r.id = f.recipe_id
		WHERE f.user_id = ?
		ORDER BY f.created_at DESC
		LIMIT ? OFFSET ?
	"""

	const val REMOVE_COOKBOOK_RECIPES_FOR_USER_RECIPE_SQL = """
		DELETE FROM cookbook_recipes
		WHERE cookbook_id IN (
			SELECT id
			FROM cookbooks
			WHERE user_id = ?
		)
			AND recipe_id = ?
	"""

	const val CREATED_RECIPES_SQL = """
		SELECT id, title, description, instructions, total_time, yields, image_url, cuisine,
		       meal_type, difficulty, cooking_method, calorie_range, dietary_preferences, tags, measurement_system
		FROM recipes
		WHERE created_by_user_id = ?
		ORDER BY created_at DESC, id DESC
	"""

	const val IS_FAVORITE_SQL = """
		SELECT EXISTS(
			SELECT 1
			FROM favorites
			WHERE user_id = ?
				AND recipe_id = ?
		) AS is_favorite
	"""

	const val RECIPE_SQL = """
		SELECT id, title, description, instructions, total_time, yields, image_url, cuisine,
		       meal_type, difficulty, cooking_method, calorie_range, dietary_preferences, tags, measurement_system
		FROM recipes
		WHERE id = ?
	"""

	const val CREATE_RECIPE_SQL = """
		INSERT INTO recipes (
			title, description, instructions, total_time, yields, image_url, cuisine, measurement_system, created_by_user_id
		)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
	"""

	const val UPDATE_RECIPE_SQL = """
		UPDATE recipes
		SET title = ?,
			description = ?,
			instructions = ?,
			total_time = ?,
			yields = ?,
			image_url = ?,
			cuisine = ?,
			measurement_system = ?
		WHERE id = ?
	"""

	const val RECIPE_EXISTS_SQL = """
		SELECT 1
		FROM recipes
		WHERE id = ?
	"""

	const val RECIPE_OWNED_BY_USER_SQL = """
		SELECT 1
		FROM recipes
		WHERE id = ?
			AND created_by_user_id = ?
	"""

	const val REMOVE_FAVORITE_SQL = """
		DELETE FROM favorites
		WHERE user_id = ?
			AND recipe_id = ?
	"""

	const val INGREDIENT_GROUPS_SQL = """
		SELECT g.id AS group_id, g.name AS group_name, i.ingredient AS ingredient, i.requirement AS requirement
		FROM ingredient_groups g
		LEFT JOIN ingredients i ON i.ingredient_group_id = g.id
		WHERE g.recipe_id = ?
		ORDER BY g.order_index ASC, i.order_index ASC
	"""

	const val STEPS_SQL = """
		SELECT step
		FROM instruction_steps
		WHERE recipe_id = ?
		ORDER BY order_index ASC
	"""

	const val DELETE_INGREDIENTS_FOR_RECIPE_SQL = """
		DELETE FROM ingredients
		WHERE ingredient_group_id IN (
			SELECT id
			FROM ingredient_groups
			WHERE recipe_id = ?
		)
	"""

	const val DELETE_INGREDIENT_GROUPS_SQL = """
		DELETE FROM ingredient_groups
		WHERE recipe_id = ?
	"""

	const val DELETE_INSTRUCTION_STEPS_SQL = """
		DELETE FROM instruction_steps
		WHERE recipe_id = ?
	"""

	const val CREATE_INGREDIENT_GROUP_SQL = """
		INSERT INTO ingredient_groups (recipe_id, name, order_index)
		VALUES (?, ?, ?)
	"""

	const val CREATE_INGREDIENT_SQL = """
		INSERT INTO ingredients (ingredient_group_id, ingredient, order_index, requirement)
		VALUES (?, ?, ?, ?)
	"""

	const val CREATE_INSTRUCTION_STEP_SQL = """
		INSERT INTO instruction_steps (recipe_id, step, order_index)
		VALUES (?, ?, ?)
	"""

	const val GET_USER_PANTRY_SQL = """
		SELECT ingredient
		FROM user_pantry
		WHERE user_id = ?
		ORDER BY ingredient
	"""

	const val GET_USER_EXCLUDED_INGREDIENTS_SQL = """
		SELECT ingredient
		FROM user_excluded_ingredients
		WHERE user_id = ?
		ORDER BY ingredient
	"""

	val IMPERIAL_UNIT_REGEX = Regex(
		pattern =
			"(?<!\\p{L})(cups?|tbsp|tablespoons?|tsp|teaspoons?|ounces?|ounce|oz|" +
				"pounds?|pound|lbs?|lb|fahrenheit|°f)\\b",
		options = setOf(RegexOption.IGNORE_CASE),
	)

	val METRIC_UNIT_REGEX = Regex(
		pattern =
			"(?<!\\p{L})(kilograms?|kilogram|kg|grams?|gram|g|milliliters?|milliliter|" +
				"ml|liters?|liter|l|celsius|°c)\\b",
		options = setOf(RegexOption.IGNORE_CASE),
	)
}
