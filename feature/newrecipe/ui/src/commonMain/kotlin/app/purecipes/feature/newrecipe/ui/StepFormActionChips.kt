package app.purecipes.feature.newrecipe.ui

internal data class StepFormActionChips(
	val saveLabel: String,
	val clearLabel: String,
	val isSaveEnabled: Boolean,
	val isClearEnabled: Boolean,
	val onSaveClick: () -> Unit,
	val onClearClick: () -> Unit,
)

internal fun createRecipeStepsFormActionChips(
	isEditing: Boolean,
	isSaving: Boolean,
	isImportingImage: Boolean,
	onSaveClick: () -> Unit,
	onClearClick: () -> Unit,
): StepFormActionChips = StepFormActionChips(
	saveLabel = if (isEditing) {
		"Update recipe"
	} else {
		"Upload recipe"
	},
	clearLabel = if (isEditing) {
		"Start new"
	} else {
		"Clear form"
	},
	isSaveEnabled = !isSaving && !isImportingImage,
	isClearEnabled = !isSaving && !isImportingImage,
	onSaveClick = onSaveClick,
	onClearClick = onClearClick,
)
