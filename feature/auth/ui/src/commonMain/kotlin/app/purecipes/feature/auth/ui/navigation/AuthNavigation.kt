package app.purecipes.feature.auth.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.auth.ui.authentication.AuthenticationScreen
import app.purecipes.feature.auth.ui.registration.RegistrationScreen
import app.purecipes.feature.auth.ui.signin.SignInScreen
import app.purecipes.shared.ui.navigation.Navigator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

inline fun EntryProviderScope<NavKey>.installAuthFlow(
	navigator: Navigator,
	googleWebClientId: String?,
	noinline onOpenSettings: () -> Unit,
	noinline onNavigateToEmailRegistration: () -> Unit,
	noinline onNavigateToSignIn: () -> Unit,
	noinline onRegistrationSuccess: (String) -> Unit,
) {
	entry<AccountDestination> {
		AuthenticationScreen(
			onOpenSettings = onOpenSettings,
			onNavigateToEmailRegistration = onNavigateToEmailRegistration,
			onNavigateToSignIn = onNavigateToSignIn,
			googleWebClientId = googleWebClientId,
			modifier = Modifier.fillMaxSize(),
		)
	}
	entry<EmailRegistrationDestination> {
		RegistrationScreen(
			onBack = { navigator.back() },
			onRegistrationSuccess = onRegistrationSuccess,
			modifier = Modifier.fillMaxSize(),
		)
	}
	entry<EmailSignInDestination> { destination ->
		SignInScreen(
			initialEmail = destination.prefilledEmail,
			showRegistrationSuccessMessage = destination.showRegistrationSuccessMessage,
			onBack = { navigator.back() },
			modifier = Modifier.fillMaxSize(),
		)
	}
}

fun authNavigationSerializersModule(): SerializersModule = SerializersModule {
	polymorphic(baseClass = NavKey::class) {
		subclass(AccountDestination.serializer())
		subclass(EmailRegistrationDestination.serializer())
		subclass(EmailSignInDestination.serializer())
	}
}
