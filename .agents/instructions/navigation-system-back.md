# System and platform back navigation

## Android

`MainScreen` registers `HandleSystemBack` for the full navigation stack. Hardware back calls `MainViewModel.onBack()`; when that returns `false` at the search tab root, `MainActivity` finishes the app.

Nested destinations are popped via `Navigator.back()` on the shared `NavBackStack`. In-UI back controls (`BackNavigationButton`, toolbar buttons) call the same `navigator.back()` path through feature `onBack` callbacks.

`NavDisplay` `onBack` is wired for navigation3 predictive back integration; system back is handled by `HandleSystemBack`, not duplicate `BackHandler` registrations elsewhere.

## Wasm

`HandleSystemBack` listens for browser `popstate` and invokes the same `onBack` callback as Android.

`PlatformNavigationHistorySync` pushes a `history` entry when the back stack depth increases so the browser back button can pop in-app destinations first.

Browser forward is not wired: there is no `Navigator.forward()`. In-app toolbar back does not call `history.back()`, so the browser history stack can diverge until the user uses browser back.

## iOS

`HandleSystemBack` is a no-op. Swipe-from-edge and navigation bar back are provided by the hosting `ComposeUIViewController` / UIKit integration with the Compose navigation stack.
