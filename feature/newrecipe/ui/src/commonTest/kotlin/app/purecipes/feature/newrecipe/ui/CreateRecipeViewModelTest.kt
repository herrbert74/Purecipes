package app.purecipes.feature.newrecipe.ui

import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.usecase.EstimateRecipeNutritionUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeNutritionEstimateRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateRecipeViewModelTest {

	@Test
	fun `loading recipes exposes the existing saved list`() = runViewModelTest {
		val recipe = sampleCreatedRecipe()
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe))
		val viewModel = createViewModel(repository = repository)

		advanceUntilIdle()

		viewModel.recipes.toList() shouldBe listOf(recipe)
		viewModel.isLoading shouldBe false
	}

	@Test
	fun `save validates required fields`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		advanceUntilIdle()
		viewModel.saveRecipe()

		viewModel.formErrorMessage shouldBe "Add a recipe title."
		repository.savedRequests.isEmpty() shouldBe true
	}

	@Test
	fun `save adds a new recipe to the list`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		advanceUntilIdle()
		viewModel.onTitleChange("Tomato Pasta")
		viewModel.onDescriptionChange("Quick weeknight dinner.")
		viewModel.onIngredientsChange("200 g pasta\n2 tomatoes")
		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.addStep()
		viewModel.onStepChange(index = 1, value = "Finish with the tomatoes")
		viewModel.saveRecipe()

		advanceUntilIdle()

		viewModel.recipes.size shouldBe 1
		viewModel.recipes.single().title shouldBe "Tomato Pasta"
		viewModel.successMessage shouldBe "Recipe uploaded."
		viewModel.formErrorMessage shouldBe null
	}

	@Test
	fun `editing a recipe updates the existing item`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(sampleCreatedRecipe()))
		val viewModel = createViewModel(repository = repository)

		advanceUntilIdle()
		viewModel.editRecipe(viewModel.recipes.single())
		viewModel.onTitleChange("Creamy Tomato Pasta")
		viewModel.saveRecipe()

		advanceUntilIdle()

		viewModel.recipes.size shouldBe 1
		viewModel.recipes.single().title shouldBe "Creamy Tomato Pasta"
		viewModel.isEditing shouldBe true
	}

	@Test
	fun `add step appends a new editable step`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		advanceUntilIdle()
		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.addStep()
		viewModel.onStepChange(index = 1, value = "Finish with the tomatoes")

		viewModel.stepInputs.toList() shouldBe listOf("Boil the pasta", "Finish with the tomatoes")
	}

	@Test
	fun `move step up reorders the list`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		advanceUntilIdle()
		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.addStep()
		viewModel.onStepChange(index = 1, value = "Finish with the tomatoes")
		viewModel.moveStepUp(index = 1)

		viewModel.stepInputs.toList() shouldBe listOf("Finish with the tomatoes", "Boil the pasta")
	}

	@Test
	fun `ingredient changes request nutrition estimate`() = runViewModelTest {
		val estimateRepository = FakeRecipeNutritionEstimateRepository(
			estimateResult = Ok(
				NutritionSummary(
					calories = 120.0,
					matchedIngredientCount = 1,
					totalIngredientCount = 1,
				),
			),
		)
		val viewModel = createViewModel(estimateRepository = estimateRepository)

		advanceUntilIdle()
		viewModel.onIngredientsChange("1 cup sugar")
		advanceTimeBy(500)
		advanceUntilIdle()

		estimateRepository.lastIngredients shouldBe listOf("1 cup sugar")
		viewModel.nutritionEstimate?.calories shouldBe 120.0
	}

	private fun createViewModel(
		repository: FakeCreatedRecipeRepository = FakeCreatedRecipeRepository(),
		estimateRepository: FakeRecipeNutritionEstimateRepository = FakeRecipeNutritionEstimateRepository(),
	): CreateRecipeViewModel =
		CreateRecipeViewModel(
			getCreatedRecipes = GetCreatedRecipesUseCase(repository),
			saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
			estimateRecipeNutrition = EstimateRecipeNutritionUseCase(estimateRepository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
		)
}

private fun sampleCreatedRecipe(): RecipeDetails = RecipeDetails(
	id = -1,
	title = "Tomato Pasta",
	description = "Quick weeknight dinner.",
	imageUrl = null,
	ingredientGroups = listOf(
		IngredientGroup(
			ingredients = listOf("200 g pasta", "2 tomatoes"),
		),
	),
	steps = listOf("Boil the pasta", "Finish with the tomatoes"),
	totalTime = 20,
	yields = "2 servings",
	cuisine = Cuisine.ITALIAN,
)
