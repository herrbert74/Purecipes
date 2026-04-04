package com.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeDetails
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

typealias RememberRecipeImagePicker = @Composable (
	onImageSelect: (String) -> Unit,
	onImportStateChange: (Boolean) -> Unit,
	onPickerError: (String) -> Unit,
) -> RecipeImagePickerLauncher?

@Immutable
private data class StepInputsState(
	val items: List<String>,
)

@Composable
fun CreateRecipeScreen(
	canUploadRecipes: Boolean,
	getCreatedRecipes: GetCreatedRecipesUseCase,
	saveCreatedRecipe: SaveCreatedRecipeUseCase,
	trackEvent: TrackEventUseCase,
	modifier: Modifier = Modifier,
	rememberImagePicker: RememberRecipeImagePicker = ::rememberRecipeImagePicker,
) {
	if (!canUploadRecipes) {
		Scaffold(
			modifier = modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = "Create recipe") },
				)
			},
		) { innerPadding ->
			UploadSignedOutContent(modifier = Modifier.padding(innerPadding))
		}
		return
	}

	val viewModel = createRecipeViewModel(
		getCreatedRecipes = getCreatedRecipes,
		saveCreatedRecipe = saveCreatedRecipe,
		trackEvent = trackEvent,
	)
	var pickerErrorMessage by remember { mutableStateOf<String?>(null) }
	var isImportingImage by remember { mutableStateOf(false) }
	val imagePickerLauncher = rememberImagePicker(
		{ imagePath ->
			isImportingImage = false
			pickerErrorMessage = null
			viewModel.onImageUrlChange(imagePath)
		},
		{ isImporting ->
			isImportingImage = isImporting
		},
		{ message ->
			isImportingImage = false
			pickerErrorMessage = message
		},
	)

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Create recipe") },
			)
		},
	) { innerPadding ->
		when {
			viewModel.isLoading && viewModel.recipes.isEmpty() -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}

			else -> LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentPadding = PaddingValues(16.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				item {
					CreateRecipeForm(
						selectedCuisine = viewModel.selectedCuisine,
						descriptionInput = viewModel.descriptionInput,
						formErrorMessage = viewModel.formErrorMessage,
						isImportingImage = isImportingImage,
						imagePickerErrorMessage = pickerErrorMessage,
						imageUrlInput = viewModel.imageUrlInput,
						ingredientsInput = viewModel.ingredientsInput,
						isEditing = viewModel.isEditing,
						isSaving = viewModel.isSaving,
						onClearImageClick = {
							isImportingImage = false
							pickerErrorMessage = null
							viewModel.onImageUrlChange("")
						},
						onCuisineChange = viewModel::onCuisineChange,
						onDescriptionChange = viewModel::onDescriptionChange,
						onImageUrlChange = { value ->
							isImportingImage = false
							pickerErrorMessage = null
							viewModel.onImageUrlChange(value)
						},
						onPickImageClick = imagePickerLauncher?.let { launcher ->
							{
								launcher.launch()
							}
						},
						onIngredientsChange = viewModel::onIngredientsChange,
						onSaveClick = viewModel::saveRecipe,
						onStartNewClick = {
							isImportingImage = false
							pickerErrorMessage = null
							viewModel.startNewRecipe()
						},
						onAddStepClick = viewModel::addStep,
						onMoveStep = viewModel::moveStep,
						onRemoveStepClick = viewModel::removeStep,
						onStepChange = viewModel::onStepChange,
						onTitleChange = viewModel::onTitleChange,
						onTotalTimeChange = viewModel::onTotalTimeChange,
						onYieldsChange = viewModel::onYieldsChange,
						stepInputs = StepInputsState(items = viewModel.stepInputs.toList()),
						successMessage = viewModel.successMessage,
						titleInput = viewModel.titleInput,
						totalTimeInput = viewModel.totalTimeInput,
						yieldsInput = viewModel.yieldsInput,
					)
				}

				item {
					SavedRecipeSectionHeader(recipeCount = viewModel.recipes.size)
				}

				if (viewModel.errorMessage != null) {
					item {
						Text(
							text = viewModel.errorMessage ?: "Unknown error",
							style = MaterialTheme.typography.bodyLarge,
							color = MaterialTheme.colorScheme.error,
						)
					}
				}

				if (viewModel.recipes.isEmpty()) {
					item {
						EmptyCreatedRecipes(
							onRetry = viewModel::retry,
							hasError = viewModel.errorMessage != null,
						)
					}
				} else {
					items(viewModel.recipes, key = RecipeDetails::id) { recipe ->
						CreatedRecipeCard(
							recipe = recipe,
							onEditClick = { viewModel.editRecipe(recipe) },
						)
					}
				}
			}
		}
	}
}

