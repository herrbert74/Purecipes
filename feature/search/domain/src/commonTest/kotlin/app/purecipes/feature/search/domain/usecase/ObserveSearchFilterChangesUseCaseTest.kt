package app.purecipes.feature.search.domain.usecase

import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveSearchFilterChangesUseCaseTest {

	@Test
	fun `emits when filters are saved`() = runTest {
		val repository = FakeRecipeSearchFilterRepository()
		val useCase = ObserveSearchFilterChangesUseCase(repository)

		val event = async { useCase().take(1).single() }
		runCurrent()

		repository.saveFilters(SearchFilters(cuisines = setOf(Cuisine.ITALIAN)))

		event.await() shouldBe Unit
	}
}
