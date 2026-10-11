package app.purecipes.feature.ads.domain

object InlineAdPlacement {

	const val FIRST_AD_AT_LIST_POSITION = 3

	const val AD_EVERY_N_LIST_POSITIONS = 12

	const val MINIMUM_CONTENT_COUNT_FOR_ADS = 2

	const val SHELF_FIRST_AD_CONTENT_INDEX = 5

	const val SHELF_AD_EVERY_N_RECIPES = 24

	fun shouldInsertAdBeforeContentIndex(contentIndex: Int, contentCount: Int): Boolean {
		if (contentCount < MINIMUM_CONTENT_COUNT_FOR_ADS) {
			return false
		}
		val nextListPosition = contentIndex + adsInsertedBeforeContentIndex(contentIndex) + 1
		return isAdListPosition(nextListPosition)
	}

	fun isAdListPosition(listPositionOneBased: Int): Boolean {
		return listPositionOneBased >= FIRST_AD_AT_LIST_POSITION &&
			(listPositionOneBased - FIRST_AD_AT_LIST_POSITION) % AD_EVERY_N_LIST_POSITIONS == 0
	}

	fun adsInsertedBeforeContentIndex(contentIndex: Int): Int {
		var ads = 0
		while (FIRST_AD_AT_LIST_POSITION + ads * AD_EVERY_N_LIST_POSITIONS <= contentIndex + ads) {
			ads++
		}
		return ads
	}

	fun shouldInsertShelfAdBeforeContentIndex(contentIndex: Int, contentCount: Int): Boolean =
		contentCount > SHELF_FIRST_AD_CONTENT_INDEX &&
			contentIndex >= SHELF_FIRST_AD_CONTENT_INDEX &&
			(contentIndex - SHELF_FIRST_AD_CONTENT_INDEX) % SHELF_AD_EVERY_N_RECIPES == 0
}

fun <T> inlineFeedEntries(
	items: List<T>,
	includeAds: Boolean,
	shouldInsertAd: (contentIndex: Int, contentCount: Int) -> Boolean =
		InlineAdPlacement::shouldInsertAdBeforeContentIndex,
): List<InlineFeedEntry<T>> {
	if (!includeAds) {
		return items.map { item -> InlineFeedEntry.Content(item) }
	}
	return buildList {
		items.forEachIndexed { index, item ->
			if (shouldInsertAd(index, items.size)) {
				add(InlineFeedEntry.Ad)
			}
			add(InlineFeedEntry.Content(item))
		}
	}
}

sealed interface InlineFeedEntry<out T> {
	data class Content<T>(val item: T) : InlineFeedEntry<T>
	data object Ad : InlineFeedEntry<Nothing>
}
