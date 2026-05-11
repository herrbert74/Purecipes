package app.purecipes.feature.search.domain.repository

import app.purecipes.shared.domain.model.PantryDelta

interface UserPantryRepository {

	suspend fun getPantry(): Set<String>

	suspend fun updatePantry(delta: PantryDelta): Set<String>
}
