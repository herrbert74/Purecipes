package app.purecipes.feature.ads.domain

object InlineAdPlacement {

	const val FIRST_AD_AT_LIST_POSITION = 3

	const val AD_EVERY_N_LIST_POSITIONS = 12

	const val MINIMUM_CONTENT_COUNT_FOR_ADS = 2

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
}
