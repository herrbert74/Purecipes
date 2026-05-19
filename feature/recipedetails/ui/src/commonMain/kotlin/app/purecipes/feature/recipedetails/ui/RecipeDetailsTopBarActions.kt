package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.ui.component.NUTRITION_FACTS_BUTTON_TAG
import app.purecipes.shared.ui.component.PurecipesTextButton
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun RecipeDetailsTopBarActions(
	canManageFavorites: Boolean,
	isFavorite: Boolean,
	isFavoriteUpdating: Boolean,
	hasRecipe: Boolean,
	showNutrition: Boolean,
	onShowNutrition: () -> Unit,
	onToggleFavorite: () -> Unit,
	onShowCookbookSheet: () -> Unit,
) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		if (showNutrition) {
			IconButton(
				onClick = onShowNutrition,
				modifier = Modifier.testTag(NUTRITION_FACTS_BUTTON_TAG),
			) {
				Icon(
					imageVector = Icons.Filled.Restaurant,
					contentDescription = "View nutrition facts",
				)
			}
		}
		IconButton(
			onClick = onToggleFavorite,
			enabled = canManageFavorites && hasRecipe && !isFavoriteUpdating,
		) {
			Icon(
				imageVector = if (isFavorite) {
					Icons.Filled.Favorite
				} else {
					Icons.Outlined.FavoriteBorder
				},
				contentDescription = if (isFavorite) {
					"Remove from favorites"
				} else {
					"Add to favorites"
				},
				tint = if (isFavorite) {
					PurecipesTheme.colorScheme.primary
				} else {
					PurecipesTheme.colorScheme.onSurfaceVariant
				},
			)
		}
		PurecipesTextButton(
			text = "Add to cookbook",
			onClick = onShowCookbookSheet,
			modifier = Modifier,
			enabled = canManageFavorites && isFavorite && !isFavoriteUpdating,
		)
	}
}
