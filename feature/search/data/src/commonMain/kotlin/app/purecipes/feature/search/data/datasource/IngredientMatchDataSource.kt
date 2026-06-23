package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.IngredientMatchResponse

interface IngredientMatchDataSource {

	interface Remote {

		suspend fun matchIngredient(name: String): SearchOutcome<IngredientMatchResponse>
	}
}
