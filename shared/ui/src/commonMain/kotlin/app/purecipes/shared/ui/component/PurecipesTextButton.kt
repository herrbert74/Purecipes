package app.purecipes.shared.ui.component

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun PurecipesTextButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	isPlaceholder: Boolean = false,
	colors: ButtonColors = ButtonDefaults.textButtonColors(),
) {
	TextButton(
		onClick = onClick,
		enabled = enabled && !isPlaceholder,
		modifier = modifier,
		colors = colors,
	) {
		Text(
			text = text,
			modifier = Modifier.purecipesPlaceholder(visible = isPlaceholder),
			style = PurecipesTheme.typography.labelLarge,
		)
	}
}

@Preview
@Composable
private fun PurecipesTextButtonLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			PurecipesTextButton(
				text = "Add to cookbook",
				onClick = {},
			)
		}
	}
}

@Preview
@Composable
private fun PurecipesTextButtonDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			PurecipesTextButton(
				text = "Add to cookbook",
				onClick = {},
			)
		}
	}
}
