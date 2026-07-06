package app.purecipes.shared.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun FixedTitleText(
	text: String,
	modifier: Modifier = Modifier,
) {
	Text(
		text = text,
		modifier = modifier,
		style = PurecipesTheme.typography.titleMedium,
		color = PurecipesTheme.fixedColors.inverseOnSurfaceFixed,
		maxLines = 2,
		overflow = TextOverflow.Ellipsis,
	)
}
