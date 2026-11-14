package app.purecipes.shared.ui.component.organism

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.atom.FavoriteHeartIcon
import app.purecipes.shared.ui.component.atom.FixedSubtitleText
import app.purecipes.shared.ui.component.atom.FixedTitleText
import app.purecipes.shared.ui.component.molecule.RecipeCardApplianceIcons
import app.purecipes.shared.ui.component.recipeImageSharedElement
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage
import kotlinx.collections.immutable.toImmutableSet

private const val PREP_TIME_UNKNOWN = "Prep time unknown"

@Composable
internal fun RecipeShelfCard(
	recipe: RecipeSummary,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	transitionKey: String? = null,
) {
	Card(
		onClick = onClick,
		modifier = modifier.width(RecipeCardDefaults.ShelfCardWidth),
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
					.aspectRatio(
						RecipeCardDefaults.ShelfImageAspectRatioWidth /
							RecipeCardDefaults.ShelfImageAspectRatioHeight,
					)
					.clip(RoundedCornerShape(PurecipesTheme.space.m)),
			) {
				AsyncImage(
					model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
					contentDescription = recipe.title,
					modifier = Modifier
						.fillMaxSize()
						.recipeImageSharedElement(
							key = transitionKey,
							cornerRadius = PurecipesTheme.space.m,
						)
						.background(PurecipesTheme.colorScheme.surfaceVariant),
					contentScale = ContentScale.Crop,
				)
				if (recipe.isFavorite) {
					Row(
						modifier = Modifier.align(Alignment.TopEnd),
						verticalAlignment = Alignment.CenterVertically,
					) {
						FavoriteHeartIcon(
							selected = true,
							tint = PurecipesTheme.fixedColors.inverseOnSurfaceFixed,
							contentDescription = "Favorited",
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
				FixedTitleText(
					text = recipe.title,
					color = PurecipesTheme.colorScheme.onSurface,
					style = PurecipesTheme.typography.titleSmall,
					minLines = RecipeCardDefaults.ShelfTitleLines,
					maxLines = RecipeCardDefaults.ShelfTitleLines,
				)
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					FixedSubtitleText(
						text = recipe.totalTime?.let { minutes -> "$minutes min" } ?: PREP_TIME_UNKNOWN,
						color = PurecipesTheme.colorScheme.onSurface,
						style = PurecipesTheme.typography.bodySmall,
					)
					RecipeCardApplianceIcons(
						appliances = recipe.appliances.toImmutableSet(),
						tint = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
				}
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
