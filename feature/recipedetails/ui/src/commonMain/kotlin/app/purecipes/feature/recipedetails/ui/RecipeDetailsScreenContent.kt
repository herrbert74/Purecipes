package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.DifficultyLevel
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeIngredient
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun RecipeDetailsScreenContent(
	darkTheme: Boolean,
	recipe: RecipeDetails,
	modifier: Modifier = Modifier,
	isRecipeConverted: Boolean = false,
	cookbookNames: ImmutableList<String> = persistentListOf(),
	canManageFavorites: Boolean = true,
	showNutrition: Boolean = false,
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
			RecipeDetailsContent(
				canManageFavorites = canManageFavorites,
				favoriteErrorMessage = null,
				isFavoriteUpdating = false,
				isRecipeConverted = isRecipeConverted,
				recipe = recipe,
				recipeCookbooks = RecipeCookbooksList(
					items = cookbookNames.mapIndexed { index, name ->
						CookbookRef(id = index + 1, name = name)
					},
				),
				showNutrition = showNutrition,
				onShowNutrition = {},
				onShowCookbookSheet = {},
				onStartCooking = {},
				onToggleFavorite = {},
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

val marketingRecipeDetails = RecipeDetails(
	id = 42,
	title = "Creamy Tuscan Chicken",
	description = "Seared chicken in a creamy sun-dried tomato sauce with spinach — " +
		"weeknight-friendly and restaurant-worthy.",
	imageUrl = null,
	ingredientGroups = listOf(
		IngredientGroup(
			name = "Chicken",
			ingredients = listOf(
				RecipeIngredient(text = "4 chicken breasts"),
				RecipeIngredient(text = "1 tsp salt"),
				RecipeIngredient(text = "1/2 tsp black pepper"),
			),
		),
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf(
				RecipeIngredient(text = "200 ml heavy cream"),
				RecipeIngredient(text = "80 g sun-dried tomatoes"),
				RecipeIngredient(text = "2 handfuls spinach"),
				RecipeIngredient(text = "2 garlic cloves"),
			),
		),
	),
	steps = listOf(
		"Season and sear the chicken until golden, then set aside.",
		"Sauté garlic, stir in cream and sun-dried tomatoes, then wilt the spinach.",
		"Return the chicken to the pan and simmer until cooked through.",
	),
	totalTime = 35,
	yields = "4 servings",
	cuisine = Cuisine.ITALIAN,
	measurementSystem = MeasurementSystem.METRIC,
	isFavorite = true,
	mealType = MealType.DINNER,
	difficultyLevel = DifficultyLevel.EASY,
	dietaryPreferences = setOf(DietaryPreference.GLUTEN_FREE),
)
