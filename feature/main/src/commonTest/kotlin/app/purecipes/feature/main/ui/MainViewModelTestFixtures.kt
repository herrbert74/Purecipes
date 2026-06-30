package app.purecipes.feature.main.ui

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository
import app.purecipes.feature.sharing.domain.usecase.ObserveIncomingLinksUseCase
import app.purecipes.feature.sharing.domain.usecase.PublishWebLaunchLinkUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal fun mainViewModelForTest(
	authenticationRepository: FakeAuthenticationRepository = FakeAuthenticationRepository(),
	incomingLinkRepository: IncomingLinkRepository = emptyIncomingLinkRepository(),
	analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	consentRepository: FakeConsentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
	searchReadiness: SearchReadinessCoordinator = SearchReadinessCoordinator(),
	onDeliverPendingIncomingLink: () -> Unit = {},
): MainViewModel {
	val viewModel = MainViewModel(
		observeAuthenticationState = ObserveAuthenticationStateUseCase(authenticationRepository),
		refreshConsent = RefreshConsentUseCase(consentRepository),
		setAnalyticsUserId = SetAnalyticsUserIdUseCase(analyticsRepository),
		observeIncomingLinks = ObserveIncomingLinksUseCase(incomingLinkRepository),
		publishWebLaunchLink = PublishWebLaunchLinkUseCase(
			object : WebLaunchLinkRepository {
				override fun readLaunchUrl(): String? = null
			},
			incomingLinkRepository,
		),
		purecipesConfig = object : PurecipesConfig {
			override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

			override fun versionName(): String = "0.0.0-test"

			override fun versionCode(): Long = 0L
		},
		searchReadiness = searchReadiness,
		onDeliverPendingIncomingLink = onDeliverPendingIncomingLink,
	)
	viewModel.initializeTabBackStacksForTest()
	return viewModel
}

internal fun emptyIncomingLinkRepository(): IncomingLinkRepository = object : IncomingLinkRepository {
	override fun observeLinks(): Flow<PurecipesLink> = emptyFlow()

	override fun deliver(url: String) = Unit
}
