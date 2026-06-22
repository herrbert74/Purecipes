package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.IngredientMatchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.IngredientMatchResponse
import dev.zacsweers.metro.Inject

@Inject
class MatchIngredientInRecipesUseCase(
	private val repository: IngredientMatchRepository,
) {

	suspend operator fun invoke(name: String): SearchOutcome<IngredientMatchResponse> =
		repository.matchIngredient(name.trim())
}
