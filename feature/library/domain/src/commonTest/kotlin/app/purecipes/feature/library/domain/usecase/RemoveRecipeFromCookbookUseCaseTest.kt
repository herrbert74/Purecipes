package app.purecipes.feature.library.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RemoveRecipeFromCookbookUseCaseTest {

	@Test
	fun `remove recipe from cookbook use case delegates to repository`() = runTest {
		val repository = FakeCookbooksRepository()
		val useCase = RemoveRecipeFromCookbookUseCase(repository)

		val outcome = useCase(cookbookId = 10, recipeId = 42)

		outcome.getError() shouldBe null
		repository.removeRecipeFromCookbookCallCount shouldBe 1
		repository.lastRemovedCookbookId shouldBe 10
		repository.lastRemovedRecipeId shouldBe 42
	}
}
