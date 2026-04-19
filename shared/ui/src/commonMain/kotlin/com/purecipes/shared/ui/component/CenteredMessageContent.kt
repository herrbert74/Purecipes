package com.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun CenteredMessageContent(
	message: String,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(PaddingValues(horizontal = 24.dp, vertical = 16.dp)),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = message,
			style = PurecipesTheme.typography.bodyLarge,
			textAlign = TextAlign.Center,
		)
	}
}

@Preview
@Composable
private fun CenteredMessageContentLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			CenteredMessageContent(message = "No cooking steps available yet.")
		}
	}
}

@Preview
@Composable
private fun CenteredMessageContentDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			CenteredMessageContent(message = "No cooking steps available yet.")
		}
	}
}
