package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val CUISINE_NONE_OPTION_TAG = "createRecipeCuisineNoneOption"

@Composable
internal fun CreateRecipeCuisinePickerSheet(
	selectedCuisine: Cuisine?,
	onCuisineChange: (Cuisine?) -> Unit,
	onDismiss: () -> Unit,
) {
	val cuisineOptions = Cuisine.entries
	val itemCount = cuisineOptions.size + 1
	val colors = createRecipeSegmentedListColors()

	ModalBottomSheet(onDismissRequest = onDismiss) {
		Text(
			text = "Cuisine",
			style = PurecipesTheme.typography.titleMedium,
			modifier = Modifier.padding(
				horizontal = PurecipesTheme.space.m,
				vertical = PurecipesTheme.space.s,
			),
		)
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState())
				.selectableGroup()
				.padding(horizontal = PurecipesTheme.space.m)
				.padding(bottom = PurecipesTheme.space.l),
			verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
		) {
			SegmentedListItem(
				selected = selectedCuisine == null,
				onClick = {
					onCuisineChange(null)
					onDismiss()
				},
				shapes = ListItemDefaults.segmentedShapes(index = 0, count = itemCount),
				modifier = Modifier.testTag(CUISINE_NONE_OPTION_TAG),
				colors = colors,
				leadingContent = {
					RadioButton(
						selected = selectedCuisine == null,
						onClick = null,
					)
				},
				content = { Text(text = "No cuisine") },
			)
			cuisineOptions.forEachIndexed { index, cuisine ->
				val selected = selectedCuisine == cuisine
				SegmentedListItem(
					selected = selected,
					onClick = {
						onCuisineChange(cuisine)
						onDismiss()
					},
					shapes = ListItemDefaults.segmentedShapes(index = index + 1, count = itemCount),
					colors = colors,
					leadingContent = {
						RadioButton(
							selected = selected,
							onClick = null,
						)
					},
					content = { Text(text = cuisine.displayName) },
				)
			}
		}
	}
}
