package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.firebase.facebook.FacebookButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.facebook.FacebookSignInButton
import kotlinx.coroutines.launch

@Composable
internal actual fun FacebookAuthenticationButton(
	onFacebookSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
) {
	val coroutineScope = rememberCoroutineScope()
	FacebookButtonUiContainerFirebase(
		linkAccount = false,
		onResult = { result ->
			val failure = result.exceptionOrNull()
			if (failure != null) {
				onFacebookSignInResult(null, null, "", null)
				return@FacebookButtonUiContainerFirebase
			}
			val firebaseUser = result.getOrNull()
			if (firebaseUser == null) {
				onFacebookSignInResult(null, null, "", null)
				return@FacebookButtonUiContainerFirebase
			}
			coroutineScope.launch {
				val profile = firebaseUser.toFacebookAuthenticationProfile()
				onFacebookSignInResult(
					profile?.idToken,
					profile?.email,
					profile?.displayName.orEmpty(),
					profile?.profileImageUrl,
				)
			}
		},
	) {
		FacebookSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
		) { this.onClick() }
	}
}
