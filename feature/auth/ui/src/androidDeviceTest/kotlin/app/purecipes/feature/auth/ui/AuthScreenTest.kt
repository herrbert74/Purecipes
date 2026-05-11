package app.purecipes.feature.auth.ui

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class AuthScreenTest {

	@Test
	fun authScreenTypingDoesNotRecomposeTitle() = runRecompositionTrackingUiTest {
		val authRepo = FakeAuthenticationRepository(AuthenticationState.SignedOut)
		val consentRepo = FakeConsentRepository(ConsentState.OBTAINED)
		setTrackedContent {
			PurecipesTheme {
				AuthenticationScreen(
					observeConsentState = ObserveConsentStateUseCase(consentRepo),
					observeAuthenticationState = ObserveAuthenticationStateUseCase(authRepo),
					signInWithEmail = SignInWithEmailUseCase(authRepo),
					registerWithEmail = RegisterWithEmailUseCase(authRepo),
					signInWithExternalProvider = SignInWithExternalProviderUseCase(authRepo),
					signInWithGoogle = SignInWithGoogleUseCase(authRepo),
					showConsentForm = ShowConsentFormUseCase(consentRepo),
					signOut = SignOutUseCase(authRepo),
					onOpenSettings = {},
					googleWebClientId = null,
					initializeGoogleAuthenticationProvider = {},
					authenticationProviderButtons = {
						_,
						onEmailProviderClick,
						_,
						_,
						_,
						->
						FakeAuthenticationProviderButtons(onEmailProviderClick = onEmailProviderClick)
					},
				)
			}
		}
		onNodeWithText("Continue with email").performClick()
		onNodeWithText("Email").assertIsDisplayed()
		onNodeWithTag(AUTH_EMAIL_FIELD_TAG).performTextInput("test@test.com")
		onNodeWithTag(AUTH_SCREEN_TITLE_TAG).assertStable()
	}

	@Composable
	private fun FakeAuthenticationProviderButtons(onEmailProviderClick: () -> Unit) {
		FilledTonalButton(onClick = onEmailProviderClick) {
			Text(text = "Continue with email")
		}
	}
}
