package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val CUISINE_FIELD_TAG = "createRecipeCuisineField"
internal const val DESCRIPTION_FIELD_TAG = "createRecipeDescriptionField"
internal const val TITLE_FIELD_TAG = "createRecipeTitleField"
internal const val TOTAL_TIME_FIELD_TAG = "createRecipeTotalTimeField"
internal const val TOTAL_TIME_ROW_TAG = "createRecipeTotalTimeRow"
internal const val YIELDS_FIELD_TAG = "createRecipeYieldsField"
internal const val YIELDS_ROW_TAG = "createRecipeYieldsRow"

private const val CUISINE_ITEM_INDEX = 0
private const val TOTAL_TIME_ITEM_INDEX = 1
private const val YIELDS_ITEM_INDEX = 2

@Composable
internal fun CreateRecipeAboutSection(
	titleInput: String,
	descriptionInput: String,
	imageUrlInput: String,
	isImportingImage: Boolean,
	isSaving: Boolean,
	imagePickerErrorMessage: String?,
	selectedCuisine: Cuisine?,
	totalTimeInput: String,
	yieldsInput: String,
	isPrivate: Boolean,
	canMakePrivate: Boolean,
	onClearImageClick: () -> Unit,
	onImageUrlChange: (String) -> Unit,
	onPickImageClick: (() -> Unit)?,
	onTitleChange: (String) -> Unit,
	onDescriptionChange: (String) -> Unit,
	onCuisineChange: (Cuisine?) -> Unit,
	onTotalTimeChange: (String) -> Unit,
	onYieldsChange: (String) -> Unit,
	onIsPrivateChange: (Boolean) -> Unit,
	onPrivacyLockedClick: () -> Unit,
	titleError: String? = null,
	descriptionError: String? = null,
	totalTimeError: String? = null,
) {
	var showCuisineSheet by remember { mutableStateOf(false) }
	val colors = createRecipeSegmentedListColors()

	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		CreateRecipeImageSection(
			titleInput = titleInput,
			imageUrlInput = imageUrlInput,
			isImportingImage = isImportingImage,
			isSaving = isSaving,
			imagePickerErrorMessage = imagePickerErrorMessage,
			onClearImageClick = onClearImageClick,
			onImageUrlChange = onImageUrlChange,
			onPickImageClick = onPickImageClick,
		)
		OutlinedTextField(
			value = titleInput,
			onValueChange = onTitleChange,
			modifier = Modifier
				.fillMaxWidth()
				.testTag(TITLE_FIELD_TAG),
			label = { Text(text = "Recipe title") },
			isError = titleError != null,
			supportingText = titleError?.let { message ->
				{ Text(text = message) }
			},
			singleLine = true,
		)
		OutlinedTextField(
			value = descriptionInput,
			onValueChange = onDescriptionChange,
			modifier = Modifier
				.fillMaxWidth()
				.testTag(DESCRIPTION_FIELD_TAG),
			label = { Text(text = "Description") },
			isError = descriptionError != null,
			supportingText = descriptionError?.let { message ->
				{ Text(text = message) }
			},
			minLines = 3,
		)
		Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
			SegmentedListItem(
				onClick = { showCuisineSheet = true },
				shapes = ListItemDefaults.segmentedShapes(
					index = CUISINE_ITEM_INDEX,
					count = METADATA_GROUP_ITEM_COUNT,
				),
				modifier = Modifier.testTag(CUISINE_FIELD_TAG),
				colors = colors,
				trailingContent = {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
						contentDescription = null,
					)
				},
				supportingContent = {
					Text(text = selectedCuisine?.displayName ?: "Select cuisine")
				},
				content = { Text(text = "Cuisine") },
			)
			ExpandableMetadataField(
				index = TOTAL_TIME_ITEM_INDEX,
				label = "Total time",
				collapsedValue = totalTimeInput.ifBlank { "Not set" },
				fieldValue = totalTimeInput,
				fieldLabel = "Total minutes",
				rowTestTag = TOTAL_TIME_ROW_TAG,
				fieldTestTag = TOTAL_TIME_FIELD_TAG,
				startExpanded = totalTimeInput.isNotBlank(),
				errorMessage = totalTimeError,
				onValueChange = onTotalTimeChange,
				colors = colors,
			)
			ExpandableMetadataField(
				index = YIELDS_ITEM_INDEX,
				label = "Yields",
				collapsedValue = yieldsInput.ifBlank { "Not set" },
				fieldValue = yieldsInput,
				fieldLabel = "Yields",
				rowTestTag = YIELDS_ROW_TAG,
				fieldTestTag = YIELDS_FIELD_TAG,
				startExpanded = yieldsInput.isNotBlank(),
				onValueChange = onYieldsChange,
				colors = colors,
			)
			CreateRecipePrivacySection(
				isPrivate = isPrivate,
				canMakePrivate = canMakePrivate,
				onIsPrivateChange = onIsPrivateChange,
				onLockedClick = onPrivacyLockedClick,
			)
		}
	}

	if (showCuisineSheet) {
		CreateRecipeCuisinePickerSheet(
			selectedCuisine = selectedCuisine,
			onCuisineChange = onCuisineChange,
			onDismiss = { showCuisineSheet = false },
		)
	}
}

