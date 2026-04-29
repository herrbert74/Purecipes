package com.purecipes.feature.search.domain.repository

import com.purecipes.shared.domain.model.PantryDelta

interface UserPantryRepository {

	suspend fun getPantry(): Set<String>

	suspend fun updatePantry(delta: PantryDelta): Set<String>
}
