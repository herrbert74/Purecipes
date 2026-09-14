package app.purecipes.feature.featurerequests.ui

import app.purecipes.shared.domain.model.FeatureRequestStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class FeatureRequestLabelsTest {

	@Test
	fun emptyCopyDescribesAnEmptyBoardWhenUnfiltered() {
		assertEquals("No requests yet", featureRequestsEmptyTitle(null))
		assertEquals(
			"Tell us what would make Purecipes better and other cooks can vote for it.",
			featureRequestsEmptyDescription(null),
		)
	}

	@Test
	fun emptyCopyDescribesAMissingStageWhenFiltered() {
		assertEquals("Nothing in this stage yet", featureRequestsEmptyTitle(FeatureRequestStatus.PLANNED))
		assertEquals(
			"Nothing has made it into Planned at the moment.",
			featureRequestsEmptyDescription(FeatureRequestStatus.PLANNED),
		)
		assertEquals(
			"Nothing has made it into In progress at the moment.",
			featureRequestsEmptyDescription(FeatureRequestStatus.IN_PROGRESS),
		)
	}
}
