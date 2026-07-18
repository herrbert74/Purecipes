package app.purecipes.feature.subscription.data.datasource

import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Inject
@ContributesBinding(AppScope::class)
class SettingsMonetisationDebugOverridesDataSource(
	private val settings: Settings = Settings(),
	private val preferencesKey: String = DEFAULT_PREFERENCES_KEY,
) : MonetisationDebugOverridesDataSource {

	private val overridesFlow = sharedOverridesFlow(
		preferencesKey = preferencesKey,
		overrides = loadOverrides(),
	)

	override fun observe(): Flow<MonetisationDebugOverrides> = overridesFlow

	override fun setPremiumStatusOverride(override: PremiumStatusOverride) {
		persist(overridesFlow.value.copy(premiumStatus = override))
	}

	override fun setAdsDisplayOverride(override: AdsDisplayOverride) {
		persist(overridesFlow.value.copy(adsDisplay = override))
	}

	private fun loadOverrides(): MonetisationDebugOverrides {
		return MonetisationDebugOverrides(
			premiumStatus = parsePremiumStatus(settings.getStringOrNull(premiumStatusKey())),
			adsDisplay = parseAdsDisplay(settings.getStringOrNull(adsDisplayKey())),
		)
	}

	private fun persist(overrides: MonetisationDebugOverrides) {
		settings.putString(premiumStatusKey(), overrides.premiumStatus.name)
		settings.putString(adsDisplayKey(), overrides.adsDisplay.name)
		overridesFlow.value = overrides
	}

	private fun premiumStatusKey(): String = "$preferencesKey.premiumStatus"

	private fun adsDisplayKey(): String = "$preferencesKey.adsDisplay"

	private fun parsePremiumStatus(stored: String?): PremiumStatusOverride {
		return stored?.let { value ->
			runCatching { PremiumStatusOverride.valueOf(value) }.getOrNull()
		} ?: PremiumStatusOverride.AUTO
	}

	private fun parseAdsDisplay(stored: String?): AdsDisplayOverride {
		return stored?.let { value ->
			runCatching { AdsDisplayOverride.valueOf(value) }.getOrNull()
		} ?: AdsDisplayOverride.AUTO
	}

	private companion object {

		const val DEFAULT_PREFERENCES_KEY = "purecipes.monetisation.debug.overrides"

		val sharedOverridesFlows = mutableMapOf<String, MutableStateFlow<MonetisationDebugOverrides>>()

		fun sharedOverridesFlow(
			preferencesKey: String,
			overrides: MonetisationDebugOverrides,
		): MutableStateFlow<MonetisationDebugOverrides> {
			return sharedOverridesFlows.getOrPut(preferencesKey) {
				MutableStateFlow(overrides)
			}
		}
	}
}
