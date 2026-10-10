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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

private const val SHELF_CARD_ASPECT_RATIO_WIDTH = 4f
private const val SHELF_CARD_ASPECT_RATIO_HEIGHT = 3f
private const val SHELF_TITLE_MAX_LINES = 2
private val SHELF_CARD_WIDTH = 160.dp
private const val PREP_TIME_UNKNOWN = "Prep time unknown"

@Composable
internal fun RecipeShelfCard(
	recipe: RecipeSummary,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		modifier = modifier.width(SHELF_CARD_WIDTH),
		colors = CardDefaults.cardColors(
			containerColor = PurecipesTheme.colorScheme.surface,
		),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.aspectRatio(SHELF_CARD_ASPECT_RATIO_WIDTH / SHELF_CARD_ASPECT_RATIO_HEIGHT)
					.clip(RoundedCornerShape(PurecipesTheme.space.m)),
			) {
				AsyncImage(
					model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
					contentDescription = recipe.title,
					modifier = Modifier
						.fillMaxSize()
						.background(PurecipesTheme.colorScheme.surfaceVariant),
					contentScale = ContentScale.Crop,
				)
				if (recipe.isFavorite) {
					Row(
						modifier = Modifier.align(Alignment.TopEnd),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							imageVector = AppIcons.Favorite,
							contentDescription = "Favorited",
							tint = Color.White,
							modifier = Modifier
								.padding(PurecipesTheme.space.s)
								.testTag("$RECIPE_CARD_FAVORITE_ICON_TAG_PREFIX${recipe.id}"),
						)
					}
				}
			}
			Column(
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				Text(
					text = recipe.title,
					style = PurecipesTheme.typography.titleSmall,
					color = PurecipesTheme.colorScheme.onSurface,
					minLines = SHELF_TITLE_MAX_LINES,
					maxLines = SHELF_TITLE_MAX_LINES,
					overflow = TextOverflow.Ellipsis,
				)
				Text(
					text = recipe.totalTime?.let { minutes -> "$minutes min" } ?: PREP_TIME_UNKNOWN,
					style = PurecipesTheme.typography.bodySmall,
					color = PurecipesTheme.colorScheme.onSurface,
				)
			}
		}
	}
}

@Preview(
	name = "Recipe card shelf",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun RecipeShelfCardPreview() {
	PurecipesTheme(darkTheme = false) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			modifier = Modifier.padding(PurecipesTheme.space.m),
		) {
			RecipeShelfCard(
				recipe = RecipeSummary(
					id = 1,
					title = "Tomato Pasta",
					cuisine = Cuisine.ITALIAN,
					imageUrl = null,
					totalTime = 20,
				),
				onClick = {},
			)
			RecipeShelfCard(
				recipe = RecipeSummary(
					id = 2,
					title = "Tomato pasta with basil and olive oil",
					cuisine = Cuisine.ITALIAN,
					imageUrl = null,
					totalTime = 25,
				),
				onClick = {},
			)
		}
	}
}
