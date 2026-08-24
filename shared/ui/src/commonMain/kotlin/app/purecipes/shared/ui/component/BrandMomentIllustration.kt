package app.purecipes.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun BrandMomentIllustration(
	icon: ImageVector,
	contentDescription: String,
	modifier: Modifier = Modifier,
	size: Dp = 120.dp,
	iconSize: Dp = 64.dp,
) {
	Box(
		modifier = modifier
			.size(size)
			.background(
				color = PurecipesTheme.colorScheme.primary.copy(alpha = BrandMomentDefaults.illustrationAlpha),
				shape = CircleShape,
			),
		contentAlignment = Alignment.Center,
	) {
		Icon(
			imageVector = icon,
			contentDescription = contentDescription,
			modifier = Modifier.size(iconSize),
			tint = PurecipesTheme.colorScheme.primary,
		)
	}
}

@Preview
@Composable
private fun BrandMomentIllustrationLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface(color = PurecipesTheme.colorScheme.primaryContainer) {
			BrandMomentIllustration(
				icon = Icons.Filled.Favorite,
				contentDescription = "Favorites",
			)
		}
	}
}

@Preview
@Composable
private fun BrandMomentIllustrationDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface(color = PurecipesTheme.colorScheme.primaryContainer) {
			BrandMomentIllustration(
				icon = Icons.Filled.Favorite,
				contentDescription = "Favorites",
			)
		}
	}
}
