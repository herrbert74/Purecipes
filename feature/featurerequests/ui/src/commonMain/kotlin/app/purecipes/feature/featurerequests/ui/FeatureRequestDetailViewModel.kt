package app.purecipes.feature.featurerequests.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.featurerequests.domain.usecase.AddFeatureRequestCommentUseCase
import app.purecipes.feature.featurerequests.domain.usecase.GetFeatureRequestCommentsUseCase
import app.purecipes.feature.featurerequests.domain.usecase.GetFeatureRequestUseCase
import app.purecipes.feature.featurerequests.domain.usecase.ToggleFeatureRequestVoteUseCase
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.launch

@AssistedInject
class FeatureRequestDetailViewModel(
	private val getFeatureRequest: GetFeatureRequestUseCase,
	private val getFeatureRequestComments: GetFeatureRequestCommentsUseCase,
	private val addFeatureRequestComment: AddFeatureRequestCommentUseCase,
	private val toggleFeatureRequestVote: ToggleFeatureRequestVoteUseCase,
	@Assisted private val requestId: Int,
) : ViewModel() {

	var featureRequest by mutableStateOf<FeatureRequest?>(null)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var commentErrorMessage by mutableStateOf<String?>(null)
		private set

	var isLoading by mutableStateOf(false)
		private set

	var isSendingComment by mutableStateOf(false)
		private set

	val comments = mutableStateListOf<FeatureRequestComment>()

	init {
		load()
	}

	fun load() {
		viewModelScope.launch {
			isLoading = true
			errorMessage = null
			val requestOutcome = getFeatureRequest(requestId)
			val loaded = requestOutcome.get()
			if (loaded == null) {
				errorMessage = requestOutcome.getError()?.message
			} else {
				featureRequest = loaded
				loadComments()
			}
			isLoading = false
		}
	}

	fun onToggleVote() {
		viewModelScope.launch {
			val outcome = toggleFeatureRequestVote(requestId)
			val updated = outcome.get()
			if (updated == null) {
				errorMessage = outcome.getError()?.message
			} else {
				featureRequest = updated
			}
		}
	}

	fun addComment(body: String, onDone: (Boolean) -> Unit = {}) {
		val trimmed = body.trim()
		if (trimmed.isEmpty()) {
			onDone(false)
			return
		}
		viewModelScope.launch {
			isSendingComment = true
			commentErrorMessage = null
			val outcome = addFeatureRequestComment(requestId, trimmed)
			val comment = outcome.get()
			isSendingComment = false
			if (comment == null) {
				commentErrorMessage = outcome.getError()?.message
				onDone(false)
			} else {
				comments += comment
				featureRequest = featureRequest?.let { it.copy(commentCount = it.commentCount + 1) }
				onDone(true)
			}
		}
	}

	private suspend fun loadComments() {
		val outcome = getFeatureRequestComments(requestId)
		val loadedComments = outcome.get()
		if (loadedComments == null) {
			commentErrorMessage = outcome.getError()?.message
		} else {
			comments.clear()
			comments.addAll(loadedComments)
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(requestId: Int): FeatureRequestDetailViewModel
	}
}
