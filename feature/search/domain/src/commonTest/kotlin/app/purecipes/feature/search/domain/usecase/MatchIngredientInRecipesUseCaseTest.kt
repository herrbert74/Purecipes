package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.IngredientMatchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.IngredientMatchCount
import app.purecipes.shared.domain.model.IngredientMatchResponse
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MatchIngredientInRecipesUseCaseTest {

	@Test
	fun `invoke trims query before matching`() = runTest {
		val repository = FakeIngredientMatchRepository()
		val useCase = MatchIngredientInRecipesUseCase(repository)

		useCase("  tarragon  ")

		repository.lastMatchedName shouldBe "tarragon"
	}

	@Test
	fun `invoke returns repository result`() = runTest {
		val expected = IngredientMatchResponse(
			query = "tarragon",
			exactMatches = listOf(IngredientMatchCount(ingredient = "Tarragon", recipeCount = 1)),
		)
		val repository = FakeIngredientMatchRepository(response = Ok(expected))
		val useCase = MatchIngredientInRecipesUseCase(repository)

		useCase("tarragon") shouldBe Ok(expected)
	}

	private class FakeIngredientMatchRepository(
		private val response: SearchOutcome<IngredientMatchResponse> = Ok(IngredientMatchResponse(query = "")),
	) : IngredientMatchRepository {

		var lastMatchedName: String? = null
			private set

		override suspend fun matchIngredient(name: String): SearchOutcome<IngredientMatchResponse> {
			lastMatchedName = name
			return response
		}
	}
}
