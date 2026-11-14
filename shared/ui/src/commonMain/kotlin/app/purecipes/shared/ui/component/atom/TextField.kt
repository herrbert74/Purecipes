package app.purecipes.shared.ui.component.atom

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun TitleText(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified,
	isPlaceholder: Boolean = false,
) {
	Text(
		text = text,
		modifier = modifier.purecipesPlaceholder(visible = isPlaceholder),
		style = PurecipesTheme.typography.titleMedium,
		maxLines = 1,
		color = color,
	)
}

@Composable
fun BodyText(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified,
	isPlaceholder: Boolean = false,
) {
	Text(
		text = text,
		modifier = modifier.purecipesPlaceholder(visible = isPlaceholder),
		style = PurecipesTheme.typography.bodyMedium,
		color = color,
	)
}
