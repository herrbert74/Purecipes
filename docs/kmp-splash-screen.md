# Porting the FlickSlate launch animation to Kotlin Multiplatform

This document explains how to reproduce FlickSlate's "wait until the app is ready,
then animate the splash away" behaviour on a Compose Multiplatform project targeting
**Android**, **iOS** and **Wasm**, and provides ready-to-paste code organized by source set.

Copy the relevant snippets into the KMP repo and adapt package names. The Kotlin
snippets use **tabs** for indentation to match project style.

---

## 1. Key idea: do not port `androidx.core.splashscreen`

The Android implementation stacks four layers:

| Layer | What it is | Portable? |
|---|---|---|
| `androidx.core.splashscreen` (`installSplashScreen`, `setKeepOnScreenCondition`) | Wrapper around the **Android OS splash window** that delays the first frame until you signal "ready". | **No** — it is an Android OS feature. |
| `SplashScreenDecorator` | Injects a `ComposeView` into the OS splash window's `ViewGroup` and fades it with `ObjectAnimator`. | **No** — View/Android-only bridge. |
| `ClapperboardTransition` | Pure Compose `Canvas` animation. | **Yes** — unchanged. |
| Orchestration in `FlickSlateActivity` | "Keep splash until `moviesListsReady`, enforce minimum duration, then dismiss." | **Yes** — becomes common code. |

iOS and Wasm have **no OS API** equivalent to `setKeepOnScreenCondition`:

* **iOS** shows a *static* `LaunchScreen` storyboard before the first frame. You cannot
  run logic during it. There is nothing to copy the androidx code into.
* **Wasm** has no OS splash at all; the browser shows a blank canvas until Skiko/Wasm
  initializes. The pre-frame gap is covered by plain HTML/CSS.

So the portable pattern is **not** "port androidx". Instead:

> Move the animation + orchestration into a **Compose overlay** that lives *inside* the
> app (a `Box` on top of the content), driven by shared state. Each platform only needs
> a tiny bit of glue to hold its native pre-frame placeholder until Compose draws frame 1.

This makes ~90% of the logic common, deletes the `SplashScreenDecorator` entirely, and
keeps platform code to a few lines each.

### Architecture

```
commonMain
├── ClapperboardTransition   (pure Compose, copied as-is)
├── SplashOverlay            (Box + ClapperboardTransition)
└── SplashHost               (state machine: readiness + minimum duration + exit)

androidMain  -> installSplashScreen(); keep OS splash until first Compose frame
iosMain      -> static LaunchScreen storyboard (background matches overlay)
wasmJsMain   -> CSS loader <div> in index.html, removed after first frame
```

The trick that avoids any visible flash: make the **native placeholder background**
(Android splash theme color / iOS storyboard color / Wasm loader color) identical to the
Compose overlay's `backgroundColor`. The handoff is then invisible and the branded
animation happens entirely in the common Compose overlay.

---

## 2. commonMain

### 2.1 `ClapperboardTransition`

Same as the Android version, but the color is passed in (instead of referencing the
Android `Colors` object) so it has no platform dependency.

```kotlin

private const val VIEWPORT_SIZE = 36f
private const val EYE_X_POS = 21f
private const val EYE_Y_POS = 22f
private const val EYE_RADIUS = 2f
private const val MAX_SCALE = 100f
private const val ANIMATION_DURATION = 1000

@Composable
fun ClapperboardTransition(
	isVisible: Boolean,
	overlayColor: Color,
	modifier: Modifier = Modifier,
	onAnimationEnd: () -> Unit = {},
) {
	val scale = remember { Animatable(1f) }
	val currentOnAnimationEnd by rememberUpdatedState(onAnimationEnd)

	LaunchedEffect(isVisible) {
		if (!isVisible) {
			scale.animateTo(
				targetValue = MAX_SCALE,
				animationSpec = tween(durationMillis = ANIMATION_DURATION, easing = FastOutSlowInEasing),
			)
			currentOnAnimationEnd()
		}
	}

	Canvas(modifier = modifier) {
		val iconSize = size.width
		val eyeX = (EYE_X_POS / VIEWPORT_SIZE) * iconSize
		val eyeY = (EYE_Y_POS / VIEWPORT_SIZE) * iconSize
		val eyeRadius = (EYE_RADIUS / VIEWPORT_SIZE) * iconSize

		drawCircle(
			color = overlayColor,
			radius = eyeRadius * scale.value,
			center = Offset(eyeX, eyeY),
		)
	}
}
```

### 2.2 `SplashOverlay`

The full-screen branded layer. Replace the placeholder colors with your theme.

```kotlin

@Composable
fun SplashOverlay(
	isVisible: Boolean,
	backgroundColor: Color,
	overlayColor: Color,
	onExitFinished: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.fillMaxSize()
			.background(backgroundColor),
		contentAlignment = Alignment.Center,
	) {
		ClapperboardTransition(
			isVisible = isVisible,
			overlayColor = overlayColor,
			modifier = Modifier.size(288.dp),
			onAnimationEnd = onExitFinished,
		)
	}
}
```

### 2.3 `SplashHost` — the orchestration state machine

This replaces the logic that lived in `FlickSlateActivity`. It enforces a minimum
on-screen duration once the app reports ready, then triggers the exit animation, and
finally removes itself when the animation finishes.

`TimeSource.Monotonic` (stdlib, multiplatform) replaces Android's `SystemClock`.

