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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.feature.analytics.domain.model.toDisplayText
import com.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import com.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.purecipes.shared.ui.component.ErrorText
import com.purecipes.shared.ui.component.PurecipesButtonDefaults
import com.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun AuthenticationScreen(
	observeConsentState: ObserveConsentStateUseCase,
	observeAuthenticationState: ObserveAuthenticationStateUseCase,
	signInWithEmail: SignInWithEmailUseCase,
	registerWithEmail: RegisterWithEmailUseCase,
	signInWithExternalProvider: SignInWithExternalProviderUseCase,
	signInWithGoogle: SignInWithGoogleUseCase,
	showConsentForm: ShowConsentFormUseCase,
	signOut: SignOutUseCase,
	onOpenSettings: () -> Unit,
	googleWebClientId: String?,
	modifier: Modifier = Modifier,
) {
	InitializeGoogleAuthenticationProvider(googleWebClientId)
	val consentState by observeConsentState().collectAsState()
	val viewModel = authenticationViewModel(
		observeAuthenticationState = observeAuthenticationState,
		signInWithEmail = signInWithEmail,
		registerWithEmail = registerWithEmail,
		signInWithExternalProvider = signInWithExternalProvider,
		signInWithGoogle = signInWithGoogle,
		signOut = signOut,
	)
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Account") },
				actions = {
					IconButton(onClick = onOpenSettings) {
						Icon(
							imageVector = Icons.Filled.Settings,
							contentDescription = "Open settings",
						)
					}
				},
			)
		},
	) { innerPadding ->
		Surface(modifier = Modifier.fillMaxSize()) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState())
					.padding(innerPadding)
					.padding(PurecipesTheme.space.l),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			) {
				when (val state = viewModel.authenticationState) {
					AuthenticationState.SignedOut ->
						SignedOutAuthenticationContent(
							consentState = consentState,
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
							onExternalProviderSignInResult = viewModel::onExternalProviderSignInResult,
							onGoogleSignInResult = viewModel::onGoogleSignInResult,
							onGoogleUnavailableClick = viewModel::onGoogleUnavailableSelected,
							onManagePrivacySettings = { showConsentForm() },
						)

					is AuthenticationState.SignedIn ->
						SignedInAuthenticationContent(
							consentState = consentState,
							user = state.user,
							isBusy = viewModel.isBusy,
							onManagePrivacySettings = { showConsentForm() },
							onSignOut = viewModel::signOut,
						)
				}
				viewModel.message?.let { message ->
					ErrorText(text = message)
				}
			}
		}
	}
}

@Composable
private fun SignedOutAuthenticationContent(
	consentState: ConsentState,
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
	onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
	onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
	onManagePrivacySettings: () -> Unit,
	onGoogleUnavailableClick: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m)) {
		Text(
			text = """Choose how you want to sign in.
				| Email registration uses your first and family name as the display name.
			""".trimIndent(),
			style = PurecipesTheme.typography.bodyLarge,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		AuthenticationProviderButtons(
			isGoogleConfigured = isGoogleConfigured,
			onEmailProviderClick = onEmailProviderClick,
			onExternalProviderSignInResult = onExternalProviderSignInResult,
			onGoogleSignInResult = onGoogleSignInResult,
			onGoogleUnavailableClick = onGoogleUnavailableClick,
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
		HorizontalDivider()
		PrivacySettingsContent(
			consentState = consentState,
			onManagePrivacySettings = onManagePrivacySettings,
		)
	}
}

@Composable
private fun AuthenticationProviderButtons(
	isGoogleConfigured: Boolean,
	onEmailProviderClick: () -> Unit,
	onExternalProviderSignInResult: (AuthProvider, Result<ExternalAuthenticationProfile?>) -> Unit,
	onGoogleSignInResult: (String?, String?, String, String?) -> Unit,
	onGoogleUnavailableClick: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		FilledTonalButton(
			modifier = Modifier
				.fillMaxWidth()
				.height(PurecipesButtonDefaults.providerButtonHeight),
			onClick = onEmailProviderClick,
		) {
			Text(text = "Continue with email")
		}
		GoogleAuthenticationButton(
			isConfigured = isGoogleConfigured,
			onGoogleSignInResult = onGoogleSignInResult,
			onUnavailable = onGoogleUnavailableClick,
		)
		AppleAuthenticationButton(
			onResult = { result -> onExternalProviderSignInResult(AuthProvider.APPLE, result) },
		)
		FacebookAuthenticationButton(
			onResult = { result -> onExternalProviderSignInResult(AuthProvider.FACEBOOK, result) },
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
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			Row(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
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
					.height(PurecipesTheme.space.xxl),
				onClick = onEmailAuthenticationSubmit,
				enabled = !isBusy,
			) {
				if (isBusy) {
					CircularProgressIndicator(
						modifier = Modifier.size(PurecipesTheme.space.m),
						strokeWidth = PurecipesTheme.space.quark,
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
	consentState: ConsentState,
	user: AuthUser,
	isBusy: Boolean,
	onManagePrivacySettings: () -> Unit,
	onSignOut: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m)) {
		ProfileHeader(user = user)
		HorizontalDivider()
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
			AssistChip(
				onClick = {},
				label = { Text(text = user.provider.name.lowercase().replaceFirstChar { it.titlecase() }) },
			)
		}
		HorizontalDivider()
		PrivacySettingsContent(
			consentState = consentState,
			onManagePrivacySettings = onManagePrivacySettings,
		)
		Button(
			modifier = Modifier.fillMaxWidth(),
			onClick = onSignOut,
			enabled = !isBusy,
		) {
			if (isBusy) {
				CircularProgressIndicator(
					modifier = Modifier.size(PurecipesTheme.space.m),
					strokeWidth = PurecipesTheme.space.quark,
				)
			} else {
				Text(text = "Sign out")
			}
		}
	}
}

@Composable
private fun PrivacySettingsContent(
	consentState: ConsentState,
	onManagePrivacySettings: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
		Text(
			text = "Privacy",
			style = PurecipesTheme.typography.titleLarge,
		)
		Text(
			text = consentState.toDisplayText(),
			style = PurecipesTheme.typography.bodyLarge,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		OutlinedButton(
			modifier = Modifier.fillMaxWidth(),
			onClick = onManagePrivacySettings,
		) {
			Text(text = "Manage privacy settings")
		}
	}
}

@Composable
private fun ProfileHeader(user: AuthUser) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		ProfileAvatar(user = user)
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
			Text(
				text = user.displayName,
				style = PurecipesTheme.typography.headlineSmall,
				fontWeight = FontWeight.SemiBold,
			)
			Text(
				text = user.email,
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
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
				.background(PurecipesTheme.colorScheme.primaryContainer),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = initials,
				style = PurecipesTheme.typography.headlineSmall,
				color = PurecipesTheme.colorScheme.onPrimaryContainer,
				textAlign = TextAlign.Center,
			)
		}
	}
