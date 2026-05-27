package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.UserPantryRepository
import dev.zacsweers.metro.Inject

@Inject
class GetUserPantryUseCase(
	private val repository: UserPantryRepository,
) {

	suspend operator fun invoke(): Set<String> = repository.getPantry()
}
