package app.purecipes.feature.search.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetUserPantryUseCaseTest {

	@Test
	fun `returns pantry from repository`() = runTest {
		val repository = FakeUserPantryRepository(setOf("Chicken"))
		val useCase = GetUserPantryUseCase(repository)

		val result = useCase()

		result shouldBe setOf("Chicken")
	}
}
