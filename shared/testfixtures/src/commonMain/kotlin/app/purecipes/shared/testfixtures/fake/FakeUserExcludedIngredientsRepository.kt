package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.feature.search.domain.repository.UserExcludedIngredientsRepository
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import com.github.michaelbull.result.Ok

class FakeUserExcludedIngredientsRepository(
	private var excludedIngredients: Set<String> = emptySet(),
) : UserExcludedIngredientsRepository {

	override suspend fun getExcludedIngredients(): Set<String> = excludedIngredients

	override suspend fun updateExcludedIngredients(
		delta: ExcludedIngredientsDelta,
	): SearchOutcome<Set<String>> {
		excludedIngredients = (excludedIngredients + delta.add) - delta.remove
		return Ok(excludedIngredients)
	}
}
