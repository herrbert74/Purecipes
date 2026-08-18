package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun CreateRecipeSectionSwitcher(
	selectedSection: CreateRecipeSection,
	onSectionChange: (CreateRecipeSection) -> Unit,
	modifier: Modifier = Modifier,
) {
	val sections = CreateRecipeSection.entries
	SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
		sections.forEachIndexed { index, section ->
			SegmentedButton(
				selected = selectedSection == section,
				onClick = { onSectionChange(section) },
				shape = SegmentedButtonDefaults.itemShape(index = index, count = sections.size),
				modifier = Modifier.testTag(section.testTag),
				icon = {},
				label = { Text(text = section.label) },
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun CreateRecipeSectionSwitcherPreview() {
	PurecipesTheme {
		CreateRecipeSectionSwitcher(
			selectedSection = CreateRecipeSection.Ingredients,
			onSectionChange = {},
		)
	}
}
