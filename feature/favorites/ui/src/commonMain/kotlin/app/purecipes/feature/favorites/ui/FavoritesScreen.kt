package app.purecipes.feature.favorites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.ads.ui.InlineListBannerAdSlot
import app.purecipes.feature.favorites.domain.CookbookNameSuggestions
import app.purecipes.feature.newrecipe.ui.CreatedRecipesTabContent
import app.purecipes.feature.sharing.ui.ShareIconButton
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.BodyText
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.component.TitleText
import app.purecipes.shared.ui.component.paging.PaginatedLazyColumn
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal const val FAVORITES_TITLE_TAG = "favoritesTitle"
internal const val DELETE_COOKBOOK_BUTTON_PREFIX = "deleteCookbookButton:"
internal const val DELETE_COOKBOOK_DIALOG_CONFIRM_TAG = "deleteCookbookDialogConfirm"
internal const val CREATE_COOKBOOK_DIALOG_INPUT_TAG = "createCookbookDialogInput"

@Composable
fun FavoritesScreen(
	modifier: Modifier = Modifier,
	sessionKey: String? = null,
	initialCookbookShareToken: String? = null,
	openMyRecipes: Boolean = false,
	recipeSaveMessage: String? = null,
	onRecipeSelect: (Int) -> Unit = {},
	onCreateRecipe: () -> Unit = {},
	onEditCreatedRecipe: (Int) -> Unit = {},
	onRequestLogIn: () -> Unit = {},
	bannerAdViewModel: BannerAdViewModel? = null,
	viewModel: FavoritesViewModel =
		assistedMetroViewModel<FavoritesViewModel, FavoritesViewModel.Factory> {
			create(sessionKey = sessionKey)
		},
) {
	var showCreateCookbookDialog by remember { mutableStateOf(false) }
	var pendingDeleteCookbook by remember { mutableStateOf<CookbookSummary?>(null) }
	val snackbarHostState = remember { SnackbarHostState() }

	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
		if (sessionKey != null) {
			viewModel.loadFavorites()
		}
	}

	LaunchedEffect(initialCookbookShareToken, sessionKey) {
		val shareToken = initialCookbookShareToken ?: return@LaunchedEffect
		if (sessionKey != null) {
			viewModel.importSharedCookbook(shareToken)
		}
	}

	LaunchedEffect(openMyRecipes) {
		if (openMyRecipes) {
			viewModel.onTabSelected(FavoritesTab.MyRecipes)
		}
	}

	LaunchedEffect(recipeSaveMessage) {
		val message = recipeSaveMessage ?: return@LaunchedEffect
		snackbarHostState.showSnackbar(message)
	}

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Favorites", modifier = Modifier.testTag(FAVORITES_TITLE_TAG)) },
			)
		},
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
	) { innerPadding ->
		if (sessionKey == null) {
			FavoritesSignedOutContent(
				onRequestLogIn = onRequestLogIn,
				modifier = Modifier.padding(innerPadding),
			)
			return@Scaffold
		}

		if (viewModel.isImportingSharedCookbook) {
			Box(
				modifier = Modifier
					.padding(innerPadding)
					.fillMaxSize(),
				contentAlignment = Alignment.Center,
			) {
				CircularProgressIndicator()
			}
			return@Scaffold
		}

		viewModel.sharedCookbookImportErrorMessage?.let { message ->
			Box(
				modifier = Modifier
					.padding(innerPadding)
					.fillMaxSize()
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				ErrorText(text = message, textAlign = TextAlign.Center)
			}
			return@Scaffold
		}

		val cookbookId = viewModel.viewingCookbookId
		if (cookbookId != null) {
			LaunchedEffect(cookbookId) {
				viewModel.loadCookbookCover(cookbookId)
			}
			val detailCoverUrl = viewModel.cookbookCoverUrls[cookbookId]
			CookbookDetailContent(
				title = viewModel.viewingCookbookName,
				errorMessage = viewModel.cookbookDetailErrorMessage,
				recipes = viewModel.cookbookDetailRecipes,
				paginationState = viewModel.cookbookDetailPaginationState,
				totalMatches = viewModel.totalCookbookDetailMatches,
				coverUrl = detailCoverUrl,
				onBack = viewModel::closeCookbookDetail,
				onShare = viewModel::shareOpenCookbook,
				modifier = Modifier.padding(innerPadding),
				onRecipeSelect = onRecipeSelect,
				bannerAdViewModel = bannerAdViewModel,
			)
			return@Scaffold
		}

		Column(modifier = Modifier.padding(innerPadding)) {
			PrimaryTabRow(selectedTabIndex = viewModel.selectedTab.ordinal) {
				Tab(
					selected = viewModel.selectedTab == FavoritesTab.SavedRecipes,
					onClick = { viewModel.onTabSelected(FavoritesTab.SavedRecipes) },
					text = { Text(text = "Saved recipes") },
				)
				Tab(
					selected = viewModel.selectedTab == FavoritesTab.Cookbooks,
					onClick = { viewModel.onTabSelected(FavoritesTab.Cookbooks) },
					text = { Text(text = "Cookbooks") },
				)
				Tab(
					selected = viewModel.selectedTab == FavoritesTab.MyRecipes,
					onClick = { viewModel.onTabSelected(FavoritesTab.MyRecipes) },
					text = { Text(text = "My recipes") },
				)
			}

			when (viewModel.selectedTab) {
				FavoritesTab.SavedRecipes -> SavedRecipesTabContent(
					errorMessage = viewModel.savedErrorMessage,
					paginationState = viewModel.savedPaginationState,
					recipes = viewModel.savedRecipes,
					totalMatches = viewModel.totalSavedMatches,
					onRecipeSelect = onRecipeSelect,
					bannerAdViewModel = bannerAdViewModel,
					modifier = Modifier.weight(1f),
				)

				FavoritesTab.Cookbooks -> CookbooksTabContent(
					cookbooks = viewModel.cookbooks,
					cookbooksErrorMessage = viewModel.cookbooksErrorMessage,
					paginationState = viewModel.cookbooksPaginationState,
					totalMatches = viewModel.totalCookbooksMatches,
					getCookbookCoverUrl = { id -> viewModel.cookbookCoverUrls[id] },
					onRequestCookbookCover = viewModel::loadCookbookCover,
					onCreateClick = { showCreateCookbookDialog = true },
					deleteCookbookError = viewModel.deleteCookbookError,
					isDeletingCookbook = viewModel.isDeletingCookbook,
					onDeleteCookbook = { cookbook, _ ->
						pendingDeleteCookbook = cookbook
					},
					onCookbookClick = { cookbook ->
						viewModel.openCookbookDetail(
							cookbookId = cookbook.id,
							name = cookbook.name,
							recipeCount = cookbook.recipeCount,
						)
					},
					modifier = Modifier.weight(1f),
				)

				FavoritesTab.MyRecipes -> CreatedRecipesTabContent(
					onCreateRecipe = onCreateRecipe,
					onEditRecipe = onEditCreatedRecipe,
					modifier = Modifier.weight(1f),
				)
			}
		}

		if (showCreateCookbookDialog) {
			CreateCookbookDialog(
				existingCookbookNames = viewModel.cookbooks.map { it.name }.toImmutableList(),
				isLoading = viewModel.isCreatingCookbook,
				errorMessage = viewModel.createCookbookError,
				onDismiss = {
					showCreateCookbookDialog = false
				},
				onConfirm = { name ->
					viewModel.createCookbookFromName(name) { ok ->
						if (ok) {
							showCreateCookbookDialog = false
						}
					}
				},
			)
		}
		pendingDeleteCookbook?.let { cookbook ->
			AlertDialog(
				onDismissRequest = { pendingDeleteCookbook = null },
				confirmButton = {
					Button(
						onClick = {
							viewModel.deleteCookbook(cookbook) {
								pendingDeleteCookbook = null
							}
						},
						modifier = Modifier.testTag(DELETE_COOKBOOK_DIALOG_CONFIRM_TAG),
					) {
						Text(text = "Delete")
					}
				},
				dismissButton = {
					TextButton(onClick = { pendingDeleteCookbook = null }) {
						Text(text = "Cancel")
					}
				},
				title = { Text(text = "Delete cookbook?") },
				text = { Text(text = "Are you sure you want to delete ${cookbook.name}?") },
			)
		}
	}
}

