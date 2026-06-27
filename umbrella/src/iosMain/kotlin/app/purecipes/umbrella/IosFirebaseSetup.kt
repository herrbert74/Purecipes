package app.purecipes.umbrella

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

object IosFirebaseSetup {
	private var configured = false

	fun configureIfNeeded() {
		if (configured) {
			return
		}
		Firebase.initialize(null)
		configured = true
	}
}
