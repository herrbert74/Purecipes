package app.purecipes.feature.auth.ui.authentication

import androidx.compose.runtime.remember
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.usecase.DeleteAccountUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithFacebookUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.feature.auth.ui.profile.DELETE_ACCOUNT_BUTTON_TAG
import app.purecipes.feature.auth.ui.profile.DELETE_ACCOUNT_DIALOG_TAG
import app.purecipes.feature.auth.ui.registration.REGISTRATION_EMAIL_FIELD_TAG
import app.purecipes.feature.auth.ui.registration.REGISTRATION_PASSWORD_ERROR_TAG
import app.purecipes.feature.auth.ui.registration.REGISTRATION_PASSWORD_FIELD_TAG
import app.purecipes.feature.auth.ui.registration.REGISTRATION_PASSWORD_POLICY_SUPPORTING_TEXT_TAG
import app.purecipes.feature.auth.ui.registration.REGISTRATION_SUBMIT_TAG
import app.purecipes.feature.auth.ui.registration.RegistrationScreen
import app.purecipes.feature.auth.ui.registration.RegistrationViewModel
import app.purecipes.shared.domain.model.PASSWORD_MISSING_LOWERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_NUMBER_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_POLICY_SUPPORTING_TEXT
import app.purecipes.shared.domain.model.PASSWORD_TOO_SHORT_MESSAGE
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class AuthScreenTest {

	@Test
	fun authScreenTypingDoesNotRecomposeTitle() = runRecompositionTrackingUiTest {
		showSignedOutAccountScreen()
		onNodeWithText("Sign in with email").assertIsDisplayed()
		onNodeWithTag(AUTH_SCREEN_TITLE_TAG).assertStable()
	}

	@Test
	fun signedOutEmailActionsNavigateToSignInAndRegistration() = runRecompositionTrackingUiTest {
		var signInClicks = 0
		var registrationClicks = 0
		showSignedOutAccountScreen(
			onNavigateToEmailRegistration = { registrationClicks++ },
			onNavigateToSignIn = { signInClicks++ },
		)
		onNodeWithText("Sign in or create an account.").assertIsDisplayed()
		onNodeWithText("or use email").assertIsDisplayed()
		onNodeWithTag(AUTH_SIGN_IN_WITH_EMAIL_TAG).performClick()
		onNodeWithTag(AUTH_CREATE_ACCOUNT_WITH_EMAIL_TAG).performClick()
		assertEquals(1, signInClicks)
		assertEquals(1, registrationClicks)
	}

	@Test
	fun signedInDeleteAccountShowsConfirmationDialog() = runRecompositionTrackingUiTest {
		showSignedInAccountScreen()
		onNodeWithTag(DELETE_ACCOUNT_BUTTON_TAG)
			.performScrollTo()
			.performClick()
		onNodeWithTag(DELETE_ACCOUNT_DIALOG_TAG).assertIsDisplayed()
		onNodeWithText("Delete account?").assertIsDisplayed()
	}

	@Test
	fun registerModeShowsPasswordPolicySupportingText() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(REGISTRATION_PASSWORD_POLICY_SUPPORTING_TEXT_TAG, useUnmergedTree = true)
			.performScrollTo()
			.assertIsDisplayed()
			.assertTextEquals(PASSWORD_POLICY_SUPPORTING_TEXT)
	}

	@Test
	fun registerWithShortPasswordShowsPolicyError() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(REGISTRATION_PASSWORD_FIELD_TAG).performTextInput("short1A")
		submitRegisterForm()
		assertPolicyError(PASSWORD_TOO_SHORT_MESSAGE)
	}

	@Test
	fun registerWithPasswordMissingLowercaseShowsPolicyError() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(REGISTRATION_PASSWORD_FIELD_TAG).performTextInput("VALIDPASS12")
		submitRegisterForm()
		assertPolicyError(PASSWORD_MISSING_LOWERCASE_MESSAGE)
	}

	@Test
	fun registerWithPasswordMissingNumberShowsPolicyError() = runRecompositionTrackingUiTest {
		showRegisterEmailForm()
		onNodeWithTag(REGISTRATION_PASSWORD_FIELD_TAG).performTextInput("ValidPasswd")
		submitRegisterForm()
		assertPolicyError(PASSWORD_MISSING_NUMBER_MESSAGE)
	}

	private fun ComposeUiTest.showSignedOutAccountScreen(
		onNavigateToEmailRegistration: () -> Unit = {},
		onNavigateToSignIn: () -> Unit = {},
	) {
		val authRepo = FakeAuthenticationRepository(AuthenticationState.SignedOut)
		val consentRepo = FakeConsentRepository(ConsentState.OBTAINED)
		setTrackedContent {
			PurecipesTheme {
				AuthenticationScreen(
					onOpenSettings = {},
					onNavigateToEmailRegistration = onNavigateToEmailRegistration,
					onNavigateToSignIn = onNavigateToSignIn,
					googleWebClientId = null,
					viewModel = AuthenticationViewModel(
						observeAuthenticationState = ObserveAuthenticationStateUseCase(authRepo),
						signInWithExternalProvider = SignInWithExternalProviderUseCase(authRepo),
						signInWithFacebook = SignInWithFacebookUseCase(authRepo),
						signInWithGoogle = SignInWithGoogleUseCase(authRepo),
						deleteAccount = DeleteAccountUseCase(authRepo),
						signOut = SignOutUseCase(authRepo),
						observeConsentState = ObserveConsentStateUseCase(consentRepo),
						showConsentForm = ShowConsentFormUseCase(consentRepo),
						trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
						logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
						sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
					),
					initializeGoogleAuthenticationProvider = {},
					authenticationProviderButtons = { _, _, _, _, _ -> },
				)
			}
		}
	}

	private fun ComposeUiTest.showSignedInAccountScreen() {
		val authRepo = FakeAuthenticationRepository(
			AuthenticationState.SignedIn(fakeAuthUser()),
		)
		val consentRepo = FakeConsentRepository(ConsentState.OBTAINED)
		setTrackedContent {
			PurecipesTheme {
				AuthenticationScreen(
					onOpenSettings = {},
					onNavigateToEmailRegistration = {},
					onNavigateToSignIn = {},
					googleWebClientId = null,
					viewModel = AuthenticationViewModel(
						observeAuthenticationState = ObserveAuthenticationStateUseCase(authRepo),
						signInWithExternalProvider = SignInWithExternalProviderUseCase(authRepo),
						signInWithFacebook = SignInWithFacebookUseCase(authRepo),
						signInWithGoogle = SignInWithGoogleUseCase(authRepo),
						deleteAccount = DeleteAccountUseCase(authRepo),
						signOut = SignOutUseCase(authRepo),
						observeConsentState = ObserveConsentStateUseCase(consentRepo),
						showConsentForm = ShowConsentFormUseCase(consentRepo),
						trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
						logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
						sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
					),
					initializeGoogleAuthenticationProvider = {},
					authenticationProviderButtons = { _, _, _, _, _ -> },
				)
			}
		}
	}

	private fun ComposeUiTest.showRegisterEmailForm() {
		val authRepo = FakeAuthenticationRepository(AuthenticationState.SignedOut)
		val trackEvent = TrackEventUseCase(FakeAnalyticsRepository())
		setTrackedContent {
			val viewModel = remember {
				RegistrationViewModel(
					registerWithEmail = RegisterWithEmailUseCase(authRepo),
					trackEvent = trackEvent,
				)
			}
			PurecipesTheme {
				RegistrationScreen(
					onBack = {},
					onRegistrationSuccess = {},
					viewModel = viewModel,
				)
			}
		}
		onNodeWithText("Display name").performTextInput("Taylor Baker")
		onNodeWithTag(REGISTRATION_EMAIL_FIELD_TAG).performTextInput("taylor@example.com")
		onNodeWithTag(REGISTRATION_PASSWORD_FIELD_TAG).performScrollTo()
	}

	private fun ComposeUiTest.submitRegisterForm() {
		onNodeWithTag(REGISTRATION_SUBMIT_TAG)
			.performScrollTo()
			.performClick()
	}

	private fun ComposeUiTest.assertPolicyError(expectedMessage: String) {
		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithTag(REGISTRATION_PASSWORD_ERROR_TAG, useUnmergedTree = true)
				.fetchSemanticsNodes()
				.isNotEmpty()
		}
		onNodeWithTag(REGISTRATION_PASSWORD_ERROR_TAG, useUnmergedTree = true)
			.performScrollTo()
			.assertIsDisplayed()
			.assertTextEquals(expectedMessage)
	}
}
