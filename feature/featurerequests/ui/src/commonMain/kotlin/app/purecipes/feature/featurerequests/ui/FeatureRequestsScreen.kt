package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.collections.immutable.toImmutableList

const val FEATURE_REQUESTS_TITLE_TAG = "featureRequestsTitle"

const val FEATURE_REQUESTS_CREATE_BUTTON_TAG = "featureRequestsCreateButton"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureRequestsScreen(
	onBack: () -> Unit,
	onFeatureRequestSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	sessionKey: String? = null,
	onRequestLogIn: () -> Unit = {},
	viewModel: FeatureRequestsViewModel =
		assistedMetroViewModel<FeatureRequestsViewModel, FeatureRequestsViewModel.Factory> {
			create(sessionKey = sessionKey)
		},
) {
	var showCreateDialog by remember { mutableStateOf(false) }

	LaunchedEffect(sessionKey) {
		viewModel.onSessionKeyChanged(sessionKey)
		viewModel.refresh()
	}

	val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
		rememberTopAppBarState(),
	)
	Scaffold(
		modifier = modifier
			.fillMaxSize()
			.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
		topBar = {
			TopAppBar(
				title = {
					Text(
						text = "Feature requests",
						modifier = Modifier.testTag(FEATURE_REQUESTS_TITLE_TAG),
					)
				},
				scrollBehavior = topAppBarScrollBehavior,
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back",
						)
					}
				},
			)
		},
		floatingActionButton = {
			if (sessionKey != null) {
				ExtendedFloatingActionButton(
					onClick = { showCreateDialog = true },
					modifier = Modifier.testTag(FEATURE_REQUESTS_CREATE_BUTTON_TAG),
					icon = {
						Icon(imageVector = Icons.Filled.Add, contentDescription = null)
					},
					text = { Text(text = "Request a feature") },
				)
			}
		},
	) { innerPadding ->
		if (sessionKey == null) {
			FeatureRequestsSignedOutContent(
				onRequestLogIn = onRequestLogIn,
				modifier = Modifier.padding(innerPadding),
			)
			return@Scaffold
		}

		FeatureRequestsListContent(
			state = FeatureRequestsListState(
				featureRequests = viewModel.featureRequests.toImmutableList(),
				sort = viewModel.sort,
				statusFilter = viewModel.statusFilter,
				totalMatches = viewModel.totalMatches,
				errorMessage = viewModel.errorMessage,
				isLoading = viewModel.isLoading,
			),
			paginationState = viewModel.paginationState,
			callbacks = FeatureRequestsListCallbacks(
				onSortSelected = viewModel::onSortSelected,
				onStatusFilterSelected = viewModel::onStatusFilterSelected,
				onFeatureRequestSelect = onFeatureRequestSelect,
				onToggleVote = viewModel::onToggleVote,
				onCreateClick = { showCreateDialog = true },
			),
			modifier = Modifier.padding(innerPadding),
		)

		if (showCreateDialog) {
			CreateFeatureRequestDialog(
				isLoading = viewModel.isCreatingFeatureRequest,
				errorMessage = viewModel.createErrorMessage,
				onDismiss = { showCreateDialog = false },
				onConfirm = { title, description ->
					viewModel.createFeatureRequestFromInput(title, description) { created ->
						if (created) {
							showCreateDialog = false
						}
					}
				},
			)
		}
	}
}
