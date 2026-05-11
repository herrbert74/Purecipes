package app.purecipes.feature.search.data.datasource

import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.PantryDelta
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UserPantryRemoteDataSourceTest {

	@Test
	fun `getPantry returns pantry from API`() = runTest {
		val api = FakePurecipesApi()
		api.updateUserPantry(PantryDelta(add = setOf("Chicken", "Tomato")))
		val dataSource = UserPantryRemoteDataSource(api)

		val result = dataSource.getPantry()

		result.get() shouldBe setOf("Chicken", "Tomato")
		result.getError() shouldBe null
	}

	@Test
	fun `updatePantry updates pantry via API`() = runTest {
		val api = FakePurecipesApi()
		val dataSource = UserPantryRemoteDataSource(api)

		val result = dataSource.updatePantry(PantryDelta(add = setOf("Chicken")))

		result.get() shouldBe setOf("Chicken")
		api.userPantry shouldBe setOf("Chicken")
	}
}
