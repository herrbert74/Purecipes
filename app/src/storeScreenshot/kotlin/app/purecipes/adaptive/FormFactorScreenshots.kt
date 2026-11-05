package app.purecipes.adaptive

import androidx.compose.runtime.Composable
import app.purecipes.feature.library.ui.LibraryScreenContent
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreenContent
import app.purecipes.feature.recipedetails.ui.marketingRecipeDetails
import app.purecipes.feature.search.ui.RecipeSearchScreenContent
import app.purecipes.marketing.MarketingCoilPreview
import app.purecipes.marketing.MarketingImages
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import com.android.tools.screenshot.PreviewTest
import kotlinx.collections.immutable.persistentListOf

private val formFactorRecipes = persistentListOf(
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
)

@PreviewTest
@PreviewFormFactors
@Composable
private fun MainShellFormFactorScreenshot() {
	MarketingCoilPreview {
		FormFactorMainShellContent(
			darkTheme = false,
			recipes = formFactorRecipes,
			totalMatches = formFactorRecipes.size,
		)
	}
}

@PreviewTest
@PreviewFormFactors
@Composable
private fun SearchFormFactorScreenshot() {
	MarketingCoilPreview {
		RecipeSearchScreenContent(
			darkTheme = false,
			isSearchExpanded = false,
			searchQuery = "",
			hasActiveFilters = false,
			isSearching = false,
			errorMessage = null,
			totalMatches = formFactorRecipes.size,
			recipes = formFactorRecipes,
		)
	}
}

@PreviewTest
@PreviewFormFactors
@Composable
private fun LibraryFormFactorScreenshot() {
	MarketingCoilPreview {
		LibraryScreenContent(
			darkTheme = false,
			recipes = formFactorRecipes,
			totalMatches = formFactorRecipes.size,
		)
	}
}

@PreviewTest
@PreviewFormFactors
@Composable
private fun DetailsFormFactorScreenshot() {
	MarketingCoilPreview {
		RecipeDetailsScreenContent(
			darkTheme = false,
			recipe = marketingRecipeDetails.copy(imageUrl = MarketingImages.TUSCAN_CHICKEN),
			isRecipeConverted = true,
			cookbookNames = persistentListOf("Weeknight", "Italian"),
		)
	}
}
