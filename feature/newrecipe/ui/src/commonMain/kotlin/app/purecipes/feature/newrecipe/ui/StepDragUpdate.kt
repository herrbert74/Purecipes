package app.purecipes.feature.newrecipe.ui

internal data class StepDragUpdate(
	val draggedIndex: Int,
	val dragOffsetY: Float,
)

internal fun applyStepDrag(
	draggedIndex: Int,
	dragOffsetY: Float,
	rowHeights: Map<Int, Int>,
	lastStepIndex: Int,
	onMoveStep: (Int, Int) -> Unit,
): StepDragUpdate {
	val currentRowHeight = rowHeights[draggedIndex] ?: rowHeights.values.maxOrNull()
	if (draggedIndex < 0 || currentRowHeight == null) {
		return StepDragUpdate(draggedIndex = draggedIndex, dragOffsetY = dragOffsetY)
	}

	val moveDownThreshold = currentRowHeight / 2f
	val moveUpThreshold = -currentRowHeight / 2f

	return when {
		dragOffsetY > moveDownThreshold && draggedIndex < lastStepIndex -> {
			onMoveStep(draggedIndex, draggedIndex + 1)
			StepDragUpdate(
				draggedIndex = draggedIndex + 1,
				dragOffsetY = dragOffsetY - currentRowHeight,
			)
		}

		dragOffsetY < moveUpThreshold && draggedIndex > 0 -> {
			onMoveStep(draggedIndex, draggedIndex - 1)
			StepDragUpdate(
				draggedIndex = draggedIndex - 1,
				dragOffsetY = dragOffsetY + currentRowHeight,
			)
		}

		else -> StepDragUpdate(draggedIndex = draggedIndex, dragOffsetY = dragOffsetY)
	}
}
