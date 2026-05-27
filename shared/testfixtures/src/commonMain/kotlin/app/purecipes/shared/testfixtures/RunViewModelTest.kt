package app.purecipes.shared.testfixtures

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
fun runViewModelTest(testBody: suspend TestScope.() -> Unit) = runTest {
	Dispatchers.setMain(StandardTestDispatcher(testScheduler))
	try {
		testBody()
	} finally {
		Dispatchers.resetMain()
	}
}

@OptIn(ExperimentalCoroutinesApi::class)
fun runUnconfinedViewModelTest(testBody: suspend TestScope.() -> Unit) = runTest {
	Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
	try {
		testBody()
	} finally {
		Dispatchers.resetMain()
	}
}
