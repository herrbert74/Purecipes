package app.purecipes.shared.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun PurecipesPreviewScaffold(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	PurecipesTheme {
		Surface(
			modifier = modifier,
			color = PurecipesTheme.colorScheme.background,
		) {
			Box(modifier = Modifier.padding(PurecipesTheme.space.m)) {
				content()
			}
		}
	}
}
