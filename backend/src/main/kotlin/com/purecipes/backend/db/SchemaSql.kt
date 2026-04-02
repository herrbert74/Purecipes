package com.purecipes.backend.db

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

internal const val RECIPES_TABLE_SQL = """
	CREATE TABLE IF NOT EXISTS recipes (
		id SERIAL PRIMARY KEY,
		title VARCHAR(255) NOT NULL,
		description TEXT,
		instructions TEXT,
		total_time INTEGER,
		yields VARCHAR(255),
		image_url VARCHAR(512),
		cuisine VARCHAR(255),
		category VARCHAR(255),
		created_by_user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	)
"""

internal const val RECIPES_ADD_DESCRIPTION_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS description TEXT
"""

internal const val RECIPES_ADD_CREATED_BY_USER_ID_SQL = """
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS created_by_user_id BIGINT REFERENCES app_users(id) ON DELETE SET NULL
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