```kotlin

@Composable
fun SplashHost(
	isAppReady: Boolean,
	modifier: Modifier = Modifier,
	minimumDuration: Duration = 1000.milliseconds,
	splash: @Composable (isVisible: Boolean, onExitFinished: () -> Unit) -> Unit,
	content: @Composable () -> Unit,
) {
	var showSplash by remember { mutableStateOf(true) }
	var isVisible by remember { mutableStateOf(true) }
	val startMark = remember { TimeSource.Monotonic.markNow() }

	Box(modifier = modifier.fillMaxSize()) {
		content()

		if (showSplash) {
			LaunchedEffect(isAppReady) {
				if (isAppReady) {
					val remaining = (minimumDuration - startMark.elapsedNow())
						.coerceAtLeast(Duration.ZERO)
					delay(remaining)
					isVisible = false
				}
			}
			splash(isVisible) { showSplash = false }
		}
	}
}
```

### 2.4 Common app entry

`isAppReady` is whatever signal you used for `moviesListsReady` (e.g. first page of movies
loaded). Wire it from your view model / navigation exactly as today.

```kotlin

@Composable
fun App() {
	var isAppReady by remember { mutableStateOf(false) }

	SplashHost(
		isAppReady = isAppReady,
		splash = { isVisible, onExitFinished ->
			SplashOverlay(
				isVisible = isVisible,
				backgroundColor = Color(0xFF101317),
				overlayColor = Color(0xFF101317),
				onExitFinished = onExitFinished,
			)
		},
	) {
		// Your real app content; call setMoviesListsReady -> isAppReady = true
		AppContent(onReady = { isAppReady = true })
	}
}
```

---

## 3. androidMain

Keep the OS splash on screen until the first Compose frame is drawn, then let the common
overlay take over. **Theme the OS splash background to the same color as the overlay** so
the handoff is invisible. The `SplashScreenDecorator`/`ComposeView`/`ObjectAnimator` code
is **no longer needed** — delete it.

```kotlin

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		var firstFrameDrawn = false
		splashScreen.setKeepOnScreenCondition { !firstFrameDrawn }

		super.onCreate(savedInstanceState)

		setContent {
			LaunchedEffect(Unit) {
				withFrameNanos { }
				firstFrameDrawn = true
			}
			App()
		}
	}
}
```

Dependency (androidMain only):

```kotlin
// build.gradle.kts -> androidMain.dependencies
implementation("androidx.core:core-splashscreen:1.0.1")
```

Theme (`res/values/themes.xml`), background must match `SplashOverlay.backgroundColor`:

```xml
<style name="Theme.App.Splash" parent="Theme.SplashScreen">
	<item name="windowSplashScreenBackground">@color/splash_background</item>
	<item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher_foreground</item>
	<item name="postSplashScreenTheme">@style/Theme.App</item>
</style>
```

> Optional: if you specifically want the icon animation to play *inside* the OS splash
> window (your existing `ic_launcher_animated.xml`), you can keep that animated vector.
> But the branded `ClapperboardTransition` now runs in the common overlay, so the OS
> splash only needs a matching static background.

---

## 4. iosMain

iOS shows a static `LaunchScreen` storyboard before the first frame; there is no
"keep on screen until ready" hook. Set the storyboard's background color to match
`SplashOverlay.backgroundColor`. Once Compose renders, the common overlay shows and
animates out when `isAppReady` becomes true.

Entry point:

```kotlin

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
```

In Xcode:

* Set **Launch Screen** to a storyboard (or `UILaunchScreen` info plist key) whose
  background color equals the overlay background.
* No additional Kotlin is required; the storyboard disappears automatically when the
  first Compose frame draws.

---

## 5. wasmJsMain

There is no OS splash. Put a CSS loader in `index.html` (background matching the overlay),
then remove it after the first Compose frame.

`index.html`:

```html
<body>
	<div id="splash-loader"></div>
	<canvas id="ComposeTarget"></canvas>
	<style>
		#splash-loader {
			position: fixed;
			inset: 0;
			background: #101317; /* must match SplashOverlay.backgroundColor */
			z-index: 1;
		}
	</style>
</body>
```

`main.kt`:

```kotlin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
	val body = document.body ?: return
	ComposeViewport(body) {
		App()
	}
	document.getElementById("splash-loader")?.remove()
}
```

> If you observe a flicker because `ComposeViewport` mounts asynchronously, remove the
> loader from a first-frame effect instead: add `expect fun dismissPlatformPlaceholder()`
> in commonMain, call it from `SplashHost` inside `LaunchedEffect(Unit) { withFrameNanos {} ; dismissPlatformPlaceholder() }`,
> and have the `wasmJsMain` actual remove the `#splash-loader` element. Android/iOS
> actuals can be no-ops since their placeholders are handled natively.

---

## 6. What maps to what (migration checklist)

| Android (today) | KMP (new home) |
|---|---|
| `ClapperboardTransition.kt` | `commonMain` (copy, parameterize color) |
| `SplashScreenDecorator.kt` | **deleted** (replaced by `SplashHost` + `SplashOverlay`) |
| `FlickSlateActivity` splash orchestration (`moviesListsReady`, minimum duration, dismiss) | `commonMain` `SplashHost` |
| `installSplashScreen()` / `setKeepOnScreenCondition` | `androidMain` `MainActivity` only |
| `SystemClock.uptimeMillis()` | `kotlin.time.TimeSource.Monotonic` |
| `ic_launcher_animated.xml` (optional OS-splash icon anim) | stays Android-only resource |
| (none) | `iosMain` LaunchScreen storyboard + `MainViewController` |
| (none) | `wasmJsMain` `index.html` loader + `main()` removal |

### Dependencies summary

* `commonMain`: Compose Multiplatform + `kotlinx-coroutines` (already present). No extra libs.
* `androidMain`: `androidx.core:core-splashscreen`.
* `iosMain` / `wasmJsMain`: none.
