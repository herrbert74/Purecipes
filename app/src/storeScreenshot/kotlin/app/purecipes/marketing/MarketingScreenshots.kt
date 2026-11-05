package app.purecipes.marketing

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.cooking.ui.StepByStepCookingScreenContent
import app.purecipes.feature.cooking.ui.marketingCookingRecipe
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreenContent
import app.purecipes.feature.recipedetails.ui.marketingRecipeDetails
import app.purecipes.feature.search.ui.RecipeSearchScreenContent
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import com.android.tools.screenshot.PreviewTest
import kotlinx.collections.immutable.persistentListOf

private const val MARKETING_DEVICE = "spec:width=1080px,height=2340px,dpi=440"

private val marketingSearchRecipes = persistentListOf(
	RecipeSummary(
		id = 1,
		title = "Creamy Tuscan Chicken",
		cuisine = Cuisine.ITALIAN,
		imageUrl = MarketingImages.TUSCAN_CHICKEN,
		totalTime = 35,
	),
	RecipeSummary(
		id = 2,
		title = "Miso Ramen Bowl",
		cuisine = Cuisine.JAPANESE,
		imageUrl = MarketingImages.MISO_RAMEN,
		totalTime = 45,
	),
	RecipeSummary(
		id = 3,
		title = "Shakshuka",
		cuisine = Cuisine.MIDDLE_EASTERN,
		imageUrl = MarketingImages.SHAKSHUKA,
		totalTime = 25,
	),
	RecipeSummary(
		id = 4,
		title = "Street Tacos al Pastor",
		cuisine = Cuisine.MEXICAN,
		imageUrl = MarketingImages.TACOS,
		totalTime = 40,
	),
	RecipeSummary(
		id = 5,
		title = "Green Thai Curry",
		cuisine = Cuisine.THAI,
		imageUrl = MarketingImages.THAI_CURRY,
		totalTime = 30,
	),
	RecipeSummary(
		id = 6,
		title = "Ratatouille",
		cuisine = Cuisine.FRENCH,
		imageUrl = MarketingImages.RATATOUILLE,
		totalTime = 55,
	),
)

@PreviewTest
@Preview(name = "search", device = MARKETING_DEVICE, showBackground = true)
@Composable
private fun SearchMarketingScreenshot() {
	MarketingCoilPreview {
		RecipeSearchScreenContent(
			darkTheme = false,
			isSearchExpanded = false,
			searchQuery = "",
			hasActiveFilters = false,
			isSearching = false,
			errorMessage = null,
			totalMatches = 6,
			recipes = marketingSearchRecipes,
		)
	}
}

@PreviewTest
@Preview(name = "details", device = MARKETING_DEVICE, showBackground = true)
@Composable
private fun DetailsMarketingScreenshot() {
	MarketingCoilPreview {
		RecipeDetailsScreenContent(
			darkTheme = false,
			recipe = marketingRecipeDetails.copy(imageUrl = MarketingImages.TUSCAN_CHICKEN),
			isRecipeConverted = true,
			cookbookNames = persistentListOf("Weeknight", "Italian"),
		)
	}
}

@PreviewTest
@Preview(name = "cooking", device = MARKETING_DEVICE, showBackground = true)
@Composable
private fun CookingMarketingScreenshot() {
	StepByStepCookingScreenContent(
		darkTheme = false,
		recipe = marketingCookingRecipe,
		currentPageIndex = 1,
	)
}

@PreviewTest
@Preview(name = "units", device = MARKETING_DEVICE, showBackground = true)
@Composable
private fun UnitsMarketingScreenshot() {
	MarketingCoilPreview {
		RecipeSearchScreenContent(
			darkTheme = false,
			isSearchExpanded = true,
			searchQuery = "pasta",
			hasActiveFilters = true,
			isSearching = false,
			errorMessage = null,
			totalMatches = 6,
			recipes = marketingSearchRecipes,
		)
	}
}
