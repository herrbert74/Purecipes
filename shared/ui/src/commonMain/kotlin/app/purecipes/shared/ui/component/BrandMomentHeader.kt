package app.purecipes.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun BrandMomentHeader(
	icon: ImageVector,
	iconContentDescription: String,
	title: String,
	description: String,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.background(
				color = PurecipesTheme.colorScheme.primaryContainer,
				shape = RoundedCornerShape(PurecipesTheme.space.m),
			)
			.padding(PurecipesTheme.space.l),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		BrandMomentIllustration(
			icon = icon,
			contentDescription = iconContentDescription,
			size = 96.dp,
			iconSize = 48.dp,
		)
		Text(
			text = title,
			style = PurecipesTheme.typography.headlineMedium,
			color = PurecipesTheme.colorScheme.onPrimaryContainer,
			textAlign = TextAlign.Center,
		)
		Text(
			text = description,
			style = PurecipesTheme.typography.bodyLarge,
			color = PurecipesTheme.colorScheme.onPrimaryContainer,
			textAlign = TextAlign.Center,
		)
	}
}

@Preview
@Composable
private fun BrandMomentHeaderLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			BrandMomentHeader(
				icon = Icons.Filled.Favorite,
				iconContentDescription = "Account",
				title = "Your kitchen, saved",
				description = "Sign in to keep favorites, cookbooks, and uploads with you.",
			)
		}
	}
}

@Preview
@Composable
private fun BrandMomentHeaderDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			BrandMomentHeader(
				icon = Icons.Filled.Favorite,
				iconContentDescription = "Account",
				title = "Your kitchen, saved",
				description = "Sign in to keep favorites, cookbooks, and uploads with you.",
			)
		}
	}
}
