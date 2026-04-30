package com.purecipes.feature.search.domain.usecase

import com.purecipes.feature.search.domain.repository.UserPantryRepository

class GetUserPantryUseCase(
	private val repository: UserPantryRepository,
) {

	suspend operator fun invoke(): Set<String> = repository.getPantry()
}
