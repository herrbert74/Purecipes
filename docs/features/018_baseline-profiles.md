# Baseline Profiles and Benchmarking

## Status: <span style="color:orange;">🟠 DRAFT</span>

## Feature Overview
Implement Baseline Profiles and Macrobenchmark to measure and improve the startup time and runtime performance of the application. The goal is to ahead-of-time (AOT) compile critical user journeys (like app launch and main screen scrolling) to reduce jank and improve speed.

## User Story
As a user, I want the app to start as quickly as possible and scroll smoothly without stuttering, so that my experience feels fast, reliable, and premium.

## Core Functionality
- **Baseline Profiles**: Pre-compile critical execution paths to improve startup time and reduce dropped frames.
- **Macrobenchmark Module**: Add an automated benchmarking suite to measure startup timing and frame timing.
- **Continuous Monitoring**: Integrate the benchmarks into the CI pipeline to catch performance regressions early.

## Technical Implementation
- **Android Focus**: The standard `androidx.benchmark:benchmark-macro-junit4` and `androidx.profileinstaller:profileinstaller` libraries will be used for Android.
- **iOS Considerations**: While Android relies on Baseline Profiles for AOT compilation optimization, iOS apps compiled via Kotlin Multiplatform (and Swift/Objective-C) already use AOT compilation out-of-the-box via LLVM. However, equivalent performance benchmarking for iOS can be established using XCTest Metrics (`measure { ... }`) or Instruments to profile App Launch time and scrolling hitches.
- **Benchmark Module Setup**: Create an isolated `benchmark` or `macrobenchmark` module that targets the Android app.
- **Profile Generator**: Write tests with `BaselineProfileRule` to generate the `baseline-prof.txt` file for the Android app.
- **Metrics Tracked**: `StartupTimingMetric`, `FrameTimingMetric`.

## Success Metrics
- Average Android app cold startup time reduced by at least 20-30%.
- Frame drop rate during initial scrolling on main screens reduced to near 0.
- Automated benchmarks successfully running and reporting metrics in the CI pipeline for both Android and iOS targets.

## Dependencies
- Completed navigation structure and core UI components (to define the critical user journeys).
- CI/CD environment with hardware devices or capable emulators for reliable benchmarking.
