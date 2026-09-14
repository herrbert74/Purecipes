package app.purecipes.shared.domain.model

const val EMAIL_NOT_VERIFIED_MESSAGE = "Email address is not verified."

const val EMAIL_REQUIRED_MESSAGE = "Email is required"

const val INVALID_EMAIL_MESSAGE = "Enter a valid email address"

const val INCORRECT_EMAIL_OR_PASSWORD_MESSAGE = "Incorrect email or password."

const val PASSWORD_REQUIRED_MESSAGE = "Password is required."

const val PASSWORD_CONFIRMATION_MISMATCH_MESSAGE = "Passwords do not match."

const val PASSWORD_TOO_SHORT_MESSAGE = "Password must be at least 10 characters."

const val PASSWORD_MISSING_UPPERCASE_MESSAGE = "Password must contain at least one uppercase letter."

const val PASSWORD_MISSING_LOWERCASE_MESSAGE = "Password must contain at least one lowercase letter."

const val PASSWORD_MISSING_NUMBER_MESSAGE = "Password must contain at least one number."

const val PASSWORD_POLICY_SUPPORTING_TEXT =
	"At least 10 characters with one uppercase letter, one lowercase letter, and one number."

const val PASSWORD_RESET_EMAIL_SENT_MESSAGE =
	"If there's an account with this email, we sent a password reset. " +
		"Check your inbox, and your spam folder if it doesn't arrive."

const val REGISTRATION_SUCCESS_MESSAGE =
	"Registration successful. Please check your email to verify your account, " +
		"and your spam folder if it doesn't arrive."

const val VERIFICATION_EMAIL_SENT_MESSAGE =
	"Verification email sent. Check your inbox, and your spam folder if it doesn't arrive."
