package com.purecipes.feature.analytics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import com.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase

@Composable
fun ConsentPreferencesScreen(
	observeConsentState: ObserveConsentStateUseCase,
	showConsentForm: ShowConsentFormUseCase,
	modifier: Modifier = Modifier,
) {
	val viewModel = consentViewModel(
		observeConsentState = observeConsentState,
		showConsentForm = showConsentForm,
	)
	val consentState by viewModel.consentState.collectAsState()

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Privacy") },
			)
		},
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Text(
				text = consentState.toDisplayText(),
				style = MaterialTheme.typography.bodyLarge,
			)
			Button(
				onClick = viewModel::onManagePrivacySettingsClick,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(text = "Manage privacy settings")
			}
		}
	}
}

private fun ConsentState.toDisplayText(): String {
	return when (this) {
		ConsentState.UNKNOWN -> "Consent status is not available yet."
		ConsentState.REQUIRED -> "Consent is required before analytics can run."
		ConsentState.OBTAINED -> "Consent has been granted for analytics."
		ConsentState.DENIED -> "Consent has been denied for analytics."
		ConsentState.NOT_REQUIRED -> "Consent is not required for analytics on this device."
	}
}