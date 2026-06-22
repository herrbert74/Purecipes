package app.purecipes.backend

import java.sql.Statement
import javax.sql.DataSource

internal fun insertRecipeIngredientsForIngredientMatchTest(
	dataSource: DataSource,
	ingredients: List<String>,
): Int =
	dataSource.connection.use { connection ->
		val recipeId = connection.prepareStatement(
			"""
				INSERT INTO recipes (title, created_at)
				VALUES ('Ingredient match test recipe', CURRENT_TIMESTAMP)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next()) { "Recipe insert did not return generated id" }
				keys.getInt(1)
			}
		}

		val groupId = connection.prepareStatement(
			"""
				INSERT INTO ingredient_groups (recipe_id, name, order_index)
				VALUES (?, NULL, 0)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				check(keys.next()) { "Ingredient group insert did not return generated id" }
				keys.getInt(1)
			}
		}

		connection.prepareStatement(
			"""
				INSERT INTO ingredients (ingredient_group_id, ingredient, order_index, requirement)
				VALUES (?, ?, ?, 'REQUIRED')
			""".trimIndent(),
		).use { statement ->
			ingredients.forEachIndexed { index, ingredient ->
				statement.setInt(1, groupId)
				statement.setString(2, ingredient)
				statement.setInt(3, index)
				statement.addBatch()
			}
			statement.executeBatch()
		}

		recipeId
	}
