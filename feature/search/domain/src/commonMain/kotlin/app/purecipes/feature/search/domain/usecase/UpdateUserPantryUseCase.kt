package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.feature.search.domain.repository.UserPantryRepository
import app.purecipes.shared.domain.model.PantryDelta
import dev.zacsweers.metro.Inject

@Inject
class UpdateUserPantryUseCase(
	private val repository: UserPantryRepository,
) {

	suspend operator fun invoke(delta: PantryDelta): SearchOutcome<Set<String>> = repository.updatePantry(delta)
}
