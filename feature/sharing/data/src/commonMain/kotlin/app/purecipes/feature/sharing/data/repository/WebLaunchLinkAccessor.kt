package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.WebLaunchLinkPlatformDataSource
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository

internal class WebLaunchLinkAccessor(
	private val platformDataSource: WebLaunchLinkPlatformDataSource,
) : WebLaunchLinkRepository {

	override fun readLaunchUrl(): String? = platformDataSource.readLaunchUrl()
}
