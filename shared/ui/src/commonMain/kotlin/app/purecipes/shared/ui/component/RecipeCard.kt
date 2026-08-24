package app.purecipes.shared.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

const val RECIPE_CARD_DELETE_BUTTON_TAG_PREFIX = "recipeCardDeleteButton:"
const val RECIPE_CARD_EDIT_BUTTON_TAG_PREFIX = "recipeCardEditButton:"
const val RECIPE_CARD_FAVORITE_ICON_TAG_PREFIX = "recipeCardFavoriteIcon:"

private const val RECIPE_CARD_ASPECT_RATIO_WIDTH = 16f
private const val RECIPE_CARD_ASPECT_RATIO_HEIGHT = 9f
private const val PREP_TIME_UNKNOWN = "Prep time unknown"
private const val QUICK_RECIPE_MAX_MINUTES = 30

@Composable
fun RecipeCard(
	recipe: RecipeSummary,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	widthFraction: Float = RecipeCardDefaults.FullWidthFraction,
	onEditClick: (() -> Unit)? = null,
	onDeleteClick: (() -> Unit)? = null,
	deleteContentDescription: String = "Delete recipe",
) {
	val tint = ContainerTint.forIndex(recipe.id)
	val colors = tint.colorFamily()
	Card(
		onClick = onClick,
		modifier = modifier.fillMaxWidth(widthFraction),
		colors = CardDefaults.cardColors(containerColor = colors.colorContainer),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			RecipeCardLabels(recipe = recipe, tint = tint)
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(RECIPE_CARD_ASPECT_RATIO_WIDTH / RECIPE_CARD_ASPECT_RATIO_HEIGHT)
					.clip(RoundedCornerShape(PurecipesTheme.space.m)),
			) {
				AsyncImage(
					model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
					contentDescription = recipe.title,
					modifier = Modifier
						.fillMaxSize()
						.background(colors.color),
					contentScale = ContentScale.Crop,
				)
				if (recipe.isFavorite || onEditClick != null || onDeleteClick != null) {
					Row(
						modifier = Modifier.align(Alignment.TopEnd),
						verticalAlignment = Alignment.CenterVertically,
					) {
						if (recipe.isFavorite) {
							Icon(
								imageVector = Icons.Filled.Favorite,
								contentDescription = "Favorited",
								tint = Color.White,
								modifier = Modifier
									.padding(PurecipesTheme.space.s)
									.testTag("$RECIPE_CARD_FAVORITE_ICON_TAG_PREFIX${recipe.id}"),
							)
						}
						if (onEditClick != null) {
							IconButton(
								onClick = onEditClick,
								modifier = Modifier.testTag("$RECIPE_CARD_EDIT_BUTTON_TAG_PREFIX${recipe.id}"),
							) {
								Icon(
									imageVector = Icons.Filled.Edit,
									contentDescription = "Edit recipe",
									tint = Color.White,
								)
							}
						}
						if (onDeleteClick != null) {
							IconButton(
								onClick = onDeleteClick,
								modifier = Modifier.testTag("$RECIPE_CARD_DELETE_BUTTON_TAG_PREFIX${recipe.id}"),
							) {
								Icon(
									imageVector = Icons.Filled.Delete,
									contentDescription = deleteContentDescription,
									tint = Color.White,
								)
							}
						}
					}
				}
			}
			Column(
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				Text(
					text = recipe.title,
					style = PurecipesTheme.typography.titleMedium,
					color = colors.onColorContainer,
				)
				Text(
					text = recipe.totalTime?.let { "$it min" } ?: PREP_TIME_UNKNOWN,
					style = PurecipesTheme.typography.bodyMedium,
					color = colors.onColorContainer,
				)
			}
		}
	}
}

@Composable
private fun RecipeCardLabels(
	recipe: RecipeSummary,
	tint: ContainerTint,
) {
	val labels = buildList {
		recipe.totalTime
			?.takeIf { minutes -> minutes <= QUICK_RECIPE_MAX_MINUTES }
			?.let { add("Quick" to ContainerTint.Tertiary) }
		recipe.cuisine?.displayName?.let { add(it to tint) }
		recipe.measurementSystem?.let { system ->
			add(system.displayName() to ContainerTint.Secondary)
		}
		if (recipe.isPrivate) {
			add("Private" to ContainerTint.Secondary)
		}
	}
	if (labels.isEmpty()) return
	Row(
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		labels.forEach { (text, chipTint) ->
			MetadataPillChip(
				text = text,
				tint = chipTint,
				filled = true,
			)
		}
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

private val previewRecipeCardFavorited = previewRecipeCardSample.copy(
	id = 4,
	title = "Favorite tomato pasta",
	isFavorite = true,
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

@Preview(
	name = "Recipe card with edit and delete",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeCardWithEditAndDeletePreview() {
	PurecipesTheme(darkTheme = false) {
		RecipeCard(
			recipe = previewRecipeCardSample,
			onClick = {},
			onEditClick = {},
			onDeleteClick = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "Recipe card favorited",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeCardFavoritedPreview() {
	PurecipesTheme(darkTheme = false) {
		RecipeCard(
			recipe = previewRecipeCardFavorited,
			onClick = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}
