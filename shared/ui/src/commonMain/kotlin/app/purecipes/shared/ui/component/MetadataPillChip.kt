package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

const val METADATA_PILL_CHIP_TAG_PREFIX = "metadataPillChip:"

@Composable
fun MetadataPillChip(
	text: String,
	modifier: Modifier = Modifier,
	icon: ImageVector? = null,
	tint: ContainerTint = ContainerTint.Primary,
	filled: Boolean = false,
	isPlaceholder: Boolean = false,
	onClick: (() -> Unit)? = null,
) {
	val colors = tint.colorFamily()
	val background = if (filled) colors.color else colors.colorContainer
	val contentColor = if (filled) colors.onColor else colors.onColorContainer
	val pillShape = RoundedCornerShape(PurecipesButtonDefaults.pillCorner)
	val chipModifier = modifier
		.purecipesPlaceholder(
			visible = isPlaceholder,
			shape = pillShape,
		)
		.then(
			if (isPlaceholder) {
				Modifier
			} else {
				Modifier.testTag("$METADATA_PILL_CHIP_TAG_PREFIX$text")
			},
		)
	val content: @Composable () -> Unit = {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			modifier = Modifier.padding(
				horizontal = PurecipesTheme.space.s,
				vertical = PurecipesTheme.space.xs,
			),
		) {
			if (icon != null) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					tint = contentColor,
					modifier = Modifier.size(PurecipesTheme.space.m),
				)
			}
			Text(
				text = text,
				style = PurecipesTheme.typography.labelLarge,
				color = contentColor,
			)
		}
	}
	if (onClick != null && !isPlaceholder) {
		Surface(
			onClick = onClick,
			modifier = chipModifier,
			shape = pillShape,
			color = background,
			content = content,
		)
	} else {
		Surface(
			modifier = chipModifier,
			shape = pillShape,
			color = background,
			content = content,
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun MetadataPillChipPreview() {
	PurecipesPreviewScaffold {
		MetadataPillChip(text = "Gluten-Free")
	}
}

@Preview(showBackground = true)
@Composable
private fun MetadataPillChipPlaceholderPreview() {
	PurecipesPreviewScaffold {
		MetadataPillChip(
			text = "Gluten-Free",
			isPlaceholder = true,
		)
	}
}
