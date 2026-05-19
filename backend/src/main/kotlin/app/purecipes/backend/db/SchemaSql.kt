package app.purecipes.backend.db

internal const val APP_USERS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS app_users (
		id BIGSERIAL PRIMARY KEY,
		provider VARCHAR(32) NOT NULL,
		external_user_id TEXT NOT NULL,
		email TEXT NOT NULL,
		display_name TEXT NOT NULL,
		first_name TEXT,
		family_name TEXT,
		profile_image_url TEXT,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		UNIQUE(provider, external_user_id)
	)
"""

internal const val AUTH_SESSIONS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS auth_sessions (
		id BIGSERIAL PRIMARY KEY,
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		access_token_hash CHAR(64) NOT NULL UNIQUE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		expires_at TIMESTAMP NOT NULL,
		revoked_at TIMESTAMP
	)
"""

internal const val FAVORITES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS favorites (
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		PRIMARY KEY (user_id, recipe_id)
	)
"""

internal const val MEASUREMENT_PREFERENCES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS measurement_preferences (
		user_id BIGINT PRIMARY KEY REFERENCES app_users(id) ON DELETE CASCADE,
		preferred_system VARCHAR(32) NOT NULL,
		format_handling VARCHAR(32) NOT NULL DEFAULT 'KEEP_AS_IS',
		detected_country_code VARCHAR(8),
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val MEASUREMENT_PREFERENCES_ADD_PREFERRED_SYSTEM_SQL = """
	ALTER TABLE measurement_preferences ADD COLUMN IF NOT EXISTS preferred_system VARCHAR(32)
"""

internal const val MEASUREMENT_PREFERENCES_ADD_FORMAT_HANDLING_SQL = """
	ALTER TABLE measurement_preferences ADD COLUMN IF NOT EXISTS format_handling VARCHAR(32)
"""

internal const val MEASUREMENT_PREFERENCES_ADD_DETECTED_COUNTRY_CODE_SQL = """
	ALTER TABLE measurement_preferences ADD COLUMN IF NOT EXISTS detected_country_code VARCHAR(8)
"""

internal const val MEASUREMENT_PREFERENCE_SEEN_RECIPES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS measurement_preference_seen_recipes (
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		recipe_id INTEGER NOT NULL,
		PRIMARY KEY (user_id, recipe_id)
	)
"""

