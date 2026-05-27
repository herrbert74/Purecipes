package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import app.purecipes.feature.analytics.data.datasource.CrashlyticsDataSource
import app.purecipes.feature.analytics.data.datasource.Ga4AnalyticsDataSource
import app.purecipes.feature.analytics.data.datasource.MixpanelAnalyticsDataSource
import app.purecipes.feature.analytics.data.datasource.PlatformConsentDataSource
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import app.purecipes.feature.analytics.domain.repository.CrashRepository
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
	fun provideCrashRepository(): CrashRepository {
		return CrashAccessor(CrashlyticsDataSource())
	}
}
