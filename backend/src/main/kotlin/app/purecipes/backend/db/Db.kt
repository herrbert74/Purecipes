package app.purecipes.backend.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

private const val DEFAULT_PURECIPES_DB_POOL_SIZE = 5

class Db private constructor(
	val dataSource: DataSource,
) {

	companion object {

		internal fun fromDataSource(dataSource: DataSource): Db {
			ensureSchema(dataSource)
			return Db(dataSource)
		}

		fun create(): Db {
			val url = System.getenv("PURECIPES_DB_URL") ?: "jdbc:postgresql://localhost:5432/purecipes"
			val user = System.getenv("PURECIPES_DB_USER") ?: "postgres"
			val password = System.getenv("PURECIPES_DB_PASSWORD") ?: "postgres"

			val config = HikariConfig().apply {
				jdbcUrl = url
				username = user
				this.password = password
				maximumPoolSize =
					(System.getenv("PURECIPES_DB_POOL_SIZE")?.toIntOrNull() ?: DEFAULT_PURECIPES_DB_POOL_SIZE)
				isAutoCommit = true
				validate()
			}
			val dataSource = HikariDataSource(config)
			ensureSchema(dataSource)
			return Db(dataSource)
		}

		private fun ensureSchema(dataSource: DataSource) {
			dataSource.connection.use { connection ->
				connection.createStatement().use { statement ->
					statement.execute(APP_USERS_TABLE_SQL)
					statement.execute(APP_USERS_ADD_IS_PREMIUM_SQL)
					statement.execute(AUTH_SESSIONS_TABLE_SQL)
					statement.execute(RECIPES_TABLE_SQL)
					statement.execute(RECIPES_ADD_DESCRIPTION_SQL)
					statement.execute(RECIPES_ADD_CREATED_BY_USER_ID_SQL)
					statement.execute(RECIPES_ADD_MEASUREMENT_SYSTEM_SQL)
					statement.execute(RECIPES_ADD_PREP_TIME_SQL)
					statement.execute(RECIPES_ADD_COOK_TIME_SQL)
					statement.execute(RECIPES_ADD_LANGUAGE_SQL)
					statement.execute(RECIPES_ADD_SOURCE_URL_SQL)
					statement.execute(RECIPES_ADD_SCRAPED_AT_SQL)
					statement.execute(RECIPES_ADD_MEAL_TYPE_SQL)
					statement.execute(RECIPES_ADD_DIFFICULTY_SQL)
					statement.execute(RECIPES_ADD_COOKING_METHOD_SQL)
					statement.execute(RECIPES_ADD_CALORIE_RANGE_SQL)
					statement.execute(RECIPES_ADD_DIETARY_PREFERENCES_SQL)
					statement.execute(RECIPES_ADD_TAGS_SQL)
					statement.execute(NUTRITION_FOODS_TABLE_SQL)
					statement.execute(NUTRITION_FOODS_NORMALIZED_NAME_INDEX_SQL)
					statement.execute(NUTRITION_FOOD_ALIASES_TABLE_SQL)
					statement.execute(NUTRITION_FOOD_MEASURES_TABLE_SQL)
					statement.execute(INGREDIENT_GROUPS_TABLE_SQL)
					statement.execute(INGREDIENTS_TABLE_SQL)
					statement.execute(INGREDIENTS_ADD_REQUIREMENT_SQL)
					statement.execute(INGREDIENTS_ADD_ALTERNATIVE_GROUP_KEY_SQL)
					statement.execute(INGREDIENT_MEASUREMENTS_TABLE_SQL)
					statement.execute(INSTRUCTION_STEPS_TABLE_SQL)
					statement.execute(FAVORITES_TABLE_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_TABLE_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_ADD_PREFERRED_SYSTEM_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_ADD_FORMAT_HANDLING_SQL)
					statement.execute(MEASUREMENT_PREFERENCES_ADD_DETECTED_COUNTRY_CODE_SQL)
					statement.execute(MEASUREMENT_PREFERENCE_SEEN_RECIPES_TABLE_SQL)
					statement.execute(FAVORITES_USER_CREATED_AT_INDEX_SQL)
					statement.execute(COOKBOOKS_TABLE_SQL)
					statement.execute(COOKBOOKS_USER_CREATED_AT_INDEX_SQL)
					statement.execute(COOKBOOK_RECIPES_TABLE_SQL)
					statement.execute(COOKBOOK_RECIPES_RECIPE_INDEX_SQL)
					statement.execute(COOKBOOK_SHARES_TABLE_SQL)
					statement.execute(COOKBOOK_SHARES_COOKBOOK_INDEX_SQL)
					statement.execute(COOKBOOK_SHARE_IMPORTS_TABLE_SQL)
					statement.execute(NUTRITION_TABLE_SQL)
					statement.execute(NUTRITION_ADD_MATCHED_INGREDIENT_COUNT_SQL)
					statement.execute(NUTRITION_ADD_TOTAL_INGREDIENT_COUNT_SQL)
					statement.execute(NUTRITION_ADD_CALCULATION_SOURCE_SQL)
					statement.execute(NUTRITION_ADD_CONFIDENCE_SQL)
					statement.execute(NUTRITION_ADD_IS_COMPLETE_SQL)
					statement.execute(NUTRITION_ADD_UPDATED_AT_SQL)
					statement.execute(NUTRITION_ADD_TOTAL_WEIGHT_GRAMS_SQL)
					statement.execute(NUTRITION_ADD_SERVING_COUNT_SQL)
					statement.execute(INGREDIENT_NUTRITION_MATCHES_TABLE_SQL)
					statement.execute(INGREDIENT_NUTRITION_CONTRIBUTIONS_TABLE_SQL)
					statement.execute(SEARCH_FILTERS_TABLE_SQL)
					statement.execute(USER_PANTRY_TABLE_SQL)
					statement.execute(USER_EXCLUDED_INGREDIENTS_TABLE_SQL)
				}
			}
		}
	}
}
