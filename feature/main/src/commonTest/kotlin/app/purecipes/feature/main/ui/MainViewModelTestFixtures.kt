package app.purecipes.feature.main.ui

import app.purecipes.feature.ads.domain.PreCookInterstitialChance
import app.purecipes.feature.ads.domain.repository.AdsRepository
import app.purecipes.feature.ads.domain.usecase.DecidePreCookInterstitialUseCase
import app.purecipes.feature.ads.domain.usecase.ObserveShouldShowAdsUseCase
import app.purecipes.feature.ads.domain.usecase.ShowInterstitialAdUseCase
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
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.feature.subscription.domain.usecase.SyncSubscriptionUserIdUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal fun mainViewModelForTest(
	authenticationRepository: FakeAuthenticationRepository = FakeAuthenticationRepository(),
	incomingLinkRepository: IncomingLinkRepository = emptyIncomingLinkRepository(),
	analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	consentRepository: FakeConsentRepository = FakeConsentRepository(ConsentState.NOT_REQUIRED),
	subscriptionRepository: FakeSubscriptionRepository = FakeSubscriptionRepository(),
	adsRepository: AdsRepository = object : AdsRepository {
		override fun initialize() = Unit

		override fun showInterstitial(onDismissed: () -> Unit) {
			onDismissed()
		}
	},
	preCookInterstitialChance: PreCookInterstitialChance = PreCookInterstitialChance { false },
	searchReadiness: SearchReadinessCoordinator = SearchReadinessCoordinator(),
	onDeliverPendingIncomingLink: () -> Unit = {},
): MainViewModel {
	val viewModel = MainViewModel(
		observeAuthenticationState = ObserveAuthenticationStateUseCase(authenticationRepository),
		refreshConsent = RefreshConsentUseCase(consentRepository),
		setAnalyticsUserId = SetAnalyticsUserIdUseCase(analyticsRepository),
		syncSubscriptionUserId = SyncSubscriptionUserIdUseCase(subscriptionRepository),
		observeIncomingLinks = ObserveIncomingLinksUseCase(incomingLinkRepository),
		publishWebLaunchLink = PublishWebLaunchLinkUseCase(
			object : WebLaunchLinkRepository {
				override fun readLaunchUrl(): String? = null
			},
			incomingLinkRepository,
		),
		decidePreCookInterstitial = DecidePreCookInterstitialUseCase(
			observeShouldShowAds = ObserveShouldShowAdsUseCase(
				observePremiumStatus = ObservePremiumStatusUseCase(
					subscriptionRepository,
					FakeMonetisationDebugOverridesRepository(),
				),
				monetisationDebugOverrides = FakeMonetisationDebugOverridesRepository(),
			),
			preCookInterstitialChance = preCookInterstitialChance,
		),
		showInterstitialAd = ShowInterstitialAdUseCase(adsRepository),
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
