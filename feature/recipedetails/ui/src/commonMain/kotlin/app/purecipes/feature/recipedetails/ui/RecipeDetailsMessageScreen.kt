package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun RecipeDetailsMessageScreen(
	message: String,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(PurecipesTheme.space.l),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Text(
				text = message,
				style = PurecipesTheme.typography.bodyLarge,
			)
			Spacer(modifier = Modifier.height(PurecipesTheme.space.xs))
			TextButton(onClick = onBack) {
				Text(text = "Back to search")
			}
		}
	}
}
