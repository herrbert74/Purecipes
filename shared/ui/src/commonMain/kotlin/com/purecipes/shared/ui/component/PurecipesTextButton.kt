package com.purecipes.shared.ui.component

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun PurecipesTextButton(
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	TextButton(
		onClick = onClick,
		enabled = enabled,
		modifier = modifier,
	) {
		Text(
			text = text,
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
