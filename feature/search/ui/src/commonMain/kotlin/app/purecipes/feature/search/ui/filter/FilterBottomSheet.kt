package app.purecipes.feature.search.ui.filter

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.CalorieRange
import app.purecipes.shared.domain.model.CookingMethod
import app.purecipes.shared.domain.model.CookingTimeRange
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.DifficultyLevel
import app.purecipes.shared.domain.model.IngredientMatchResponse
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.NutritionFilter
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

private const val SCROLLBAR_MIN_THUMB_FRACTION = 0.1f
private const val FILTER_TAB_CONTENT_SMALL_SCREEN_HEIGHT_DP = 720
private const val FILTER_TAB_CONTENT_HEIGHT_FRACTION_LARGE = 0.8f
private const val FILTER_TAB_CONTENT_HEIGHT_FRACTION_SMALL = 0.9f

internal const val FILTER_BOTTOM_SHEET_SIGN_IN_PROMPT_TITLE_TAG = "filterBottomSheetSignInPromptTitle"
internal const val FILTER_BOTTOM_SHEET_GO_TO_ACCOUNT_BUTTON_TAG = "filterBottomSheetGoToAccountButton"
internal const val FILTER_BOTTOM_SHEET_PANTRY_TAB_TAG = "filterBottomSheetPantryTab"
internal const val FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG = "filterBottomSheetRecipeFiltersTab"
internal const val FILTER_BOTTOM_SHEET_PANTRY_INTRO_TAG = "filterBottomSheetPantryIntro"
internal const val FILTER_BOTTOM_SHEET_RECIPE_FILTERS_INTRO_TAG = "filterBottomSheetRecipeFiltersIntro"
internal const val FILTER_BOTTOM_SHEET_SCROLL_TAG = "filterBottomSheetScroll"

private enum class FilterTab {
	Pantry,
	RecipeFilters,
}

@Composable
internal fun FilterBottomSheet(
	filters: SearchFilters,
	isSignedIn: Boolean,
	pantryIngredients: ImmutableSet<String>,
	excludedIngredients: ImmutableSet<String>,
	customPantryIngredients: ImmutableSet<String>,
	ingredientMatchPreview: IngredientMatchResponse?,
	isIngredientMatchLoading: Boolean,
	sheetState: SheetState,
	onDismiss: () -> Unit,
	onFiltersChange: (SearchFilters) -> Unit,
	onIngredientSelectionChange: (pantryIngredients: Set<String>, excludedIngredients: Set<String>) -> Unit,
	onCustomIngredientToggle: (String) -> Unit,
	onRemoveCustomIngredient: (String) -> Unit,
	onAddIngredientQueryChange: (String) -> Unit,
	onAddIngredient: (String) -> Unit,
	onClearIngredientMatchPreview: () -> Unit,
	onRequestLogIn: () -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = sheetState,
	) {
		FilterBottomSheetContent(
			filters = filters,
			isSignedIn = isSignedIn,
			pantryIngredients = pantryIngredients,
			excludedIngredients = excludedIngredients,
			customPantryIngredients = customPantryIngredients,
			ingredientMatchPreview = ingredientMatchPreview,
			isIngredientMatchLoading = isIngredientMatchLoading,
			onFiltersChange = onFiltersChange,
			onIngredientSelectionChange = onIngredientSelectionChange,
			onCustomIngredientToggle = onCustomIngredientToggle,
			onRemoveCustomIngredient = onRemoveCustomIngredient,
			onAddIngredientQueryChange = onAddIngredientQueryChange,
			onAddIngredient = onAddIngredient,
			onClearIngredientMatchPreview = onClearIngredientMatchPreview,
			onRequestLogIn = onRequestLogIn,
		)
	}
}

