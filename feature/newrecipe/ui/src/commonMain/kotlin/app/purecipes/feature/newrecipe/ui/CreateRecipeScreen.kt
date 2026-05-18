package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

typealias RememberRecipeImagePicker = @Composable (
	onImageSelect: (String) -> Unit,
	onImportStateChange: (Boolean) -> Unit,
	onPickerError: (String) -> Unit,
) -> RecipeImagePickerLauncher?

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
				contentPadding = PaddingValues(PurecipesTheme.space.m),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
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
							style = PurecipesTheme.typography.bodyLarge,
							color = PurecipesTheme.colorScheme.error,
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
private fun SavedRecipeSectionHeader(recipeCount: Int) {
	SectionHeader(
		title = "Uploaded recipes",
		subtitle = if (recipeCount == 1) {
			"1 recipe uploaded to your account"
		} else {
			"$recipeCount recipes uploaded to your account"
		},
		titleStyle = PurecipesTheme.typography.titleLarge,
		subtitleStyle = PurecipesTheme.typography.bodyMedium,
	)
}

@Composable
private fun EmptyCreatedRecipes(hasError: Boolean, onRetry: () -> Unit) {
	Card(
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.l),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = "Create recipe",
				modifier = Modifier.size(56.dp),
				tint = PurecipesTheme.colorScheme.primary,
			)
			Text(
				text = if (hasError) "Could not load uploaded recipes" else "No recipes uploaded yet",
				style = PurecipesTheme.typography.headlineSmall,
				textAlign = TextAlign.Center,
			)
			Text(
				text = if (hasError) {
					"Try loading your uploaded recipe list again."
				} else {
					"Upload your own recipes here, then tap Edit any time to update them."
				},
				style = PurecipesTheme.typography.bodyLarge,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
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
private fun CreatedRecipeCard(recipe: RecipeDetails, onEditClick: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onEditClick),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			if (!recipe.imageUrl.isNullOrBlank()) {
				AsyncImage(
					model = recipe.imageUrl,
					contentDescription = recipe.title,
					modifier = Modifier
						.fillMaxWidth()
						.height(160.dp)
						.clip(RoundedCornerShape(PurecipesTheme.space.m)),
					contentScale = ContentScale.Crop,
				)
			}

			Text(
				text = recipe.title,
				style = PurecipesTheme.typography.titleLarge,
			)
			Text(
				text = recipe.description,
				style = PurecipesTheme.typography.bodyLarge,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
			Text(
				text = listOfNotNull(
					recipe.cuisine?.displayName,
					recipe.totalTime?.let { "$it min" },
					recipe.yields?.takeIf { it.isNotBlank() },
					recipe.steps.size.takeIf { it > 0 }?.let { "$it steps" },
				).joinToString(separator = " • "),
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
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
