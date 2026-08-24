package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun TintedFeatureCard(
	tint: ContainerTint,
	modifier: Modifier = Modifier,
	label: String? = null,
	onClick: (() -> Unit)? = null,
	content: @Composable ColumnScope.() -> Unit,
) {
	val colors = tint.colorFamily()
	val cardColors = CardDefaults.cardColors(containerColor = colors.colorContainer)
	val cardContent: @Composable ColumnScope.() -> Unit = {
		label?.let { chipLabel ->
			MetadataPillChip(
				text = chipLabel,
				tint = tint,
				modifier = Modifier.padding(bottom = PurecipesTheme.space.s),
			)
		}
		content()
	}
	if (onClick != null) {
		Card(
			onClick = onClick,
			modifier = modifier.fillMaxWidth(),
			colors = cardColors,
			content = cardContent,
		)
	} else {
		Card(
			modifier = modifier.fillMaxWidth(),
			colors = cardColors,
			content = cardContent,
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun TintedFeatureCardPreview() {
	PurecipesPreviewScaffold {
		TintedFeatureCard(
			tint = ContainerTint.Primary,
			label = "Quick dinner",
		) {
			TitleText(
				text = "Tomato pasta",
				color = PurecipesTheme.colorScheme.onPrimaryContainer,
			)
			BodyText(
				text = "25 min",
				color = PurecipesTheme.colorScheme.onPrimaryContainer,
			)
		}
	}
}