@Composable
private fun FilterBottomSheetContent(
	filters: SearchFilters,
	isSignedIn: Boolean,
	pantryIngredients: ImmutableSet<String>,
	excludedIngredients: ImmutableSet<String>,
	customPantryIngredients: ImmutableSet<String>,
	ingredientMatchPreview: IngredientMatchResponse?,
	isIngredientMatchLoading: Boolean,
	onFiltersChange: (SearchFilters) -> Unit,
	onIngredientSelectionChange: (pantryIngredients: Set<String>, excludedIngredients: Set<String>) -> Unit,
	onCustomIngredientToggle: (String) -> Unit,
	onRemoveCustomIngredient: (String) -> Unit,
	onAddIngredientQueryChange: (String) -> Unit,
	onAddIngredient: (String) -> Unit,
	onClearIngredientMatchPreview: () -> Unit,
	onRequestLogIn: () -> Unit,
) {
	if (!isSignedIn) {
		FilterLoginRequiredContent(onRequestLogIn = onRequestLogIn)
	} else {
		var selectedTab by remember { mutableStateOf(FilterTab.Pantry) }
		BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
			val sheetHeightFraction = if (maxHeight < FILTER_TAB_CONTENT_SMALL_SCREEN_HEIGHT_DP.dp) {
				FILTER_TAB_CONTENT_HEIGHT_FRACTION_SMALL
			} else {
				FILTER_TAB_CONTENT_HEIGHT_FRACTION_LARGE
			}
			val sheetHeightModifier = if (maxHeight != Dp.Infinity) {
				Modifier.height(maxHeight * sheetHeightFraction)
			} else {
				Modifier
			}
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.then(sheetHeightModifier),
			) {
				PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
					Tab(
						selected = selectedTab == FilterTab.Pantry,
						onClick = { selectedTab = FilterTab.Pantry },
						modifier = Modifier.testTag(FILTER_BOTTOM_SHEET_PANTRY_TAB_TAG),
						text = { Text(text = "Pantry") },
					)
					Tab(
						selected = selectedTab == FilterTab.RecipeFilters,
						onClick = { selectedTab = FilterTab.RecipeFilters },
						modifier = Modifier.testTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG),
						text = { Text(text = "Recipe filters") },
					)
				}
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f),
				) {
					when (selectedTab) {
						FilterTab.Pantry -> PantryFilterTabContent(
							pantryIngredients = pantryIngredients,
							excludedIngredients = excludedIngredients,
							customPantryIngredients = customPantryIngredients,
							ingredientMatchPreview = ingredientMatchPreview,
							isIngredientMatchLoading = isIngredientMatchLoading,
							onIngredientSelectionChange = onIngredientSelectionChange,
							onCustomIngredientToggle = onCustomIngredientToggle,
							onRemoveCustomIngredient = onRemoveCustomIngredient,
							onAddIngredientQueryChange = onAddIngredientQueryChange,
							onAddIngredient = onAddIngredient,
							onClearIngredientMatchPreview = onClearIngredientMatchPreview,
						)

						FilterTab.RecipeFilters -> RecipeFiltersTabContent(
							filters = filters,
							onFiltersChange = onFiltersChange,
						)
					}
				}
			}
		}
	}
}

@Composable
private fun PantryFilterTabContent(
	pantryIngredients: ImmutableSet<String>,
	excludedIngredients: ImmutableSet<String>,
	customPantryIngredients: ImmutableSet<String>,
	ingredientMatchPreview: IngredientMatchResponse?,
	isIngredientMatchLoading: Boolean,
	onIngredientSelectionChange: (pantryIngredients: Set<String>, excludedIngredients: Set<String>) -> Unit,
	onCustomIngredientToggle: (String) -> Unit,
	onRemoveCustomIngredient: (String) -> Unit,
	onAddIngredientQueryChange: (String) -> Unit,
	onAddIngredient: (String) -> Unit,
	onClearIngredientMatchPreview: () -> Unit,
) {
	FilterScrollableColumn {
		item {
			Text(
				text = "Tap an ingredient to mark it as in your pantry. Tap again to exclude it from " +
					"search results. Tap a third time to clear the selection. Red chips exclude recipes " +
					"that contain that ingredient.",
				style = PurecipesTheme.typography.bodyMedium,
				modifier = Modifier
					.testTag(FILTER_BOTTOM_SHEET_PANTRY_INTRO_TAG)
					.padding(
						start = PurecipesTheme.space.m,
						end = PurecipesTheme.space.m,
						top = PurecipesTheme.space.s,
					),
			)
		}
		item {
			IngredientFilterLegend(
				modifier = Modifier.padding(
					top = PurecipesTheme.space.s,
					bottom = PurecipesTheme.space.xs,
				),
			)
		}
		item {
			IngredientFilterSection(
				pantryIngredients = pantryIngredients,
				excludedIngredients = excludedIngredients,
				onSelectionChange = onIngredientSelectionChange,
			)
		}
		item {
			YourIngredientsSection(
				customPantryIngredients = customPantryIngredients,
				pantryIngredients = pantryIngredients,
				excludedIngredients = excludedIngredients,
				ingredientMatchPreview = ingredientMatchPreview,
				isIngredientMatchLoading = isIngredientMatchLoading,
				onCustomIngredientToggle = onCustomIngredientToggle,
				onRemoveCustomIngredient = onRemoveCustomIngredient,
				onAddIngredientQueryChange = onAddIngredientQueryChange,
				onAddIngredient = onAddIngredient,
				onClearIngredientMatchPreview = onClearIngredientMatchPreview,
			)
		}
	}
}

