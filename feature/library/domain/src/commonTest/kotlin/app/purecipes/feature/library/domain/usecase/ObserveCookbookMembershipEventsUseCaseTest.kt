package app.purecipes.feature.library.domain.usecase

import app.purecipes.feature.library.domain.model.CookbookMembershipEvent
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCookbookMembershipEventsUseCaseTest {

	@Test
	fun `observe cookbook membership events use case delegates to repository`() = runTest {
		val repository = FakeCookbooksRepository()
		val useCase = ObserveCookbookMembershipEventsUseCase(repository)
		val event = async { useCase().take(1).single() }
		runCurrent()

		repository.emitCookbookMembershipEvent(
			CookbookMembershipEvent.Removed(recipeId = 42, cookbookId = 10),
		)

		event.await() shouldBe CookbookMembershipEvent.Removed(recipeId = 42, cookbookId = 10)
	}
}