@Composable
private fun CreateCookbookDialog(
	existingCookbookNames: ImmutableList<String>,
	isLoading: Boolean,
	errorMessage: String?,
	onDismiss: () -> Unit,
	onConfirm: (String) -> Unit,
) {
	var nameField by remember { mutableStateOf("") }
	val existingCookbookNamesNormalized = remember(existingCookbookNames) {
		existingCookbookNames
			.map { it.trim().lowercase() }
			.toSet()
	}
	val suggestionNames = remember(existingCookbookNamesNormalized) {
		CookbookNameSuggestions.values.filter { suggestion ->
			suggestion.trim().lowercase() !in existingCookbookNamesNormalized
		}
	}
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			Button(
				onClick = { onConfirm(nameField) },
				enabled = !isLoading && nameField.trim().isNotEmpty(),
			) {
				Text(text = "Create")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = "Cancel")
			}
		},
		title = { Text(text = "New cookbook") },
		text = {
			Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				LazyRow(
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					modifier = Modifier.fillMaxWidth(),
				) {
					items(suggestionNames, key = { it }) { suggestion ->
						FilterChip(
							selected = false,
							onClick = { nameField = suggestion },
							label = { Text(text = suggestion) },
						)
					}
				}
				OutlinedTextField(
					value = nameField,
					onValueChange = { nameField = it },
					modifier = Modifier
						.fillMaxWidth()
						.testTag(CREATE_COOKBOOK_DIALOG_INPUT_TAG),
					label = { Text(text = "Name") },
					singleLine = true,
				)
				errorMessage?.let { ErrorText(text = it) }
				if (isLoading) {
					CircularProgressIndicator(modifier = Modifier.size(24.dp))
				}
			}
		},
	)
}

