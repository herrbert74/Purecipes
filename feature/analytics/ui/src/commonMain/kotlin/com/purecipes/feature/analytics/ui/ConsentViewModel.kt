package com.purecipes.feature.analytics.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import com.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import kotlinx.coroutines.flow.StateFlow

internal class ConsentViewModel(
	observeConsentState: ObserveConsentStateUseCase,
	private val showConsentForm: ShowConsentFormUseCase,
) : ViewModel() {

	val consentState: StateFlow<ConsentState> = observeConsentState()

	fun onManagePrivacySettingsClick() {
		showConsentForm()
	}
}

@Composable
internal fun consentViewModel(
	observeConsentState: ObserveConsentStateUseCase,
	showConsentForm: ShowConsentFormUseCase,
): ConsentViewModel {
	return viewModel(
		key = "ConsentViewModel:${observeConsentState.hashCode()}:${showConsentForm.hashCode()}",
		factory = viewModelFactory {
			initializer {
				ConsentViewModel(
					observeConsentState = observeConsentState,
					showConsentForm = showConsentForm,
				)
			}
		},
	)
}
