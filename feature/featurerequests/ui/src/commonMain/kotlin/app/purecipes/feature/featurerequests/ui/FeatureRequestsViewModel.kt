package app.purecipes.feature.featurerequests.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.featurerequests.domain.usecase.CreateFeatureRequestUseCase
import app.purecipes.feature.featurerequests.domain.usecase.GetFeatureRequestsPageUseCase
import app.purecipes.feature.featurerequests.domain.usecase.ToggleFeatureRequestVoteUseCase
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.component.paging.PaginationState
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

private const val FIRST_PAGE_NUMBER = 1

private const val PAGE_SIZE = 20

@AssistedInject
class FeatureRequestsViewModel(
	private val getFeatureRequestsPage: GetFeatureRequestsPageUseCase,
	private val createFeatureRequest: CreateFeatureRequestUseCase,
	private val toggleFeatureRequestVote: ToggleFeatureRequestVoteUseCase,
	@Assisted sessionKey: String?,
) : ViewModel() {

	private var activeSessionKey: String? = sessionKey

	var sort by mutableStateOf(FeatureRequestSort.TOP_VOTES)
		private set

	var statusFilter by mutableStateOf<FeatureRequestStatus?>(null)
		private set

	var errorMessage by mutableStateOf<String?>(null)
		private set

	var createErrorMessage by mutableStateOf<String?>(null)
		private set

	var isCreatingFeatureRequest by mutableStateOf(false)
		private set

	var isLoading by mutableStateOf(false)
		private set

	var totalMatches by mutableIntStateOf(0)
		private set

	val featureRequests = mutableStateListOf<FeatureRequest>()

	val paginationState: PaginationState<Int, FeatureRequest> = PaginationState(
		initialPageKey = FIRST_PAGE_NUMBER,
		onRequestPage = { pageKey ->
			viewModelScope.launch {
				loadPage(pageKey)
			}
		},
	)

	init {
		refresh()
	}

	fun onSessionKeyChanged(sessionKey: String?) {
		if (sessionKey == activeSessionKey) {
			return
		}
		activeSessionKey = sessionKey
		refresh()
	}

	fun onSortSelected(sort: FeatureRequestSort) {
		if (sort == this.sort) {
			return
		}
		this.sort = sort
		refresh()
	}

	fun onStatusFilterSelected(status: FeatureRequestStatus?) {
		if (status == statusFilter) {
			return
		}
		statusFilter = status
		refresh()
	}

	fun refresh() {
		if (activeSessionKey == null) {
			return
		}
		viewModelScope.launch {
			isLoading = true
			featureRequests.clear()
			totalMatches = 0
			errorMessage = null
			paginationState.refresh(initialPageKey = FIRST_PAGE_NUMBER)
			loadPage(FIRST_PAGE_NUMBER)
			isLoading = false
		}
	}

	fun createFeatureRequestFromInput(title: String, description: String, onDone: (Boolean) -> Unit) {
		val trimmedTitle = title.trim()
		val trimmedDescription = description.trim()
		if (trimmedTitle.isEmpty() || trimmedDescription.isEmpty()) {
			createErrorMessage = "Add a title and a description"
			onDone(false)
			return
		}
		viewModelScope.launch {
			isCreatingFeatureRequest = true
			createErrorMessage = null
			val outcome = createFeatureRequest(trimmedTitle, trimmedDescription)
			val created = outcome.get()
			isCreatingFeatureRequest = false
			if (created == null) {
				createErrorMessage = outcome.getError()?.message
				onDone(false)
			} else {
				refresh()
				onDone(true)
			}
		}
	}

	fun onToggleVote(featureRequest: FeatureRequest) {
		viewModelScope.launch {
			val outcome = toggleFeatureRequestVote(featureRequest.id)
			val updated = outcome.get()
			if (updated == null) {
				errorMessage = outcome.getError()?.message
			} else {
				val index = featureRequests.indexOfFirst { it.id == updated.id }
				if (index >= 0) {
					featureRequests[index] = updated
				}
			}
		}
	}

	private suspend fun loadPage(pageNumber: Int) {
		val outcome = getFeatureRequestsPage(
			sort = sort,
			status = statusFilter,
			pageNumber = pageNumber,
			pageSize = PAGE_SIZE,
		)
		val page = outcome.get()
		if (page == null) {
			val error = outcome.getError()
			if (error != null) {
				paginationState.setError(IllegalStateException(error.message))
				errorMessage = error.message
			}
		} else {
			if (pageNumber == FIRST_PAGE_NUMBER) {
				featureRequests.clear()
				totalMatches = page.totalMatches
			}
			featureRequests.addAll(page.items)
			paginationState.appendPage(
				pageKey = pageNumber,
				items = page.items,
				nextPageKey = pageNumber + 1,
				isLastPage = (page.pageNumber * page.pageSize) >= page.totalMatches,
			)
		}
	}

	@AssistedFactory
	@ManualViewModelAssistedFactoryKey
	@ContributesIntoMap(AppScope::class)
	interface Factory : ManualViewModelAssistedFactory {

		fun create(sessionKey: String?): FeatureRequestsViewModel
	}
}