@Composable
private fun RecipeFiltersTabContent(
	filters: SearchFilters,
	onFiltersChange: (SearchFilters) -> Unit,
) {
	FilterScrollableColumn {
		item {
			Text(
				text = "With the filters below you can get a more tailored result.",
				style = PurecipesTheme.typography.bodyMedium,
				modifier = Modifier
					.testTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_INTRO_TAG)
					.padding(
						start = PurecipesTheme.space.m,
						end = PurecipesTheme.space.m,
						top = PurecipesTheme.space.s,
					),
			)
		}
		item {
			FilterChipSection(
				title = "Dietary Preferences",
				items = DietaryPreference.entries.toImmutableList(),
				selected = filters.dietaryPreferences.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(dietaryPreferences = it)) },
			)
		}
		item {
			FilterChipSection(
				title = "Cuisine",
				items = Cuisine.entries.toImmutableList(),
				selected = filters.cuisines.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(cuisines = it)) },
			)
		}
		item {
			FilterChipSection(
				title = "Meal Type",
				items = MealType.entries.toImmutableList(),
				selected = filters.mealTypes.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(mealTypes = it)) },
			)
		}
		item {
			FilterChipSection(
				title = "Cooking Time",
				items = CookingTimeRange.entries.toImmutableList(),
				selected = filters.cookingTimeRanges.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(cookingTimeRanges = it)) },
			)
		}
		item {
			FilterChipSection(
				title = "Difficulty Level",
				items = DifficultyLevel.entries.toImmutableList(),
				selected = filters.difficultyLevels.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(difficultyLevels = it)) },
			)
		}
		item {
			FilterChipSection(
				title = "Cooking Method",
				items = CookingMethod.entries.toImmutableList(),
				selected = filters.cookingMethods.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(cookingMethods = it)) },
			)
		}
		item {
			FilterChipSection(
				title = "Calorie Range",
				items = CalorieRange.entries.toImmutableList(),
				selected = filters.calorieRanges.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(calorieRanges = it)) },
			)
		}
		item {
			FilterChipSection(
				title = "Nutrition",
				items = NutritionFilter.entries.toImmutableList(),
				selected = filters.nutritionFilters.toImmutableSet(),
				itemLabel = { it.displayName },
				onSelectionChange = { onFiltersChange(filters.copy(nutritionFilters = it)) },
			)
		}
	}
}

@Composable
private fun FilterScrollableColumn(
	content: LazyListScope.() -> Unit,
) {
	val scrollState = rememberLazyListState()
	Box(modifier = Modifier.fillMaxSize()) {
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.testTag(FILTER_BOTTOM_SHEET_SCROLL_TAG),
			state = scrollState,
			contentPadding = PaddingValues(bottom = PurecipesTheme.space.xxl),
			content = content,
		)
		VerticalScrollbar(
			state = scrollState,
			modifier = Modifier.align(Alignment.CenterEnd),
		)
	}
}

@Composable
private fun FilterLoginRequiredContent(
	onRequestLogIn: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.m,
				top = PurecipesTheme.space.s,
				bottom = PurecipesTheme.space.xxl,
			),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Text(
			text = "Sign in to use filters",
			style = PurecipesTheme.typography.titleMedium,
			modifier = Modifier.testTag(FILTER_BOTTOM_SHEET_SIGN_IN_PROMPT_TITLE_TAG),
		)
		Text(
			text = "Pantry matching and your filter choices apply to search once you are signed in.",
			style = PurecipesTheme.typography.bodyMedium,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		Button(
			onClick = onRequestLogIn,
			modifier = Modifier.testTag(FILTER_BOTTOM_SHEET_GO_TO_ACCOUNT_BUTTON_TAG),
		) {
			Text("Go to Account")
		}
	}
}

