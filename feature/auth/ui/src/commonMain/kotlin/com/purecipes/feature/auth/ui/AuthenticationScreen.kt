package com.purecipes.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.mmk.kmpauth.uihelper.facebook.FacebookSignInButton
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase

@Composable
fun AuthenticationScreen(
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	signInWithEmail: SignInWithEmailUseCase,
	registerWithEmail: RegisterWithEmailUseCase,
	signInWithGoogle: SignInWithGoogleUseCase,
	signOut: SignOutUseCase,
	googleWebClientId: String?,
	modifier: Modifier = Modifier,
) {
	InitializeGoogleAuthenticationProvider(googleWebClientId)
	val viewModel = authenticationViewModel(
		observeAuthenticationState = observeAuthenticationState,
		signInWithEmail = signInWithEmail,
		registerWithEmail = registerWithEmail,
		signInWithGoogle = signInWithGoogle,
		signOut = signOut,
	)
	Surface(modifier = modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(24.dp),
			verticalArrangement = Arrangement.spacedBy(20.dp),
		) {
			when (val state = viewModel.authenticationState) {
				AuthenticationState.SignedOut ->
					SignedOutAuthenticationContent(
						emailAuthenticationMode = viewModel.emailAuthenticationMode,
						isEmailFormVisible = viewModel.isEmailFormVisible,
						firstName = viewModel.firstName,
						familyName = viewModel.familyName,
						email = viewModel.email,
						password = viewModel.password,
						isBusy = viewModel.isBusy,
						isGoogleConfigured = !googleWebClientId.isNullOrBlank(),
						onEmailProviderClick = viewModel::onEmailProviderSelected,
						onEmailAuthenticationModeChange = viewModel::onEmailAuthenticationModeSelected,
						onFirstNameChange = viewModel::onFirstNameChange,
						onFamilyNameChange = viewModel::onFamilyNameChange,
						onEmailChange = viewModel::onEmailChange,
						onPasswordChange = viewModel::onPasswordChange,
						onEmailAuthenticationSubmit = viewModel::submitEmailAuthentication,
						onGoogleSignInResult = viewModel::onGoogleSignInResult,
						onGoogleUnavailableClick = viewModel::onGoogleUnavailableSelected,
						onDeferredProviderClick = viewModel::onDeferredProviderSelected,
					)

				is AuthenticationState.SignedIn ->
					SignedInAuthenticationContent(
						user = state.user,
						isBusy = viewModel.isBusy,
						onSignOut = viewModel::signOut,
					)
			}
			viewModel.message?.let { message ->
				Text(
					text = message,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error,
				)
			}
		}
	}
}

@Composable
private fun SignedOutAuthenticationContent(
	emailAuthenticationMode: EmailAuthenticationMode,
	isEmailFormVisible: Boolean,
	firstName: String,
	familyName: String,
	email: String,
	password: String,
	isBusy: Boolean,
	isGoogleConfigured: Boolean,
	onEmailProviderClick: () -> Unit,
	onEmailAuthenticationModeChange: (EmailAuthenticationMode) -> Unit,
	onFirstNameChange: (String) -> Unit,
	onFamilyNameChange: (String) -> Unit,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onEmailAuthenticationSubmit: () -> Unit,
	onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
	onGoogleUnavailableClick: () -> Unit,
	onDeferredProviderClick: (AuthProvider) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		Text(
			text = "Account",
			style = MaterialTheme.typography.headlineMedium,
		)
		Text(
			text = "Choose how you want to sign in. Email registration uses your first and family name as the display name.",
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		AuthenticationProviderButtons(
			isGoogleConfigured = isGoogleConfigured,
			onEmailProviderClick = onEmailProviderClick,
			onGoogleSignInResult = onGoogleSignInResult,
			onGoogleUnavailableClick = onGoogleUnavailableClick,
			onDeferredProviderClick = onDeferredProviderClick,
		)
		if (isEmailFormVisible) {
			EmailAuthenticationForm(
				emailAuthenticationMode = emailAuthenticationMode,
				firstName = firstName,
				familyName = familyName,
				email = email,
				password = password,
				isBusy = isBusy,
				onEmailAuthenticationModeChange = onEmailAuthenticationModeChange,
				onFirstNameChange = onFirstNameChange,
				onFamilyNameChange = onFamilyNameChange,
				onEmailChange = onEmailChange,
				onPasswordChange = onPasswordChange,
				onEmailAuthenticationSubmit = onEmailAuthenticationSubmit,
			)
		}
	}
}