internal const val RECIPES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS recipes (
		id SERIAL PRIMARY KEY,
		title VARCHAR(255) NOT NULL,
		description TEXT,
		instructions TEXT,
		total_time INTEGER,
		prep_time INTEGER,
		cook_time INTEGER,
		yields VARCHAR(255),
		image_url VARCHAR(512),
		language VARCHAR(10) DEFAULT 'en',
		cuisine VARCHAR(255),
		meal_type TEXT,
		difficulty VARCHAR(20),
		cooking_method VARCHAR(50),
		calorie_range VARCHAR(20),
		dietary_preferences TEXT ARRAY,
		tags TEXT ARRAY,
		source_url TEXT UNIQUE,
		measurement_system VARCHAR(32),
		created_by_user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL,
		scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val RECIPES_ADD_DESCRIPTION_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS description TEXT
"""

internal const val RECIPES_ADD_CREATED_BY_USER_ID_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS created_by_user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL
"""

internal const val RECIPES_ADD_MEASUREMENT_SYSTEM_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS measurement_system VARCHAR(32)
"""

internal const val RECIPES_ADD_PREP_TIME_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS prep_time INTEGER
"""

internal const val RECIPES_ADD_COOK_TIME_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS cook_time INTEGER
"""

internal const val RECIPES_ADD_LANGUAGE_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS language VARCHAR(10) DEFAULT 'en'
"""

internal const val RECIPES_ADD_SOURCE_URL_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS source_url TEXT UNIQUE
"""

internal const val RECIPES_ADD_SCRAPED_AT_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
"""

internal const val RECIPES_ADD_MEAL_TYPE_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS meal_type TEXT
"""

internal const val RECIPES_ADD_DIFFICULTY_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS difficulty VARCHAR(20)
"""

internal const val RECIPES_ADD_COOKING_METHOD_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS cooking_method VARCHAR(50)
"""

internal const val RECIPES_ADD_CALORIE_RANGE_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS calorie_range VARCHAR(20)
"""

internal const val RECIPES_ADD_DIETARY_PREFERENCES_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS dietary_preferences TEXT ARRAY
"""

internal const val RECIPES_ADD_TAGS_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS tags TEXT ARRAY
"""

internal const val INGREDIENT_GROUPS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS ingredient_groups (
		id SERIAL PRIMARY KEY,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		name VARCHAR(255),
		order_index INTEGER NOT NULL
	)
"""

internal const val INGREDIENTS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS ingredients (
		id SERIAL PRIMARY KEY,
		ingredient_group_id INTEGER NOT NULL REFERENCES ingredient_groups(id) ON DELETE CASCADE,
		ingredient VARCHAR(255),
		order_index INTEGER NOT NULL
	)
"""

internal const val INSTRUCTION_STEPS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS instruction_steps (
		id SERIAL PRIMARY KEY,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		step TEXT,
		order_index INTEGER NOT NULL
	)
"""

internal const val FAVORITES_USER_CREATED_AT_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_favorites_user_created_at
	ON favorites (user_id, created_at DESC)
"""

internal const val COOKBOOKS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS cookbooks (
		id SERIAL PRIMARY KEY,
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		name TEXT NOT NULL,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val COOKBOOKS_USER_CREATED_AT_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_cookbooks_user_created_at
	ON cookbooks (user_id, created_at DESC)
"""

internal const val COOKBOOK_RECIPES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS cookbook_recipes (
		cookbook_id INTEGER NOT NULL REFERENCES cookbooks(id) ON DELETE CASCADE,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		PRIMARY KEY (cookbook_id, recipe_id)
	)
"""

internal const val COOKBOOK_RECIPES_RECIPE_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_cookbook_recipes_recipe
	ON cookbook_recipes (recipe_id)
"""

internal const val NUTRITION_FOODS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS nutrition_foods (
		id SERIAL PRIMARY KEY,
		source_name VARCHAR(32) NOT NULL,
		source_id TEXT NOT NULL,
		display_name TEXT NOT NULL,
		normalized_name TEXT NOT NULL,
		calories_per_100g DECIMAL(10,2),
		protein_per_100g DECIMAL(10,2),
		carbohydrates_per_100g DECIMAL(10,2),
		fat_per_100g DECIMAL(10,2),
		fiber_per_100g DECIMAL(10,2),
		sugar_per_100g DECIMAL(10,2),
		sodium_per_100g DECIMAL(10,2),
		source_metadata TEXT,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		UNIQUE (source_name, source_id)
	)
"""

internal const val NUTRITION_FOODS_NORMALIZED_NAME_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_nutrition_foods_normalized_name
	ON nutrition_foods (normalized_name)
"""

internal const val NUTRITION_FOOD_ALIASES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS nutrition_food_aliases (
		id SERIAL PRIMARY KEY,
		food_id INTEGER NOT NULL REFERENCES nutrition_foods(id) ON DELETE CASCADE,
		alias TEXT NOT NULL,
		normalized_alias TEXT NOT NULL,
		UNIQUE (normalized_alias)
	)
"""

internal const val NUTRITION_FOOD_MEASURES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS nutrition_food_measures (
		id SERIAL PRIMARY KEY,
		food_id INTEGER NOT NULL REFERENCES nutrition_foods(id) ON DELETE CASCADE,
		measure_name VARCHAR(32) NOT NULL,
		grams_per_measure DECIMAL(12,4) NOT NULL,
		UNIQUE (food_id, measure_name)
	)
"""

internal const val INGREDIENT_MEASUREMENTS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS ingredient_measurements (
		ingredient_id INTEGER PRIMARY KEY REFERENCES ingredients(id) ON DELETE CASCADE,
		raw_text TEXT NOT NULL,
		quantity DECIMAL(12,4),
		unit VARCHAR(32),
		parsed_name VARCHAR(255),
		is_measurable BOOLEAN NOT NULL DEFAULT FALSE
	)
"""

internal const val NUTRITION_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS nutrition (
		id SERIAL PRIMARY KEY,
		recipe_id INTEGER UNIQUE REFERENCES recipes(id) ON DELETE CASCADE,
		calories DECIMAL(10,2),
		protein DECIMAL(10,2),
		carbohydrates DECIMAL(10,2),
		fat DECIMAL(10,2),
		fiber DECIMAL(10,2),
		sugar DECIMAL(10,2),
		sodium DECIMAL(10,2)
	)
"""

internal const val NUTRITION_ADD_MATCHED_INGREDIENT_COUNT_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS matched_ingredient_count INTEGER
"""

internal const val NUTRITION_ADD_TOTAL_INGREDIENT_COUNT_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS total_ingredient_count INTEGER
"""

internal const val NUTRITION_ADD_CALCULATION_SOURCE_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS calculation_source VARCHAR(32)
"""

internal const val NUTRITION_ADD_CONFIDENCE_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS confidence VARCHAR(32)
"""

internal const val NUTRITION_ADD_IS_COMPLETE_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS is_complete BOOLEAN
"""

internal const val NUTRITION_ADD_UPDATED_AT_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
"""

internal const val INGREDIENT_NUTRITION_MATCHES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS ingredient_nutrition_matches (
		id SERIAL PRIMARY KEY,
		ingredient_id INTEGER NOT NULL UNIQUE REFERENCES ingredients(id) ON DELETE CASCADE,
		raw_text TEXT NOT NULL,
		quantity DECIMAL(12,4),
		unit VARCHAR(32),
		parsed_name VARCHAR(255),
		food_id INTEGER REFERENCES nutrition_foods(id) ON DELETE SET NULL,
		confidence DECIMAL(5,4),
		match_source VARCHAR(32),
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val SEARCH_FILTERS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS search_filters (
		user_id BIGINT PRIMARY KEY REFERENCES app_users(id) ON DELETE CASCADE,
		filters_json TEXT NOT NULL,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val USER_PANTRY_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS user_pantry (
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		ingredient TEXT NOT NULL,
		PRIMARY KEY (user_id, ingredient)
	)
"""
