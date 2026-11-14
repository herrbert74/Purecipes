package app.purecipes.screenshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionConfidence
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.component.atom.BodyText
import app.purecipes.shared.ui.component.atom.ContainerTint
import app.purecipes.shared.ui.component.atom.MetadataPillChip
import app.purecipes.shared.ui.component.atom.OutlinedStatChip
import app.purecipes.shared.ui.component.atom.PurecipesButton
import app.purecipes.shared.ui.component.atom.PurecipesOutlinedButton
import app.purecipes.shared.ui.component.atom.PurecipesTextButton
import app.purecipes.shared.ui.component.atom.TitleText
import app.purecipes.shared.ui.component.molecule.BrowseTileGrid
import app.purecipes.shared.ui.component.molecule.EmptyStateContent
import app.purecipes.shared.ui.component.molecule.RecipeDetailsSection
import app.purecipes.shared.ui.component.molecule.RecipeSectionSegmentedControl
import app.purecipes.shared.ui.component.molecule.SectionHeader
import app.purecipes.shared.ui.component.molecule.TintedFeatureCard
import app.purecipes.shared.ui.component.molecule.browseTiles
import app.purecipes.shared.ui.component.organism.BrandMomentHeader
import app.purecipes.shared.ui.component.organism.NutritionSummaryCard
import app.purecipes.shared.ui.component.organism.RecipeCard
import app.purecipes.shared.ui.component.organism.RecipeCardDefaults
import app.purecipes.shared.ui.component.organism.RecipeCardSkeleton
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.android.tools.screenshot.PreviewTest
import kotlinx.collections.immutable.persistentListOf

private const val SAMPLE_RECIPE_ID = 1
private const val SAMPLE_TOTAL_TIME_MINUTES = 35
private const val SAMPLE_CALORIES = 520.0
private const val SAMPLE_PROTEIN = 32.5
private const val SAMPLE_CARBOHYDRATES = 48.0
private const val SAMPLE_FAT = 21.0
private const val SAMPLE_MATCHED_INGREDIENTS = 6
private const val SAMPLE_TOTAL_INGREDIENTS = 6

private val sampleRecipe = RecipeSummary(
	id = SAMPLE_RECIPE_ID,
	title = "Creamy Tuscan Chicken",
	cuisine = Cuisine.ITALIAN,
	imageUrl = "https://example.com/tuscan-chicken.jpg",
	totalTime = SAMPLE_TOTAL_TIME_MINUTES,
	isFavorite = true,
)

private val sampleNutrition = NutritionSummary(
	calories = SAMPLE_CALORIES,
	protein = SAMPLE_PROTEIN,
	carbohydrates = SAMPLE_CARBOHYDRATES,
	fat = SAMPLE_FAT,
	matchedIngredientCount = SAMPLE_MATCHED_INGREDIENTS,
	totalIngredientCount = SAMPLE_TOTAL_INGREDIENTS,
	calculationSource = NutritionCalculationSource.CALCULATED,
	confidence = NutritionConfidence.COMPLETE,
	isComplete = true,
)

@PreviewTest
@Preview(name = "recipe_card_light", showBackground = true)
@Composable
private fun RecipeCardLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		ScreenshotCoilPreview {
			RecipeCard(
				recipe = sampleRecipe,
				onClick = {},
			)
		}
	}
}

@PreviewTest
@Preview(name = "recipe_card_dark", showBackground = true)
@Composable
private fun RecipeCardDarkScreenshot() {
	ScreenshotSurface(darkTheme = true) {
		ScreenshotCoilPreview {
			RecipeCard(
				recipe = sampleRecipe,
				onClick = {},
			)
		}
	}
}

@PreviewTest
@Preview(name = "recipe_shelf_card_light", showBackground = true)
@Composable
private fun RecipeShelfCardLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		ScreenshotCoilPreview {
			RecipeCard(
				recipe = sampleRecipe,
				onClick = {},
				layout = RecipeCardDefaults.Layout.SHELF,
			)
		}
	}
}

@PreviewTest
@Preview(name = "purecipes_button_states", showBackground = true)
@Composable
private fun PurecipesButtonStatesScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
			PurecipesButton(
				text = "Start cooking",
				onClick = {},
				enabled = true,
			)
			PurecipesButton(
				text = "Start cooking",
				onClick = {},
				enabled = false,
			)
		}
	}
}

