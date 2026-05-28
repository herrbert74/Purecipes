package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.SharePlatformDataSource
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class ShareAccessor(
	private val sharePlatformDataSource: SharePlatformDataSource,
) : ShareRepository {

	override fun shareText(text: String, title: String?) {
		sharePlatformDataSource.shareText(text, title)
	}
}
