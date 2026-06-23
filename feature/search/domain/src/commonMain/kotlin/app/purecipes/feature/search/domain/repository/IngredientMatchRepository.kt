package app.purecipes.feature.search.domain.repository

import app.purecipes.shared.domain.model.IngredientMatchResponse

interface IngredientMatchRepository {

	suspend fun matchIngredient(name: String): SearchOutcome<IngredientMatchResponse>
}
