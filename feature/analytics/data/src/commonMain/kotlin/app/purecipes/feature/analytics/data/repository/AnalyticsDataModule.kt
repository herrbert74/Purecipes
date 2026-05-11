package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import app.purecipes.feature.analytics.data.datasource.CrashlyticsDataSource
import app.purecipes.feature.analytics.data.datasource.Ga4AnalyticsDataSource
import app.purecipes.feature.analytics.data.datasource.MixpanelAnalyticsDataSource
import app.purecipes.feature.analytics.data.datasource.PlatformConsentDataSource
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import app.purecipes.feature.analytics.domain.repository.CrashRepository
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.SetCrashCustomValueUseCase
import app.purecipes.feature.analytics.domain.usecase.SetCrashUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AnalyticsDataModule {

	@Provides
	fun provideConsentRepository(purecipesConfig: PurecipesConfig): ConsentRepository {
		return ConsentAccessor(PlatformConsentDataSource(purecipesConfig))
	}

	@Provides
	fun provideAnalyticsRepository(
		consentRepository: ConsentRepository,
		purecipesConfig: PurecipesConfig,
	): AnalyticsRepository {
		return AnalyticsAccessor(
			analyticsDataSources = listOf<AnalyticsDataSource>(
				Ga4AnalyticsDataSource(purecipesConfig),
				MixpanelAnalyticsDataSource(purecipesConfig),
			),
			consentRepository = consentRepository,
		)
	}

	@Provides
	fun provideObserveConsentStateUseCase(repository: ConsentRepository): ObserveConsentStateUseCase {
		return ObserveConsentStateUseCase(repository)
	}

	@Provides
	fun provideRefreshConsentUseCase(repository: ConsentRepository): RefreshConsentUseCase {
		return RefreshConsentUseCase(repository)
	}

	@Provides
	fun provideShowConsentFormUseCase(repository: ConsentRepository): ShowConsentFormUseCase {
		return ShowConsentFormUseCase(repository)
	}

	@Provides
	fun provideTrackEventUseCase(repository: AnalyticsRepository): TrackEventUseCase {
		return TrackEventUseCase(repository)
	}

	@Provides
	fun provideSetAnalyticsUserIdUseCase(repository: AnalyticsRepository): SetAnalyticsUserIdUseCase {
		return SetAnalyticsUserIdUseCase(repository)
	}

	@Provides
	fun provideCrashRepository(): CrashRepository {
		return CrashAccessor(CrashlyticsDataSource())
	}

	@Provides
	fun provideLogBreadcrumbUseCase(repository: CrashRepository): LogBreadcrumbUseCase {
		return LogBreadcrumbUseCase(repository)
	}

	@Provides
	fun provideSendHandledExceptionUseCase(repository: CrashRepository): SendHandledExceptionUseCase {
		return SendHandledExceptionUseCase(repository)
	}

	@Provides
	fun provideSetCrashCustomValueUseCase(repository: CrashRepository): SetCrashCustomValueUseCase {
		return SetCrashCustomValueUseCase(repository)
	}

	@Provides
	fun provideSetCrashUserIdUseCase(repository: CrashRepository): SetCrashUserIdUseCase {
		return SetCrashUserIdUseCase(repository)
	}
}
