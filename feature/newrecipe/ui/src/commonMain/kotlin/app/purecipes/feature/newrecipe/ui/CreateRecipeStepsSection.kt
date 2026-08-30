package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlin.math.roundToInt

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
	formActionChips: StepFormActionChips? = null,
	onLastStepFieldFocusChange: (Boolean) -> Unit = {},
) {
	val rowHeights = remember { mutableStateMapOf<Int, Int>() }
	var draggedIndex by remember { mutableIntStateOf(-1) }
	var dragOffsetY by remember { mutableFloatStateOf(0f) }
	val stepFocusRequesters = remember(stepInputs.items.size) {
		List(stepInputs.items.size) { FocusRequester() }
	}
	var previousStepCount by remember { mutableIntStateOf(stepInputs.items.size) }
	var stepToFocus by remember { mutableIntStateOf(-1) }
	val expandedSteps = remember { mutableStateMapOf(0 to true) }
	val colors = createRecipeSegmentedListColors()

	fun requestFocusOnStep(index: Int) {
		expandedSteps[index] = true
		stepToFocus = index
	}

	LaunchedEffect(stepsError) {
		if (stepsError != null) {
			expandedSteps[0] = true
		}
	}
	LaunchedEffect(stepToFocus, stepInputs.items.size) {
		if (stepToFocus in stepFocusRequesters.indices) {
			stepFocusRequesters[stepToFocus].requestFocus()
			stepToFocus = -1
		}
	}
	LaunchedEffect(stepInputs.items.size) {
		if (stepInputs.items.size > previousStepCount) {
			requestFocusOnStep(stepInputs.items.lastIndex)
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
				val isLastStep = index == stepInputs.items.lastIndex
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
						val updatedDragOffsetY = dragOffsetY + dragAmountY
						val dragUpdate = applyStepDrag(
							draggedIndex = draggedIndex,
							dragOffsetY = updatedDragOffsetY,
							rowHeights = rowHeights,
							lastStepIndex = stepInputs.items.lastIndex,
							onMoveStep = onMoveStep,
						)
						draggedIndex = dragUpdate.draggedIndex
						dragOffsetY = dragUpdate.dragOffsetY
					},
					onDragEnd = {
						draggedIndex = -1
						dragOffsetY = 0f
					},
					colors = colors,
					focusRequester = stepFocusRequesters[index],
					onStepActionClick = if (isLastStep) {
						onAddStepClick
					} else {
						{ requestFocusOnStep(index + 1) }
					},
					formActionChips = formActionChips.takeIf { isLastStep },
					onLastStepFieldFocusChange = onLastStepFieldFocusChange.takeIf { isLastStep },
					modifier = Modifier
						.onSizeChanged { rowHeights[index] = it.height }
						.offset {
							IntOffset(
								x = 0,
								y = if (draggedIndex == index) dragOffsetY.roundToInt() else 0,
							)
						}
						.zIndex(if (draggedIndex == index) 1f else 0f),
				)
			}
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
