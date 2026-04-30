package com.purecipes.feature.search.domain.usecase

import com.purecipes.feature.search.domain.repository.UserPantryRepository
import com.purecipes.shared.domain.model.PantryDelta

class UpdateUserPantryUseCase(
	private val repository: UserPantryRepository,
) {

	suspend operator fun invoke(delta: PantryDelta): Set<String> = repository.updatePantry(delta)
}
