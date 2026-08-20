package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlin.math.roundToInt

internal const val STEP_ADD_BUTTON_TAG = "createRecipeAddStepButton"

@Composable
internal fun CreateRecipeStepsSection(
	stepInputs: StepInputsState,
	stepsError: String?,
	onAddStepClick: () -> Unit,
	onMoveStep: (Int, Int) -> Unit,
	onMoveStepUp: (Int) -> Unit,
	onMoveStepDown: (Int) -> Unit,
	onRemoveStepClick: (Int) -> Unit,
	onStepChange: (Int, String) -> Unit,
) {
	val rowHeights = remember { mutableStateMapOf<Int, Int>() }
	var draggedIndex by remember { mutableIntStateOf(-1) }
	var dragOffsetY by remember { mutableFloatStateOf(0f) }
	val addButtonBringIntoViewRequester = remember { BringIntoViewRequester() }
	val newStepFocusRequester = remember { FocusRequester() }
	var previousStepCount by remember { mutableIntStateOf(stepInputs.items.size) }
	val expandedSteps = remember { mutableStateMapOf(0 to true) }
	val colors = createRecipeSegmentedListColors()
	var focusNewStep by remember { mutableStateOf(false) }

	LaunchedEffect(stepsError) {
		if (stepsError != null) {
			expandedSteps[0] = true
		}
	}
	LaunchedEffect(stepInputs.items.size) {
		if (stepInputs.items.size > previousStepCount) {
			expandedSteps[stepInputs.items.lastIndex] = true
			focusNewStep = true
			addButtonBringIntoViewRequester.bringIntoView()
		}
		previousStepCount = stepInputs.items.size
	}

	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
			Text(
				text = "Cooking steps",
				style = PurecipesTheme.typography.titleMedium,
			)
			Text(
				text = "These appear as numbered steps when you cook.",
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}

		Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
			stepInputs.items.forEachIndexed { index, stepInput ->
				val expanded = expandedSteps[index] == true
				CreateRecipeStepCard(
					index = index,
					stepInput = stepInput,
					expanded = expanded,
					errorMessage = stepsError.takeIf { index == 0 },
					canMoveUp = index > 0,
					canMoveDown = index < stepInputs.items.lastIndex,
					canRemove = stepInputs.items.size > 1,
					itemCount = stepInputs.items.size,
					onExpandToggle = { expandedSteps[index] = !expanded },
					onStepChange = { onStepChange(index, it) },
					onMoveUpClick = { onMoveStepUp(index) },
					onMoveDownClick = { onMoveStepDown(index) },
					onRemoveClick = { onRemoveStepClick(index) },
					onDragStart = {
						draggedIndex = index
						dragOffsetY = 0f
					},
					onDrag = { dragAmountY ->
						val currentIndex = draggedIndex

						if (currentIndex >= 0) {
							dragOffsetY += dragAmountY
							val currentRowHeight = rowHeights[currentIndex] ?: rowHeights.values.maxOrNull()

							if (currentRowHeight != null) {
								val moveDownThreshold = currentRowHeight / 2f
								val moveUpThreshold = -currentRowHeight / 2f

								if (dragOffsetY > moveDownThreshold && currentIndex < stepInputs.items.lastIndex) {
									onMoveStep(currentIndex, currentIndex + 1)
									draggedIndex = currentIndex + 1
									dragOffsetY -= currentRowHeight
								} else if (dragOffsetY < moveUpThreshold && currentIndex > 0) {
									onMoveStep(currentIndex, currentIndex - 1)
									draggedIndex = currentIndex - 1
									dragOffsetY += currentRowHeight
								}
							}
						}
					},
					onDragEnd = {
						draggedIndex = -1
						dragOffsetY = 0f
					},
					modifier = Modifier
						.onSizeChanged { rowHeights[index] = it.height }
						.offset {
							IntOffset(
								x = 0,
								y = if (draggedIndex == index) dragOffsetY.roundToInt() else 0,
							)
						}
						.zIndex(if (draggedIndex == index) 1f else 0f),
					focusRequester = newStepFocusRequester.takeIf {
						focusNewStep && index == stepInputs.items.lastIndex
					},
					colors = colors,
				)
			}
		}

		FilledTonalButton(
			onClick = onAddStepClick,
			modifier = Modifier
				.fillMaxWidth()
				.bringIntoViewRequester(addButtonBringIntoViewRequester)
				.testTag(STEP_ADD_BUTTON_TAG),
		) {
			Text(text = "Add step")
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun CreateRecipeStepsSectionPreview() {
	PurecipesTheme {
		CreateRecipeStepsSection(
			stepInputs = StepInputsState(
				items = listOf(
					"Bring a large pot of salted water to a boil.",
					"Cook the pasta until al dente.",
				),
			),
			stepsError = null,
			onAddStepClick = {},
			onMoveStep = { _, _ -> },
			onMoveStepUp = {},
			onMoveStepDown = {},
			onRemoveStepClick = {},
			onStepChange = { _, _ -> },
		)
	}
}