@Composable
private fun CookbookDetailContent(
	title: String,
	errorMessage: String?,
	recipes: SnapshotStateList<RecipeSummary>,
	paginationState: PaginationState<Int, RecipeSummary>,
	totalMatches: Int,
	coverUrl: String?,
	onBack: () -> Unit,
	onShare: () -> Unit,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	bannerAdViewModel: BannerAdViewModel? = null,
) {
	Column(modifier = modifier.fillMaxSize()) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			IconButton(onClick = onBack) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Back",
				)
			}
			AsyncImage(
				model = coverUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = title,
				modifier = Modifier
					.size(48.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.s))
					.background(PurecipesTheme.colorScheme.secondaryContainer),
				contentScale = ContentScale.Crop,
			)
			Text(
				text = title,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier.weight(1f),
			)
			ShareIconButton(
				onShare = onShare,
				contentDescription = "Share cookbook",
			)
		}
		when {
			errorMessage != null -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				ErrorText(text = errorMessage, textAlign = TextAlign.Center)
			}

			else -> PaginatedLazyColumn(
				paginationState = paginationState,
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				contentPadding = PaddingValues(PurecipesTheme.space.m),
			) {
				item {
					Text(
						text = "$totalMatches recipes",
						style = PurecipesTheme.typography.labelMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
				}
				items(recipes.size, key = { recipes[it].id }) { index ->
					val recipe = recipes[index]
					InlineListBannerAdSlot(
						contentIndex = index,
						contentCount = recipes.size,
						viewModel = bannerAdViewModel,
					) {
						RecipeCard(
							recipe = recipe,
							onClick = { onRecipeSelect(recipe.id) },
						)
					}
				}
			}
		}
	}
}

