package app.purecipes.shared.data.session

fun interface UnauthorizedSessionClearer {

	fun clearUnauthorizedSession()
}

internal object UnauthorizedSessionClearers {

	var instance: UnauthorizedSessionClearer = NoOpUnauthorizedSessionClearer
		private set

	fun install(clearer: UnauthorizedSessionClearer) {
		instance = clearer
	}

	fun reset() {
		instance = NoOpUnauthorizedSessionClearer
	}
}

private object NoOpUnauthorizedSessionClearer : UnauthorizedSessionClearer {

	override fun clearUnauthorizedSession() = Unit
}
