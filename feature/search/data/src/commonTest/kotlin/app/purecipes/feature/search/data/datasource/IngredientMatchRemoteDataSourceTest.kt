package app.purecipes.feature.search.data.datasource

import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.IngredientMatchCount
import app.purecipes.shared.domain.model.IngredientMatchResponse
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class IngredientMatchRemoteDataSourceTest {

	@Test
	fun `matchIngredient returns response from API`() = runTest {
		val api = FakePurecipesApi().apply {
			ingredientMatchResponse = IngredientMatchResponse(
				query = "tarragon",
				exactMatches = listOf(IngredientMatchCount(ingredient = "Tarragon", recipeCount = 2)),
			)
		}
		val dataSource = IngredientMatchRemoteDataSource(api)

		val result = dataSource.matchIngredient("tarragon")

		result.get() shouldBe IngredientMatchResponse(
			query = "tarragon",
			exactMatches = listOf(IngredientMatchCount(ingredient = "Tarragon", recipeCount = 2)),
		)
		result.getError() shouldBe null
		api.ingredientMatchCalls shouldBe listOf("tarragon")
	}
}
