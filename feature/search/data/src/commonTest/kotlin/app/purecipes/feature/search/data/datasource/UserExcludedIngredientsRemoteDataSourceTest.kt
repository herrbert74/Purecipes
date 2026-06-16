package app.purecipes.feature.search.data.datasource

import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UserExcludedIngredientsRemoteDataSourceTest {

	@Test
	fun `getExcludedIngredients returns excluded ingredients from API`() = runTest {
		val api = FakePurecipesApi()
		api.updateUserExcludedIngredients(ExcludedIngredientsDelta(add = setOf("Garlic", "Peanut")))
		val dataSource = UserExcludedIngredientsRemoteDataSource(api)

		val result = dataSource.getExcludedIngredients()

		result.get() shouldBe setOf("Garlic", "Peanut")
		result.getError() shouldBe null
	}

	@Test
	fun `updateExcludedIngredients updates excluded ingredients via API`() = runTest {
		val api = FakePurecipesApi()
		val dataSource = UserExcludedIngredientsRemoteDataSource(api)

		val result = dataSource.updateExcludedIngredients(ExcludedIngredientsDelta(add = setOf("Dairy")))

		result.get() shouldBe setOf("Dairy")
		api.userExcludedIngredients shouldBe setOf("Dairy")
	}
}
