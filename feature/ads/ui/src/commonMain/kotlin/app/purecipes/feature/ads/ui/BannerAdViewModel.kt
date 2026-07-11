package app.purecipes.feature.ads.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.ads.domain.AdMobDefaults
import app.purecipes.feature.ads.domain.usecase.ObserveShouldShowAdsUseCase
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class BannerAdViewModel(
	observeShouldShowAds: ObserveShouldShowAdsUseCase,
	purecipesConfig: PurecipesConfig,
) : ViewModel() {

	var shouldShowAds by mutableStateOf(false)
		private set

	val bannerAdUnitId: String = purecipesConfig.adMobBannerAdUnitId()
		?.takeIf { it.isNotBlank() }
		?: AdMobDefaults.BANNER_AD_UNIT_ID

	init {
		viewModelScope.launch {
			observeShouldShowAds().collectLatest { shouldShow ->
				shouldShowAds = shouldShow
			}
		}
	}
}
