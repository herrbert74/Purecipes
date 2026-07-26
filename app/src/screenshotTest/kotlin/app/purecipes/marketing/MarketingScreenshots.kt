package app.purecipes.marketing

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.search.ui.RecipeSearchScreenContent
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import com.android.tools.screenshot.PreviewTest
import kotlinx.collections.immutable.persistentListOf

private const val MARKETING_DEVICE = "spec:width=1080px,height=2340px,dpi=440"

private val marketingSearchRecipes = persistentListOf(
	RecipeSummary(id = 1, title = "Creamy Tuscan Chicken", cuisine = Cuisine.ITALIAN, imageUrl = null, totalTime = 35),
	RecipeSummary(id = 2, title = "Miso Ramen Bowl", cuisine = Cuisine.JAPANESE, imageUrl = null, totalTime = 45),
	RecipeSummary(id = 3, title = "Shakshuka", cuisine = Cuisine.MIDDLE_EASTERN, imageUrl = null, totalTime = 25),
	RecipeSummary(id = 4, title = "Street Tacos al Pastor", cuisine = Cuisine.MEXICAN, imageUrl = null, totalTime = 40),
	RecipeSummary(id = 5, title = "Green Thai Curry", cuisine = Cuisine.THAI, imageUrl = null, totalTime = 30),
	RecipeSummary(id = 6, title = "Ratatouille", cuisine = Cuisine.FRENCH, imageUrl = null, totalTime = 55),
)

@PreviewTest
@Preview(name = "search", device = MARKETING_DEVICE, showBackground = true)
@Composable
private fun SearchMarketingScreenshot() {
	RecipeSearchScreenContent(
		darkTheme = false,
		isSearchExpanded = false,
		searchQuery = "",
		hasActiveFilters = false,
		measurementLabel = null,
		isSearching = false,
		errorMessage = null,
		totalMatches = 6,
		recipes = marketingSearchRecipes,
	)
}
