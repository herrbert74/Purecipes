package app.purecipes.shared.ui.component.atom

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun FixedTitleText(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = PurecipesTheme.fixedColors.inverseOnSurfaceFixed,
	style: TextStyle = PurecipesTheme.typography.titleMedium,
	minLines: Int = 1,
	maxLines: Int = 2,
	isPlaceholder: Boolean = false,
) {
	Text(
		text = text,
		modifier = modifier.purecipesPlaceholder(visible = isPlaceholder),
		style = style,
		color = color,
		minLines = minLines,
		maxLines = maxLines,
		overflow = TextOverflow.Ellipsis,
	)
}

@Preview
@Composable
private fun FixedTitleTextPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			FixedTitleText(
				text = "Tomato pasta with basil",
				color = PurecipesTheme.colorScheme.onSurface,
			)
		}
	}
}

@Preview
@Composable
private fun FixedTitleTextPlaceholderPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			FixedTitleText(
				text = "Tomato pasta with basil",
				color = PurecipesTheme.colorScheme.onSurface,
				isPlaceholder = true,
			)
		}
	}
}