@Composable
private fun AuthenticationProviderButtons(
	isGoogleConfigured: Boolean,
	onEmailProviderClick: () -> Unit,
	onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
	onGoogleUnavailableClick: () -> Unit,
	onDeferredProviderClick: (AuthProvider) -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(52.dp),
			onClick = onEmailProviderClick,
		) {
			Text(text = "Continue with email")
		}
		GoogleAuthenticationButton(
			isConfigured = isGoogleConfigured,
			onGoogleSignInResult = onGoogleSignInResult,
			onUnavailable = onGoogleUnavailableClick,
		)
		AppleSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(52.dp),
			onClick = { onDeferredProviderClick(AuthProvider.APPLE) },
		)
		FacebookSignInButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(52.dp),
			onClick = { onDeferredProviderClick(AuthProvider.FACEBOOK) },
		)
	}
}

@Composable
private fun EmailAuthenticationForm(
	emailAuthenticationMode: EmailAuthenticationMode,
	firstName: String,
	familyName: String,
	email: String,
	password: String,
	isBusy: Boolean,
	onEmailAuthenticationModeChange: (EmailAuthenticationMode) -> Unit,
	onFirstNameChange: (String) -> Unit,
	onFamilyNameChange: (String) -> Unit,
	onEmailChange: (String) -> Unit,
	onPasswordChange: (String) -> Unit,
	onEmailAuthenticationSubmit: () -> Unit,
) {
	Surface(
		modifier = Modifier.fillMaxWidth(),
		shape = MaterialTheme.shapes.large,
		tonalElevation = 2.dp,
	) {
		Column(
			modifier = Modifier.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				OutlinedButton(
					modifier = Modifier.weight(1f),
					onClick = { onEmailAuthenticationModeChange(EmailAuthenticationMode.SIGN_IN) },
				) {
					Text(text = "Sign in")
				}
				Button(
					modifier = Modifier.weight(1f),
					onClick = { onEmailAuthenticationModeChange(EmailAuthenticationMode.REGISTER) },
					enabled = emailAuthenticationMode != EmailAuthenticationMode.REGISTER,
				) {
					Text(text = "Register")
				}
			}
			if (emailAuthenticationMode == EmailAuthenticationMode.REGISTER) {
				OutlinedTextField(
					value = firstName,
					onValueChange = onFirstNameChange,
					modifier = Modifier.fillMaxWidth(),
					label = { Text("First name") },
					singleLine = true,
				)
				OutlinedTextField(
					value = familyName,
					onValueChange = onFamilyNameChange,
					modifier = Modifier.fillMaxWidth(),
					label = { Text("Family name") },
					singleLine = true,
				)
			}
			OutlinedTextField(
				value = email,
				onValueChange = onEmailChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Email") },
				singleLine = true,
			)
			OutlinedTextField(
				value = password,
				onValueChange = onPasswordChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text("Password") },
				singleLine = true,
				visualTransformation = PasswordVisualTransformation(),
			)
			Button(
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp),
				onClick = onEmailAuthenticationSubmit,
				enabled = !isBusy,
			) {
				if (isBusy) {
					CircularProgressIndicator(
						modifier = Modifier.size(18.dp),
						strokeWidth = 2.dp,
					)
				} else {
					Text(
						text = if (emailAuthenticationMode == EmailAuthenticationMode.REGISTER) {
							"Create account"
						} else {
							"Sign in"
						},
					)
				}
			}
		}
	}
}

@Composable
private fun SignedInAuthenticationContent(
	user: AuthUser,
	isBusy: Boolean,
	onSignOut: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
		Text(
			text = "Profile",
			style = MaterialTheme.typography.headlineMedium,
		)
		ProfileHeader(user = user)
		HorizontalDivider()
		Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
			AssistChip(
				onClick = {},
				label = { Text(text = user.provider.name.lowercase().replaceFirstChar { it.titlecase() }) },
			)
		}
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = onSignOut,
			enabled = !isBusy,
		) {
			if (isBusy) {
				CircularProgressIndicator(
					modifier = Modifier.size(18.dp),
					strokeWidth = 2.dp,
				)
			} else {
				Text(text = "Sign out")
			}
		}
	}
}

@Composable
private fun ProfileHeader(user: AuthUser) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
	) {
		ProfileAvatar(user = user)
		Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
			Text(
				text = user.displayName,
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.SemiBold,
			)
			Text(
				text = user.email,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Composable
private fun ProfileAvatar(user: AuthUser) {
	val initials = remember(user.displayName) {
		user.displayName
			.split(' ')
			.filter { it.isNotBlank() }
			.take(2)
			.joinToString(separator = "") { it.first().uppercase() }
			.ifBlank { user.email.take(1).uppercase() }
	}
	if (user.profileImageUrl != null) {
		AsyncImage(
			model = user.profileImageUrl,
			contentDescription = user.displayName,
			modifier = Modifier
				.size(88.dp)
				.clip(CircleShape),
			contentScale = ContentScale.Crop,
		)
	} else {
		Box(
			modifier = Modifier
				.size(88.dp)
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.primaryContainer),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = initials,
				style = MaterialTheme.typography.headlineSmall,
				color = MaterialTheme.colorScheme.onPrimaryContainer,
				textAlign = TextAlign.Center,
			)
		}
	}
}
