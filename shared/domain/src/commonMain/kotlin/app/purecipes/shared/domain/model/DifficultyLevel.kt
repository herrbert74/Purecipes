package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DifficultyLevel(val displayName: String) {
	EASY("Easy"),
	MEDIUM("Medium"),
	HARD("Hard"),
}
