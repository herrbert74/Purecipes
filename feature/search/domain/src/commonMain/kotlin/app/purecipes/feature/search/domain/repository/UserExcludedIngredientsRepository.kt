package app.purecipes.feature.search.domain.repository

import app.purecipes.shared.domain.model.ExcludedIngredientsDelta

interface UserExcludedIngredientsRepository {

	suspend fun getExcludedIngredients(): Set<String>

	suspend fun updateExcludedIngredients(delta: ExcludedIngredientsDelta): SearchOutcome<Set<String>>
}
