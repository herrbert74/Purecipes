package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.PurecipesOutlinedButton
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun RecipeDetailsActionButtons(
	canManageFavorites: Boolean,
	favoriteErrorMessage: String?,
	isFavorite: Boolean,
	isFavoriteUpdating: Boolean,
	hasSteps: Boolean,
	onStartCooking: () -> Unit,
	onToggleFavorite: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		PurecipesButton(
			text = "Start cooking",
			onClick = onStartCooking,
			enabled = hasSteps,
		)
		PurecipesOutlinedButton(
			text = when {
				!canManageFavorites -> "Sign in to save favorites"
				isFavorite -> "Remove from favorites"
				else -> "Add to favorites"
			},
			onClick = onToggleFavorite,
			enabled = canManageFavorites && !isFavoriteUpdating,
		)
		favoriteErrorMessage?.let { message ->
			ErrorText(text = message)
		}
	}
}
