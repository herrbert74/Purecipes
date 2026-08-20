package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.coroutines.flow.drop

@Composable
internal fun DenseOutlinedTextField(
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	readOnly: Boolean = false,
	label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
	trailingIcon: @Composable (() -> Unit)? = null,
	suffix: @Composable (() -> Unit)? = null,
	isError: Boolean = false,
	supportingText: @Composable (() -> Unit)? = null,
	keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
	colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
) {
	val state = remember { TextFieldState(value) }
	val latestOnValueChange = rememberUpdatedState(onValueChange)
	SideEffect {
		if (state.text.toString() != value) {
			state.setTextAndPlaceCursorAtEnd(value)
		}
	}
	LaunchedEffect(state) {
		snapshotFlow { state.text.toString() }
			.drop(1)
			.collect { text ->
				latestOnValueChange.value(text)
			}
	}

	OutlinedTextField(
		state = state,
		modifier = modifier.heightIn(min = PurecipesTheme.space.xxl),
		enabled = enabled,
		readOnly = readOnly,
		label = label,
		trailingIcon = trailingIcon,
		suffix = suffix,
		isError = isError,
		supportingText = supportingText,
		keyboardOptions = keyboardOptions,
		lineLimits = TextFieldLineLimits.SingleLine,
		colors = colors,
		contentPadding = denseOutlinedTextFieldPadding(),
	)
}

@Composable
private fun denseOutlinedTextFieldPadding(): PaddingValues =
	PaddingValues(
		start = DENSE_TEXT_FIELD_HORIZONTAL_PADDING,
		top = PurecipesTheme.space.xs,
		end = DENSE_TEXT_FIELD_HORIZONTAL_PADDING,
		bottom = PurecipesTheme.space.xs,
	)

private val DENSE_TEXT_FIELD_HORIZONTAL_PADDING = 12.dp
