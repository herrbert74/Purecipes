package com.purecipes.feature.analytics.domain.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsentStateTest {

	@Test
	fun `allowsAnalytics returns true when obtained`() {
		assertTrue(ConsentState.OBTAINED.allowsAnalytics())
	}

	@Test
	fun `allowsAnalytics returns true when not required`() {
		assertTrue(ConsentState.NOT_REQUIRED.allowsAnalytics())
	}

	@Test
	fun `allowsAnalytics returns false when unknown`() {
		assertFalse(ConsentState.UNKNOWN.allowsAnalytics())
	}

	@Test
	fun `allowsAnalytics returns false when required`() {
		assertFalse(ConsentState.REQUIRED.allowsAnalytics())
	}

	@Test
	fun `allowsAnalytics returns false when denied`() {
		assertFalse(ConsentState.DENIED.allowsAnalytics())
	}
}
