package app.purecipes.feature.settings.ui.about

import androidx.lifecycle.ViewModel
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class AboutViewModel(
	purecipesConfig: PurecipesConfig,
) : ViewModel() {

	val versionText: String =
		"Version ${purecipesConfig.versionName()} (${purecipesConfig.versionCode()})"
}
