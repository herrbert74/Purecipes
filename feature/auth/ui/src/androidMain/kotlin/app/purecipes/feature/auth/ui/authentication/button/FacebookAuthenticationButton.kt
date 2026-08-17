package app.purecipes.feature.auth.ui.authentication.button

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import app.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.mmk.kmpauth.facebook.FacebookLoginTracking
import com.mmk.kmpauth.facebook.rememberFacebookAuthState
import com.mmk.kmpauth.uihelper.facebook.FacebookSignInButton
import kotlinx.coroutines.launch

@Composable
internal actual fun FacebookAuthenticationButton(
	onFacebookSignInResult: (idToken: String?, email: String?, displayName: String, profileImageUrl: String?) -> Unit,
) {
	if (LocalInspectionMode.current) {
		OutlinedButton(
			onClick = {},
			enabled = false,
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
		) {
			Text(text = "Continue with Facebook")
		}
		return
	}
	val coroutineScope = rememberCoroutineScope()
	val facebookAuth = rememberFacebookAuthState(
		linkAccount = false,
		loginTracking = FacebookLoginTracking.Enabled,
		onResult = { result ->
			coroutineScope.launch {
				val profileResult = result.toFacebookAuthenticationProfileResult()
				val failure = profileResult.exceptionOrNull()
				if (failure != null) {
					onFacebookSignInResult(null, null, "", null)
					return@launch
				}
				val profile = profileResult.getOrNull()
				onFacebookSignInResult(
					profile?.idToken,
					profile?.email,
					profile?.displayName.orEmpty(),
					profile?.profileImageUrl,
				)
			}
		},
	)
	FacebookSignInButton(
		modifier = Modifier
			.fillMaxWidth()
			.height(PurecipesButtonDefaults.providerButtonHeight),
		onClick = { facebookAuth.launch() },
	)
}
