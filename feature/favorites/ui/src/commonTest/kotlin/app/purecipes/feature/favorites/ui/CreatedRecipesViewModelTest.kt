package app.purecipes.feature.favorites.ui

import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import app.purecipes.shared.testfixtures.fake.recipeIngredients
import app.purecipes.shared.testfixtures.runViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreatedRecipesViewModelTest {

	@Test
	fun `loading recipes maps details to summaries`() = runViewModelTest {
		val recipe = RecipeDetails(
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
		val viewModel = CreatedRecipesViewModel(
			getCreatedRecipes = GetCreatedRecipesUseCase(
				FakeCreatedRecipeRepository(initialRecipes = listOf(recipe)),
			),
		)

		advanceUntilIdle()

		viewModel.isLoading shouldBe false
		viewModel.errorMessage shouldBe null
		viewModel.recipes.single().id shouldBe 42
		viewModel.recipes.single().title shouldBe "Tomato Pasta"
		viewModel.recipes.single().cuisine shouldBe Cuisine.ITALIAN
		viewModel.recipes.single().totalTime shouldBe 20
	}
}
