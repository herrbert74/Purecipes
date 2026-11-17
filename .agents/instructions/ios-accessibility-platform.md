# iOS accessibility platform integration

Compose Multiplatform (1.12.2) maps Compose semantics to UIKit accessibility. Platform bridges used by Purecipes:

## ComposeUIViewController

Configured in `umbrella/.../RecipeSearchViewControllerFactory.kt`.

- `enforceStrictPlistSanityCheck = false` — required for this app’s Info.plist setup.
- Accessibility tree sync is lazy when Accessibility Services run; `AccessibilitySyncOptions` is no longer configured (removed from CMP’s public configuration path after 1.8).

## VoiceOver escape

Escape is implemented by CMP as a synthetic `Key.Escape`, which `BackNavigationEventInput` turns into a completed back navigation event. Sheets/dialogs with `dismissOnBackPress` dismiss first; `HandleSystemBack` then pops in-app destinations.

## VoiceOver focus

Destination headers use `headingSemantics()` (#403). Tab and navigation depth changes post `UIAccessibilityScreenChangedNotification` via `PlatformNavigationHistorySync` (`screenChangeKey`) so focus is not trapped on removed UI or a previous tab.

## Dynamic Type

CMP reads `preferredContentSizeCategory` into `Density.fontScale` when a scene is created. Live changes while the app is open are applied by `ProvideDynamicTypeFontScale` (observes `UIContentSizeCategoryDidChangeNotification` and overrides `LocalDensity.fontScale`). Remove that bridge after upgrading past the CMP release that lands live Dynamic Type updates (#3306 / 1.13+).

Per-screen large-text layout checks remain in #404.
