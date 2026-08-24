package app.purecipes.shared.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun FavoriteHeartIcon(
	selected: Boolean,
	modifier: Modifier = Modifier,
	tint: Color? = null,
	contentDescription: String? = null,
) {
	val resolvedTint = tint ?: if (selected) {
		PurecipesTheme.colorScheme.primary
	} else {
		PurecipesTheme.colorScheme.onSurfaceVariant
	}
	val scale = remember { Animatable(1f) }
	LaunchedEffect(selected) {
		if (selected) {
			scale.snapTo(FAVORITE_SCALE_START)
			scale.animateTo(
				targetValue = 1f,
				animationSpec = spring(
					dampingRatio = Spring.DampingRatioMediumBouncy,
					stiffness = Spring.StiffnessMedium,
				),
			)
		} else {
			scale.snapTo(1f)
		}
	}
	Icon(
		imageVector = if (selected) {
			Icons.Filled.Favorite
		} else {
			Icons.Outlined.FavoriteBorder
		},
		contentDescription = contentDescription,
		tint = resolvedTint,
		modifier = modifier.graphicsLayer {
			scaleX = scale.value
			scaleY = scale.value
		},
	)
}

private const val FAVORITE_SCALE_START = 0.7f

@Preview(showBackground = true)
@Composable
private fun FavoriteHeartIconPreview() {
	PurecipesPreviewScaffold {
		FavoriteHeartIcon(selected = true)
	}
}
