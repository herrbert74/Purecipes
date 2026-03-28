# Multi-Platform Authentication

## Status: <span style="color:green;">🟢 ACCEPTED</span>

## Feature Overview
Implement secure, seamless authentication across all platforms using KMPAuth for consistent user experience. Support Google and Facebook authentication on all platforms, with Apple Sign-In specifically for iOS.

## Current Implementation Snapshot
- Authentication currently lives in the account tab, not in onboarding yet.
- The shared auth feature is split into domain, data, and ui modules following the project feature layering rules.
- Email authentication is implemented locally in memory so the flow is testable before backend and Firebase setup exists.
- Email registration requires first name and family name, and the UI uses those values as the display name.
- Logged-in users see a profile image, display name, email address, provider, and a sign-out action.
- Google uses KMPAuth where the platform setup is available.
- Apple and Facebook are visible in the UI on all platforms, but they currently show a deferred-setup message until the Firebase-backed provider configuration is added.
- iOS Google is also shown in the UI, but it stays unavailable until the native Google Sign-In dependency is wired into the iOS app.
- There is no separate profile-name field at this stage. The app uses first name plus family name for email accounts, or the provider display name for Google accounts.

## Google Sign-In Setup Summary

### What the app expects today
- The current implementation reads `googleWebClientId()` from `PurecipesConfig`.
- That value is now populated from one of these inputs, in this order:
    1. Gradle property `purecipes.googleWebClientId`
    2. Gradle property `PURECIPES_GOOGLE_WEB_CLIENT_ID`
    3. Environment variable `PURECIPES_GOOGLE_WEB_CLIENT_ID`
- The value is compiled into Android `BuildConfig` and umbrella `BuildKonfig`, then exposed to Android, iOS, and Wasm through the shared config interface.

### What is secret and what is not
- The Google web client ID is not a secret. It is safe to compile into the app.
- A Google OAuth client secret must never be shipped in the app.
- Firebase web `apiKey` is also not a secret. It identifies the Firebase app, but it does not grant privileged backend access by itself.
- Private service credentials must stay on the backend or in CI secrets, not in the client app.

### Recommended local configuration
- Add the client ID to your user Gradle properties file or an uncommitted project override.
- Example in `~/.gradle/gradle.properties`:

```properties
purecipes.googleWebClientId=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

- You can also export it as an environment variable:

```bash
export PURECIPES_GOOGLE_WEB_CLIENT_ID=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

### Do you need Firebase for the current Google button?
- Not necessarily for the current staged Android and Wasm Google flow. The current button only needs a valid Google web client ID.
- You do need Firebase if you want one unified authentication backend, Firebase Auth session handling, or the KMPAuth Apple and Facebook flows that rely on Firebase-backed setup.

## Firebase Connection Checklist

### 1. Create the Firebase project
1. Create a Firebase project for Purecipes.
2. Enable Authentication.
3. Enable the Google sign-in provider in Firebase Authentication.

### 2. Register apps in Firebase
1. Add the Android app with package name `com.purecipes`.
2. Add the iOS app with the final iOS bundle identifier you plan to ship.
3. Add a Web app for Wasm/browser usage.

### 3. Collect the platform config artifacts
- Android Firebase SDK setup uses `google-services.json`.
- iOS Firebase SDK setup uses `GoogleService-Info.plist`.
- Web Firebase SDK setup uses the Firebase web config object with values like `apiKey`, `authDomain`, `projectId`, and `appId`.

### 4. Decide which integration path you want

#### Path A: Keep the current staged Google flow first
- Set `purecipes.googleWebClientId`.
- Validate Android and Wasm sign-in.
- Postpone Firebase Auth session exchange until later.

#### Path B: Move to Firebase-backed auth
- Add Firebase SDK wiring per platform.
- Exchange the Google sign-in result for a Firebase credential.
- Persist the authenticated Firebase user/session instead of the current in-memory user state.
- This path also lines up with enabling Apple and Facebook later.

## What still needs to be implemented for full Firebase-backed auth

### Android
- If you use Firebase SDK directly, add `google-services.json` and the Google services Gradle plugin.
- If you stay on the current staged path, the client ID property is enough for now.

### iOS
- Add the native Google Sign-In dependency.
- Add Firebase iOS SDK if you want Firebase Auth-backed sessions.
- Add `GoogleService-Info.plist` if Firebase SDK is used.
- Update the iOS actual Google button from unavailable to live once the native dependency is present.
- For native Google Sign-In, the iOS app must forward callback URLs to `GIDSignIn.sharedInstance.handle(url)`.
- The iOS app must declare the reversed iOS client ID in `CFBundleURLTypes`.
- `GoogleService-Info.plist` is the easiest source for the iOS `CLIENT_ID` and `REVERSED_CLIENT_ID` values.
- The current blocker in this repository is native SDK visibility, not Firebase project setup: the Kotlin iOS target also needs access to the Google Sign-In SDK before the existing Kotlin `iosMain` auth button can be turned on.
- Adding the Swift package only to the Xcode app target is not enough on its own, because the shared Kotlin iOS framework is linked by Gradle before the Xcode app target is built.

### Wasm/Web
- If you use Firebase Auth on web, provide the Firebase web config object.
- The current staged path still only needs the Google web client ID.

## Recommended order of work
1. Create the Google/Firebase project and obtain the web client ID.
2. Set `purecipes.googleWebClientId` locally and verify Android plus Wasm Google sign-in.
3. Decide whether you want Firebase Auth as the real source of truth now, or later together with Apple and Facebook.
4. If yes, add Firebase SDK setup for Android, iOS, and web, then replace the in-memory auth state with Firebase-backed session handling.

