# Launching Animation

## Status: <span style="color:orange;">🟠 DRAFT</span>

## Feature Overview
Implement a custom splash animation to hide the initial loading time and provide a continuous, engaging transition into the app. This feature will improve upon the standard Android splash screen implementation by utilizing a specialized third-party Compose library, incorporating one or more animations based on the current launcher icon.

## User Story
As a user, I want to see a smooth, engaging animation when opening the app, so that the initial loading time feels shorter and the application provides a premium, polished experience from the very first interaction.

## Core Functionality
- **Custom Splash Animation**: A fluid animation sequence based on the app's launcher icon.
- **Continuous Transition**: Seamless handover from the native splash screen to the first app screen without flickering.
- **Platform Support**: Primary focus on Android, with consideration for multiplatform extensions if applicable.
- **Performance**: Lightweight animation that does not block or delay the actual app initialization.

## Technical Implementation
- **Library Integration**: Utilize [kibotu/androidx-splashscreen-compose](https://github.com/kibotu/androidx-splashscreen-compose) to improve upon the standard `androidx.core:core-splashscreen` API.
- **Animation Assets**: Create vector or Lottie-based animations derived from the launcher icon.
- **State Management**: Keep the splash screen visible until the initial data (e.g., user preferences, authentication state) is loaded.
- **Android Limitations**: The standard [Android Splash Screen API](https://developer.android.com/develop/ui/views/launch/splash-screen) has constraints with continuous animations into Compose, which the selected third-party library helps mitigate.

## Success Metrics
- 0 visible flickers during app launch.
- Perceived app load time reduced according to user feedback.
- Crash-free sessions during the launch phase remain >99.9%.

## Dependencies
- Initial app loading logic (Authentication, Database initialization).
- Design assets for the launcher icon animation.