@Composable
private fun VerticalScrollbar(state: LazyListState, modifier: Modifier = Modifier) {
	val thumbFractions by remember {
		derivedStateOf {
			val layoutInfo = state.layoutInfo
			val totalItems = layoutInfo.totalItemsCount
			val visibleItems = layoutInfo.visibleItemsInfo
			if (totalItems == 0 || visibleItems.isEmpty()) return@derivedStateOf null
			val viewportH = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).toFloat()
			if (viewportH <= 0f) return@derivedStateOf null
			val firstItem = visibleItems.first()
			val lastItem = visibleItems.last()
			val avgH = if (visibleItems.size > 1) {
				(lastItem.offset + lastItem.size - firstItem.offset).toFloat() / visibleItems.size
			} else {
				firstItem.size.toFloat()
			}
			val totalH = totalItems * avgH
			if (totalH <= viewportH) return@derivedStateOf null
			val thumbH = (viewportH / totalH).coerceIn(SCROLLBAR_MIN_THUMB_FRACTION, 1f)
			val scrolled = firstItem.index * avgH - firstItem.offset.toFloat()
			val maxScroll = totalH - viewportH
			val thumbStart = if (maxScroll > 0f) {
				(scrolled / maxScroll * (1f - thumbH)).coerceIn(0f, 1f - thumbH)
			} else {
				0f
			}
			thumbStart to thumbStart + thumbH
		}
	}
	val (thumbStart, thumbEnd) = thumbFractions ?: return
	Canvas(
		modifier = modifier
			.fillMaxHeight()
			.width(6.dp),
	) {
		drawRoundRect(
			color = Color.Gray.copy(alpha = 0.4f),
			topLeft = Offset(1.dp.toPx(), size.height * thumbStart),
			size = Size(4.dp.toPx(), size.height * (thumbEnd - thumbStart)),
			cornerRadius = CornerRadius(2.dp.toPx()),
		)
	}
}

@Preview(
	name = "Filter Bottom Sheet Light Phone",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Preview(
	name = "Filter Bottom Sheet Light Tablet",
	device = Devices.TABLET,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FilterBottomSheetLightPreview() {
	FilterBottomSheetPreviewContent(darkTheme = false)
}

@Preview(
	name = "Filter Bottom Sheet Dark Phone",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Preview(
	name = "Filter Bottom Sheet Dark Tablet",
	device = Devices.TABLET,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun FilterBottomSheetDarkPreview() {
	FilterBottomSheetPreviewContent(darkTheme = true)
}

@Preview(
	name = "Filter Bottom Sheet Font 85",
	device = Devices.PIXEL_4,
	fontScale = 0.85f,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Preview(
	name = "Filter Bottom Sheet Font 130",
	device = Devices.PIXEL_4,
	fontScale = 1.3f,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FilterBottomSheetFontScalePreview() {
	FilterBottomSheetPreviewContent(darkTheme = false)
}

@Preview(
	name = "Filter Bottom Sheet Sign In Required Light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FilterBottomSheetSignInRequiredLightPreview() {
	FilterBottomSheetPreviewContent(darkTheme = false, isSignedIn = false)
}

@Preview(
	name = "Filter Bottom Sheet Sign In Required Dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun FilterBottomSheetSignInRequiredDarkPreview() {
	FilterBottomSheetPreviewContent(darkTheme = true, isSignedIn = false)
}

@Composable
private fun FilterBottomSheetPreviewContent(
	darkTheme: Boolean,
	isSignedIn: Boolean = true,
) {
	PurecipesTheme(darkTheme = darkTheme) {
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = PurecipesTheme.colorScheme.surfaceContainerLow,
		) {
			FilterBottomSheetContent(
				filters = SearchFilters.default(),
				isSignedIn = isSignedIn,
				pantryIngredients = persistentSetOf(),
				excludedIngredients = persistentSetOf(),
				customPantryIngredients = persistentSetOf(),
				ingredientMatchPreview = null,
				isIngredientMatchLoading = false,
				onFiltersChange = {},
				onIngredientSelectionChange = { _, _ -> },
				onCustomIngredientToggle = {},
				onRemoveCustomIngredient = {},
				onAddIngredientQueryChange = {},
				onAddIngredient = {},
				onClearIngredientMatchPreview = {},
				onRequestLogIn = {},
			)
		}
	}
}
