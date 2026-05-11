package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.UserPantryRepository
import app.purecipes.shared.domain.model.PantryDelta

class FakeUserPantryRepository(
	private var pantry: Set<String> = emptySet(),
) : UserPantryRepository {

	override suspend fun getPantry(): Set<String> = pantry

	override suspend fun updatePantry(delta: PantryDelta): Set<String> {
		pantry = (pantry + delta.add) - delta.remove
		return pantry
	}
}
