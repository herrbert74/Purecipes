package app.purecipes.feature.recipedetails.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import app.purecipes.feature.recipedetails.data.datasource.RecipeDetailsRemoteDataSource
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RecipeDetailsAccessorTest {

	@Test
	fun `details repository returns shared domain recipe details`() = runTest {
		val expected = fakeRecipeDetails(steps = listOf("Boil pasta", "Make sauce"))
		val accessor = RecipeDetailsAccessor(
			RecipeDetailsRemoteDataSource(FakePurecipesApi(initialRecipeDetails = listOf(expected))),
		)

		val outcome = accessor.getRecipeDetails(42)

		outcome.get() shouldBe expected
		outcome.getError() shouldBe null
	}
}
