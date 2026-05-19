package app.purecipes.backend.feature.nutrition

import javax.sql.DataSource

internal fun insertRecipeWithIngredients(
	dataSource: DataSource,
	ingredients: List<String>,
	yields: String? = null,
): Int =
	dataSource.connection.use { connection ->
		val recipeId = connection.prepareStatement(
			"""
			INSERT INTO recipes (title, yields, created_at)
			VALUES ('Nutrition test recipe', ?, CURRENT_TIMESTAMP)
			RETURNING id
			""".trimIndent(),
		).use { statement ->
			statement.setString(1, yields)
			statement.executeQuery().use { resultSet ->
				resultSet.next()
				resultSet.getInt("id")
			}
		}

		val groupId = connection.prepareStatement(
			"""
			INSERT INTO ingredient_groups (recipe_id, name, order_index)
			VALUES (?, NULL, 0)
			RETURNING id
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeQuery().use { resultSet ->
				resultSet.next()
				resultSet.getInt("id")
			}
		}

		connection.prepareStatement(
			"""
			INSERT INTO ingredients (ingredient_group_id, ingredient, order_index)
			VALUES (?, ?, ?)
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

internal fun readRecipeCalories(dataSource: DataSource, recipeId: Int): java.math.BigDecimal? =
	dataSource.connection.use { connection ->
		connection.prepareStatement(
			"SELECT calories FROM nutrition WHERE recipe_id = ?",
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.executeQuery().use(::readNullableCalories)
		}
	}

private fun readNullableCalories(resultSet: java.sql.ResultSet): java.math.BigDecimal? =
	if (resultSet.next()) {
		resultSet.getBigDecimal("calories")
	} else {
		null
	}
