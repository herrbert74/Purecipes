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
		is_premium BOOLEAN NOT NULL DEFAULT FALSE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		UNIQUE(provider, external_user_id)
	)
"""

internal const val APP_USERS_ADD_IS_PREMIUM_SQL = """
	ALTER TABLE app_users ADD COLUMN IF NOT EXISTS is_premium BOOLEAN NOT NULL DEFAULT FALSE
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
		appliances TEXT ARRAY,
		source_url TEXT UNIQUE,
		measurement_system VARCHAR(32),
		created_by_user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL,
		is_private BOOLEAN NOT NULL DEFAULT FALSE,
		scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		catalog_rank INTEGER
	)
"""

internal const val RECIPES_ADD_DESCRIPTION_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS description TEXT
"""

internal const val RECIPES_ADD_CREATED_BY_USER_ID_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS created_by_user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL
"""

internal const val RECIPES_ADD_IS_PRIVATE_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS is_private BOOLEAN NOT NULL DEFAULT FALSE
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

internal const val RECIPES_ADD_CATALOG_RANK_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS catalog_rank INTEGER
"""

internal const val RECIPES_ADD_APPLIANCES_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS appliances TEXT ARRAY
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
		order_index INTEGER NOT NULL,
		requirement VARCHAR(20) NOT NULL DEFAULT 'REQUIRED',
		alternative_group_key INTEGER
	)
"""

internal const val INGREDIENTS_ADD_REQUIREMENT_SQL = """
	ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS requirement VARCHAR(20) NOT NULL DEFAULT 'REQUIRED'
"""

internal const val INGREDIENTS_ADD_ALTERNATIVE_GROUP_KEY_SQL = """
	ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS alternative_group_key INTEGER
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