## User Story
As a user, I want to sign in quickly and securely using my existing social accounts so I can access my recipes and preferences across all my devices.

## Core Functionality
- **Social Authentication**: Google, Facebook on all platforms
- **Apple Sign-In**: iOS-specific Apple authentication
- **Cross-Platform Sync**: Seamless authentication state across Android, iOS, and Web
- **Secure Token Management**: JWT token handling and refresh
- **Offline Access**: Cached authentication for offline usage

## Technical Implementation

### KMPAuth Integration
- **Library**: KMPAuth (https://klibs.io/project/mirzemehdi/KMPAuth)
- **Platform Support**: Unified API across Android, iOS, and Web
- **Token Management**: Secure storage and automatic refresh
- **Session Persistence**: Cross-platform session synchronization

### Platform-Specific Implementation

#### Android
- **Google Sign-In**: Google Identity Services SDK
- **Facebook Login**: Facebook SDK for Android
- **Secure Storage**: Android Keystore for token storage
- **Biometric Auth**: Fingerprint/Face unlock support

#### iOS
- **Google Sign-In**: Google Sign-In SDK for iOS
- **Facebook Login**: Facebook SDK for iOS
- **Apple Sign-In**: AuthenticationServices framework
- **Keychain**: Secure token storage in iOS Keychain

#### Web (Wasm)
- **Google OAuth 2.0**: Web-based Google authentication
- **Facebook Login**: Facebook JavaScript SDK
- **Token Storage**: Secure browser storage (httpOnly cookies)
- **Session Management**: Cross-tab authentication state

### Security Features
- **OAuth 2.0**: Industry-standard authentication flow
- **PKCE**: Proof Key for Code Exchange for enhanced security
- **Token Encryption**: Encrypted token storage
- **Session Timeout**: Configurable session expiration
- **Revocation Handling**: Token revocation and logout

## User Experience Flow

### Registration/Login
1. User opens app and sees login screen
2. Selects preferred authentication provider
3. Redirects to provider's authentication flow
4. User grants permissions
5. Returns to app with authenticated session
6. Profile creation/initial setup

### Cross-Platform Experience
1. User logs in on Android device
2. Authentication state syncs to backend
3. User opens iOS app
4. Automatically authenticated via synced session
5. Consistent experience across all platforms

### Session Management
1. Automatic token refresh in background
2. Secure logout across all devices
3. Session recovery after app reinstall
4. Offline authentication with cached tokens

## Platform Considerations

### KMP Shared Module
```kotlin
// Common authentication interface
expect class AuthManager() {
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signInWithFacebook(): Result<User>
    suspend fun signInWithApple(): Result<User> // iOS only
    suspend fun signOut()
    val currentUser: Flow<User?>
}

// User data model
data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatar: String?,
    val provider: AuthProvider
)

enum class AuthProvider {
    GOOGLE, FACEBOOK, APPLE
}
```

### Backend Integration
- **JWT Validation**: Server-side token verification
- **User Profile Management**: Centralized user data storage
- **Session Management**: Redis-based session storage
- **API Security**: Authenticated API endpoints

## Success Metrics
- **Authentication Success Rate**: >98% successful logins
- **Cross-Platform Sync Success**: >95% session synchronization
- **Time to Authenticate**: <3 seconds average login time
- **User Satisfaction**: >4.5 rating for authentication experience
- **Security Incidents**: Zero security breaches

## Dependencies
- **KMPAuth**: Primary authentication library
- **Platform SDKs**: Google, Facebook, Apple SDKs
- **Backend Services**: JWT validation and user management
- **Secure Storage**: Platform-specific secure storage

## Privacy and Compliance
- **GDPR Compliance**: User consent and data protection
- **Data Minimization**: Collect only necessary user data
- **Transparency**: Clear privacy policy and permissions
- **User Control**: Easy account deletion and data export

## Potential Challenges
- **Platform SDK Differences**: Varying SDK capabilities across platforms
- **Token Management**: Secure cross-platform token synchronization
- **User Experience**: Consistent authentication flow across platforms
- **Security Requirements**: Meeting platform-specific security standards

## Monetization Impact
- **User Acquisition**: Reduced friction for user registration
- **Premium Features**: Authentication required for premium features
- **Data Analytics**: User behavior tracking with proper consent
- **Partnership Opportunities**: Integration with brand partners

## Current Status: ACCEPTED
Reason: Authentication is a critical must-have feature for Phase 1. KMPAuth provides the cross-platform consistency needed for Purecipes' multi-platform strategy. Social authentication reduces user friction and improves conversion rates.

## Implementation Priority
- **Phase 1**: Core authentication implementation
- **Phase 2**: Enhanced security features
- **Phase 3**: Advanced authentication options (biometric, etc.)
- **Phase 4**: Enterprise authentication options

## Testing Strategy
- **Unit Tests**: Authentication logic validation
- **Integration Tests**: Cross-platform authentication flow
- **Security Tests**: Token security and session management
- **User Experience Tests**: Authentication user journey
- **Performance Tests**: Authentication speed and reliability

## Validation Status
- `./gradlew detektAll`
- `./gradlew :feature:auth:domain:iosSimulatorArm64Test :feature:auth:data:iosSimulatorArm64Test :feature:auth:ui:iosSimulatorArm64Test`
- `./gradlew :app:assembleDebug :umbrella:linkDebugFrameworkIosSimulatorArm64`

These checks currently pass for the staged implementation above.
