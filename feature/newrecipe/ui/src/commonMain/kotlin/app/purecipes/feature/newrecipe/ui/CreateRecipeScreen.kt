package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumFeature
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
	onOpenPaywall: (String) -> Unit = {},
	rememberImagePicker: RememberRecipeImagePicker = ::rememberRecipeImagePicker,
	viewModel: CreateRecipeViewModel = metroViewModel(),
) {
	if (!canUploadRecipes) {
		CreateRecipeSignedOutScaffold(
			modifier = modifier,
			onBack = onBack,
			onRequestLogIn = onRequestLogIn,
		)
		return
	}

	val currentOnSaveSuccess by rememberUpdatedState(onSaveSuccess)
	val showRecipeLoading = isCreateRecipeLoading(
		recipeId = recipeId,
		editingRecipeId = viewModel.editingRecipeId,
		isLoadingRecipe = viewModel.isLoadingRecipe,
	)

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
	val showEditorChrome = !showRecipeLoading && viewModel.loadErrorMessage == null
	val scrollState = rememberScrollState()
	var isLastStepFieldFocused by remember { mutableStateOf(false) }
	var showClearFormConfirmation by remember { mutableStateOf(false) }
	LaunchedEffect(viewModel.selectedSection) {
		scrollState.scrollTo(0)
		if (viewModel.selectedSection != CreateRecipeSection.Steps) {
			isLastStepFieldFocused = false
		}
	}

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
			if (showEditorChrome && !isLastStepFieldFocused) {
				CreateRecipeSaveBar(
					isEditing = viewModel.isEditing,
					isSaving = viewModel.isSaving,
					isImportingImage = isImportingImage,
					onSaveClick = viewModel::saveRecipe,
					onClearClick = { showClearFormConfirmation = true },
				)
			}
		},
	) { innerPadding ->
		when {
			showRecipeLoading -> CreateRecipeLoadingContent(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
			)

			viewModel.loadErrorMessage != null -> CreateRecipeLoadErrorContent(
				message = viewModel.loadErrorMessage ?: "Unknown error",
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
			)

			else -> Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
				CreateRecipeSectionSwitcher(
					selectedSection = viewModel.selectedSection,
					onSectionChange = { viewModel.selectedSection = it },
					modifier = Modifier.padding(
						start = PurecipesTheme.space.m,
						end = PurecipesTheme.space.m,
						top = PurecipesTheme.space.m,
					),
				)
				Column(
					modifier = Modifier
						.weight(1f)
						.fillMaxWidth()
						.verticalScroll(scrollState)
						.imePadding()
						.padding(PurecipesTheme.space.m),
				) {
					CreateRecipeForm(
						selectedSection = viewModel.selectedSection,
						selectedCuisine = viewModel.selectedCuisine,
						descriptionInput = viewModel.descriptionInput,
						formErrorMessage = viewModel.formErrorMessage,
						fieldErrors = viewModel.fieldErrors,
						isImportingImage = isImportingImage,
						imagePickerErrorMessage = pickerErrorMessage,
						imageUrlInput = viewModel.imageUrlInput,
						ingredientRows = IngredientRowsState(items = viewModel.ingredientsEditor.rows.toList()),
						suggestedUnits = viewModel.suggestedUnits,
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
						isPrivate = viewModel.isPrivate,
						canMakePrivate = viewModel.canMakePrivate,
						onIsPrivateChange = viewModel::onIsPrivateChange,
						onPrivacyLockedClick = {
							viewModel.onIsPrivateChange(true)
							onOpenPaywall(AnalyticsPremiumFeature.PRIVATE_RECIPES)
						},
						stepsFormActionChips = createRecipeStepsFormActionChips(
							isEditing = viewModel.isEditing,
							isSaving = viewModel.isSaving,
							isImportingImage = isImportingImage,
							onSaveClick = viewModel::saveRecipe,
							onClearClick = { showClearFormConfirmation = true },
						),
						onLastStepFieldFocusChange = { isLastStepFieldFocused = it },
					)
				}
			}
		}
	}

	CreateRecipeClearFormConfirmation(
		visible = showClearFormConfirmation,
		isEditing = viewModel.isEditing,
		onDismiss = { showClearFormConfirmation = false },
		onConfirm = {
			isImportingImage = false
			pickerErrorMessage = null
			viewModel.startNewRecipe()
			showClearFormConfirmation = false
		},
	)
}

@Composable
private fun CreateRecipeClearFormConfirmation(
	visible: Boolean,
	isEditing: Boolean,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
) {
	if (visible) {
		CreateRecipeClearFormDialog(
			isEditing = isEditing,
			onDismiss = onDismiss,
			onConfirm = onConfirm,
		)
	}
}

@Composable
private fun CreateRecipeSignedOutScaffold(
	modifier: Modifier = Modifier,
	onBack: (() -> Unit)? = null,
	onRequestLogIn: () -> Unit = {},
) {
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
}

@Composable
private fun CreateRecipeLoadingContent(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center,
	) {
		CircularProgressIndicator()
	}
}

@Composable
private fun CreateRecipeLoadErrorContent(
	message: String,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center,
	) {
		ErrorText(
			text = message,
			textAlign = TextAlign.Center,
		)
	}
}

private fun isCreateRecipeLoading(
	recipeId: Int?,
	editingRecipeId: Int?,
	isLoadingRecipe: Boolean,
): Boolean = isLoadingRecipe || (recipeId != null && editingRecipeId != recipeId)