internal const val COOKBOOK_SHARES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS cookbook_shares (
		token TEXT PRIMARY KEY,
		cookbook_id INTEGER NOT NULL REFERENCES cookbooks(id) ON DELETE CASCADE,
		created_by_user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val COOKBOOK_SHARES_COOKBOOK_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_cookbook_shares_cookbook
	ON cookbook_shares (cookbook_id)
"""

internal const val COOKBOOK_SHARE_IMPORTS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS cookbook_share_imports (
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		share_token TEXT NOT NULL REFERENCES cookbook_shares(token) ON DELETE CASCADE,
		imported_cookbook_id INTEGER NOT NULL REFERENCES cookbooks(id) ON DELETE CASCADE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		PRIMARY KEY (user_id, share_token)
	)
"""

internal const val FEATURE_REQUESTS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS feature_requests (
		id SERIAL PRIMARY KEY,
		created_by_user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL,
		title TEXT NOT NULL,
		description TEXT NOT NULL,
		status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val FEATURE_REQUESTS_STATUS_CREATED_AT_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_feature_requests_status_created_at
	ON feature_requests (status, created_at DESC)
"""

internal const val FEATURE_REQUEST_VOTES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS feature_request_votes (
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		request_id INTEGER NOT NULL REFERENCES feature_requests(id) ON DELETE CASCADE,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		PRIMARY KEY (user_id, request_id)
	)
"""

internal const val FEATURE_REQUEST_VOTES_REQUEST_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_feature_request_votes_request
	ON feature_request_votes (request_id)
"""

internal const val FEATURE_REQUEST_COMMENTS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS feature_request_comments (
		id SERIAL PRIMARY KEY,
		request_id INTEGER NOT NULL REFERENCES feature_requests(id) ON DELETE CASCADE,
		user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL,
		author_display_name TEXT NOT NULL,
		body TEXT NOT NULL,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val FEATURE_REQUEST_COMMENTS_REQUEST_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_feature_request_comments_request
	ON feature_request_comments (request_id, created_at)
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

internal const val NUTRITION_FOODS_SEED_CUSTOM_SQL = """
	INSERT INTO nutrition_foods (
		source_name,
		source_id,
		display_name,
		normalized_name,
		calories_per_100g,
		protein_per_100g,
		carbohydrates_per_100g,
		fat_per_100g,
		fiber_per_100g,
		sugar_per_100g,
		sodium_per_100g,
		source_metadata
	)
	SELECT
		'purecipes_custom',
		'non_alcoholic_spirit',
		'Non-alcoholic spirit, gin alternative',
		'non alcoholic spirit gin alternative',
		2.00,
		0.00,
		0.50,
		0.00,
		0.00,
		0.00,
		1.00,
		'Curated non-alcoholic botanical spirit'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'non_alcoholic_spirit'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'brandy_butter',
		'Brandy butter',
		'brandy butter',
		525.00,
		0.40,
		51.00,
		35.00,
		0.00,
		51.00,
		8.00,
		'Curated brandy butter (butter, sugar, brandy)'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'brandy_butter'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'wild_garlic_leaves',
		'Wild garlic leaves, raw',
		'wild garlic leaves raw',
		26.00,
		2.20,
		2.50,
		0.30,
		2.60,
		1.00,
		5.00,
		'Curated wild garlic leaves (Allium ursinum)'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'wild_garlic_leaves'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'galangal_raw',
		'Galangal root, raw',
		'galangal root raw',
		71.00,
		1.00,
		15.00,
		0.50,
		2.00,
		0.50,
		16.00,
		'Curated galangal root (Alpinia galanga)'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'galangal_raw'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'sourdough_starter',
		'Sourdough starter (100% hydration)',
		'sourdough starter 100 hydration',
		183.00,
		6.00,
		36.50,
		0.90,
		1.20,
		0.20,
		2.00,
		'Curated sourdough starter (equal parts flour and water)'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'sourdough_starter'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'rye_sourdough_starter',
		'Rye sourdough starter (100% hydration)',
		'rye sourdough starter 100 hydration',
		168.00,
		4.50,
		35.00,
		0.80,
		7.50,
		0.50,
		2.00,
		'Curated rye sourdough starter (equal parts rye flour and water)'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'rye_sourdough_starter'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'bread_sauce_mix_dry',
		'Bread sauce mix, dry packet',
		'bread sauce mix dry packet',
		330.00,
		8.10,
		65.10,
		3.70,
		4.40,
		4.40,
		3200.00,
		'Curated bread sauce mix, dry packet powder'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'bread_sauce_mix_dry'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'bread_sauce_prepared',
		'Bread sauce, prepared',
		'bread sauce prepared',
		104.00,
		3.80,
		12.50,
		4.20,
		0.80,
		3.90,
		380.00,
		'Curated bread sauce, prepared with whole milk and butter'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'bread_sauce_prepared'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'asafoetida_powder',
		'Asafoetida powder',
		'asafoetida powder',
		297.00,
		4.00,
		68.00,
		1.10,
		4.10,
		0.00,
		50.00,
		'Curated asafoetida (hing) powder compounded with gum arabic/flour'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'asafoetida_powder'
	)
	UNION ALL
	SELECT
		'purecipes_custom',
		'pastry_baking_mix',
		'Pastry baking mix, dry',
		'pastry baking mix dry',
		428.00,
		8.00,
		63.40,
		15.40,
		2.50,
		3.00,
		1300.00,
		'Curated all-purpose pastry/biscuit baking mix, dry'
	WHERE NOT EXISTS (
		SELECT 1 FROM nutrition_foods
		WHERE source_name = 'purecipes_custom' AND source_id = 'pastry_baking_mix'
	)
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

internal const val NUTRITION_ADD_TOTAL_WEIGHT_GRAMS_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS total_weight_grams DECIMAL(12,4)
"""

internal const val NUTRITION_ADD_SERVING_COUNT_SQL = """
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS serving_count DECIMAL(8,2)
"""

internal const val INGREDIENT_NUTRITION_CONTRIBUTIONS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS ingredient_nutrition_contributions (
		ingredient_id INTEGER PRIMARY KEY REFERENCES ingredients(id) ON DELETE CASCADE,
		grams_resolved DECIMAL(12,4),
		grams_source VARCHAR(32),
		calories DECIMAL(10,2),
		protein DECIMAL(10,2),
		carbohydrates DECIMAL(10,2),
		fat DECIMAL(10,2),
		fiber DECIMAL(10,2),
		sugar DECIMAL(10,2),
		sodium DECIMAL(10,2),
		override_calories DECIMAL(10,2),
		override_protein DECIMAL(10,2),
		override_carbohydrates DECIMAL(10,2),
		override_fat DECIMAL(10,2),
		override_fiber DECIMAL(10,2),
		override_sugar DECIMAL(10,2),
		override_sodium DECIMAL(10,2),
		uses_user_override BOOLEAN NOT NULL DEFAULT FALSE,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val INGREDIENT_NUTRITION_CONTRIBUTIONS_ADD_GRAMS_SOURCE_SQL = """
	ALTER TABLE ingredient_nutrition_contributions ADD COLUMN IF NOT EXISTS grams_source VARCHAR(32)
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

internal const val USER_EXCLUDED_INGREDIENTS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS user_excluded_ingredients (
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		ingredient TEXT NOT NULL,
		PRIMARY KEY (user_id, ingredient)
	)
"""

internal const val HOME_COLLECTIONS_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS home_collections (
		id SERIAL PRIMARY KEY,
		slug VARCHAR(64) NOT NULL UNIQUE,
		title TEXT NOT NULL,
		source VARCHAR(16) NOT NULL,
		filter_payload TEXT,
		sort VARCHAR(16) NOT NULL DEFAULT 'catalog',
		position INTEGER NOT NULL,
		enabled BOOLEAN NOT NULL DEFAULT TRUE,
		starts_at TIMESTAMP,
		ends_at TIMESTAMP
	)
"""

internal const val HOME_COLLECTIONS_POSITION_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_home_collections_position
	ON home_collections (enabled, position, id)
"""

internal const val HOME_COLLECTION_RECIPES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS home_collection_recipes (
		collection_id INTEGER NOT NULL REFERENCES home_collections(id) ON DELETE CASCADE,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		position INTEGER NOT NULL,
		PRIMARY KEY (collection_id, recipe_id)
	)
"""

internal const val HOME_COLLECTION_RECIPES_ORDER_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_home_collection_recipes_order
	ON home_collection_recipes (collection_id, position, recipe_id)
"""

internal const val RECIPE_ACTIVITY_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS recipe_activity (
		id BIGSERIAL PRIMARY KEY,
		user_id BIGINT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		event VARCHAR(32) NOT NULL,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val RECIPE_ACTIVITY_USER_CREATED_INDEX_SQL = """
	CREATE INDEX IF NOT EXISTS idx_recipe_activity_user_created
	ON recipe_activity (user_id, created_at)
"""
