# Changelog

All notable changes to Purecipes are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

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
