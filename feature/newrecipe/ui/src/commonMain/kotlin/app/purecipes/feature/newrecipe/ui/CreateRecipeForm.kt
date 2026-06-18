package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.ui.component.NutritionSummaryCard
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage
import kotlin.math.roundToInt

private const val CUISINE_FIELD_TAG = "createRecipeCuisineField"
private const val DESCRIPTION_FIELD_TAG = "createRecipeDescriptionField"
private const val IMAGE_PICK_BUTTON_TAG = "createRecipeImagePickButton"
private const val IMAGE_CLEAR_BUTTON_TAG = "createRecipeImageClearButton"
private const val IMAGE_FIELD_TAG = "createRecipeImageField"
private const val INGREDIENTS_FIELD_TAG = "createRecipeIngredientsField"
private const val SAVE_BUTTON_TAG = "createRecipeSaveButton"
private const val STEP_ADD_BUTTON_TAG = "createRecipeAddStepButton"
private const val STEP_FIELD_TAG_PREFIX = "createRecipeStepField"
private const val STEP_REORDER_BUTTON_TAG_PREFIX = "createRecipeReorderStepButton"
private const val STEP_REMOVE_BUTTON_TAG_PREFIX = "createRecipeRemoveStepButton"
private const val TITLE_FIELD_TAG = "createRecipeTitleField"
private const val TOTAL_TIME_FIELD_TAG = "createRecipeTotalTimeField"
private const val YIELDS_FIELD_TAG = "createRecipeYieldsField"

@Immutable
internal data class StepInputsState(
	val items: List<String>,
)

