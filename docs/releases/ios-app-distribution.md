# iOS releases (TestFlight + optional Firebase App Distribution)

This is the iOS counterpart of [`android-app-distribution.md`](android-app-distribution.md). Android testers already use **Firebase App Distribution** plus **Play closed testing**. iOS does **not** work the same way, because Apple will not install an unsigned or “just emailed” IPA on a random iPhone.

## TestFlight vs Firebase App Distribution

**Use Internal TestFlight as the main iOS tester channel.** Keep Firebase App Distribution optional.

| | Internal TestFlight | Firebase App Distribution (iOS) |
|---|---|---|
| Closest Android analogue | Play closed testing | Firebase App Tester |
| How testers install | Email invite in the TestFlight app | Firebase invite, then a profile / IPA |
| Device UDIDs | **Not needed** | **Required** (Ad Hoc: every iPhone/iPad must be registered; 100 devices / year) |
| Extra Apple review | No, for up to 100 internal testers | No |
| Path to App Store | Yes (same binary pipeline) | No |
| CI | Upload IPA with an App Store Connect API key | Upload Ad Hoc IPA with a Firebase service account |

Firebase App Distribution is enough only if **you** (and a handful of people whose devices are already in your Apple Developer account) are the testers. As soon as a friend/tester should install without sending you their device UDID, you need TestFlight.

External TestFlight (public beta, more than the internal 100) needs Apple **Beta App Review**. Skip that until you actually want a wider beta.

GitHub Actions:

- **Build iOS** (`.github/workflows/build-ios.yml`) — compiles for the iOS Simulator. No Apple secrets. Catch Kotlin/Swift/Xcode breakage.
- **Distribute iOS** (`.github/workflows/distribute-ios.yml`) — on version tags (`v*`), archives a Release IPA and uploads to **TestFlight**. Optionally also uploads the same IPA to Firebase App Distribution (Ad Hoc + registered UDIDs).

## One-time Apple / portal work (cannot be done from the repo)

Do these in a browser while signed into your Apple / Google / RevenueCat accounts. The repo is already wired to consume the results.

### 1. Apple Team ID on the VPS (Universal Links)

iOS “open `https://purecipes.app/r/…` in the app” needs Apple’s AASA file. The Ktor backend already serves it when `PURECIPES_IOS_TEAM_ID` is set. See [`docs/deep-linking-website-setup.md`](../deep-linking-website-setup.md).

