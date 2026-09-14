package app.purecipes.feature.featurerequests.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.feature.featurerequests.ui.detail.FeatureRequestDetailContent
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun FeatureRequestDetailScreen(
	requestId: Int,
	onBack: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: FeatureRequestDetailViewModel =
		assistedMetroViewModel<FeatureRequestDetailViewModel, FeatureRequestDetailViewModel.Factory>(
			key = requestId.toString(),
		) {
			create(requestId = requestId)
		},
) {
	FeatureRequestDetailContent(
		featureRequest = viewModel.featureRequest,
		comments = viewModel.comments.toImmutableList(),
		state = FeatureRequestDetailUiState(
			isLoading = viewModel.isLoading,
			isSendingComment = viewModel.isSendingComment,
			errorMessage = viewModel.errorMessage,
			commentErrorMessage = viewModel.commentErrorMessage,
		),
		callbacks = FeatureRequestDetailCallbacks(
			onBack = onBack,
			onToggleVote = viewModel::onToggleVote,
			onAddComment = { body -> viewModel.addComment(body) },
		),
		modifier = modifier,
	)
}