@Composable
internal fun SavedRecipesTabContent(
	errorMessage: String?,
	paginationState: PaginationState<Int, RecipeSummary>,
	recipes: SnapshotStateList<RecipeSummary>,
	totalMatches: Int,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	bannerAdViewModel: BannerAdViewModel? = null,
) {
	when {
		errorMessage != null -> Box(
			modifier = modifier
				.fillMaxSize()
				.padding(PurecipesTheme.space.l),
			contentAlignment = Alignment.Center,
		) {
			ErrorText(text = errorMessage, textAlign = TextAlign.Center)
		}

		recipes.isEmpty() && totalMatches == 0 -> EmptyStateContent(
			icon = Icons.Filled.Favorite,
			iconContentDescription = "Favorites",
			title = "No favorites yet",
			description = "Add recipes from the details screen and they will appear here.",
			modifier = modifier,
		)

		else -> PaginatedLazyColumn(
			paginationState = paginationState,
			modifier = modifier.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			contentPadding = PaddingValues(PurecipesTheme.space.m),
		) {
			item {
				Text(
					text = "$totalMatches saved recipes",
					style = PurecipesTheme.typography.labelMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			items(recipes.size, key = { recipes[it].id }) { index ->
				val recipe = recipes[index]
				InlineListBannerAdSlot(
					contentIndex = index,
					contentCount = recipes.size,
					viewModel = bannerAdViewModel,
				) {
					RecipeCard(
						recipe = recipe,
						onClick = { onRecipeSelect(recipe.id) },
					)
				}
			}
		}
	}
}

@Composable
private fun CookbooksTabContent(
	cookbooks: SnapshotStateList<CookbookSummary>,
	cookbooksErrorMessage: String?,
	paginationState: PaginationState<Int, CookbookSummary>,
	totalMatches: Int,
	getCookbookCoverUrl: (Int) -> String?,
	onRequestCookbookCover: (Int) -> Unit,
	onCreateClick: () -> Unit,
	deleteCookbookError: String?,
	isDeletingCookbook: Boolean,
	onDeleteCookbook: (CookbookSummary, (Boolean) -> Unit) -> Unit,
	onCookbookClick: (CookbookSummary) -> Unit,
	modifier: Modifier = Modifier,
) {
	when {
		cookbooksErrorMessage != null -> Box(
			modifier = modifier
				.fillMaxSize()
				.padding(PurecipesTheme.space.l),
			contentAlignment = Alignment.Center,
		) {
			ErrorText(text = cookbooksErrorMessage, textAlign = TextAlign.Center)
		}

		cookbooks.isEmpty() && totalMatches == 0 -> Box(
			modifier = modifier.fillMaxSize(),
			contentAlignment = Alignment.Center,
		) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				modifier = Modifier.padding(PurecipesTheme.space.l),
			) {
				Text(
					text = "Organize saved recipes into cookbooks",
					style = PurecipesTheme.typography.bodyLarge,
					textAlign = TextAlign.Center,
				)
				Button(onClick = onCreateClick) {
					Text(text = "Create new cookbook")
				}
			}
		}

		else -> Column(modifier = modifier.fillMaxSize()) {
			deleteCookbookError?.let { message ->
				ErrorText(
					text = message,
					textAlign = TextAlign.Center,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.s),
				)
			}
			TextButton(
				onClick = onCreateClick,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.s),
			) {
				Text(text = "Create new cookbook")
			}
			PaginatedLazyColumn(
				paginationState = paginationState,
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				contentPadding = PaddingValues(
					start = PurecipesTheme.space.m,
					end = PurecipesTheme.space.m,
					bottom = PurecipesTheme.space.m,
				),
			) {
				items(cookbooks.size, key = { cookbooks[it].id }) { index ->
					val cookbook = cookbooks[index]
					CookbookRow(
						cookbook = cookbook,
						coverUrl = getCookbookCoverUrl(cookbook.id),
						onRequestCookbookCover = { onRequestCookbookCover(cookbook.id) },
						isDeletingCookbook = isDeletingCookbook,
						onDeleteCookbook = { onDeleteCookbook(cookbook) {} },
						onClick = { onCookbookClick(cookbook) },
					)
				}
			}
		}
	}
}

@Composable
private fun CookbookRow(
	cookbook: CookbookSummary,
	coverUrl: String?,
	onRequestCookbookCover: () -> Unit,
	isDeletingCookbook: Boolean,
	onDeleteCookbook: () -> Unit,
	onClick: () -> Unit,
) {
	val requestCover by rememberUpdatedState(onRequestCookbookCover)
	LaunchedEffect(cookbook.id) {
		requestCover()
	}
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(
			containerColor = PurecipesTheme.colorScheme.surfaceContainerLow,
		),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			AsyncImage(
				model = coverUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = cookbook.name,
				modifier = Modifier
					.size(56.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.s))
					.background(PurecipesTheme.colorScheme.secondaryContainer),
				contentScale = ContentScale.Crop,
			)
			Column(modifier = Modifier.weight(1f)) {
				TitleText(text = cookbook.name)
				BodyText(
					text = "${cookbook.recipeCount} recipes",
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			TextButton(
				onClick = onDeleteCookbook,
				enabled = !isDeletingCookbook && cookbook.recipeCount == 0,
				modifier = Modifier.testTag("$DELETE_COOKBOOK_BUTTON_PREFIX${cookbook.id}"),
			) {
				Text(text = "Delete")
			}
		}
	}
}
