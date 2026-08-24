package app.purecipes.shared.ui.component.paging

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.ShowLoading

/**
 * Copied from: [https://github.com/Ahmad-Hamwi/lazy-pagination-compose]
 *
 * Modified: Replaced CircularProgressIndicator() with ShowLoading()
 */
@Composable
fun FirstPageProgressIndicator(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		ShowLoading()
	}
}

@Composable
fun NewPageProgressIndicator(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.fillMaxWidth()
			.padding(vertical = 16.dp),
		contentAlignment = Alignment.Center,
	) {
		LinearProgressIndicator()
	}
}

@Composable
fun FirstPageErrorIndicator(
	exception: Exception,
	modifier: Modifier = Modifier,
	onRetryClick: () -> Unit = {},
) {
	EmptyStateContent(
		icon = Icons.Filled.Warning,
		iconContentDescription = "Error",
		title = exception.message ?: "Something went wrong",
		description = "Check your connection, then try again.",
		modifier = modifier,
		action = {
			PurecipesButton(
				text = "Retry",
				onClick = onRetryClick,
			)
		},
	)
}

@Composable
fun NewPageErrorIndicator(
	exception: Exception,
	modifier: Modifier = Modifier,
	onRetryClick: () -> Unit = {},
) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = exception.message.orEmpty(),
				textAlign = TextAlign.Center,
			)

			Box(modifier = Modifier.width(32.dp))

			TextButton(onClick = onRetryClick) {
				Text(text = "Retry")
			}
		}
	}
}
