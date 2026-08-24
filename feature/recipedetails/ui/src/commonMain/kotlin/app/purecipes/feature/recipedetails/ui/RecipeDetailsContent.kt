package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.DifficultyLevel
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeIngredient
import app.purecipes.shared.domain.model.RecipeNutrition
import app.purecipes.shared.ui.component.ContainerTint
import app.purecipes.shared.ui.component.MetadataPillChip
import app.purecipes.shared.ui.component.RecipeDetailsSection
import app.purecipes.shared.ui.component.RecipeSectionSegmentedControl
import app.purecipes.shared.ui.component.RecipeStatChipsRow
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

const val RECIPE_DETAILS_CONTENT_TAG = "recipeDetailsContent"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RecipeDetailsContent(
	canManageFavorites: Boolean,
	favoriteErrorMessage: String?,
	isFavoriteUpdating: Boolean,
	isRecipeConverted: Boolean,
	recipe: RecipeDetails,
	recipeCookbooks: RecipeCookbooksList,
	showNutrition: Boolean,
	onShowNutrition: () -> Unit,
	onShowCookbookSheet: () -> Unit,
	onStartCooking: () -> Unit,
	onToggleFavorite: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val sections = if (showNutrition) {
		RecipeDetailsSection.entries.toImmutableList()
	} else {
		persistentListOf(
			RecipeDetailsSection.Ingredients,
			RecipeDetailsSection.Method,
		)
	}
	var selectedSection by remember {
		mutableStateOf(RecipeDetailsSection.Ingredients)
	}
	val activeSection = if (selectedSection in sections) {
		selectedSection
	} else {
		RecipeDetailsSection.Ingredients
	}
	val cookbookRefs = recipeCookbooks.items
	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.testTag(RECIPE_DETAILS_CONTENT_TAG),
		contentPadding = PaddingValues(
			start = PurecipesTheme.space.m,
			top = PurecipesTheme.space.m,
			end = PurecipesTheme.space.m,
			bottom = PurecipesTheme.space.l,
		),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		item {
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.fillMaxWidth()
					.height(240.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.l))
					.background(PurecipesTheme.colorScheme.surfaceContainerLow),
				contentScale = ContentScale.Crop,
			)
		}

		item {
			RecipeDetailsTopBarActions(
				canManageFavorites = canManageFavorites,
				isFavorite = recipe.isFavorite,
				isFavoriteUpdating = isFavoriteUpdating,
				hasRecipe = true,
				onToggleFavorite = onToggleFavorite,
				onShowCookbookSheet = onShowCookbookSheet,
			)
		}

		item {
			Text(
				text = recipe.description,
				style = PurecipesTheme.typography.bodyLarge,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}

		item {
			RecipeStatChipsRow(
				totalTimeMinutes = recipe.totalTime,
				servings = recipe.yields,
				difficulty = recipe.difficultyLevel?.displayName,
			)
		}

		item {
			RecipeMetadataRow(recipe = recipe, isRecipeConverted = isRecipeConverted)
		}

		item {
			if (cookbookRefs.isNotEmpty()) {
				FlowRow(
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					modifier = Modifier.fillMaxWidth(),
				) {
					cookbookRefs.forEach { cookbook ->
						MetadataPillChip(
							text = cookbook.name,
							tint = ContainerTint.Secondary,
						)
					}
				}
			}
		}

		item {
			RecipeDetailsActionButtons(
				canManageFavorites = canManageFavorites,
				favoriteErrorMessage = favoriteErrorMessage,
				isFavorite = recipe.isFavorite,
				isFavoriteUpdating = isFavoriteUpdating,
				hasSteps = recipe.steps.isNotEmpty(),
				onStartCooking = onStartCooking,
				onToggleFavorite = onToggleFavorite,
			)
		}

		item {
			RecipeSectionSegmentedControl(
				sections = sections,
				selectedSection = activeSection,
				onSectionChange = { section -> selectedSection = section },
			)
		}

		when (activeSection) {
			RecipeDetailsSection.Ingredients -> {
				items(recipe.ingredientGroups) { group ->
					IngredientGroupCard(group = group)
				}
			}

			RecipeDetailsSection.Method -> {
				items(recipe.steps.indices.toList()) { stepIndex ->
					StepCard(
						stepNumber = stepIndex + 1,
						step = recipe.steps[stepIndex],
					)
				}
			}

			RecipeDetailsSection.Nutrition -> {
				item {
					recipe.nutrition?.let { nutrition ->
						RecipeDetailsNutritionSection(
							nutrition = nutrition,
							onShowFullNutrition = onShowNutrition,
						)
					}
				}
			}
		}
	}
}

private val previewRecipeDetails = RecipeDetails(
	id = 42,
	title = "Tomato Pasta",
	description = "A quick weeknight dinner with fresh tomatoes and garlic.",
	imageUrl = "https://example.com/pasta.jpg",
	ingredientGroups = listOf(
		IngredientGroup(
			name = "Pasta",
			ingredients = listOf(
				RecipeIngredient(text = "400 g spaghetti"),
				RecipeIngredient(text = "Salt"),
			),
		),
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf(
				RecipeIngredient(text = "2 tomatoes"),
				RecipeIngredient(text = "1 garlic clove"),
				RecipeIngredient(text = "Olive oil"),
			),
		),
	),
	steps = listOf(
		"Bring a large pot of salted water to a boil.",
		"Cook the pasta until al dente.",
		"Sauté garlic, add tomatoes, and simmer until saucy.",
	),
	totalTime = 25,
	yields = "2 servings",
	cuisine = Cuisine.ITALIAN,
	measurementSystem = MeasurementSystem.METRIC,
	isFavorite = true,
	mealType = MealType.DINNER,
	difficultyLevel = DifficultyLevel.EASY,
	dietaryPreferences = setOf(DietaryPreference.VEGETARIAN),
	nutrition = RecipeNutrition(
		recipeTotals = NutritionSummary(
			calories = 640.0,
			protein = 22.0,
			carbohydrates = 84.0,
			fat = 18.0,
		),
		perServing = NutritionSummary(
			calories = 320.0,
			protein = 11.0,
			carbohydrates = 42.0,
			fat = 9.0,
		),
	),
)

@Preview(
	name = "Recipe details light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeDetailsContentLightPreview() {
	RecipeDetailsScreenContent(
		darkTheme = false,
		recipe = previewRecipeDetails,
		cookbookNames = persistentListOf("Weeknight", "Pasta"),
		showNutrition = true,
	)
}

@Preview(
	name = "Recipe details dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun RecipeDetailsContentDarkPreview() {
	RecipeDetailsScreenContent(
		darkTheme = true,
		recipe = previewRecipeDetails,
		cookbookNames = persistentListOf("Weeknight", "Pasta"),
		showNutrition = true,
	)
}
