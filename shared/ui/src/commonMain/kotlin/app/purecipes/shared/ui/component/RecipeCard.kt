package app.purecipes.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

const val RECIPE_CARD_EDIT_BUTTON_TAG_PREFIX = "recipeCardEditButton:"

private const val RECIPE_CARD_ASPECT_RATIO_WIDTH = 16f
private const val RECIPE_CARD_ASPECT_RATIO_HEIGHT = 9f
private const val PREP_TIME_UNKNOWN = "Prep time unknown"

@Composable
fun RecipeCard(
	recipe: RecipeSummary,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	widthFraction: Float = RecipeCardDefaults.FullWidthFraction,
	onEditClick: (() -> Unit)? = null,
) {
	val fixedColors = PurecipesTheme.fixedColors
	Card(
		modifier = modifier.fillMaxWidth(widthFraction),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.aspectRatio(RECIPE_CARD_ASPECT_RATIO_WIDTH / RECIPE_CARD_ASPECT_RATIO_HEIGHT),
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.clickable(onClick = onClick),
			) {
				AsyncImage(
					model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
					contentDescription = recipe.title,
					modifier = Modifier
						.fillMaxSize()
						.background(PurecipesTheme.colorScheme.secondaryContainer),
					contentScale = ContentScale.Crop,
				)
				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(PurecipesTheme.fixedScrimBrush()),
				)
				Row(
					modifier = Modifier
						.align(Alignment.TopStart)
						.padding(PurecipesTheme.space.s),
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
				) {
					recipe.cuisine?.displayName?.let { cuisine ->
						RecipeCardLabel(
							text = cuisine,
							backgroundColor = fixedColors.primaryFixed,
							contentColor = fixedColors.onPrimaryFixed,
						)
					}
					recipe.measurementSystem?.let { measurementSystem ->
						RecipeCardLabel(
							text = measurementSystem.displayName(),
							backgroundColor = fixedColors.tertiaryFixed,
							contentColor = fixedColors.onTertiaryFixed,
						)
					}
				}
				Column(
					modifier = Modifier
						.align(Alignment.BottomStart)
						.padding(PurecipesTheme.space.m),
					verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
				) {
					FixedTitleText(text = recipe.title)
					FixedSubtitleText(
						text = recipe.totalTime?.let { "$it min" } ?: PREP_TIME_UNKNOWN,
					)
				}
			}
			if (onEditClick != null) {
				IconButton(
					onClick = onEditClick,
					modifier = Modifier
						.align(Alignment.TopEnd)
						.testTag("$RECIPE_CARD_EDIT_BUTTON_TAG_PREFIX${recipe.id}"),
				) {
					Icon(
						imageVector = Icons.Filled.Edit,
						contentDescription = "Edit recipe",
						tint = Color.White,
					)
				}
			}
		}
	}
}

@Composable
private fun RecipeCardLabel(
	text: String,
	backgroundColor: Color,
	contentColor: Color,
) {
	Surface(
		shape = RoundedCornerShape(999.dp),
		color = backgroundColor,
	) {
		Text(
			text = text,
			modifier = Modifier.padding(
				horizontal = PurecipesTheme.space.s,
				vertical = PurecipesTheme.space.xs,
			),
			style = PurecipesTheme.typography.labelLarge,
			color = contentColor,
		)
	}
}

private fun MeasurementSystem.displayName(): String = when (this) {
	MeasurementSystem.IMPERIAL -> "Imperial"
	MeasurementSystem.METRIC -> "Metric"
	MeasurementSystem.MIXED -> "Mixed"
}

private val previewRecipeCardSample = RecipeSummary(
	id = 1,
	title = "Tomato Pasta",
	cuisine = Cuisine.ITALIAN,
	imageUrl = null,
	totalTime = 20,
	measurementSystem = MeasurementSystem.METRIC,
)

private val previewRecipeCardSampleB = RecipeSummary(
	id = 2,
	title = "Smoked brisket with a very long title that may wrap to two lines",
	cuisine = Cuisine.AMERICAN,
	imageUrl = "https://picsum.photos/seed/purecipes-brisket/800/450",
	totalTime = 240,
	measurementSystem = MeasurementSystem.IMPERIAL,
)

private val previewRecipeCardSampleCuisineOnly = RecipeSummary(
	id = 3,
	title = "Green salad",
	cuisine = Cuisine.FRENCH,
	imageUrl = null,
	totalTime = null,
)

@Preview(
	name = "Recipe card light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeCardLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			RecipeCard(recipe = previewRecipeCardSample, onClick = {})
			RecipeCard(recipe = previewRecipeCardSampleCuisineOnly, onClick = {})
		}
	}
}

@Preview(
	name = "Recipe card dark with image",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun RecipeCardDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		RecipeCard(
			recipe = previewRecipeCardSampleB,
			onClick = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "Recipe card carousel width",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeCardCarouselWidthPreview() {
	PurecipesTheme(darkTheme = false) {
		RecipeCard(
			recipe = previewRecipeCardSampleB,
			onClick = {},
			widthFraction = RecipeCardDefaults.CarouselWidthFraction,
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "Recipe card with edit",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeCardWithEditPreview() {
	PurecipesTheme(darkTheme = false) {
		RecipeCard(
			recipe = previewRecipeCardSample,
			onClick = {},
			onEditClick = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}
