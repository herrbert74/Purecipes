package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.ui.component.NutritionSummaryCard
import app.purecipes.shared.ui.theme.PurecipesTheme

private const val CUISINE_FIELD_TAG = "createRecipeCuisineField"
private const val DESCRIPTION_FIELD_TAG = "createRecipeDescriptionField"
private const val TITLE_FIELD_TAG = "createRecipeTitleField"
private const val TOTAL_TIME_FIELD_TAG = "createRecipeTotalTimeField"
private const val YIELDS_FIELD_TAG = "createRecipeYieldsField"

@Composable
internal fun CreateRecipeForm(
	selectedCuisine: Cuisine?,
	descriptionInput: String,
	formErrorMessage: String?,
	isImportingImage: Boolean,
	imagePickerErrorMessage: String?,
	imageUrlInput: String,
	ingredientRows: IngredientRowsState,
	isNutritionEstimateLoading: Boolean,
	isSaving: Boolean,
	nutritionEstimate: NutritionSummary?,
	onClearImageClick: () -> Unit,
	onCuisineChange: (Cuisine?) -> Unit,
	onDescriptionChange: (String) -> Unit,
	onImageUrlChange: (String) -> Unit,
	onPickImageClick: (() -> Unit)?,
	onIngredientRowChange: (Int, IngredientRowInput) -> Unit,
	onAddIngredientClick: () -> Unit,
	onRemoveIngredientClick: (Int) -> Unit,
	onAddIngredientAlternativeClick: (Int) -> Unit,
	onRemoveIngredientAlternativeClick: (Int, Int) -> Unit,
	onPasteIngredientLines: (String) -> Unit,
	onAddStepClick: () -> Unit,
	onMoveStep: (Int, Int) -> Unit,
	onMoveStepUp: (Int) -> Unit,
	onMoveStepDown: (Int) -> Unit,
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
	isPrivate: Boolean,
	canMakePrivate: Boolean,
	onIsPrivateChange: (Boolean) -> Unit,
	onPrivacyLockedClick: () -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
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
		CreateRecipePrivacySection(
			isPrivate = isPrivate,
			canMakePrivate = canMakePrivate,
			onIsPrivateChange = onIsPrivateChange,
			onLockedClick = onPrivacyLockedClick,
		)
		CreateRecipeIngredientsSection(
			ingredientRows = ingredientRows,
			onRowChange = onIngredientRowChange,
			onAddRowClick = onAddIngredientClick,
			onRemoveRowClick = onRemoveIngredientClick,
			onAddAlternativeClick = onAddIngredientAlternativeClick,
			onRemoveAlternativeClick = onRemoveIngredientAlternativeClick,
			onPasteLines = onPasteIngredientLines,
		)
		NutritionSummaryCard(
			nutrition = nutritionEstimate,
			isLoading = isNutritionEstimateLoading,
		)
		CreateRecipeStepsSection(
			stepInputs = stepInputs,
			onAddStepClick = onAddStepClick,
			onMoveStep = onMoveStep,
			onMoveStepUp = onMoveStepUp,
			onMoveStepDown = onMoveStepDown,
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
					ingredientRows = IngredientRowsState(
						items = listOf(
							IngredientRowInput(
								primary = IngredientPartInput(
									amount = "400",
									unit = "g",
									name = "spaghetti",
								),
							),
							IngredientRowInput(
								primary = IngredientPartInput(
									amount = "2",
									name = "tomatoes",
								),
							),
						),
					),
					isNutritionEstimateLoading = false,
					isSaving = false,
					nutritionEstimate = null,
					onClearImageClick = {},
					onCuisineChange = {},
					onDescriptionChange = {},
					onImageUrlChange = {},
					onPickImageClick = {},
					onIngredientRowChange = { _, _ -> },
					onAddIngredientClick = {},
					onRemoveIngredientClick = {},
					onAddIngredientAlternativeClick = {},
					onRemoveIngredientAlternativeClick = { _, _ -> },
					onPasteIngredientLines = {},
					onAddStepClick = {},
					onMoveStep = { _, _ -> },
					onMoveStepUp = {},
					onMoveStepDown = {},
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
					isPrivate = false,
					canMakePrivate = true,
					onIsPrivateChange = {},
					onPrivacyLockedClick = {},
				)
			}
		}
	}
}
