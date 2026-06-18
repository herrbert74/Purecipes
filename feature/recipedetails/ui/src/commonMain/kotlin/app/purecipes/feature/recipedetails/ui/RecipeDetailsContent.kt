package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeIngredient
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

const val RECIPE_DETAILS_CONTENT_TAG = "recipeDetailsContent"

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
				showNutrition = showNutrition,
				onShowNutrition = onShowNutrition,
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
			RecipeMetadataRow(recipe = recipe, isRecipeConverted = isRecipeConverted)
		}

		item {
			if (cookbookRefs.isNotEmpty()) {
				LazyRow(
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					modifier = Modifier.fillMaxWidth(),
				) {
					items(cookbookRefs.size, key = { cookbookRefs[it].id }) { index ->
						val cookbook = cookbookRefs[index]
						Surface(
							shape = RoundedCornerShape(999.dp),
							color = PurecipesTheme.colorScheme.secondaryContainer,
						) {
							Text(
								text = cookbook.name,
								modifier = Modifier.padding(
									horizontal = PurecipesTheme.space.s,
									vertical = PurecipesTheme.space.s,
								),
								style = PurecipesTheme.typography.labelLarge,
							)
						}
					}
				}
			}
		}

		item {
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				Button(
					onClick = onStartCooking,
					enabled = recipe.steps.isNotEmpty(),
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(text = "Start cooking")
				}
				Button(
					onClick = onToggleFavorite,
					enabled = canManageFavorites && !isFavoriteUpdating,
					modifier = Modifier.fillMaxWidth(),
				) {
					Text(
						text = if (!canManageFavorites) {
							"Sign in to save favorites"
						} else if (recipe.isFavorite) {
							"Remove from favorites"
						} else {
							"Add to favorites"
						},
					)
				}
				favoriteErrorMessage?.let {
					ErrorText(text = it)
				}
			}
		}

		item {
			Text(
				text = "Ingredients",
				style = PurecipesTheme.typography.titleLarge,
			)
		}

		items(recipe.ingredientGroups) { group ->
			IngredientGroupCard(group = group)
		}

		item {
			Text(
				text = "Steps",
				style = PurecipesTheme.typography.titleLarge,
			)
		}

		items(recipe.steps.indices.toList()) { stepIndex ->
			StepCard(
				stepNumber = stepIndex + 1,
				step = recipe.steps[stepIndex],
			)
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
)

@Preview(
	name = "Recipe details light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeDetailsContentLightPreview() {
	RecipeDetailsContentPreviewScaffold(darkTheme = false)
}

@Preview(
	name = "Recipe details dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun RecipeDetailsContentDarkPreview() {
	RecipeDetailsContentPreviewScaffold(darkTheme = true)
}

@Composable
private fun RecipeDetailsContentPreviewScaffold(darkTheme: Boolean) {
	PurecipesTheme(darkTheme = darkTheme) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = previewRecipeDetails.title) },
					navigationIcon = {
						BackNavigationButton(onBack = {})
					},
				)
			},
		) { innerPadding ->
			RecipeDetailsContent(
				canManageFavorites = true,
				favoriteErrorMessage = null,
				isFavoriteUpdating = false,
				isRecipeConverted = false,
				recipe = previewRecipeDetails,
				recipeCookbooks = RecipeCookbooksList(
					items = listOf(
						CookbookRef(id = 1, name = "Weeknight"),
						CookbookRef(id = 2, name = "Pasta"),
					),
				),
				showNutrition = false,
				onShowNutrition = {},
				onShowCookbookSheet = {},
				onStartCooking = {},
				onToggleFavorite = {},
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}
