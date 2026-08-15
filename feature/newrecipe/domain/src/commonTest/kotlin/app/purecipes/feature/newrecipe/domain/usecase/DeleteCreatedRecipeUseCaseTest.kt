package app.purecipes.feature.newrecipe.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteCreatedRecipeUseCaseTest {

	@Test
	fun `delete created recipe use case delegates to repository`() = runTest {
		val repository = FakeCreatedRecipeRepository()
		val useCase = DeleteCreatedRecipeUseCase(repository)

		val outcome = useCase(recipeId = 42)

		outcome.getError() shouldBe null
		repository.deleteCreatedRecipeCallCount shouldBe 1
		repository.deletedRecipeIds.single() shouldBe 42
	}
}
