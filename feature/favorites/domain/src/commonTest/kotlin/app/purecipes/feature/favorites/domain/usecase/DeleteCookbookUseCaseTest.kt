package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeleteCookbookUseCaseTest {

	@Test
	fun `delete cookbook use case delegates to repository`() = runTest {
		val repository = FakeCookbooksRepository()
		val useCase = DeleteCookbookUseCase(repository)

		val outcome = useCase(cookbookId = 42)

		outcome.getError() shouldBe null
		repository.deleteCookbookCallCount shouldBe 1
	}
}
