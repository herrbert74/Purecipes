package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun RecipeStatChipsRow(
	totalTimeMinutes: Int?,
	servings: String?,
	difficulty: String?,
	modifier: Modifier = Modifier,
) {
	val chips = buildList {
		totalTimeMinutes?.let { minutes ->
			add(
				StatChipSpec(
					label = "Cook time",
					value = "$minutes min",
					tint = ContainerTint.Primary,
				),
			)
		}
		servings?.takeIf { it.isNotBlank() }?.let { value ->
			add(
				StatChipSpec(
					label = "Servings",
					value = value,
					tint = ContainerTint.Secondary,
				),
			)
		}
		difficulty?.takeIf { it.isNotBlank() }?.let { value ->
			add(
				StatChipSpec(
					label = "Difficulty",
					value = value,
					tint = ContainerTint.Tertiary,
				),
			)
		}
	}
	if (chips.isEmpty()) return

	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		chips.forEach { chip ->
			OutlinedStatChip(
				label = chip.label,
				value = chip.value,
				tint = chip.tint,
				modifier = Modifier.weight(1f),
			)
		}
	}
}

private data class StatChipSpec(
	val label: String,
	val value: String,
	val tint: ContainerTint,
)

@Preview(showBackground = true)
@Composable
private fun RecipeStatChipsRowPreview() {
	PurecipesPreviewScaffold {
		RecipeStatChipsRow(
			totalTimeMinutes = 25,
			servings = "4",
			difficulty = "Easy",
		)
	}
}
