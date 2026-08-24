package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import app.purecipes.shared.ui.component.FavoriteHeartIcon
import app.purecipes.shared.ui.preview.PreviewScreenSizes
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RecipeDetailsTopBarActions(
	canManageFavorites: Boolean,
	isFavorite: Boolean,
	isFavoriteUpdating: Boolean,
	hasRecipe: Boolean,
	onToggleFavorite: () -> Unit,
	onShowCookbookSheet: () -> Unit,
	modifier: Modifier = Modifier,
) {
	FlowRow(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		FilterChip(
			selected = isFavorite,
			onClick = onToggleFavorite,
			label = { Text(text = "Favorites") },
			leadingIcon = {
				FavoriteHeartIcon(selected = isFavorite)
			},
			enabled = canManageFavorites && hasRecipe && !isFavoriteUpdating,
		)
		AssistChip(
			onClick = onShowCookbookSheet,
			label = { Text(text = "Cookbook") },
			leadingIcon = {
				Icon(
					imageVector = Icons.AutoMirrored.Outlined.MenuBook,
					contentDescription = null,
				)
			},
			enabled = canManageFavorites && isFavorite && !isFavoriteUpdating,
		)
	}
}

@PreviewScreenSizes
@PreviewLightDark
@Composable
private fun RecipeDetailsTopBarActionsPreview(
	@PreviewParameter(RecipeDetailsTopBarActionsPreviewParameterProvider::class)
	previewCase: RecipeDetailsTopBarActionsPreviewCase,
) {
	PurecipesPreviewScaffold {
		RecipeDetailsTopBarActions(
			canManageFavorites = previewCase.canManageFavorites,
			isFavorite = previewCase.isFavorite,
			isFavoriteUpdating = false,
			hasRecipe = true,
			onToggleFavorite = {},
			onShowCookbookSheet = {},
		)
	}
}

private data class RecipeDetailsTopBarActionsPreviewCase(
	val canManageFavorites: Boolean,
	val isFavorite: Boolean,
) {

	override fun toString(): String = if (canManageFavorites) {
		"Signed in"
	} else {
		"Signed out"
	}
}

private class RecipeDetailsTopBarActionsPreviewParameterProvider :
	PreviewParameterProvider<RecipeDetailsTopBarActionsPreviewCase> {

	override val values: Sequence<RecipeDetailsTopBarActionsPreviewCase> = sequenceOf(
		RecipeDetailsTopBarActionsPreviewCase(
			canManageFavorites = true,
			isFavorite = true,
		),
		RecipeDetailsTopBarActionsPreviewCase(
			canManageFavorites = true,
			isFavorite = false,
		),
		RecipeDetailsTopBarActionsPreviewCase(
			canManageFavorites = false,
			isFavorite = false,
		),
	)

	override fun getDisplayName(index: Int): String? {
		return when (index) {
			0 -> "Favorite"
			1 -> "NOT Favorite"
			else -> "Favorite disabled"
		}
	}
}
