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
import app.purecipes.shared.ui.component.atom.ContainerTint
import app.purecipes.shared.ui.component.atom.MetadataPillChip
import app.purecipes.shared.ui.component.atom.PurecipesButton
import app.purecipes.shared.ui.component.molecule.EmptyStateContent
import app.purecipes.shared.ui.component.molecule.SectionHeader
import app.purecipes.shared.ui.component.organism.NutritionSummaryCard
import app.purecipes.shared.ui.component.organism.RecipeCard
import app.purecipes.shared.ui.component.organism.RecipeCardDefaults
import app.purecipes.shared.ui.component.organism.RecipeCardSkeleton
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.android.tools.screenshot.PreviewTest

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