@Composable
private fun CreateRecipeForm(
	selectedCuisine: Cuisine?,
	descriptionInput: String,
	formErrorMessage: String?,
	isImportingImage: Boolean,
	imagePickerErrorMessage: String?,
	imageUrlInput: String,
	ingredientsInput: String,
	isEditing: Boolean,
	isSaving: Boolean,
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
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Text(
				text = if (isEditing) "Edit recipe" else "New recipe",
				style = MaterialTheme.typography.headlineSmall,
			)
			Text(
				text = "Write one ingredient per line and add cooking steps below. Recipes are uploaded to your account, " +
					"and local image paths are uploaded as image files.",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
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
				horizontalArrangement = Arrangement.spacedBy(12.dp),
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
								modifier = Modifier.size(18.dp),
								strokeWidth = 2.dp,
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
						.clip(RoundedCornerShape(16.dp)),
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
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error,
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
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
				minLines = 4,
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
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error,
				)
			}

			successMessage?.let {
				Text(
					text = it,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.primary,
				)
			}

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
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

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = "Cooking steps",
				style = MaterialTheme.typography.titleMedium,
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
				horizontalArrangement = Arrangement.spacedBy(12.dp),
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
			.size(48.dp)
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
			tint = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun ImageImportPlaceholder() {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(180.dp),
			contentAlignment = Alignment.Center,
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(12.dp),
			) {
				CircularProgressIndicator()
				Text(
					text = "Preparing image preview...",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
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
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun SavedRecipeSectionHeader(recipeCount: Int) {
	Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
		Text(
			text = "Uploaded recipes",
			style = MaterialTheme.typography.titleLarge,
		)
		Text(
			text = if (recipeCount == 1) {
				"1 recipe uploaded to your account"
			} else {
				"$recipeCount recipes uploaded to your account"
			},
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun EmptyCreatedRecipes(hasError: Boolean, onRetry: () -> Unit) {
	Card(
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = "Create recipe",
				modifier = Modifier.size(56.dp),
				tint = MaterialTheme.colorScheme.primary,
			)
			Text(
				text = if (hasError) "Could not load uploaded recipes" else "No recipes uploaded yet",
				style = MaterialTheme.typography.headlineSmall,
				textAlign = TextAlign.Center,
			)
			Text(
				text = if (hasError) {
					"Try loading your uploaded recipe list again."
				} else {
					"Upload your own recipes here, then tap Edit any time to update them."
				},
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center,
			)
			if (hasError) {
				TextButton(onClick = onRetry) {
					Text(text = "Retry")
				}
			}
		}
	}
}

@Composable
private fun UploadSignedOutContent(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.padding(24.dp),
		contentAlignment = Alignment.Center,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = "Create recipe",
				modifier = Modifier.size(56.dp),
				tint = MaterialTheme.colorScheme.primary,
			)
			Text(
				text = "Sign in to upload recipes",
				style = MaterialTheme.typography.headlineSmall,
			)
			Text(
				text = "Recipe upload is tied to your account so you can edit your uploaded recipes later.",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				textAlign = TextAlign.Center,
			)
		}
	}
}

@Composable
private fun CreatedRecipeCard(recipe: RecipeDetails, onEditClick: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onEditClick),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			if (!recipe.imageUrl.isNullOrBlank()) {
				AsyncImage(
					model = recipe.imageUrl,
					contentDescription = recipe.title,
					modifier = Modifier
						.fillMaxWidth()
						.height(160.dp)
						.clip(RoundedCornerShape(16.dp)),
					contentScale = ContentScale.Crop,
				)
			}

			Text(
				text = recipe.title,
				style = MaterialTheme.typography.titleLarge,
			)
			Text(
				text = recipe.description,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Text(
				text = listOfNotNull(
					recipe.cuisine?.displayName,
					recipe.totalTime?.let { "$it min" },
					recipe.yields?.takeIf { it.isNotBlank() },
					recipe.steps.size.takeIf { it > 0 }?.let { "$it steps" },
				).joinToString(separator = " • "),
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.End,
			) {
				TextButton(onClick = onEditClick) {
					Icon(
						imageVector = Icons.Filled.Edit,
						contentDescription = null,
					)
					Text(text = "Edit")
				}
			}
		}
	}
}

@Composable
private fun CuisinePicker(
	selectedCuisine: Cuisine?,
	onCuisineChange: (Cuisine?) -> Unit,
	modifier: Modifier = Modifier,
) {
	var isExpanded by remember { mutableStateOf(false) }

	Box(modifier = modifier) {
		Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
			Text(
				text = "Cuisine",
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
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
							MaterialTheme.colorScheme.onSurfaceVariant
						} else {
							MaterialTheme.colorScheme.onSurface
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
