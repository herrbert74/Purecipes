package app.purecipes

import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.data.config.purecipesBuildType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AndroidPurecipesConfigModule {

	@Provides
	fun providePurecipesConfig(): PurecipesConfig {
		return object : PurecipesConfig {
			override fun buildType() = purecipesBuildType(BuildConfig.BUILD_TYPE)

			override fun versionName(): String = BuildConfig.VERSION_NAME

			override fun versionCode(): Long = BuildConfig.VERSION_CODE.toLong()

			override fun debugBackendHostOverride(): String? {
				if (!BuildConfig.DEBUG) {
					return null
				}
				return BuildConfig.PURECIPES_DEBUG_BACKEND_HOST.takeIf { it.isNotBlank() }
			}

			override fun googleWebClientId(): String? {
				return BuildConfig.PURECIPES_GOOGLE_WEB_CLIENT_ID.takeIf { it.isNotBlank() }
			}

			override fun gaMeasurementId(): String? {
				return BuildConfig.PURECIPES_GA_MEASUREMENT_ID.takeIf { it.isNotBlank() }
			}

			override fun mixpanelProjectToken(): String? {
				return BuildConfig.PURECIPES_MIXPANEL_PROJECT_TOKEN.takeIf { it.isNotBlank() }
			}

			override fun usercentricsSettingsId(): String? {
				return BuildConfig.PURECIPES_USERCENTRICS_SETTINGS_ID.takeIf { it.isNotBlank() }
			}

			override fun revenueCatApiKey(): String? {
				return BuildConfig.PURECIPES_REVENUECAT_API_KEY.takeIf { it.isNotBlank() }
			}

			override fun showMonetisationDebugOverrides(): Boolean {
				return BuildConfig.PURECIPES_SHOW_MONETISATION_DEBUG_OVERRIDES
			}

			override fun adMobAppId(): String? {
				return BuildConfig.PURECIPES_ADMOB_APP_ID.takeIf { it.isNotBlank() }
			}

			override fun adMobBannerAdUnitId(): String? {
				return BuildConfig.PURECIPES_ADMOB_BANNER_AD_UNIT_ID.takeIf { it.isNotBlank() }
			}

			override fun adMobInterstitialAdUnitId(): String? {
				return BuildConfig.PURECIPES_ADMOB_INTERSTITIAL_AD_UNIT_ID.takeIf { it.isNotBlank() }
			}
		}
	}
}
