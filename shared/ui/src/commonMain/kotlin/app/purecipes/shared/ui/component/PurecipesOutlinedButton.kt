package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun PurecipesOutlinedButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	OutlinedButton(
		modifier = modifier.fillMaxWidth(),
		onClick = onClick,
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
private fun PurecipesOutlinedButtonLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			PurecipesOutlinedButton(
				text = "Send Test Notification",
				onClick = {},
			)
		}
	}
}

@Preview
@Composable
private fun PurecipesOutlinedButtonDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			PurecipesOutlinedButton(
				text = "Send Test Notification",
				onClick = {},
			)
		}
	}
}
