package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import app.purecipes.shared.ui.component.BackNavigationButton
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.metroViewModel

typealias RememberRecipeImagePicker = @Composable (
	onImageSelect: (String) -> Unit,
	onImportStateChange: (Boolean) -> Unit,
	onPickerError: (String) -> Unit,
) -> RecipeImagePickerLauncher?

@Composable
fun CreateRecipeScreen(
	canUploadRecipes: Boolean,
	modifier: Modifier = Modifier,
	recipeId: Int? = null,
	onBack: (() -> Unit)? = null,
	onSaveSuccess: (String) -> Unit = {},
	onRequestLogIn: () -> Unit = {},
	rememberImagePicker: RememberRecipeImagePicker = ::rememberRecipeImagePicker,
	viewModel: CreateRecipeViewModel = metroViewModel(),
) {
	if (!canUploadRecipes) {
		Scaffold(
			modifier = modifier.fillMaxSize(),
			topBar = {
				TopAppBar(
					title = { Text(text = "Create recipe") },
					navigationIcon = {
						if (onBack != null) {
							BackNavigationButton(onBack = onBack)
						}
					},
				)
			},
		) { innerPadding ->
			UploadSignedOutContent(
				onRequestLogIn = onRequestLogIn,
				modifier = Modifier.padding(innerPadding),
			)
		}
		return
	}

	val currentOnSaveSuccess by rememberUpdatedState(onSaveSuccess)

	LaunchedEffect(recipeId) {
		viewModel.onRecipeIdChanged(recipeId)
	}

	LaunchedEffect(viewModel.saveCompletedEvent) {
		if (viewModel.saveCompletedEvent > 0) {
			val message = viewModel.successMessage ?: return@LaunchedEffect
			currentOnSaveSuccess(message)
		}
	}

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
	val showEditorChrome = !viewModel.isLoadingRecipe && viewModel.loadErrorMessage == null

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = if (recipeId != null || viewModel.isEditing) {
							"Edit recipe"
						} else {
							"Create recipe"
						},
					)
				},
				navigationIcon = {
					if (onBack != null) {
						BackNavigationButton(onBack = onBack)
					}
				},
			)
		},
		bottomBar = {
			if (showEditorChrome) {
				CreateRecipeSaveBar(
					isEditing = viewModel.isEditing,
					isSaving = viewModel.isSaving,
					isImportingImage = isImportingImage,
					onSaveClick = viewModel::saveRecipe,
					onClearClick = {
						isImportingImage = false
						pickerErrorMessage = null
						viewModel.startNewRecipe()
					},
				)
			}
		},
	) { innerPadding ->
		when {
			viewModel.isLoadingRecipe -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}

			viewModel.loadErrorMessage != null -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				ErrorText(
					text = viewModel.loadErrorMessage ?: "Unknown error",
					textAlign = TextAlign.Center,
				)
			}

			else -> LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentPadding = PaddingValues(PurecipesTheme.space.m),
			) {
				item {
					CreateRecipeForm(
						selectedCuisine = viewModel.selectedCuisine,
						descriptionInput = viewModel.descriptionInput,
						formErrorMessage = viewModel.formErrorMessage,
						isImportingImage = isImportingImage,
						imagePickerErrorMessage = pickerErrorMessage,
						imageUrlInput = viewModel.imageUrlInput,
						ingredientRows = IngredientRowsState(items = viewModel.ingredientsEditor.rows.toList()),
						isNutritionEstimateLoading = viewModel.isNutritionEstimateLoading,
						isSaving = viewModel.isSaving,
						nutritionEstimate = viewModel.nutritionEstimate,
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
						onIngredientRowChange = { index, row ->
							viewModel.ingredientsEditor.onRowChange(index, row)
							viewModel.onIngredientsEdited()
						},
						onAddIngredientClick = {
							viewModel.ingredientsEditor.addRow()
						},
						onRemoveIngredientClick = { index ->
							viewModel.ingredientsEditor.removeRow(index)
							viewModel.onIngredientsEdited()
						},
						onAddIngredientAlternativeClick = { index ->
							viewModel.ingredientsEditor.addAlternative(index)
						},
						onRemoveIngredientAlternativeClick = { rowIndex, alternativeIndex ->
							viewModel.ingredientsEditor.removeAlternative(rowIndex, alternativeIndex)
							viewModel.onIngredientsEdited()
						},
						onPasteIngredientLines = { text ->
							viewModel.ingredientsEditor.pasteLines(text)
							viewModel.onIngredientsEdited()
						},
						onAddStepClick = viewModel::addStep,
						onMoveStep = viewModel::moveStep,
						onMoveStepUp = viewModel::moveStepUp,
						onMoveStepDown = viewModel::moveStepDown,
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
			}
		}
	}
}
