package app.purecipes.feature.featurerequests.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val FEATURE_REQUEST_COMMENT_INPUT_TAG = "featureRequestCommentInput"

internal const val FEATURE_REQUEST_COMMENT_SEND_TAG = "featureRequestCommentSend"

@Composable
internal fun FeatureRequestCommentComposer(
	isSending: Boolean,
	onSubmit: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	var commentField by remember { mutableStateOf("") }
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(PurecipesTheme.space.m),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalAlignment = Alignment.CenterVertically,
	) {
		OutlinedTextField(
			value = commentField,
			onValueChange = { commentField = it },
			modifier = Modifier
				.weight(1f)
				.testTag(FEATURE_REQUEST_COMMENT_INPUT_TAG),
			label = { Text(text = "Add a comment") },
			singleLine = true,
			enabled = !isSending,
		)
		IconButton(
			onClick = {
				onSubmit(commentField)
				commentField = ""
			},
			modifier = Modifier.testTag(FEATURE_REQUEST_COMMENT_SEND_TAG),
			enabled = !isSending && commentField.trim().isNotEmpty(),
		) {
			Icon(
				imageVector = Icons.AutoMirrored.Filled.Send,
				contentDescription = "Send comment",
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestCommentComposerPreview() {
	PurecipesPreviewScaffold {
		FeatureRequestCommentComposer(
			isSending = false,
			onSubmit = {},
		)
	}
}
