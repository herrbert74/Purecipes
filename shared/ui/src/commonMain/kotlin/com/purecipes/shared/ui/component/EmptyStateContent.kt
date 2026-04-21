package com.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun EmptyStateContent(
	icon: ImageVector,
	iconContentDescription: String,
	title: String,
	description: String,
	modifier: Modifier = Modifier,
	action: @Composable (() -> Unit)? = null,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(PurecipesTheme.space.l),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Icon(
				imageVector = icon,
				contentDescription = iconContentDescription,
				modifier = Modifier.size(56.dp),
				tint = PurecipesTheme.colorScheme.primary,
			)
			Text(
				text = title,
				style = PurecipesTheme.typography.headlineSmall,
			)
			Text(
				text = description,
				style = PurecipesTheme.typography.bodyLarge,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center,
			)
			action?.invoke()
		}
	}
}

@Preview
@Composable
private fun EmptyStateContentLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			EmptyStateContent(
				icon = Icons.Filled.Favorite,
				iconContentDescription = "Favorites",
				title = "No favorites yet",
				description = "Add recipes from the details screen and they will appear here.",
			)
		}
	}
}

@Preview
@Composable
private fun EmptyStateContentDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			EmptyStateContent(
				icon = Icons.Filled.Favorite,
				iconContentDescription = "Favorites",
				title = "No favorites yet",
				description = "Add recipes from the details screen and they will appear here.",
			)
		}
	}
}
