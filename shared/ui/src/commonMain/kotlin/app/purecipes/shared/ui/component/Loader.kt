package app.purecipes.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.loading

@Composable
fun ShowLoading(
	modifier: Modifier = Modifier,
	text: String = stringResource(resource = Res.string.loading),
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.background(PurecipesTheme.colorScheme.primaryContainer)
			.padding(PurecipesTheme.space.l),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			CircularProgressIndicator(
				modifier = Modifier.size(56.dp),
				color = PurecipesTheme.colorScheme.primary,
				strokeWidth = 4.dp,
			)
			Text(
				text = text,
				style = PurecipesTheme.typography.headlineLarge,
				color = PurecipesTheme.colorScheme.onPrimaryContainer,
				textAlign = TextAlign.Center,
			)
		}
	}
}

@Preview
@Composable
private fun ShowLoadingLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			ShowLoading()
		}
	}
}

@Preview
@Composable
private fun ShowLoadingDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			ShowLoading()
		}
	}
}
