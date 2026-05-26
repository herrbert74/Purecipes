package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CookbookImportResult(
	val cookbook: CookbookSummary,
	val recipesImported: Int,
	val recipesSkipped: Int,
	val alreadyImported: Boolean,
)
