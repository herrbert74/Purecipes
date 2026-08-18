package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val STEP_FIELD_TAG_PREFIX = "createRecipeStepField"
internal const val STEP_REORDER_BUTTON_TAG_PREFIX = "createRecipeReorderStepButton"
internal const val STEP_MOVE_UP_BUTTON_TAG_PREFIX = "createRecipeMoveStepUpButton"
internal const val STEP_MOVE_DOWN_BUTTON_TAG_PREFIX = "createRecipeMoveStepDownButton"
internal const val STEP_REMOVE_BUTTON_TAG_PREFIX = "createRecipeRemoveStepButton"

@Composable
internal fun CreateRecipeStepCard(
	index: Int,
	stepInput: String,
	expanded: Boolean,
	canMoveUp: Boolean,
	canMoveDown: Boolean,
	canRemove: Boolean,
	itemCount: Int,
	onExpandToggle: () -> Unit,
	onStepChange: (String) -> Unit,
	onMoveUpClick: () -> Unit,
	onMoveDownClick: () -> Unit,
	onRemoveClick: () -> Unit,
	onDragStart: () -> Unit,
	onDrag: (Float) -> Unit,
	onDragEnd: () -> Unit,
	colors: ListItemColors,
	modifier: Modifier = Modifier,
	focusRequester: FocusRequester? = null,
) {
	val stepNumber = index + 1
	val headline = stepInput.trim().lineSequence().firstOrNull().orEmpty().ifBlank { "Step $stepNumber" }
	val shapes = ListItemDefaults.segmentedShapes(index = index, count = itemCount)

	if (expanded) {
		Column(modifier = modifier) {
			SegmentedListItem(
				onClick = onExpandToggle,
				shapes = shapes,
				colors = colors,
				leadingContent = { StepNumberBadge(stepNumber = stepNumber) },
				trailingContent = {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Icon(
							imageVector = Icons.Filled.ExpandLess,
							contentDescription = "Collapse step $stepNumber",
						)
						StepDragHandle(
							index = index,
							onDragStart = onDragStart,
							onDrag = onDrag,
							onDragEnd = onDragEnd,
						)
						if (canRemove) {
							IconButton(
								onClick = onRemoveClick,
								modifier = Modifier.testTag("$STEP_REMOVE_BUTTON_TAG_PREFIX$index"),
							) {
								Icon(
									imageVector = Icons.Filled.Delete,
									contentDescription = "Remove step $stepNumber",
								)
							}
						}
					}
				},
				content = { Text(text = "Step $stepNumber") },
			)
			Column(
				modifier = Modifier.padding(
					start = PurecipesTheme.space.m,
					end = PurecipesTheme.space.m,
					bottom = PurecipesTheme.space.s,
				),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				Row {
					IconButton(
						onClick = onMoveUpClick,
						enabled = canMoveUp,
						modifier = Modifier.testTag("$STEP_MOVE_UP_BUTTON_TAG_PREFIX$index"),
					) {
						Icon(
							imageVector = Icons.Filled.KeyboardArrowUp,
							contentDescription = "Move step $stepNumber up",
						)
					}
					IconButton(
						onClick = onMoveDownClick,
						enabled = canMoveDown,
						modifier = Modifier.testTag("$STEP_MOVE_DOWN_BUTTON_TAG_PREFIX$index"),
					) {
						Icon(
							imageVector = Icons.Filled.KeyboardArrowDown,
							contentDescription = "Move step $stepNumber down",
						)
					}
				}
				OutlinedTextField(
					value = stepInput,
					onValueChange = onStepChange,
					modifier = Modifier
						.fillMaxWidth()
						.then(
							if (focusRequester != null) {
								Modifier.focusRequester(focusRequester)
							} else {
								Modifier
							},
						)
						.testTag("$STEP_FIELD_TAG_PREFIX$index"),
					placeholder = { Text(text = "Describe this step") },
					minLines = 2,
				)
				if (focusRequester != null) {
					val requester = focusRequester
					LaunchedEffect(requester) {
						requester.requestFocus()
					}
				}
			}
		}
	} else {
		SegmentedListItem(
			onClick = onExpandToggle,
			shapes = shapes,
			modifier = modifier,
			colors = colors,
			leadingContent = { StepNumberBadge(stepNumber = stepNumber) },
			trailingContent = {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Icon(
						imageVector = Icons.Filled.ExpandMore,
						contentDescription = "Expand step $stepNumber",
					)
					StepDragHandle(
						index = index,
						onDragStart = onDragStart,
						onDrag = onDrag,
						onDragEnd = onDragEnd,
					)
					if (canRemove) {
						IconButton(
							onClick = onRemoveClick,
							modifier = Modifier.testTag("$STEP_REMOVE_BUTTON_TAG_PREFIX$index"),
						) {
							Icon(
								imageVector = Icons.Filled.Delete,
								contentDescription = "Remove step $stepNumber",
							)
						}
					}
				}
			},
			supportingContent = { Text(text = headline) },
			content = { Text(text = "Step $stepNumber") },
		)
	}
}

@Composable
private fun StepNumberBadge(stepNumber: Int) {
	Surface(
		modifier = Modifier.size(PurecipesTheme.space.xl),
		shape = CircleShape,
		color = PurecipesTheme.colorScheme.primaryContainer,
	) {
		Box(contentAlignment = Alignment.Center) {
			Text(
				text = stepNumber.toString(),
				style = PurecipesTheme.typography.labelLarge,
				color = PurecipesTheme.colorScheme.onPrimaryContainer,
			)
		}
	}
}

@Composable
private fun StepDragHandle(
	index: Int,
	onDragStart: () -> Unit,
	onDrag: (Float) -> Unit,
	onDragEnd: () -> Unit,
) {
	Box(
		modifier = Modifier
			.size(PurecipesTheme.space.xxl)
			.testTag("$STEP_REORDER_BUTTON_TAG_PREFIX$index"),
		contentAlignment = Alignment.Center,
	) {
		Box(
			modifier = Modifier
				.matchParentSize()
				.pointerInput(index) {
					detectDragGesturesAfterLongPress(
						onDragStart = {
							onDragStart()
						},
						onDragEnd = onDragEnd,
						onDragCancel = onDragEnd,
						onDrag = { change, dragAmount ->
							change.consume()
							onDrag(dragAmount.y)
						},
					)
				},
		)
		Icon(
			imageVector = Icons.Filled.DragHandle,
			contentDescription = "Reorder step ${index + 1}",
			tint = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun CreateRecipeStepCardPreview() {
	PurecipesTheme {
		CreateRecipeStepCard(
			index = 0,
			stepInput = "Bring a large pot of salted water to a boil.",
			expanded = true,
			canMoveUp = false,
			canMoveDown = true,
			canRemove = true,
			itemCount = 1,
			onExpandToggle = {},
			onStepChange = {},
			onMoveUpClick = {},
			onMoveDownClick = {},
			onRemoveClick = {},
			onDragStart = {},
			onDrag = {},
			onDragEnd = {},
			colors = createRecipeSegmentedListColors(),
		)
	}
}
