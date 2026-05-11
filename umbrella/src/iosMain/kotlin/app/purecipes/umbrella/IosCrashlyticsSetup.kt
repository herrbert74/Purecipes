package app.purecipes.umbrella

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook

object IosCrashlyticsSetup {
	fun setup() {
		enableCrashlytics()
		setCrashlyticsUnhandledExceptionHook()
	}
}
