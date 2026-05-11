package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.UserPantryRepository
import app.purecipes.shared.domain.model.PantryDelta

class UpdateUserPantryUseCase(
	private val repository: UserPantryRepository,
) {

	suspend operator fun invoke(delta: PantryDelta): Set<String> = repository.updatePantry(delta)
}
