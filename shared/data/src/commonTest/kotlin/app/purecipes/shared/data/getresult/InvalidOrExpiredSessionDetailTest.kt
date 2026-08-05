package app.purecipes.shared.data.getresult

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class InvalidOrExpiredSessionDetailTest {

	@Test
	fun `recognizes backend session unauthorized details`() {
		"Session is invalid or expired".isInvalidOrExpiredSessionDetail() shouldBe true
		"Missing bearer token".isInvalidOrExpiredSessionDetail() shouldBe true
		"Session user is invalid".isInvalidOrExpiredSessionDetail() shouldBe true
	}

	@Test
	fun `rejects other unauthorized details`() {
		"Google token verification failed".isInvalidOrExpiredSessionDetail() shouldBe false
		"Unauthorized".isInvalidOrExpiredSessionDetail() shouldBe false
	}
}
