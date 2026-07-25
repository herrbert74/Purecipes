# Multi-Platform Authentication

## Status: <span style="color:green;">🟢 ACCEPTED</span>

## Feature Overview
Implement secure, seamless authentication across all platforms using KMPAuth for consistent user experience. Support Google and Facebook authentication on all platforms, with Apple Sign-In specifically for iOS.

## Current Implementation Snapshot
- Authentication lives in the account tab.
- The shared auth feature is split into domain, data, and ui modules.
- **Email** and **Google** sign-in exchange a Firebase ID token with the backend and receive a bearer session.
- **Facebook** uses the same backend exchange (`POST /auth/facebook`) after KMPAuth + Firebase sign-in. See [Facebook login setup](../auth/facebook-login-setup.md) for Development-mode limits, adding testers, and Meta review requirements.
- **Apple** sign-in updates local auth state only (no backend session yet).
- Logged-in users see profile info, provider, and sign-out on the Account screen.

For Google client ID setup, Firebase checklist, and legacy staged-flow notes, see the sections below (some are outdated relative to the snapshot above).

## Account Deletion

In-app deletion runs in this order so a partial failure stays retryable:

1. The Firebase identity is deleted first. Firebase may reject this with a recent-login error, and nothing has changed yet at that point.
2. The backend account is deleted with `DELETE /auth/account` while the bearer session is still valid. The backend runs one transaction that reassigns the user's recipes to the reserved system owner (provider `SYSTEM`, external id `orphaned-recipes`, display name `Purecipes`) and then deletes the `app_users` row, so foreign keys cascade through sessions, favourites, cookbooks, shares, pantry, search filters, excluded ingredients, and measurement preferences.
3. Local auth and session state is cleared only after the backend call succeeds. If the backend call fails, the local session is kept so the user can retry; the already-deleted Firebase identity makes step 1 a no-op on retry.

Retained recipes stay available to other users, but they are no longer associated with the deleted account.

### Handling verified email deletion requests

[`docs/legal/delete-account.html`](../legal/delete-account.html) tells users who cannot use the app to email a deletion request. After verifying that the requester controls the account email, run the admin tool from the repository root. It defaults to a dry run:

```bash
./gradlew :backend:deleteAccount -PdeleteAccount.email=user@example.com
```

The dry run prints the resolved account and how much data would be deleted or reassigned. If one email matches several providers, narrow it with `-PdeleteAccount.provider=EMAIL` or pick a specific account with `-PdeleteAccount.userId=123`. Add the execute flag to delete:

```bash
./gradlew :backend:deleteAccount -PdeleteAccount.userId=123 -PdeleteAccount.execute=true
```

The tool refuses to delete the reserved `Purecipes` recipe owner. The backend has no Firebase Admin credentials, so afterwards delete the Firebase Authentication user manually in the Firebase Console; the tool prints this reminder.

## Google Sign-In Setup Summary

### What the app expects today
- The current implementation reads `googleWebClientId()` from `PurecipesConfig`.
- On Android, that value is now build-type specific so debug builds use the **purecipes-debug** Firebase web client ID and release builds use **purecipes-50e5c**:
    1. Gradle property `purecipes.googleWebClientId.<buildType>` (for example `.debug` / `.release`)
    2. For non-debug builds only: `purecipes.googleWebClientId`, `PURECIPES_GOOGLE_WEB_CLIENT_ID`, or the env var of the same name
    3. Built-in defaults matching `client_type` 3 in `app/src/<buildType>/google-services.json`
- Debug ignores the legacy single `purecipes.googleWebClientId` on purpose: that value is the production web client and breaks Google Sign-In against `purecipes-debug`.
- iOS and Wasm still read the shared umbrella BuildKonfig web client ID (production Firebase).

### Debug Firebase checklist (Android)
After switching debug analytics to `purecipes-debug`, Google Sign-In needs the debug project fully set up:

1. Firebase Console → **purecipes-debug** → Authentication → enable the **Google** provider.
2. Project settings → Android app `app.purecipes.debug` → add the debug keystore SHA-1 (`FA:09:90:0A:96:65:48:9A:9A:6B:70:A5:D9:E7:3F:D2:16:5F:CA:55`).
3. Re-download `google-services.json` into `app/src/debug/google-services.json` (it should gain a `client_type` 1 Android OAuth client with that certificate hash).
4. Rebuild the debug app so `BuildConfig.PURECIPES_GOOGLE_WEB_CLIENT_ID` matches the debug web client (`740437012648-…`).

Local `:backend:run` trusts Firebase projects resolved for **debug**, **release**, and **staging** (defaults: `purecipes-debug` + `purecipes-50e5c`). Packaged/production backends embed the **release** project only; keep CI/production on `PURECIPES_FIREBASE_PROJECT_ID=purecipes-50e5c` (or `purecipes.firebaseProjectId.release`).

### Recommended local configuration
- Optional overrides in `~/.gradle/gradle.properties`:

```properties
purecipes.googleWebClientId.debug=740437012648-ujd18e6l3pn7co7nslloofr9fvqq08mm.apps.googleusercontent.com
purecipes.googleWebClientId.release=922845075790-aiom7ev08u8uamcrlt9714kfmfumked7.apps.googleusercontent.com
purecipes.firebaseProjectId.debug=purecipes-debug
purecipes.firebaseProjectId.release=purecipes-50e5c
purecipes.firebaseProjectNumber.debug=740437012648
purecipes.firebaseProjectNumber.release=922845075790
```

- Release/CI can keep a single env/secret (used as the non-debug fallback):

```bash
export PURECIPES_GOOGLE_WEB_CLIENT_ID=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
export PURECIPES_FIREBASE_PROJECT_ID=purecipes-50e5c
export PURECIPES_FIREBASE_PROJECT_NUMBER=922845075790
```

### What is secret and what is not
- The Google web client ID is not a secret. It is safe to compile into the app.
- A Google OAuth client secret must never be shipped in the app.
- Firebase web `apiKey` is also not a secret. It identifies the Firebase app, but it does not grant privileged backend access by itself.
- Private service credentials must stay on the backend or in CI secrets, not in the client app.

### Do you need Firebase for the current Google button?
- Not necessarily for the current staged Android and Wasm Google flow. The current button only needs a valid Google web client ID.
- You do need Firebase if you want one unified authentication backend, Firebase Auth session handling, or the KMPAuth Apple and Facebook flows that rely on Firebase-backed setup.

## Firebase Connection Checklist

### 1. Create the Firebase project
1. Create a Firebase project for Purecipes.
2. Enable Authentication.
3. Enable the Google sign-in provider in Firebase Authentication.

### 2. Register apps in Firebase
1. Add the Android app with package name `app.purecipes`.
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
