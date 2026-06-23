package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.IngredientMatchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.IngredientMatchResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError

class FakeIngredientMatchRepository(
	private val response: SearchOutcome<IngredientMatchResponse> = Ok(IngredientMatchResponse(query = "")),
) : IngredientMatchRepository {

	val matchedNames = mutableListOf<String>()

	override suspend fun matchIngredient(name: String): SearchOutcome<IngredientMatchResponse> {
		matchedNames += name
		val error = response.getError()
		if (error != null) {
			return Err(error)
		}
		val value = response.get() ?: IngredientMatchResponse(query = name)
		return Ok(value.copy(query = name))
	}
}
