package app.purecipes.feature.cooking.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeIngredient
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.PurecipesOutlinedButton
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

internal const val COOKING_FINISHED_CONTENT_TAG = "cookingFinishedContent"
internal const val COOKING_ADD_TO_COOKBOOK_BUTTON_TAG = "cookingAddToCookbookButton"

@Composable
internal fun CookingFinishedContent(
	recipe: RecipeDetails,
	canManageFavorites: Boolean,
	isFavoriteUpdating: Boolean,
	favoriteErrorMessage: String?,
	onToggleFavorite: () -> Unit,
	onShare: () -> Unit,
	onShowCookbookSheet: () -> Unit,
	onDone: () -> Unit,
	onFindMoreRecipes: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.testTag(COOKING_FINISHED_CONTENT_TAG),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.verticalScroll(rememberScrollState()),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			AsyncImage(
				model = recipe.imageUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = recipe.title,
				modifier = Modifier
					.fillMaxWidth()
					.height(180.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.l))
					.background(PurecipesTheme.colorScheme.surfaceContainerLow),
				contentScale = ContentScale.Crop,
			)
			Text(
				text = "Enjoy your meal",
				style = PurecipesTheme.typography.headlineSmall,
				color = PurecipesTheme.colorScheme.primary,
				textAlign = TextAlign.Center,
			)
			Text(
				text = recipe.title,
				style = PurecipesTheme.typography.titleLarge,
				color = PurecipesTheme.colorScheme.onSurface,
				textAlign = TextAlign.Center,
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				FilledTonalButton(
					onClick = onToggleFavorite,
					enabled = canManageFavorites && !isFavoriteUpdating,
					modifier = Modifier.weight(1f),
				) {
					Icon(
						imageVector = if (recipe.isFavorite) {
							Icons.Filled.Favorite
						} else {
							Icons.Outlined.FavoriteBorder
						},
						contentDescription = null,
						modifier = Modifier.size(ButtonDefaults.IconSize),
					)
					Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
					Text(text = favoriteButtonLabel(canManageFavorites, recipe.isFavorite))
				}
				if (!recipe.isPrivate) {
					FilledTonalButton(
						onClick = onShare,
						modifier = Modifier.weight(1f),
					) {
						Icon(
							imageVector = Icons.Filled.Share,
							contentDescription = null,
							modifier = Modifier.size(ButtonDefaults.IconSize),
						)
						Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
						Text(text = "Share")
					}
				}
			}
			if (canManageFavorites && recipe.isFavorite) {
				FilledTonalButton(
					onClick = onShowCookbookSheet,
					enabled = !isFavoriteUpdating,
					modifier = Modifier
						.fillMaxWidth()
						.testTag(COOKING_ADD_TO_COOKBOOK_BUTTON_TAG),
				) {
					Icon(
						imageVector = Icons.AutoMirrored.Outlined.MenuBook,
						contentDescription = null,
						modifier = Modifier.size(ButtonDefaults.IconSize),
					)
					Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
					Text(text = "Add to cookbook")
				}
			}
			favoriteErrorMessage?.let { message ->
				ErrorText(
					text = message,
					textAlign = TextAlign.Center,
				)
			}
		}
		Button(
			onClick = onDone,
			modifier = Modifier.fillMaxWidth(),
		) {
			Text(text = "Done")
		}
		PurecipesOutlinedButton(
			text = "Find more recipes",
			onClick = onFindMoreRecipes,
		)
	}
}

private fun favoriteButtonLabel(canManageFavorites: Boolean, isFavorite: Boolean): String = when {
	!canManageFavorites -> "Sign in to save"
	isFavorite -> "Saved"
	else -> "Favorite"
}

private val previewFinishedRecipe = RecipeDetails(
	id = 42,
	title = "Tomato Pasta",
	description = "A quick weeknight dinner.",
	imageUrl = "https://example.com/pasta.jpg",
	ingredientGroups = listOf(
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf(
				RecipeIngredient(text = "2 tomatoes"),
				RecipeIngredient(text = "1 garlic clove"),
			),
		),
	),
	steps = listOf("Boil pasta", "Make sauce", "Serve"),
	totalTime = 25,
	yields = "2 servings",
	cuisine = Cuisine.ITALIAN,
)

@Preview(
	name = "Cooking finished light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CookingFinishedContentLightPreview() {
	PurecipesTheme(darkTheme = false) {
		CookingFinishedContent(
			recipe = previewFinishedRecipe,
			canManageFavorites = true,
			isFavoriteUpdating = false,
			favoriteErrorMessage = null,
			onToggleFavorite = {},
			onShare = {},
			onShowCookbookSheet = {},
			onDone = {},
			onFindMoreRecipes = {},
		)
	}
}

@Preview(
	name = "Cooking finished dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun CookingFinishedContentDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		CookingFinishedContent(
			recipe = previewFinishedRecipe.copy(isFavorite = true),
			canManageFavorites = true,
			isFavoriteUpdating = false,
			favoriteErrorMessage = null,
			onToggleFavorite = {},
			onShare = {},
			onShowCookbookSheet = {},
			onDone = {},
			onFindMoreRecipes = {},
		)
	}
}

@Preview(
	name = "Cooking finished signed out",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CookingFinishedContentSignedOutPreview() {
	PurecipesTheme(darkTheme = false) {
		CookingFinishedContent(
			recipe = previewFinishedRecipe,
			canManageFavorites = false,
			isFavoriteUpdating = false,
			favoriteErrorMessage = null,
			onToggleFavorite = {},
			onShare = {},
			onShowCookbookSheet = {},
			onDone = {},
			onFindMoreRecipes = {},
		)
	}
}
