package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.shared.data.config.PurecipesConfig
import com.usercentrics.sdk.BannerSettings
import com.usercentrics.sdk.Usercentrics
import com.usercentrics.sdk.UsercentricsBanner
import com.usercentrics.sdk.UsercentricsOptions
import com.usercentrics.sdk.UsercentricsServiceConsent
import com.usercentrics.sdk.models.common.UsercentricsLoggerLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal actual class PlatformConsentDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : ConsentDataSource {

	private val settingsId = purecipesConfig.usercentricsSettingsId().orEmpty()
	private var isInitialized = false
	private val mutableConsentState = MutableStateFlow(
		if (settingsId.isBlank()) ConsentState.NOT_REQUIRED else ConsentState.UNKNOWN,
	)

	actual override val consentState: StateFlow<ConsentState> = mutableConsentState

	actual override fun refreshConsent() {
		if (settingsId.isBlank()) {
			mutableConsentState.value = ConsentState.NOT_REQUIRED
			return
		}
		initializeIfNeeded()
		Usercentrics.isReady(
			onSuccess = { status ->
				mutableConsentState.value = if (status.shouldCollectConsent) {
					ConsentState.REQUIRED
				} else {
					ConsentState.OBTAINED
				}
				if (status.shouldCollectConsent) {
					showConsentForm()
				}
			},
			onFailure = {
				mutableConsentState.value = ConsentState.UNKNOWN
			},
		)
	}

	actual override fun showConsentForm() {
		if (settingsId.isBlank()) {
			mutableConsentState.value = ConsentState.NOT_REQUIRED
			return
		}
		initializeIfNeeded()
		val activity = AnalyticsAndroidRuntime.currentActivity() ?: return
		UsercentricsBanner(activity, BannerSettings()).showSecondLayer { response ->
			mutableConsentState.value = if (response == null) {
				ConsentState.UNKNOWN
			} else {
				response.consents.toConsentState()
			}
		}
	}

	private fun initializeIfNeeded() {
		if (isInitialized) {
			return
		}
		Usercentrics.initialize(
			AnalyticsAndroidRuntime.applicationContext,
			UsercentricsOptions(
				settingsId = settingsId,
				loggerLevel = UsercentricsLoggerLevel.NONE,
			),
		)
		isInitialized = true
	}
}

private fun List<UsercentricsServiceConsent>.toConsentState(): ConsentState {
	val nonEssentialConsents = filterNot { it.isEssential }
	return when {
		nonEssentialConsents.isEmpty() -> ConsentState.OBTAINED
		nonEssentialConsents.any { it.status } -> ConsentState.OBTAINED
		else -> ConsentState.DENIED
	}
}
