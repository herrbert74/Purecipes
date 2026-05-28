package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Inject
@ContributesBinding(AppScope::class)
actual class PlatformConsentDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : ConsentDataSource {

	private val mutableConsentState = MutableStateFlow(
		if (purecipesConfig.usercentricsSettingsId().isNullOrBlank()) {
			ConsentState.NOT_REQUIRED
		} else {
			ConsentState.UNKNOWN
		},
	)

	actual override val consentState: StateFlow<ConsentState> = mutableConsentState

	actual override fun refreshConsent() {
		if (mutableConsentState.value == ConsentState.UNKNOWN) {
			mutableConsentState.value = ConsentState.REQUIRED
		}
	}

	actual override fun showConsentForm() {
		if (mutableConsentState.value != ConsentState.NOT_REQUIRED) {
			mutableConsentState.value = ConsentState.OBTAINED
		}
	}
}
