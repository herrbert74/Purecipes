package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

const val METADATA_PILL_CHIP_TAG_PREFIX = "metadataPillChip:"

@Composable
fun MetadataPillChip(
	text: String,
	modifier: Modifier = Modifier,
	tint: ContainerTint = ContainerTint.Primary,
	filled: Boolean = false,
	onClick: (() -> Unit)? = null,
) {
	val colors = tint.colorFamily()
	val background = if (filled) colors.color else colors.colorContainer
	val contentColor = if (filled) colors.onColor else colors.onColorContainer
	val chipModifier = modifier.testTag("$METADATA_PILL_CHIP_TAG_PREFIX$text")
	val content: @Composable () -> Unit = {
		Text(
			text = text,
			modifier = Modifier.padding(
				horizontal = PurecipesTheme.space.s,
				vertical = PurecipesTheme.space.xs,
			),
			style = PurecipesTheme.typography.labelLarge,
			color = contentColor,
		)
	}
	if (onClick != null) {
		Surface(
			onClick = onClick,
			modifier = chipModifier,
			shape = RoundedCornerShape(PurecipesButtonDefaults.pillCorner),
			color = background,
			content = content,
		)
	} else {
		Surface(
			modifier = chipModifier,
			shape = RoundedCornerShape(PurecipesButtonDefaults.pillCorner),
			color = background,
			content = content,
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun MetadataPillChipPreview() {
	PurecipesPreviewScaffold {
		MetadataPillChip(
			text = "Easy",
			tint = ContainerTint.Primary,
		)
	}
}