1. [Apple Developer](https://developer.apple.com/account) → **Membership details** → copy **Team ID** (10 characters).
2. SSH to the VPS and append to `/etc/purecipes-backend.env`:

   ```bash
   PURECIPES_IOS_TEAM_ID=XXXXXXXXXX
   ```

3. `sudo systemctl restart purecipes-backend`
4. Check: `curl -sS https://purecipes.app/.well-known/apple-app-site-association` should return JSON containing `app.purecipes.PurecipesIOSApp`, not 404.

### 2. App Store Connect listing (no screenshots yet)

[App Store Connect](https://appstoreconnect.apple.com) → the Purecipes iOS app (SKU `purecipes`):

- Privacy policy URL: `https://purecipes.app/privacy`
- Age rating questionnaire
- App Privacy labels (what data you collect: accounts, analytics, ads, purchases)
- Category (Food & Drink)

Screenshots and a store “What’s New” can wait until a real store submission.

Export compliance: `ITSAppUsesNonExemptEncryption` is already `false` in `Info.plist`, so Apple should not ask about encryption on every upload. Change that only if you add custom non-HTTPS crypto.

### 3. App Store Connect API key (CI → TestFlight)

This is the iOS equivalent of `PLAY_SERVICE_ACCOUNT_JSON`.

1. App Store Connect → **Users and Access** → **Integrations** → **App Store Connect API**.
2. **Generate API Key** with access **App Manager** (or Admin).
3. Download the `.p8` file (shown once). Note **Key ID** and **Issuer ID**.
4. Put these in GitHub repository secrets:

   | Secret | Value |
   |--------|--------|
   | `APP_STORE_CONNECT_API_KEY_ID` | Key ID |
   | `APP_STORE_CONNECT_ISSUER_ID` | Issuer ID |
   | `APP_STORE_CONNECT_API_KEY_P8` | Full contents of the `.p8` file |
   | `PURECIPES_IOS_DEVELOPMENT_TEAM` | Same 10-character Team ID as step 1 |

Xcode on CI uses **Automatic Signing** with that API key (`-allowProvisioningUpdates`). You do **not** need to export a `.p12` certificate if this works. If archive fails on signing, export an Apple Distribution certificate from Xcode → Settings → Accounts → Manage Certificates and we can add it as a secret later.

### 4. RevenueCat App Store key

Android already uses a Play key (`goog_…`). iOS needs a **different** key (`appl_…`).

1. [RevenueCat](https://app.revenuecat.com) → the Purecipes project → **Apps**.
2. Add an **App Store** app with bundle ID `app.purecipes.PurecipesIOSApp`.
3. Copy the Apple App Store SDK key (`appl_…`).
4. GitHub secret: `PURECIPES_REVENUECAT_IOS_API_KEY`.

Local Xcode Release builds can also use Gradle property `purecipes.revenueCatApiKey.ios`.

In App Store Connect you still need the in-app products / subscription group that RevenueCat maps to. That is store configuration, not code.

### 5. AdMob iOS app and ad units

Android AdMob IDs **cannot** be reused. Create an iOS app in [AdMob](https://apps.admob.com):

1. Apps → **Add app** → iOS → bundle ID `app.purecipes.PurecipesIOSApp`.
2. Create a **Banner** ad unit and an **Interstitial** ad unit.
3. GitHub secrets (and optional local Gradle properties):

   | Secret | Gradle property |
   |--------|-----------------|
   | `PURECIPES_ADMOB_IOS_APP_ID` | `purecipes.adMobIosAppId` |
   | `PURECIPES_ADMOB_IOS_BANNER_AD_UNIT_ID` | `purecipes.adMobIosBannerAdUnitId` |
   | `PURECIPES_ADMOB_IOS_INTERSTITIAL_AD_UNIT_ID` | `purecipes.adMobIosInterstitialAdUnitId` |

Until those are set, iOS builds use Google’s **sample** iOS IDs (safe for debug; they show test ads).

Also add the iOS app in AdMob’s app settings so it is linked to the same AdMob account as Android.

### 6. Google Sign-In / Facebook bundle ID

Confirm in Google Cloud Console (OAuth iOS client) and Meta Developer (iOS platform) that the bundle ID is `app.purecipes.PurecipesIOSApp`.

There are currently **two** Google iOS client IDs:

- Xcode `GIDClientID` / reversed URL scheme: `922845075790-c274na6hroesrddmf7p3nusihvblg55f`
- Firebase `GoogleService-Info.plist` `CLIENT_ID`: `922845075790-i59pdcfupifo9fqknu04arpth6endi4v`

On a device, try Google Sign-In. If it fails with a client-id / URL-scheme mismatch, align `PURECIPES_GOOGLE_IOS_CLIENT_ID` and `PURECIPES_GOOGLE_IOS_REVERSED_CLIENT_ID` in the Xcode target with the client that actually has this bundle ID.

### 7. Internal TestFlight testers

App Store Connect → TestFlight → **Internal Testing** → add the App Store Connect users who should install. They need an Apple ID. This is like adding people to Play closed testing, except they install from the **TestFlight** app, not Play.

## Versioning (already in the repo)

`gradle/libs.versions.toml` (`versionName` / `versionCode`) is still the single source of truth. Gradle task `:umbrella:syncIosVersion` writes:

`iosApp/PurecipesIOSApp/Config/Versions.xcconfig`

Xcode reads `MARKETING_VERSION` / `CURRENT_PROJECT_VERSION` from that file (home-screen version and App Store build number). `versionCode` is the iOS **build** (`CFBundleVersion`); it must increase for every TestFlight upload, even if `versionName` stays the same.

`kotlin scripts/release/bump_android_version.main.kts <version> true` now also rewrites the xcconfig. Xcode builds run `syncIosVersion` automatically (scheme pre-action + embed script).

Do not add a second Kotlin version constant.

## Local Archive (optional, before CI secrets exist)

In Xcode: Product → Archive. Organizer → Distribute App → **App Store Connect** → Upload. That is the GUI equivalent of the distribute workflow. First upload creates the TestFlight build.

Debug runs on device keep using Automatic Signing + development push (`Config/Debug.entitlements`). Archives use production push (`Config/Release.entitlements`).

## Crashlytics dSYMs

Release archives should upload dSYMs so iOS crashes are symbolicated. A Run Script build phase calls `scripts/ios/upload-crashlytics-symbols.sh`. If a crash is still unsymbolicated, upload the archive’s `dSYMs/` folder in Firebase Console → Crashlytics → the iOS app.

Debug: `find ~/Library/Developer/Xcode/DerivedData -name 'PurecipesIOSApp.app.dSYM'`
