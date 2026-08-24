package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun RecipeSectionSegmentedControl(
	sections: ImmutableList<RecipeDetailsSection>,
	selectedSection: RecipeDetailsSection,
	onSectionChange: (RecipeDetailsSection) -> Unit,
	modifier: Modifier = Modifier,
) {
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

enum class RecipeDetailsSection(
	val label: String,
	val testTag: String,
) {

	Ingredients(
		label = "Ingredients",
		testTag = "recipeSectionIngredients",
	),
	Method(
		label = "Method",
		testTag = "recipeSectionMethod",
	),
	Nutrition(
		label = "Nutrition",
		testTag = "recipeSectionNutrition",
	),
}

@Preview(showBackground = true)
@Composable
private fun RecipeSectionSegmentedControlPreview() {
	PurecipesPreviewScaffold {
		RecipeSectionSegmentedControl(
			sections = persistentListOf(
				RecipeDetailsSection.Ingredients,
				RecipeDetailsSection.Method,
				RecipeDetailsSection.Nutrition,
			),
			selectedSection = RecipeDetailsSection.Ingredients,
			onSectionChange = {},
		)
	}
}
