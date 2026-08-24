package app.purecipes.feature.cooking.ui

internal data class CookingTimerState(
	val label: String,
	val totalSeconds: Int,
	val remainingSeconds: Int,
) {

	val isComplete: Boolean
		get() = remainingSeconds <= 0

	val displayTime: String
		get() {
			val safeRemaining = remainingSeconds.coerceAtLeast(0)
			val minutes = safeRemaining / SECONDS_PER_MINUTE
			val seconds = safeRemaining % SECONDS_PER_MINUTE
			return "$minutes:${seconds.toString().padStart(2, '0')}"
		}

	companion object {

		fun fromDuration(duration: CookingStepHighlight.Duration): CookingTimerState =
			CookingTimerState(
				label = duration.text,
				totalSeconds = duration.totalSeconds,
				remainingSeconds = duration.totalSeconds,
			)

		private const val SECONDS_PER_MINUTE = 60
	}
}
