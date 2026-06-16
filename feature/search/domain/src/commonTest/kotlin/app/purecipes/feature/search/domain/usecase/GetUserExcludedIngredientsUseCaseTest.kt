package app.purecipes.feature.search.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetUserExcludedIngredientsUseCaseTest {

	@Test
	fun `returns excluded ingredients from repository`() = runTest {
		val repository = FakeUserExcludedIngredientsRepository(setOf("Garlic", "Peanut"))
		val useCase = GetUserExcludedIngredientsUseCase(repository)

		val result = useCase()

		result shouldBe setOf("Garlic", "Peanut")
	}
}
