package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.SharePlatformDataSource
import app.purecipes.feature.sharing.domain.repository.ShareRepository

internal class ShareAccessor(
	private val sharePlatformDataSource: SharePlatformDataSource,
) : ShareRepository {

	override fun shareText(text: String, title: String?) {
		sharePlatformDataSource.shareText(text, title)
	}
}
