# Recipe Sharing & Deep Linking

## Status: <span style="color:orange;">🟠 DRAFT</span>

## Feature Overview
Allow users to share recipes from inside the app via system share sheets, and let inbound links open directly on the corresponding recipe (or cooking session) on Android, iOS, and Web, with a graceful web fallback when the native app is not installed.

## User Story
As a user, I want to share a recipe link with a friend through any messaging app and have it open straight to that recipe in Purecipes, so I can recommend recipes without friction.

## Core Functionality
- **Share Action**: System share sheet integration from recipe and cooking-step screens
- **Deep Links**: Universal Links (iOS) and App Links (Android) for recipe and cooking-session URLs
- **Web Fallback**: The same canonical URL renders the recipe in the Wasm web app when the native app is not installed
- **Deferred Deep Links**: After install, the user is routed to the originally shared recipe
- **Rich Previews**: Open Graph / Twitter Card metadata for share-target previews
- **Copy Link**: One-tap copy of the canonical recipe URL

## Technical Implementation
- **Canonical URLs**: `https://purecipes.app/r/{recipeId}` and `/s/{sessionId}` handled by all three clients
- **Routing**: Hook into the Jetpack Navigation graph in `umbrella` so the same routes resolve on Android and iOS
- **Android**: App Links with `assetlinks.json` hosted by the backend
- **iOS**: Universal Links with `apple-app-site-association` hosted by the backend
- **Web**: SSR-friendly metadata so previews render even before Wasm boots
- **Backend**: Endpoints serving link-verification files and share-preview metadata

## Platform Considerations
- **Android**: `ShareCompat.IntentBuilder` plus App Links intent filters
- **iOS**: `UIActivityViewController` plus Universal Link handling
- **Web (Wasm)**: `navigator.share` where available, with copy-to-clipboard fallback
- **Sync**: Shared IDs must resolve regardless of which device created the recipe (depends on cloud sync)

## Success Metrics
- 20%+ of monthly active users share at least one recipe
- 30%+ click-through rate on shared links opening the recipe screen
- <1 second deep-link → recipe screen time on a warm app
- Deferred deep-link recovery success rate >90% after install

## Dependencies
- Feature 002 (authentication) for personalised links and saved state
- Feature 008 (analytics) for share-funnel tracking
- Cloud sync of recipes so shared IDs resolve cross-device

## Potential Challenges
- Keeping a single source of truth for routes across Android, iOS, and Wasm
- Verifying `assetlinks.json` / `apple-app-site-association` in CI to avoid silent breakage
- Permission model for private vs public recipes when shared
- Deferred deep-link reliability on iOS

## Privacy Considerations
- Default to public-recipe sharing; gate user-uploaded recipes behind explicit consent
- Strip personally identifying information from preview metadata
- Honour GDPR right-to-be-forgotten by invalidating shared links for deleted recipes
