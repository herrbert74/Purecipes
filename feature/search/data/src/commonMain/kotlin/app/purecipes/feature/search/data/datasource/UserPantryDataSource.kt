package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.PantryDelta

interface UserPantryDataSource {

	interface Remote {
		suspend fun getPantry(): SearchOutcome<Set<String>>
		suspend fun updatePantry(delta: PantryDelta): SearchOutcome<Set<String>>
	}

	interface Local {
		fun getPantry(): Set<String>
		fun savePantry(pantry: Set<String>)
	}
}
