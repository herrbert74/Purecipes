package app.purecipes.shared.ui.component

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun ErrorText(
	text: String,
	modifier: Modifier = Modifier,
	textAlign: TextAlign? = null,
) {
	Text(
		text = text,
		modifier = modifier,
		style = PurecipesTheme.typography.bodyMedium,
		color = PurecipesTheme.colorScheme.error,
		textAlign = textAlign,
	)
}

@Preview
@Composable
private fun ErrorTextLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			ErrorText(text = "Something went wrong. Please try again.")
		}
	}
}

@Preview
@Composable
private fun ErrorTextDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			ErrorText(text = "Something went wrong. Please try again.")
		}
	}
}
