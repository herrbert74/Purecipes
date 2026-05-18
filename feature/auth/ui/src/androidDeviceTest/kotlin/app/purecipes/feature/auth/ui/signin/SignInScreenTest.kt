package app.purecipes.feature.auth.ui.signin

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SendPasswordResetEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class SignInScreenTest {

	@Test
	fun signInScreenShowsForgotPasswordButton() = runRecompositionTrackingUiTest {
		setSignInScreen()
		onNodeWithTag(SIGN_IN_FORGOT_PASSWORD_TAG).assertIsDisplayed()
		onNodeWithText("Forgot password?").assertIsDisplayed()
	}

	@Test
	fun forgotPasswordWithValidEmailShowsSuccessMessage() = runRecompositionTrackingUiTest {
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = { Ok(Unit) },
		)
		setSignInScreen(repository)
		onNodeWithTag(SIGN_IN_EMAIL_FIELD_TAG).performTextInput("taylor@example.com")
		onNodeWithTag(SIGN_IN_FORGOT_PASSWORD_TAG).performClick()
		waitForIdle()
		onNodeWithTag(SIGN_IN_INFO_MESSAGE_TAG)
			.assertIsDisplayed()
			.assertTextEquals("Password reset email sent. Please check your inbox.")
	}

	@Test
	fun forgotPasswordWithBlankEmailShowsEmailError() = runRecompositionTrackingUiTest {
		setSignInScreen()
		onNodeWithTag(SIGN_IN_FORGOT_PASSWORD_TAG).performClick()
		waitForIdle()
		onNodeWithTag(SIGN_IN_EMAIL_ERROR_TAG, useUnmergedTree = true)
			.assertIsDisplayed()
			.assertTextEquals(EMAIL_REQUIRED_MESSAGE)
	}

	private fun ComposeUiTest.setSignInScreen(
		repository: FakeAuthenticationRepository = FakeAuthenticationRepository(),
	) {
		setTrackedContent {
			PurecipesTheme {
				SignInScreen(
					signInWithEmail = SignInWithEmailUseCase(repository),
					resendEmailVerification = ResendEmailVerificationUseCase(repository),
					sendPasswordResetEmail = SendPasswordResetEmailUseCase(repository),
					initialEmail = "",
					showRegistrationSuccessMessage = false,
					onBack = {},
				)
			}
		}
	}
}
