package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun PurecipesButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	Button(
		onClick = onClick,
		modifier = modifier.fillMaxWidth(),
		enabled = enabled,
		shape = RoundedCornerShape(PurecipesButtonDefaults.pillCorner),
		contentPadding = PaddingValues(vertical = PurecipesTheme.space.s),
	) {
		Text(
			text = text,
			style = PurecipesTheme.typography.labelLarge,
		)
	}
}

@Preview
@Composable
private fun PurecipesButtonLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			PurecipesButton(
				text = "Start cooking",
				onClick = {},
			)
		}
	}
}

@Preview
@Composable
private fun PurecipesButtonDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			PurecipesButton(
				text = "Start cooking",
				onClick = {},
			)
		}
	}
}
