package app.purecipes.shared.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

const val OUTLINED_STAT_CHIP_TAG_PREFIX = "outlinedStatChip:"

private val OUTLINED_STAT_CHIP_BORDER_WIDTH = 1.5.dp

@Composable
fun OutlinedStatChip(
	label: String,
	value: String,
	tint: ContainerTint,
	modifier: Modifier = Modifier,
	isPlaceholder: Boolean = false,
) {
	val colors = tint.colorFamily()
	val chipShape = RoundedCornerShape(PurecipesTheme.space.s)
	Surface(
		modifier = modifier
			.purecipesPlaceholder(
				visible = isPlaceholder,
				shape = chipShape,
			)
			.then(
				if (isPlaceholder) {
					Modifier
				} else {
					Modifier
						.testTag("$OUTLINED_STAT_CHIP_TAG_PREFIX$label")
						.border(
							width = OUTLINED_STAT_CHIP_BORDER_WIDTH,
							color = colors.color,
							shape = chipShape,
						)
				},
			),
		shape = chipShape,
		color = PurecipesTheme.colorScheme.surface,
	) {
		Column(
			modifier = Modifier.padding(
				horizontal = PurecipesTheme.space.s,
				vertical = PurecipesTheme.space.s,
			),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.quark),
		) {
			Text(
				text = value,
				style = PurecipesTheme.typography.titleMedium,
				color = colors.color,
			)
			Text(
				text = label,
				style = PurecipesTheme.typography.labelMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun OutlinedStatChipPreview() {
	PurecipesPreviewScaffold {
		OutlinedStatChip(
			label = "Cook time",
			value = "25 min",
			tint = ContainerTint.Primary,
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun OutlinedStatChipPlaceholderPreview() {
	PurecipesPreviewScaffold {
		OutlinedStatChip(
			label = "Cook time",
			value = "25 min",
			tint = ContainerTint.Primary,
			isPlaceholder = true,
		)
	}
}
