package com.purecipes.feature.analytics.data.repository

import com.purecipes.feature.analytics.data.datasource.ConsentDataSource
import com.purecipes.feature.analytics.domain.model.ConsentState
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test

class ConsentAccessorTest {

	@Test
	fun `observeConsentState returns the data source state flow`() {
		val state = MutableStateFlow(ConsentState.OBTAINED)
		val accessor = ConsentAccessor(FakeConsentDataSource(state))

		accessor.observeConsentState() shouldBe state
	}

	@Test
	fun `currentConsentState returns current flow value`() {
		val state = MutableStateFlow(ConsentState.DENIED)
		val accessor = ConsentAccessor(FakeConsentDataSource(state))

		accessor.currentConsentState() shouldBe ConsentState.DENIED
	}

	@Test
	fun `currentConsentState reflects state changes`() {
		val state = MutableStateFlow(ConsentState.UNKNOWN)
		val accessor = ConsentAccessor(FakeConsentDataSource(state))

		state.value = ConsentState.REQUIRED

		accessor.currentConsentState() shouldBe ConsentState.REQUIRED
	}

	@Test
	fun `refreshConsent delegates to data source`() {
		val dataSource = FakeConsentDataSource()
		val accessor = ConsentAccessor(dataSource)

		accessor.refreshConsent()

		dataSource.refreshConsentCalled shouldBe true
	}

	@Test
	fun `showConsentForm delegates to data source`() {
		val dataSource = FakeConsentDataSource()
		val accessor = ConsentAccessor(dataSource)

		accessor.showConsentForm()

		dataSource.showConsentFormCalled shouldBe true
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
