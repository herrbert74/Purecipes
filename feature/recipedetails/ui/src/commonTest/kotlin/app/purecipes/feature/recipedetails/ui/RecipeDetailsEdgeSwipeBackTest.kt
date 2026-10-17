package app.purecipes.feature.recipedetails.ui

import androidx.compose.runtime.MonotonicFrameClock
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsEdgeSwipeBackTest {

	private class AutoAdvancingFrameClock(private var timeNanos: Long = 0L) : MonotonicFrameClock {

		override val key: CoroutineContext.Key<*> get() = MonotonicFrameClock
		override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
			timeNanos += 16_000_000L
			return onFrame(timeNanos)
		}
	}

	@Test
	fun initialOffsetIsZero() = runTest {
		val state = RecipeDetailsEdgeSwipeBackState(
			enabled = true,
			coroutineScope = this,
			onDismiss = {},
		)
		state.offsetX.value shouldBe 0f
	}

	@Test
	fun dragUpdatesOffsetWhenEnabled() = runTest {
		val state = RecipeDetailsEdgeSwipeBackState(
			enabled = true,
			coroutineScope = this,
			onDismiss = {},
		).apply {
			maxOffsetPx = 1000f
		}
		state.onDrag(200f)
		advanceUntilIdle()
		state.offsetX.value shouldBe 200f
	}

	@Test
	fun dragDoesNotUpdateWhenDisabled() = runTest {
		val state = RecipeDetailsEdgeSwipeBackState(
			enabled = false,
			coroutineScope = this,
			onDismiss = {},
		).apply {
			maxOffsetPx = 1000f
		}
		state.onDrag(200f)
		advanceUntilIdle()
		state.offsetX.value shouldBe 0f
	}

	@Test
	fun dragClampsToZeroAndMax() = runTest {
		val state = RecipeDetailsEdgeSwipeBackState(
			enabled = true,
			coroutineScope = this,
			onDismiss = {},
		).apply {
			maxOffsetPx = 500f
		}
		state.onDrag(-100f)
		advanceUntilIdle()
		state.offsetX.value shouldBe 0f

		state.onDrag(800f)
		advanceUntilIdle()
		state.offsetX.value shouldBe 500f
	}

	@Test
	fun dragPastThresholdTriggersDismiss() = runTest {
		withContext(AutoAdvancingFrameClock()) {
			var dismissed = false
			val state = RecipeDetailsEdgeSwipeBackState(
				enabled = true,
				coroutineScope = this,
				onDismiss = { dismissed = true },
			).apply {
				maxOffsetPx = 1000f
			}
			state.onDrag(400f)
			advanceUntilIdle()
			state.onDragEnd()
			advanceUntilIdle()
			dismissed shouldBe true
			state.offsetX.value shouldBe 1000f
		}
	}

	@Test
	fun dragBelowThresholdSnapsBackToZero() = runTest {
		withContext(AutoAdvancingFrameClock()) {
			var dismissed = false
			val state = RecipeDetailsEdgeSwipeBackState(
				enabled = true,
				coroutineScope = this,
				onDismiss = { dismissed = true },
			).apply {
				maxOffsetPx = 1000f
			}
			state.onDrag(200f)
			advanceUntilIdle()
			state.onDragEnd()
			advanceUntilIdle()
			dismissed shouldBe false
			state.offsetX.value shouldBe 0f
		}
	}

	@Test
	fun dragCancelSnapsBackToZero() = runTest {
		withContext(AutoAdvancingFrameClock()) {
			val state = RecipeDetailsEdgeSwipeBackState(
				enabled = true,
				coroutineScope = this,
				onDismiss = {},
			).apply {
				maxOffsetPx = 1000f
			}
			state.onDrag(200f)
			advanceUntilIdle()
			state.onDragCancel()
			advanceUntilIdle()
			state.offsetX.value shouldBe 0f
		}
	}
}
