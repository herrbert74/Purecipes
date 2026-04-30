package com.purecipes.shared.testfixtures.fake

import com.purecipes.feature.search.domain.repository.UserPantryRepository
import com.purecipes.shared.domain.model.PantryDelta

class FakeUserPantryRepository(
	private var pantry: Set<String> = emptySet(),
) : UserPantryRepository {

	override suspend fun getPantry(): Set<String> = pantry

	override suspend fun updatePantry(delta: PantryDelta): Set<String> {
		pantry = (pantry + delta.add) - delta.remove
		return pantry
	}
}
