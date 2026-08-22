package app.purecipes.feature.library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.feature.library.ui.cookbooks.CookbooksTabContent
import app.purecipes.feature.library.ui.cookbooks.CreateCookbookDialog
import app.purecipes.feature.library.ui.cookbooks.DeleteCookbookDialog
import app.purecipes.shared.domain.model.CookbookSummary
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun LibraryCookbooksScreen(
	modifier: Modifier = Modifier,
	sessionKey: String? = null,
	onCookbookSelect: (CookbookSummary) -> Unit = {},
	viewModel: LibraryViewModel = assistedMetroViewModel<LibraryViewModel, LibraryViewModel.Factory> {
		create(sessionKey = sessionKey)
	},
) {
	var showCreateCookbookDialog by remember { mutableStateOf(false) }
	var pendingDeleteCookbook by remember { mutableStateOf<CookbookSummary?>(null) }

	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
		if (sessionKey != null) {
			viewModel.loadLibrary()
		}
	}

	LaunchedEffect(Unit) {
		viewModel.onTabSelected(LibraryTab.Cookbooks)
	}

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Library", modifier = Modifier.testTag(LIBRARY_TITLE_TAG)) },
			)
		},
	) { innerPadding ->
		Column(modifier = Modifier.padding(innerPadding)) {
			CookbooksTabContent(
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
				onCookbookClick = onCookbookSelect,
				modifier = Modifier.weight(1f),
			)
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
			DeleteCookbookDialog(
				cookbookName = cookbook.name,
				onDismiss = { pendingDeleteCookbook = null },
				onConfirm = {
					viewModel.deleteCookbook(cookbook) {
						pendingDeleteCookbook = null
					}
				},
			)
		}
	}
}
