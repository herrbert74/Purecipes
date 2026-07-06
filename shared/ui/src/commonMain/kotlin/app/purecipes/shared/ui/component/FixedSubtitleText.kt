package app.purecipes.shared.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.purecipes.shared.ui.theme.PurecipesTheme

private const val FIXED_SUBTITLE_ALPHA = 0.9f

@Composable
fun FixedSubtitleText(
	text: String,
	modifier: Modifier = Modifier,
) {
	Text(
		text = text,
		modifier = modifier,
		style = PurecipesTheme.typography.bodyMedium,
		color = PurecipesTheme.fixedColors.inverseOnSurfaceFixed.copy(alpha = FIXED_SUBTITLE_ALPHA),
		maxLines = 1,
		overflow = TextOverflow.Ellipsis,
	)
}
