package com.purecipes.feature.analytics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.purecipes.feature.analytics.domain.model.toDisplayText
import com.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import com.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import com.purecipes.shared.ui.theme.PurecipesTheme

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
				.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			Text(
				text = consentState.toDisplayText(),
				style = PurecipesTheme.typography.bodyLarge,
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
