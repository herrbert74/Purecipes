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

private const val FIXED_SUBTITLE_ALPHA = 0.9f

@Composable
fun FixedSubtitleText(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = PurecipesTheme.fixedColors.inverseOnSurfaceFixed.copy(alpha = FIXED_SUBTITLE_ALPHA),
	style: TextStyle = PurecipesTheme.typography.bodyMedium,
	minLines: Int = 1,
	maxLines: Int = 1,
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
private fun FixedSubtitleTextPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			FixedSubtitleText(
				text = "25 min",
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Preview
@Composable
private fun FixedSubtitleTextPlaceholderPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			FixedSubtitleText(
				text = "25 min",
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
				isPlaceholder = true,
			)
		}
	}
}
