package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.UserExcludedIngredientsRepository
import dev.zacsweers.metro.Inject

@Inject
class GetUserExcludedIngredientsUseCase(
	private val repository: UserExcludedIngredientsRepository,
) {

	suspend operator fun invoke(): Set<String> = repository.getExcludedIngredients()
}
