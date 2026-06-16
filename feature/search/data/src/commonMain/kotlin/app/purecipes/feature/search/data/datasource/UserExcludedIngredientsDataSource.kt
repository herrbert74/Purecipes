package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta

interface UserExcludedIngredientsDataSource {

	interface Remote {
		suspend fun getExcludedIngredients(): SearchOutcome<Set<String>>
		suspend fun updateExcludedIngredients(delta: ExcludedIngredientsDelta): SearchOutcome<Set<String>>
	}

	interface Local {
		fun getExcludedIngredients(): Set<String>
		fun saveExcludedIngredients(excludedIngredients: Set<String>)
	}
}
