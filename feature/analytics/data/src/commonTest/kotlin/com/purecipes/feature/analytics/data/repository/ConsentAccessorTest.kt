package com.purecipes.feature.analytics.data.repository

import com.purecipes.feature.analytics.data.datasource.ConsentDataSource
import com.purecipes.feature.analytics.domain.model.ConsentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConsentAccessorTest {

	@Test
	fun `observeConsentState returns the data source state flow`() {
		val state = MutableStateFlow(ConsentState.OBTAINED)
		val accessor = ConsentAccessor(FakeConsentDataSource(state))

		assertEquals(state, accessor.observeConsentState())
	}

	@Test
	fun `currentConsentState returns current flow value`() {
		val state = MutableStateFlow(ConsentState.DENIED)
		val accessor = ConsentAccessor(FakeConsentDataSource(state))

		assertEquals(ConsentState.DENIED, accessor.currentConsentState())
	}

	@Test
	fun `currentConsentState reflects state changes`() {
		val state = MutableStateFlow(ConsentState.UNKNOWN)
		val accessor = ConsentAccessor(FakeConsentDataSource(state))

		state.value = ConsentState.REQUIRED

		assertEquals(ConsentState.REQUIRED, accessor.currentConsentState())
	}

	@Test
	fun `refreshConsent delegates to data source`() {
		val dataSource = FakeConsentDataSource()
		val accessor = ConsentAccessor(dataSource)

		accessor.refreshConsent()

		assertTrue(dataSource.refreshConsentCalled)
	}

	@Test
	fun `showConsentForm delegates to data source`() {
		val dataSource = FakeConsentDataSource()
		val accessor = ConsentAccessor(dataSource)

		accessor.showConsentForm()

		assertTrue(dataSource.showConsentFormCalled)
	}

	private class FakeConsentDataSource(
		state: MutableStateFlow<ConsentState> = MutableStateFlow(ConsentState.UNKNOWN),
	) : ConsentDataSource {

		override val consentState: StateFlow<ConsentState> = state
		var refreshConsentCalled = false
		var showConsentFormCalled = false

		override fun refreshConsent() {
			refreshConsentCalled = true
		}

		override fun showConsentForm() {
			showConsentFormCalled = true
		}
	}
}
