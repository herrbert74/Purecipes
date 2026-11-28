package app.purecipes.shared.testfixtures.uitest

import android.os.Build
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckResult.AccessibilityCheckResultType
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult
import com.google.android.apps.common.testing.accessibility.framework.integrations.espresso.AccessibilityValidator
import kotlinx.coroutines.test.TestResult
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks as enableRuleAccessibilityChecks
import dejavu.runRecompositionTrackingUiTest as runDejavuRecompositionTrackingUiTest

object PurecipesAccessibilityChecks {

	private val suppressions = mutableListOf<AccessibilityCheckSuppression>()

	fun suppress(suppression: AccessibilityCheckSuppression) {
		suppressions += suppression
	}

	fun clearSuppressions() {
		suppressions.clear()
	}

	internal fun snapshot(): List<AccessibilityCheckSuppression> = suppressions.toList()
}

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.enablePurecipesAccessibilityChecks(
	extraSuppressions: List<AccessibilityCheckSuppression> = emptyList(),
) {
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
		return
	}
	enableAccessibilityChecks(
		purecipesAccessibilityValidator(PurecipesAccessibilityChecks.snapshot() + extraSuppressions),
	)
}

fun ComposeTestRule.enablePurecipesAccessibilityChecks(
	extraSuppressions: List<AccessibilityCheckSuppression> = emptyList(),
) {
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
		return
	}
	enableRuleAccessibilityChecks(
		purecipesAccessibilityValidator(PurecipesAccessibilityChecks.snapshot() + extraSuppressions),
	)
}

@OptIn(ExperimentalTestApi::class)
fun runRecompositionTrackingUiTest(
	accessibilitySuppressions: List<AccessibilityCheckSuppression> = emptyList(),
	block: suspend ComposeUiTest.() -> Unit,
): TestResult = runDejavuRecompositionTrackingUiTest {
	enablePurecipesAccessibilityChecks(accessibilitySuppressions)
	block()
}

private fun purecipesAccessibilityValidator(
	suppressions: List<AccessibilityCheckSuppression>,
): AccessibilityValidator {
	return AccessibilityValidator().apply {
		setRunChecksFromRootView(true)
		setThrowExceptionFor(AccessibilityCheckResultType.ERROR)
		setSuppressingResultMatcher(AccessibilitySuppressionMatcher(suppressions))
	}
}

private class AccessibilitySuppressionMatcher(
	private val suppressions: List<AccessibilityCheckSuppression>,
) : TypeSafeMatcher<AccessibilityViewCheckResult>() {

	override fun describeTo(description: Description) {
		description.appendText("suppressed Purecipes accessibility checks")
	}

	override fun matchesSafely(result: AccessibilityViewCheckResult): Boolean {
		val checkClassName = result.sourceCheckClass.simpleName
		val visibleHeightPx = result.element?.boundsInScreen?.height
		val message = result.message?.toString().orEmpty()
		val hierarchyDescription = result.toString()
		val matchesConfiguredSuppression = checkClassName != null && suppressions.any { suppression ->
			suppression.matches(checkClassName, message, hierarchyDescription)
		}
		val matchesClippedSpeakableText = checkClassName != null &&
			shouldSuppressClippedSpeakableText(checkClassName, visibleHeightPx)
		return matchesClippedSpeakableText || matchesConfiguredSuppression
	}
}
