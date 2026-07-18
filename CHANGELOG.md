# Changelog

All notable changes to Purecipes are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [0.6.1] - 2026-07-18

### Fixed

- Monetisation debug settings now stay applied after you restart the app.
- Required ingredients filter works again when Monetisation debug is set to premium.

## [0.6.0] - 2026-07-17

### Added

- Mark pantry items as required ingredients so every search result contains them.
- See recipes missing only one ingredient when no exact matches are found.
- Free users now see banner and interstitial ads, and paywalls on some Recipe filters.
  - Only the test paywalls are enabled. You can test buying a subscription successfully or unsuccessfully, but this won't remove the paywalls or ads.
  - To remove the paywalls and ads, go to Account/Settings/Monetisation debug, and switch to Subscription/premium and Ads/Off.

### Changed

- Recipe cards are larger and use recipe images as backgrounds, with clearer titles and cuisine and measurement labels.
- Analytics improvements: made Mixpanel working and added events for Google Analytics.
- Improved Crashlytics by adding breadcrumbs to crash reports.

### Fixed

- Favourites update immediately after signing in.
- Network blocks show a friendly error instead of a raw web page.
- Removed unwanted space above screen headers.

## [0.5.0] - 2026-07-03

### Added

- Add your own ingredients in Pantry filters: type any ingredient name, see catalogue matches and typo suggestions, and use it when searching for recipes.
- Sign in with Facebook. Only works for test users added to the approved list on Facebook developer portal until business account is approved.
- About screen in Settings with the app version number.
- Open source licences list, reachable from About.

### Changed

- Debug and staging builds use a differently coloured app icon so you can tell them apart from the standard release build.

## [0.4.0] - 2026-06-20

### Added

- Exclude ingredients from search: mark ingredients as excluded in the Pantry filter tab and hide recipes that use them.
- Optional and alternative ingredients on recipes, shown in recipe details, step-by-step cooking, and when creating or editing recipes.
- Three new ingredients in the catalogue.

### Changed

- Search filters are split into Pantry and Recipe filters tabs for easier navigation.
- Pantry and recipe filter bulk actions are clearer, with select and clear actions inside each expanded section.
- Ingredient groups in filters are reordered for quicker browsing.
- Search tab uses an updated bottom bar icon.

### Fixed

- Recipe details no longer get stuck loading.
- Measurement units update correctly across the app when you change your preferences.
- Ingredient amounts and units display more consistently in recipe details and cooking steps.
- Search hint text truncates cleanly on smaller screens.
- Medium-sized titles use consistent text styling.

## [0.3.1] - 2026-06-08

### Fixed

- Android release builds now include all required app configuration, so tester updates can be distributed successfully.

## [0.3.0] - 2026-06-08

### Added

- Animated splash screen when the app starts.
- Share a recipe from recipe details with a link others can open in Purecipes.
- Share a cookbook from your favorites so others can import it.
- Deep links that open a recipe or import a shared cookbook when you tap a Purecipes link.

### Changed

- Bottom navigation keeps a separate back stack for each tab, so switching tabs restores where you left off.
- Tapping the active tab again returns to that tab's home screen.
- Android back button navigates nested screens more predictably; from other tabs' home screens it returns to Search.

### Fixed

- Favorites list updates automatically when you favorite or unfavorite a recipe elsewhere in the app.
- Search filter and cookbook share links work reliably after sign-in and when reopening the app.

## [0.2.2] - 2026-05-25

### Fixed

- Google Sign-In on distributed Android builds: release builds from CI now receive the Google Web Client ID required for sign-in.

## [0.2.1] - 2026-05-25

### Fixed

- Google Sign-In on distributed Android builds. Release 0.2.0 was built without the Google Web Client ID in CI, which blocked sign-in; CI now supplies that configuration so Google sign-in works again.
- Account screen: general error messages appear at the top so they are easier to notice.

## [0.2.0] - 2026-05-24

### Added

- Nutrition summary on recipe details when data is available (calories, protein, carbohydrates, fat, and more).
- Live nutrition estimate while creating or editing a recipe as you add ingredients.
- Reset your password if you forget your sign-in credentials.
- Delete your account from account settings.
- More ingredient categories for search and recipe filtering.
- Email verification after signing up with email.

### Changed

- Collapsible search bar on the recipe search screen so you can see more results at a glance.
- Redesigned account area with clearer layout and navigation.
- Separate sign-in and registration screens with clearer password rules and field-level error messages.
- Single display name instead of separate first and last name when signing up.
- Password fields include a show/hide toggle.

### Fixed

- Email sign-in and registration reliability, including clearer validation feedback.

## [0.1.0] - 2026-01-01

### Added

- Browse a growing recipe catalog imported from popular cooking websites.
- Sign in with email or Google and manage your account from the account tab.
- Open recipe details with ingredients, steps, and a shortcut to start cooking.
- Follow recipes step by step with manual navigation, progress, timers, and basic read-aloud for steps.
- Search recipes by text with filters for cuisine, difficulty, and cooking time.
- Filter recipes with ingredient chips and tags (diet, meal type, cooking time, and more).
- Save favorites and find them quickly in your personal list.
- Create and edit your own recipes with title, description, steps, and an optional photo.
- Choose imperial or metric measurements, with sensible defaults and recipe display options.
- Push notification support for cross-platform messaging (FCM-based).
- Privacy-conscious analytics to improve the app (with consent where required).
- First build distributed via Google Play internal testing.

### Changed

- Initial public preview on Android (internal testing track).
