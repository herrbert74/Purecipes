package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RecipeDetailsSwipeDismissTest {

	@Test
	fun disabledConnectionIgnoresScrollAndFling() = runTest {
		var dismissed = false
		val connection = RecipeDetailsSwipeDismissConnection(
			listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
			enabled = false,
			thresholdPx = 300f,
			onDismiss = { dismissed = true },
			currentOffset = { 0f },
			onOffsetChange = {},
			onResetAnimation = {},
		)

		val postConsumed = connection.onPostScroll(
			consumed = Offset.Zero,
			available = Offset(0f, 50f),
			source = NestedScrollSource.UserInput,
		)
		postConsumed shouldBe Offset.Zero

		val preConsumed = connection.onPreScroll(
			available = Offset(0f, -20f),
			source = NestedScrollSource.UserInput,
		)
		preConsumed shouldBe Offset.Zero

		val flingConsumed = connection.onPreFling(Velocity(0f, 2000f))
		flingConsumed shouldBe Velocity.Zero
		dismissed shouldBe false
	}

	@Test
	fun enabledConnectionConsumesDownwardScrollWhenAtTop() = runTest {
		var offsetValue = 0f
		val connection = RecipeDetailsSwipeDismissConnection(
			listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
			enabled = true,
			thresholdPx = 300f,
			onDismiss = {},
			currentOffset = { offsetValue },
			onOffsetChange = { offsetValue = it },
			onResetAnimation = {},
		)

		val consumed = connection.onPostScroll(
			consumed = Offset.Zero,
			available = Offset(0f, 40f),
			source = NestedScrollSource.UserInput,
		)

		consumed shouldBe Offset(0f, 40f)
		offsetValue shouldBe 20f
	}

	@Test
	fun enabledConnectionDismissesWhenFlingExceedsThreshold() = runTest {
		var dismissed = false
		val connection = RecipeDetailsSwipeDismissConnection(
			listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
			enabled = true,
			thresholdPx = 300f,
			onDismiss = { dismissed = true },
			currentOffset = { 50f },
			onOffsetChange = {},
			onResetAnimation = {},
		)

		val consumed = connection.onPreFling(Velocity(0f, 1500f))
		consumed shouldBe Velocity(0f, 1500f)
		dismissed shouldBe true
	}

	@Test
	fun enabledConnectionResetsWhenBelowThreshold() = runTest {
		var dismissed = false
		var resetCalled = false
		val connection = RecipeDetailsSwipeDismissConnection(
			listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
			enabled = true,
			thresholdPx = 300f,
			onDismiss = { dismissed = true },
			currentOffset = { 50f },
			onOffsetChange = {},
			onResetAnimation = { resetCalled = true },
		)

		val consumed = connection.onPreFling(Velocity(0f, 100f))
		consumed shouldBe Velocity.Zero
		dismissed shouldBe false
		resetCalled shouldBe true
	}
}
