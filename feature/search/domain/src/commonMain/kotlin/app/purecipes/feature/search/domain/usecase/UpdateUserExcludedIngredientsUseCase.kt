package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.feature.search.domain.repository.UserExcludedIngredientsRepository
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import dev.zacsweers.metro.Inject

@Inject
class UpdateUserExcludedIngredientsUseCase(
	private val repository: UserExcludedIngredientsRepository,
) {

	suspend operator fun invoke(delta: ExcludedIngredientsDelta): SearchOutcome<Set<String>> =
		repository.updateExcludedIngredients(delta)
}
