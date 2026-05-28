package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.ConsentDataSource
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.StateFlow

@Inject
@ContributesBinding(AppScope::class)
class ConsentAccessor(
	private val consentDataSource: ConsentDataSource,
) : ConsentRepository {

	override fun observeConsentState(): StateFlow<ConsentState> = consentDataSource.consentState

	override fun currentConsentState(): ConsentState = consentDataSource.consentState.value

	override fun refreshConsent() {
		consentDataSource.refreshConsent()
	}

	override fun showConsentForm() {
		consentDataSource.showConsentForm()
	}
}
