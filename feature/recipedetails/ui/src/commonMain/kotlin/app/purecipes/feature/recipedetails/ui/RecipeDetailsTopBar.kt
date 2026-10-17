package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.feature.sharing.ui.ShareIconButton
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.theme.PurecipesTheme

const val RECIPE_DETAILS_TOP_BAR_TAG = "recipeDetailsTopBar"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipeDetailsTopBar(
	title: String,
	showBackNavigation: Boolean,
	isPrivate: Boolean,
	onBack: () -> Unit,
	onShare: () -> Unit,
	modifier: Modifier = Modifier,
	containerColor: Color = Color.Transparent,
) {
	Box(modifier = modifier.fillMaxWidth()) {
		if (containerColor == Color.Transparent) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(100.dp)
					.background(
						Brush.verticalGradient(
							colors = listOf(
								PurecipesTheme.colorScheme.surface.copy(alpha = 0.45f),
								Color.Transparent,
							),
						),
					),
			)
		}
		TopAppBar(
			title = {
				Text(
					text = title,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
				)
			},
			navigationIcon = {
				if (showBackNavigation) {
					BackNavigationButton(
						onBack = onBack,
						modifier = Modifier
							.clip(CircleShape)
							.background(PurecipesTheme.colorScheme.surface.copy(alpha = 0.75f)),
					)
				}
			},
			actions = {
				if (!isPrivate) {
					ShareIconButton(
						onShare = onShare,
						contentDescription = "Share recipe",
						modifier = Modifier
							.clip(CircleShape)
							.background(PurecipesTheme.colorScheme.surface.copy(alpha = 0.75f)),
					)
				}
			},
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = containerColor,
			),
			modifier = Modifier.testTag(RECIPE_DETAILS_TOP_BAR_TAG),
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun RecipeDetailsTopBarPreview() {
	PurecipesTheme {
		RecipeDetailsTopBar(
			title = "Roasted Carrots",
			showBackNavigation = true,
			isPrivate = false,
			onBack = {},
			onShare = {},
			containerColor = Color.Transparent,
		)
	}
}
