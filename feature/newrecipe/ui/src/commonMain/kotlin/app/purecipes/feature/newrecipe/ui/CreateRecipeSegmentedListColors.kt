package app.purecipes.feature.newrecipe.ui

import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun createRecipeSegmentedListColors() = ListItemDefaults.colors(
	containerColor = PurecipesTheme.colorScheme.surfaceContainer,
)