@PreviewTest
@Preview(name = "metadata_pill_chips", showBackground = true)
@Composable
private fun MetadataPillChipsScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		Row(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
			MetadataPillChip(
				text = "Italian",
				tint = ContainerTint.Primary,
			)
			MetadataPillChip(
				text = "Favorite",
				icon = AppIcons.Favorite,
				tint = ContainerTint.Secondary,
				filled = true,
			)
		}
	}
}

@PreviewTest
@Preview(name = "section_header_light", showBackground = true)
@Composable
private fun SectionHeaderLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		SectionHeader(
			title = "Notifications",
			subtitle = "Manage push notification settings across devices.",
		)
	}
}

@PreviewTest
@Preview(name = "nutrition_summary_card_light", showBackground = true)
@Composable
private fun NutritionSummaryCardLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		NutritionSummaryCard(
			nutrition = sampleNutrition,
			isLoading = false,
		)
	}
}

@PreviewTest
@Preview(name = "empty_state_content_light", showBackground = true)
@Composable
private fun EmptyStateContentLightScreenshot() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			EmptyStateContent(
				icon = AppIcons.Favorite,
				iconContentDescription = "Favorites",
				title = "No favorites yet",
				description = "Add recipes from the details screen and they will appear here.",
			)
		}
	}
}

@PreviewTest
@Preview(name = "recipe_card_skeleton_light", showBackground = true)
@Composable
private fun RecipeCardSkeletonLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		RecipeCardSkeleton()
	}
}

@PreviewTest
@Preview(name = "brand_moment_header_light", showBackground = true)
@Composable
private fun BrandMomentHeaderLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		BrandMomentHeader(
			icon = AppIcons.Favorite,
			iconContentDescription = "Account",
			title = "Your kitchen, saved",
			description = "Sign in to keep favorites, cookbooks, and uploads with you.",
		)
	}
}

@PreviewTest
@Preview(name = "browse_tile_grid_light", showBackground = true)
@Composable
private fun BrowseTileGridLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		BrowseTileGrid(
			tiles = browseTiles(SearchFilters()),
			onTileClick = {},
		)
	}
}

@PreviewTest
@Preview(name = "recipe_section_segmented_control_light", showBackground = true)
@Composable
private fun RecipeSectionSegmentedControlLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		RecipeSectionSegmentedControl(
			sections = persistentListOf(
				RecipeDetailsSection.Ingredients,
				RecipeDetailsSection.Method,
				RecipeDetailsSection.Nutrition,
			),
			selectedSection = RecipeDetailsSection.Ingredients,
			onSectionChange = {},
		)
	}
}

@PreviewTest
@Preview(name = "tinted_feature_card_dark", showBackground = true)
@Composable
private fun TintedFeatureCardDarkScreenshot() {
	ScreenshotSurface(darkTheme = true) {
		TintedFeatureCard(
			tint = ContainerTint.Primary,
			label = "Featured",
		) {
			Column(
				modifier = Modifier.padding(PurecipesTheme.space.m),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				TitleText(text = "Seasonal Picks")
				BodyText(text = "Fresh weeknight dinners curated from your saved preferences.")
			}
		}
	}
}

@PreviewTest
@Preview(name = "secondary_actions_and_stat_chips_light", showBackground = true)
@Composable
private fun SecondaryActionsAndStatChipsLightScreenshot() {
	ScreenshotSurface(darkTheme = false) {
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
			PurecipesOutlinedButton(
				text = "Send Test Notification",
				onClick = {},
			)
			Row(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				PurecipesTextButton(
					text = "Add to cookbook",
					onClick = {},
				)
				OutlinedStatChip(
					label = "Prep",
					value = "15 min",
					tint = ContainerTint.Secondary,
				)
			}
		}
	}
}

@Composable
private fun ScreenshotSurface(
	darkTheme: Boolean,
	content: @Composable () -> Unit,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Surface(color = PurecipesTheme.colorScheme.background) {
			Column(modifier = Modifier.padding(PurecipesTheme.space.m)) {
				content()
			}
		}
	}
}
