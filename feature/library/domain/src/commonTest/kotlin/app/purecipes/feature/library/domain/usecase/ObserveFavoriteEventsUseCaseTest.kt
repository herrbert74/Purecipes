package app.purecipes.feature.library.domain.usecase

import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveFavoriteEventsUseCaseTest {

	@Test
	fun `add favorite emits added event`() = runTest {
		val repository = FakeFavoritesRepository()
		val useCase = ObserveFavoriteEventsUseCase(repository)
		val event = async { useCase().take(1).single() }
		runCurrent()

		repository.addFavorite(recipeId = 42)

		event.await() shouldBe FavoriteEvent.Added(recipeId = 42)
	}

	@Test
	fun `remove favorite emits removed event`() = runTest {
		val repository = FakeFavoritesRepository()
		val useCase = ObserveFavoriteEventsUseCase(repository)
		val event = async { useCase().take(1).single() }
		runCurrent()

		repository.removeFavorite(recipeId = 7)

		event.await() shouldBe FavoriteEvent.Removed(recipeId = 7)
	}
}
