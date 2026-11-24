package app.purecipes.feature.newrecipe.domain.usecase

import app.purecipes.feature.newrecipe.domain.model.CreatedRecipeEvent
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import com.github.michaelbull.result.get
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCreatedRecipeEventsUseCaseTest {

	@Test
	fun `save created recipe emits saved event`() = runTest {
		val repository = FakeCreatedRecipeRepository()
		val useCase = ObserveCreatedRecipeEventsUseCase(repository)
		val event = async { useCase().take(1).single() }
		runCurrent()

		val saved = repository.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				steps = listOf("Boil the pasta"),
			),
		).get().shouldNotBeNull()

		event.await() shouldBe CreatedRecipeEvent.Saved(recipeId = saved.id)
	}

	@Test
	fun `delete created recipe emits deleted event`() = runTest {
		val repository = FakeCreatedRecipeRepository()
		val useCase = ObserveCreatedRecipeEventsUseCase(repository)
		val event = async { useCase().take(1).single() }
		runCurrent()

		repository.deleteCreatedRecipe(recipeId = 42)

		event.await() shouldBe CreatedRecipeEvent.Deleted(recipeId = 42)
	}
}
