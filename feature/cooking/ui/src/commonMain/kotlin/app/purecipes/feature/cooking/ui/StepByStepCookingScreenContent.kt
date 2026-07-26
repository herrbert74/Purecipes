package app.purecipes.feature.cooking.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeIngredient
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun StepByStepCookingScreenContent(
	darkTheme: Boolean,
	recipe: RecipeDetails,
	currentStepIndex: Int,
	modifier: Modifier = Modifier,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Scaffold(
			modifier = modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = recipe.title) },
					navigationIcon = {
						BackNavigationButton(onBack = {})
					},
				)
			},
		) { innerPadding ->
			StepByStepCookingScreen(
				recipe = recipe,
				currentStepIndex = currentStepIndex,
				onStepChange = {},
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

val marketingCookingRecipe = RecipeDetails(
	id = 42,
	title = "Creamy Tuscan Chicken",
	description = "Seared chicken in a creamy sun-dried tomato sauce.",
	imageUrl = null,
	ingredientGroups = listOf(
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf(
				RecipeIngredient(text = "200 ml heavy cream"),
				RecipeIngredient(text = "2 garlic cloves"),
			),
		),
	),
	steps = listOf(
		"Season the chicken with salt and pepper, then sear in a hot pan until golden on both sides.",
		"Remove the chicken. Sauté garlic, pour in the cream, and stir in sun-dried tomatoes.",
		"Add spinach until wilted, nestle the chicken back in, and simmer until cooked through.",
		"Taste, adjust seasoning, and serve with crusty bread or pasta.",
	),
	totalTime = 35,
	yields = "4 servings",
	cuisine = Cuisine.ITALIAN,
)
