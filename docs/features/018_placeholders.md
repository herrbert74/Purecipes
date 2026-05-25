# Placeholder for Loading Lists/Images

## Status: <span style="color:orange;">🟠 DRAFT</span>

## Feature Overview
Implement skeleton loading placeholders to display while content (such as lists or images) is being fetched from the network or database. This will reduce perceived latency and provide users with a visual cue of the incoming layout structure before the data fully loads.

## User Story
As a user, I want to see a placeholder structure indicating where text and images will appear while the app is loading, so that I understand the content layout and perceive the application to be fast and responsive.

## Core Functionality
- **Skeleton Views**: UI representations showing the shape of the content (cards, images, text lines) while data is loading.
- **Animations**: Subtle animations (such as a fade or a shimmer effect) applied to the placeholders to signal ongoing loading activity.
- **Seamless Transition**: Smooth crossfade from the placeholder skeleton to the actual loaded content.

## Technical Implementation
- **Compose UI**: Develop or integrate compose modifiers and components to draw the placeholders.
- **Implementation Options**: We will evaluate and adopt one of the following approaches:
  1. A dedicated third-party library: [eygraber/compose-placeholder](https://github.com/eygraber/compose-placeholder) for a straightforward modifier-based approach.
  2. A custom implementation: A bespoke, highly customizable implementation inspired by [ShimmerAnimation.kt](https://gist.github.com/Kyriakos-Georgiopoulos/0109b73939638db11a6c624470e007bb).
- **Multiplatform Compatibility**: Ensure the chosen placeholder rendering approach functions properly across Android, iOS, and Web (Wasm).

## Success Metrics
- Increase in perceived performance according to user testing/feedback.
- Consistent placeholder behavior without UI blocking or frame drops across all targeted platforms.
- Placeholders accurately matching the final content dimensions to prevent content shift when the data arrives.

## Dependencies
- Proper UI layout definitions.
- Loading states exposed by ViewModels and domain layers to control placeholder visibility.