@Composable
internal fun CreateRecipeForm(
	selectedCuisine: Cuisine?,
	descriptionInput: String,
	formErrorMessage: String?,
	isImportingImage: Boolean,
	imagePickerErrorMessage: String?,
	imageUrlInput: String,
	ingredientsInput: String,
	isEditing: Boolean,
	isNutritionEstimateLoading: Boolean,
	isSaving: Boolean,
	nutritionEstimate: NutritionSummary?,
	onClearImageClick: () -> Unit,
	onCuisineChange: (Cuisine?) -> Unit,
	onDescriptionChange: (String) -> Unit,
	onImageUrlChange: (String) -> Unit,
	onPickImageClick: (() -> Unit)?,
	onIngredientsChange: (String) -> Unit,
	onSaveClick: () -> Unit,
	onStartNewClick: () -> Unit,
	onAddStepClick: () -> Unit,
	onMoveStep: (Int, Int) -> Unit,
	onRemoveStepClick: (Int) -> Unit,
	onStepChange: (Int, String) -> Unit,
	onTitleChange: (String) -> Unit,
	onTotalTimeChange: (String) -> Unit,
	onYieldsChange: (String) -> Unit,
	stepInputs: StepInputsState,
	successMessage: String?,
	titleInput: String,
	totalTimeInput: String,
	yieldsInput: String,
) {
	Card(
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Text(
				text = if (isEditing) "Edit recipe" else "New recipe",
				style = PurecipesTheme.typography.headlineSmall,
			)
			Text(
				text = """Write one ingredient per line and add cooking steps below. Recipes are uploaded to your
					| account, and local image paths are uploaded as image files.
				""".trimIndent(),
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)

			OutlinedTextField(
				value = titleInput,
				onValueChange = onTitleChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(TITLE_FIELD_TAG),
				label = { Text(text = "Recipe title") },
				singleLine = true,
			)
			OutlinedTextField(
				value = descriptionInput,
				onValueChange = onDescriptionChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(DESCRIPTION_FIELD_TAG),
				label = { Text(text = "Description") },
				minLines = 3,
			)
			OutlinedTextField(
				value = imageUrlInput,
				onValueChange = onImageUrlChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(IMAGE_FIELD_TAG),
				label = { Text(text = "Image URL or local file path") },
				singleLine = true,
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				if (onPickImageClick != null) {
					FilledTonalButton(
						onClick = onPickImageClick,
						enabled = !isSaving && !isImportingImage,
						modifier = Modifier
							.weight(1f)
							.testTag(IMAGE_PICK_BUTTON_TAG),
					) {
						if (isImportingImage) {
							CircularProgressIndicator(
								modifier = Modifier.size(PurecipesTheme.space.m),
								strokeWidth = PurecipesTheme.space.quark,
							)
							Text(text = "Importing image")
						} else {
							Text(text = "Choose image")
						}
					}
				}
				if (imageUrlInput.isNotBlank()) {
					TextButton(
						onClick = onClearImageClick,
						enabled = !isSaving && !isImportingImage,
						modifier = Modifier.testTag(IMAGE_CLEAR_BUTTON_TAG),
					) {
						Text(text = "Clear image")
					}
				}
			}

			if (isImportingImage) {
				ImageImportPlaceholder()
			}

			if (imageUrlInput.isNotBlank() && !isImportingImage) {
				AsyncImage(
					model = imagePreviewModel(imageUrlInput),
					contentDescription = titleInput.ifBlank { "Recipe image preview" },
					modifier = Modifier
						.fillMaxWidth()
						.height(180.dp)
						.clip(RoundedCornerShape(PurecipesTheme.space.m)),
					contentScale = ContentScale.Crop,
				)
			}

			ImageImportStatus(
				imageUrlInput = imageUrlInput,
				isImportingImage = isImportingImage,
			)

			imagePickerErrorMessage?.let {
				Text(
					text = it,
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.error,
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				CuisinePicker(
					selectedCuisine = selectedCuisine,
					onCuisineChange = onCuisineChange,
					modifier = Modifier
						.weight(1f)
						.testTag(CUISINE_FIELD_TAG),
				)
				OutlinedTextField(
					value = totalTimeInput,
					onValueChange = onTotalTimeChange,
					modifier = Modifier
						.weight(1f)
						.testTag(TOTAL_TIME_FIELD_TAG),
					label = { Text(text = "Total minutes") },
					singleLine = true,
				)
			}

			OutlinedTextField(
				value = yieldsInput,
				onValueChange = onYieldsChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(YIELDS_FIELD_TAG),
				label = { Text(text = "Yields") },
				singleLine = true,
			)
			OutlinedTextField(
				value = ingredientsInput,
				onValueChange = onIngredientsChange,
				modifier = Modifier
					.fillMaxWidth()
					.testTag(INGREDIENTS_FIELD_TAG),
				label = { Text(text = "Ingredients") },
				supportingText = {
					Text(text = "Prefix a line with optional: to mark it as not required for pantry search.")
				},
				minLines = 4,
			)
			NutritionSummaryCard(
				nutrition = nutritionEstimate,
				isLoading = isNutritionEstimateLoading,
			)
			StepInputSection(
				stepInputs = stepInputs,
				onAddStepClick = onAddStepClick,
				onMoveStep = onMoveStep,
				onRemoveStepClick = onRemoveStepClick,
				onStepChange = onStepChange,
			)

			formErrorMessage?.let {
				Text(
					text = it,
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.error,
				)
			}

			successMessage?.let {
				Text(
					text = it,
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.primary,
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				Button(
					onClick = onSaveClick,
					enabled = !isSaving && !isImportingImage,
					modifier = Modifier
						.weight(1f)
						.testTag(SAVE_BUTTON_TAG),
				) {
					Text(text = if (isEditing) "Update recipe" else "Upload recipe")
				}
				FilledTonalButton(
					onClick = onStartNewClick,
					modifier = Modifier.weight(1f),
				) {
					Text(text = if (isEditing) "Start new" else "Clear form")
				}
			}
		}
	}
}

@Composable
private fun StepInputSection(
	stepInputs: StepInputsState,
	onAddStepClick: () -> Unit,
	onMoveStep: (Int, Int) -> Unit,
	onRemoveStepClick: (Int) -> Unit,
	onStepChange: (Int, String) -> Unit,
) {
	val rowHeights = remember { mutableStateMapOf<Int, Int>() }
	var draggedIndex by remember { mutableIntStateOf(-1) }
	var dragOffsetY by remember { mutableFloatStateOf(0f) }

	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = "Cooking steps",
				style = PurecipesTheme.typography.titleMedium,
			)
			FilledTonalButton(
				onClick = onAddStepClick,
				modifier = Modifier.testTag(STEP_ADD_BUTTON_TAG),
			) {
				Text(text = "Add step")
			}
		}

		stepInputs.items.forEachIndexed { index, stepInput ->
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.onSizeChanged { rowHeights[index] = it.height }
					.offset {
						IntOffset(
							x = 0,
							y = if (draggedIndex == index) dragOffsetY.roundToInt() else 0,
						)
					}
					.zIndex(if (draggedIndex == index) 1f else 0f),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
				verticalAlignment = Alignment.CenterVertically,
			) {
				StepDragHandle(
					index = index,
					onDragStart = {
						draggedIndex = index
						dragOffsetY = 0f
					},
					onDrag = { dragAmountY ->
						val currentIndex = draggedIndex

						if (currentIndex >= 0) {
							dragOffsetY += dragAmountY
							val currentRowHeight = rowHeights[currentIndex]

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
				)
				OutlinedTextField(
					value = stepInput,
					onValueChange = { onStepChange(index, it) },
					modifier = Modifier
						.weight(1f)
						.testTag("$STEP_FIELD_TAG_PREFIX$index"),
					label = { Text(text = "Step ${index + 1}") },
					minLines = 2,
				)
				if (stepInputs.items.size > 1) {
					IconButton(
						onClick = { onRemoveStepClick(index) },
						modifier = Modifier.testTag("$STEP_REMOVE_BUTTON_TAG_PREFIX$index"),
					) {
						Icon(
							imageVector = Icons.Filled.Delete,
							contentDescription = "Remove step ${index + 1}",
						)
					}
				}
			}
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

@Composable
private fun ImageImportPlaceholder() {
	Card(
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerHighest),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(180.dp),
			contentAlignment = Alignment.Center,
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				CircularProgressIndicator()
				Text(
					text = "Preparing image preview...",
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

@Composable
private fun ImageImportStatus(imageUrlInput: String, isImportingImage: Boolean) {
	val statusText = when {
		isImportingImage -> "Importing the selected image before upload."
		imageUrlInput.isBlank() -> null
		imageUrlInput.isRemoteImageUrl() -> "This image will be used from its remote URL."
		else -> "This local image will upload with the recipe when you save it."
	}

	statusText?.let {
		Text(
			text = it,
			style = PurecipesTheme.typography.bodyMedium,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
	}
}

private fun imagePreviewModel(imageUrlInput: String): String {
	val trimmedInput = imageUrlInput.trim()
	return if (trimmedInput.isRemoteImageUrl() ||
		trimmedInput.startsWith("file://", ignoreCase = true)
	) {
		trimmedInput
	} else {
		"file://$trimmedInput"
	}
}

private fun String.isRemoteImageUrl(): Boolean {
	return startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)
}

@Composable
private fun CuisinePicker(
	selectedCuisine: Cuisine?,
	onCuisineChange: (Cuisine?) -> Unit,
	modifier: Modifier = Modifier,
) {
	var isExpanded by remember { mutableStateOf(false) }

	Box(modifier = modifier) {
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
			Text(
				text = "Cuisine",
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
			OutlinedButton(
				onClick = { isExpanded = true },
				modifier = Modifier.fillMaxWidth(),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text(
						text = selectedCuisine?.displayName ?: "Select cuisine",
						color = if (selectedCuisine == null) {
							PurecipesTheme.colorScheme.onSurfaceVariant
						} else {
							PurecipesTheme.colorScheme.onSurface
						},
					)
					Icon(
						imageVector = Icons.Filled.ArrowDropDown,
						contentDescription = null,
					)
				}
			}
		}

		DropdownMenu(
			expanded = isExpanded,
			onDismissRequest = { isExpanded = false },
		) {
			DropdownMenuItem(
				text = { Text(text = "No cuisine") },
				onClick = {
					onCuisineChange(null)
					isExpanded = false
				},
			)
			Cuisine.entries.forEach { cuisine ->
				DropdownMenuItem(
					text = { Text(text = cuisine.displayName) },
					onClick = {
						onCuisineChange(cuisine)
						isExpanded = false
					},
				)
			}
		}
	}
}

@Preview(
	name = "Create recipe form light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CreateRecipeFormLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Scaffold(
			modifier = Modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = "Create recipe") },
				)
			},
		) { innerPadding ->
			Box(
				modifier = Modifier
					.padding(innerPadding)
					.padding(PurecipesTheme.space.m),
			) {
			CreateRecipeForm(
				selectedCuisine = Cuisine.ITALIAN,
				descriptionInput = "A quick weeknight dinner with pantry staples.",
				formErrorMessage = null,
				isImportingImage = false,
				imagePickerErrorMessage = null,
				imageUrlInput = "",
				ingredientsInput = "400 g spaghetti\n2 tomatoes\n1 garlic clove",
				isEditing = false,
				isNutritionEstimateLoading = false,
				isSaving = false,
				nutritionEstimate = null,
				onClearImageClick = {},
				onCuisineChange = {},
				onDescriptionChange = {},
				onImageUrlChange = {},
				onPickImageClick = {},
				onIngredientsChange = {},
				onSaveClick = {},
				onStartNewClick = {},
				onAddStepClick = {},
				onMoveStep = { _, _ -> },
				onRemoveStepClick = {},
				onStepChange = { _, _ -> },
				onTitleChange = {},
				onTotalTimeChange = {},
				onYieldsChange = {},
				stepInputs = StepInputsState(
					items = listOf(
						"Bring a large pot of salted water to a boil.",
						"Cook the pasta until al dente.",
					),
				),
				successMessage = null,
				titleInput = "Tomato Pasta",
				totalTimeInput = "25",
				yieldsInput = "2 servings",
			)
			}
		}
	}
}
