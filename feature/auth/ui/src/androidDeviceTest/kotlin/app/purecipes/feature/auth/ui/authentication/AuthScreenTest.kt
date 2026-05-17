package app.purecipes.feature.auth.ui.authentication

import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.shared.domain.model.PASSWORD_MISSING_LOWERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_NUMBER_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_POLICY_SUPPORTING_TEXT
import app.purecipes.shared.domain.model.PASSWORD_TOO_SHORT_MESSAGE
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
					resendEmailVerification = ResendEmailVerificationUseCase(authRepo),
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

	@Test
	fun registerModeShowsPasswordPolicySupportingText() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(AUTH_PASSWORD_POLICY_SUPPORTING_TEXT_TAG, useUnmergedTree = true)
			.performScrollTo()
			.assertIsDisplayed()
			.assertTextEquals(PASSWORD_POLICY_SUPPORTING_TEXT)
	}

	@Test
	fun registerWithShortPasswordShowsPolicyError() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(AUTH_PASSWORD_FIELD_TAG).performTextInput("short1A")
		submitRegisterForm()
		assertPolicyError(PASSWORD_TOO_SHORT_MESSAGE)
	}

	@Test
	fun registerWithPasswordMissingLowercaseShowsPolicyError() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(AUTH_PASSWORD_FIELD_TAG).performTextInput("VALIDPASS12")
		submitRegisterForm()
		assertPolicyError(PASSWORD_MISSING_LOWERCASE_MESSAGE)
	}

	@Test
	fun registerWithPasswordMissingNumberShowsPolicyError() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(AUTH_PASSWORD_FIELD_TAG).performTextInput("ValidPasswd")
		submitRegisterForm()
		assertPolicyError(PASSWORD_MISSING_NUMBER_MESSAGE)
	}

	private fun ComposeUiTest.showRegisterEmailForm() {
		val authRepo = FakeAuthenticationRepository(AuthenticationState.SignedOut)
		val consentRepo = FakeConsentRepository(ConsentState.OBTAINED)
		setTrackedContent {
			PurecipesTheme {
				AuthenticationScreen(
					observeConsentState = ObserveConsentStateUseCase(consentRepo),
					observeAuthenticationState = ObserveAuthenticationStateUseCase(authRepo),
					signInWithEmail = SignInWithEmailUseCase(authRepo),
					registerWithEmail = RegisterWithEmailUseCase(authRepo),
					resendEmailVerification = ResendEmailVerificationUseCase(authRepo),
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
		onNodeWithText("Register").performClick()
		onNodeWithText("First name").assertIsDisplayed()
		onNodeWithText("First name").performTextInput("Taylor")
		onNodeWithText("Family name").performTextInput("Baker")
		onNodeWithTag(AUTH_EMAIL_FIELD_TAG).performTextInput("taylor@example.com")
	}

	private fun ComposeUiTest.submitRegisterForm() {
		onNodeWithText("Create account").performClick()
	}

	private fun ComposeUiTest.assertPolicyError(expectedMessage: String) {
		onNodeWithTag(AUTH_PASSWORD_ERROR_TAG, useUnmergedTree = true)
			.performScrollTo()
			.assertIsDisplayed()
			.assertTextEquals(expectedMessage)
	}

	@Composable
	private fun FakeAuthenticationProviderButtons(onEmailProviderClick: () -> Unit) {
		FilledTonalButton(onClick = onEmailProviderClick) {
			Text(text = "Continue with email")
		}
	}
}
