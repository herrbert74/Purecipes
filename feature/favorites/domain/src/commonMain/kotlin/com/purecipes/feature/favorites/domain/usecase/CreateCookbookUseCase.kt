package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository
import com.purecipes.shared.domain.model.CookbookSummary

class CreateCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(name: String): Outcome<CookbookSummary> {
		return repository.createCookbook(name)
	}
}
