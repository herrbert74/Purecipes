package app.purecipes.feature.library.ui.myrecipes

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.usecase.DeleteCreatedRecipeUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.ObserveCreatedRecipeEventsUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import app.purecipes.shared.testfixtures.fake.recipeIngredients
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyRecipesViewModelTest {

	@Test
	fun `loading recipes maps details to summaries`() = runViewModelTest {
		val viewModel = createViewModel(
			repository = FakeCreatedRecipeRepository(initialRecipes = listOf(sampleRecipe())),
		)

		advanceUntilIdle()

		viewModel.isLoading shouldBe false
		viewModel.errorMessage shouldBe null
		viewModel.recipes.single().id shouldBe 42
		viewModel.recipes.single().title shouldBe "Tomato Pasta"
		viewModel.recipes.single().cuisine shouldBe Cuisine.ITALIAN
		viewModel.recipes.single().totalTime shouldBe 20
	}

	@Test
	fun `reload keeps existing recipes visible while refreshing`() = runViewModelTest {
		val viewModel = createViewModel(
			repository = FakeCreatedRecipeRepository(initialRecipes = listOf(sampleRecipe())),
		)
		advanceUntilIdle()

		viewModel.reload()

		viewModel.isLoading shouldBe false
		viewModel.recipes.single().id shouldBe 42

		advanceUntilIdle()

		viewModel.isLoading shouldBe false
		viewModel.recipes.single().id shouldBe 42
		viewModel.recipes.single().title shouldBe "Tomato Pasta"
	}

	@Test
	fun `deleting a recipe removes it and tracks recipe deleted`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(sampleRecipe()))
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			repository = repository,
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()

		viewModel.deleteRecipe(viewModel.recipes.single())
		advanceUntilIdle()

		viewModel.recipes.isEmpty() shouldBe true
		viewModel.errorMessage shouldBe null
		repository.deleteCreatedRecipeCallCount shouldBe 1
		repository.deletedRecipeIds.single() shouldBe 42
		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.RecipeDeleted(
			recipeId = 42,
			recipeName = "Tomato Pasta",
			isPrivate = false,
		)
	}

	@Test
	fun `delete failure keeps recipe and shows error`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository(
			initialRecipes = listOf(sampleRecipe()),
			deleteCreatedRecipeResult = Err(Failure.ServerError("Could not delete recipe")),
		)
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			repository = repository,
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()

		viewModel.deleteRecipe(viewModel.recipes.single())
		advanceUntilIdle()

		viewModel.recipes.single().id shouldBe 42
		viewModel.errorMessage shouldBe "Could not delete recipe"
		analyticsRepository.trackedEvents shouldBe emptyList()
	}

	@Test
	fun `creating a recipe refreshes my recipes list`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)
		advanceUntilIdle()
		viewModel.recipes shouldBe emptyList()

		repository.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Pesto Gnocchi",
				description = "Fast dinner.",
				steps = listOf("Boil gnocchi", "Toss with pesto"),
				cuisine = Cuisine.ITALIAN,
				totalTime = 15,
			),
		)
		advanceUntilIdle()

		viewModel.isLoading shouldBe false
		viewModel.errorMessage shouldBe null
		viewModel.recipes.single().title shouldBe "Pesto Gnocchi"
		viewModel.recipes.single().cuisine shouldBe Cuisine.ITALIAN
		viewModel.recipes.single().totalTime shouldBe 15
	}

	@Test
	fun `updating a recipe refreshes my recipes list`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(sampleRecipe()))
		val viewModel = createViewModel(repository = repository)
		advanceUntilIdle()

		repository.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				recipeId = 42,
				title = "Creamy Tomato Pasta",
				description = "Updated dinner.",
				steps = listOf("Boil the pasta", "Add cream"),
				cuisine = Cuisine.ITALIAN,
				totalTime = 25,
			),
		)
		advanceUntilIdle()

		viewModel.recipes.single().id shouldBe 42
		viewModel.recipes.single().title shouldBe "Creamy Tomato Pasta"
		viewModel.recipes.single().totalTime shouldBe 25
	}

	@Test
	fun `deleting a recipe via repository removes it from my recipes list`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(sampleRecipe()))
		val viewModel = createViewModel(repository = repository)
		advanceUntilIdle()

		repository.deleteCreatedRecipe(recipeId = 42)
		advanceUntilIdle()

		viewModel.recipes shouldBe emptyList()
	}

	private fun createViewModel(
		repository: FakeCreatedRecipeRepository,
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	): MyRecipesViewModel = MyRecipesViewModel(
		getCreatedRecipes = GetCreatedRecipesUseCase(repository),
		deleteCreatedRecipe = DeleteCreatedRecipeUseCase(repository),
		observeCreatedRecipeEvents = ObserveCreatedRecipeEventsUseCase(repository),
		trackEvent = TrackEventUseCase(analyticsRepository),
	)

	private fun sampleRecipe(): RecipeDetails = RecipeDetails(
		id = 42,
		title = "Tomato Pasta",
		description = "Quick weeknight dinner.",
		imageUrl = "https://example.com/pasta.jpg",
		ingredientGroups = listOf(
			IngredientGroup(
				ingredients = recipeIngredients("200 g pasta"),
			),
		),
		steps = listOf("Boil the pasta"),
		totalTime = 20,
		yields = "2 servings",
		cuisine = Cuisine.ITALIAN,
	)
}
