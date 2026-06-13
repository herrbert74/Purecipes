package app.purecipes.shared.testfixtures.fake

import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails

fun fakeRecipeDetails(
	id: Int = 42,
	title: String = "Tomato Pasta",
	description: String = "Simple dinner.",
	imageUrl: String? = "https://example.com/pasta.jpg",
	ingredientGroups: List<IngredientGroup> = listOf(
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf("2 tomatoes", "1 garlic clove"),
		),
	),
	steps: List<String> = listOf("Boil pasta", "Make sauce", "Serve"),
	totalTime: Int? = 25,
	yields: String? = "2 servings",
	cuisine: Cuisine? = Cuisine.ITALIAN,
	measurementSystem: MeasurementSystem? = null,
): RecipeDetails = RecipeDetails(
	id = id,
	title = title,
	description = description,
	imageUrl = imageUrl,
	ingredientGroups = ingredientGroups,
	steps = steps,
	totalTime = totalTime,
	yields = yields,
	cuisine = cuisine,
	measurementSystem = measurementSystem,
)
