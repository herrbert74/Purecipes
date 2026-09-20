# iOS release preparation (local)

Distribution CI is tag-driven: [`.github/workflows/distribute-ios.yml`](../../.github/workflows/distribute-ios.yml). Full portal + tester setup: [`docs/releases/ios-app-distribution.md`](../../docs/releases/ios-app-distribution.md).

## Versioning

`versionName` and `versionCode` live in [`gradle/libs.versions.toml`](../../gradle/libs.versions.toml). After a bump, keep iOS in sync:

```bash
./gradlew :umbrella:syncIosVersion
```

or bump both Android and the xcconfig:

```bash
kotlin scripts/release/bump_android_version.main.kts 0.2.0 true
```

Do not introduce another Kotlin version constant. Do not edit `MARKETING_VERSION` in the Xcode project by hand.

## Validation before a first TestFlight upload

1. Xcode → run Debug on a device (existing workflow).
2. Confirm the home-screen name is **Purecipes**.
3. Confirm Settings → About shows the catalog `versionName` / `versionCode`.
4. Product → Archive once locally, or rely on **Distribute iOS** after GitHub secrets from the doc are in place.
