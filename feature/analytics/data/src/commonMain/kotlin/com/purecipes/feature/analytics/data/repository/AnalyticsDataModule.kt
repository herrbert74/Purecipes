package com.purecipes.feature.analytics.data.repository

import com.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import com.purecipes.feature.analytics.data.datasource.Ga4AnalyticsDataSource
import com.purecipes.feature.analytics.data.datasource.MixpanelAnalyticsDataSource
import com.purecipes.feature.analytics.data.datasource.PlatformConsentDataSource
import com.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import com.purecipes.feature.analytics.domain.repository.ConsentRepository
import com.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import com.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import com.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import com.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.shared.data.config.PurecipesConfig
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
}