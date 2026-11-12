package app.purecipes.backend.tools

internal const val DEFAULT_PROGRESS_LOG_INTERVAL = 100
private const val PERCENT_MULTIPLIER = 100

internal class ProgressLogger(
	private val stepLabel: String,
	private val total: Int,
	private val startMessage: String? = null,
	private val interval: Int = DEFAULT_PROGRESS_LOG_INTERVAL,
	private val logLine: (String) -> Unit = ::println,
) {

	private var started = false

	fun onItemProcessed(current: Int) {
		if (!started) {
			started = true
			startMessage?.let(logLine)
		}
		if (total <= 0) {
			return
		}
		if (current % interval == 0 || current == total) {
			val percent = current * PERCENT_MULTIPLIER / total
			logLine("$stepLabel: $current/$total ($percent%)")
		}
	}
}

internal fun createNutritionProgressCallback(
	logLine: (String) -> Unit = ::println,
): (current: Int, total: Int) -> Unit {
	var logger: ProgressLogger? = null
	return { current, total ->
		val activeLogger = logger ?: ProgressLogger(
			stepLabel = "Calculating nutrition",
			total = total,
			startMessage = "Calculating nutrition for $total recipes...",
			logLine = logLine,
		).also { logger = it }
		activeLogger.onItemProcessed(current)
	}
}