@Composable
private fun ExpandableMetadataField(
	index: Int,
	label: String,
	collapsedValue: String,
	fieldValue: String,
	fieldLabel: String,
	rowTestTag: String,
	fieldTestTag: String,
	startExpanded: Boolean,
	onValueChange: (String) -> Unit,
	colors: ListItemColors,
	errorMessage: String? = null,
) {
	var expanded by remember { mutableStateOf(startExpanded) }
	LaunchedEffect(errorMessage) {
		if (errorMessage != null) {
			expanded = true
		}
	}
	val shapes = ListItemDefaults.segmentedShapes(
		index = index,
		count = METADATA_GROUP_ITEM_COUNT,
	)

	Column {
		SegmentedListItem(
			onClick = { expanded = !expanded },
			shapes = shapes,
			modifier = Modifier.testTag(rowTestTag),
			colors = colors,
			trailingContent = {
				Icon(
					imageVector = if (expanded) {
						Icons.Filled.ExpandLess
					} else {
						Icons.Filled.ExpandMore
					},
					contentDescription = if (expanded) {
						"Collapse $label"
					} else {
						"Expand $label"
					},
				)
			},
			supportingContent = if (expanded) {
				null
			} else {
				{ Text(text = collapsedValue) }
			},
			content = { Text(text = label) },
		)
		if (expanded) {
			OutlinedTextField(
				value = fieldValue,
				onValueChange = onValueChange,
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						start = PurecipesTheme.space.m,
						end = PurecipesTheme.space.m,
						bottom = PurecipesTheme.space.s,
					)
					.testTag(fieldTestTag),
				label = { Text(text = fieldLabel) },
				isError = errorMessage != null,
				supportingText = errorMessage?.let { message ->
					{ Text(text = message) }
				},
				singleLine = true,
			)
		}
	}
}

@Preview(
	name = "Create recipe about light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CreateRecipeAboutSectionPreview() {
	PurecipesTheme(darkTheme = false) {
		CreateRecipeAboutSection(
			titleInput = "Tomato Pasta",
			descriptionInput = "A quick weeknight dinner with pantry staples.",
			imageUrlInput = "",
			isImportingImage = false,
			isSaving = false,
			imagePickerErrorMessage = null,
			selectedCuisine = Cuisine.ITALIAN,
			totalTimeInput = "25",
			yieldsInput = "2 servings",
			isPrivate = false,
			canMakePrivate = true,
			onClearImageClick = {},
			onImageUrlChange = {},
			onPickImageClick = {},
			onTitleChange = {},
			onDescriptionChange = {},
			onCuisineChange = {},
			onTotalTimeChange = {},
			onYieldsChange = {},
			onIsPrivateChange = {},
			onPrivacyLockedClick = {},
		)
	}
}
