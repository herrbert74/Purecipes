package app.purecipes.feature.search.domain.usecase

import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdateUserExcludedIngredientsUseCaseTest {

	@Test
	fun `forwards excluded ingredients delta to repository`() = runTest {
		val repository = FakeUserExcludedIngredientsRepository(setOf("Garlic"))
		val useCase = UpdateUserExcludedIngredientsUseCase(repository)

		val result = useCase(
			ExcludedIngredientsDelta(
				add = setOf("Peanut"),
				remove = setOf("Garlic"),
			),
		)

		result shouldBe setOf("Peanut")
	}
}
