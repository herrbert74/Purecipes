package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.feature.search.domain.repository.UserPantryRepository
import app.purecipes.shared.domain.model.PantryDelta
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

class FakeUserPantryRepository(
	private var pantry: Set<String> = emptySet(),
	private val updateFailure: Failure? = null,
) : UserPantryRepository {

	override suspend fun getPantry(): Set<String> = pantry

	override suspend fun updatePantry(delta: PantryDelta): SearchOutcome<Set<String>> {
		updateFailure?.let { failure -> return Err(failure) }
		pantry = (pantry + delta.add) - delta.remove
		return Ok(pantry)
	}
}
