package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.ui.component.NutritionSummaryCard
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun CreateRecipeForm(
	selectedSection: CreateRecipeSection,
	selectedCuisine: Cuisine?,
	descriptionInput: String,
	formErrorMessage: String?,
	isImportingImage: Boolean,
	imagePickerErrorMessage: String?,
	imageUrlInput: String,
	ingredientRows: IngredientRowsState,
	suggestedUnits: SuggestedIngredientUnits,
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
		when (selectedSection) {
			CreateRecipeSection.About -> CreateRecipeAboutSection(
				titleInput = titleInput,
				descriptionInput = descriptionInput,
				imageUrlInput = imageUrlInput,
				isImportingImage = isImportingImage,
				isSaving = isSaving,
				imagePickerErrorMessage = imagePickerErrorMessage,
				selectedCuisine = selectedCuisine,
				totalTimeInput = totalTimeInput,
				yieldsInput = yieldsInput,
				isPrivate = isPrivate,
				canMakePrivate = canMakePrivate,
				onClearImageClick = onClearImageClick,
				onImageUrlChange = onImageUrlChange,
				onPickImageClick = onPickImageClick,
				onTitleChange = onTitleChange,
				onDescriptionChange = onDescriptionChange,
				onCuisineChange = onCuisineChange,
				onTotalTimeChange = onTotalTimeChange,
				onYieldsChange = onYieldsChange,
				onIsPrivateChange = onIsPrivateChange,
				onPrivacyLockedClick = onPrivacyLockedClick,
			)

			CreateRecipeSection.Ingredients -> Column(
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				CreateRecipeIngredientsSection(
					ingredientRows = ingredientRows,
					suggestedUnits = suggestedUnits,
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
			}

			CreateRecipeSection.Steps -> CreateRecipeStepsSection(
				stepInputs = stepInputs,
				onAddStepClick = onAddStepClick,
				onMoveStep = onMoveStep,
				onMoveStepUp = onMoveStepUp,
				onMoveStepDown = onMoveStepDown,
				onRemoveStepClick = onRemoveStepClick,
				onStepChange = onStepChange,
			)
		}

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
			Column(
				modifier = Modifier
					.padding(innerPadding)
					.padding(PurecipesTheme.space.m),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			) {
				CreateRecipeSectionSwitcher(
					selectedSection = CreateRecipeSection.About,
					onSectionChange = {},
				)
				CreateRecipeForm(
					selectedSection = CreateRecipeSection.About,
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
					suggestedUnits = SuggestedIngredientUnits(
						items = listOf("g", "kg", "ml", "l"),
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
