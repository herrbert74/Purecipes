package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.WebLaunchLinkPlatformDataSource
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class WebLaunchLinkAccessor(
	private val platformDataSource: WebLaunchLinkPlatformDataSource,
) : WebLaunchLinkRepository {

	override fun readLaunchUrl(): String? = platformDataSource.readLaunchUrl()
}
