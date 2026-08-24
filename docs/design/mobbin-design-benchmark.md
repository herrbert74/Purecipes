# Design Benchmark: Mobbin Apps Closest to Purecipes

Research date: 2026-08-19. Sources: Mobbin (iOS) screen search + the current Purecipes theme code.

## 1. Where Purecipes is today

Facts from the codebase (`shared/ui/.../theme/`):

- **Palette**: a standard Material3 tonal palette generated from a rose/berry seed.
  Light primary `#8D495A` (muted rose), secondary `#745B0B` (mustard), tertiary
  `#296A47` (green), warm pink-tinted surfaces (`#FFF8F7` background,
  `#FFF0F1`/`#FBEAEB` containers). Chroma is low across the board — the palette
  reads *warm and gentle*, not *vibrant and happy*.
- **Typography**: Cabin for every M3 style at default M3 sizes. Only
  `cabin_regular` and `cabin_italic` are bundled; **`FontWeight.Bold` is mapped to
  the regular font file** (`Type.kt`), so "bold" text renders as synthetic or
  effectively regular weight. This is the single biggest reason the app lacks
  boldness today.
- **Components**: default M3 shapes and components; cards on
  `surfaceContainerLow`, default `CircularProgressIndicator` loaders, icon +
  text empty states tinted `primary`/`onSurfaceVariant`.

Conclusion up front: the M3 foundation does **not** need an overhaul. The gap to
"vibrant and happy" is closable with (a) a higher-chroma palette, (b) real bold
display type, (c) a few signature components (chips, tinted cards, pill
buttons), and (d) motion. The apps below show exactly how.

## 2. Closest matches found on Mobbin

### 2.1 Cherrypick — closest overall match (palette + mood)

Screens reviewed:
[home feed](https://mobbin.com/screens/80727abe-4e15-4961-b5d7-68ec004c7504),
[recipe detail](https://mobbin.com/screens/671ebdad-9d43-4387-8912-876466031535),
[ingredients list](https://mobbin.com/screens/9e272be4-79d3-4cc9-8c67-2f98a7c29a82),
[shoppable recipe](https://mobbin.com/screens/b02a2e02-9703-4fa6-a604-77f0abaa6060),
["what you'll need"](https://mobbin.com/screens/1a2efcf3-bba4-4ff0-9bfc-1f8743ce7571).

Cherrypick is essentially Purecipes' palette turned up: saturated berry/magenta
primary on warm peach card backgrounds, with a deep forest green as the heading
and "positive/price" color, and mint green for tags. Bold rounded headings,
filled pill chips ("Easy Level", "Freezable", "Batch cook"), full-width pill
CTAs ("Add to my shop"), and a segmented Ingredients/Method/Nutrition control.

**To match Cherrypick, Purecipes would change:**

- Raise primary chroma: move from muted `#8D495A` toward a saturated berry
  (~`#A3145E`–`#B00050` family) as the M3 seed; regenerate the tonal palette.
- Promote the existing green tertiary to a *semantic* role: headings/section
  titles and positive values in a deep green, instead of everything on
  `onSurface`.
- Use warm peach/pink `primaryContainer`-style fills as full card backgrounds
  for hero/featured recipe cards (image inside the tinted card, not edge to
  edge), like the "Trending this week" card.
- Replace plain text metadata with filled pill chips (difficulty, prep time,
  dietary tags) in `primary`/`primaryContainer` colors.
- Full-width pill-shaped primary buttons for the main action on each screen.
- Segmented control (M3 `SegmentedButton`) for Ingredients / Method / Nutrition
  on the recipe details screen.

### 2.2 Kitchen Stories — editorial warmth, photography-first

Screens reviewed:
["For You" feed](https://mobbin.com/screens/bd9b5484-23f9-400f-a749-c0a9369e217c),
[cook mode](https://mobbin.com/screens/85a4ace6-390e-40b5-a231-14f134af1752).

Warm orange accent on white; the vibrancy comes from large food photography and
a single loud accent, not from tinted surfaces. Strongest ideas: inline
highlighted values in instruction text (times/temperatures rendered in the
accent color with a clock glyph, tappable to start a timer), per-step utensil
and ingredient summaries above the instruction, an always-visible running-timer
chip, and friendly onboarding coach-mark tooltips in a contrasting green.

**To match Kitchen Stories, Purecipes would change:**

- Make cooking-step text large (`headlineSmall`+) with parsed times and
  temperatures rendered in `primary` and tappable to start a timer.
- Show a persistent floating timer chip in cook mode once a timer runs.
- List the utensils/ingredients needed for *this step* above the instruction.
- Bigger, edge-to-edge photography on the recipe feed with a "Recipe of the
  day" hero slot.

### 2.3 Crouton — playful pastel cards, calm cook mode

Screens reviewed:
[discover feed](https://mobbin.com/screens/739109cc-b27c-457a-bb5f-d52fb6a5a323),
[cook mode](https://mobbin.com/screens/b095159b-a8a3-4ff6-8ef9-61bbd5147ecd).

Each feed card sits on its own soft pastel tint (peach, mint, blue) so the list
feels happy without any single loud color; a floating pill-shaped bottom bar;
cook mode is nearly empty — step number, instruction with highlighted values,
a big circular "next" FAB, page dots, and a floating timer card with a green
check when done.

**To match Crouton, Purecipes would change:**

- Rotate a small set of pastel container tints across feed cards (derived from
  `primaryContainer`, `secondaryContainer`, `tertiaryContainer` — the theme
  already has all three; they are just underused).
- Slim the cook-mode chrome down to step text + one large forward affordance +
  horizontal-pager dots, with swipe navigation between steps.
- Consider a floating pill navigation bar instead of a full-width M3 bar.

### 2.4 Supporting references

- [Recime cook mode](https://mobbin.com/screens/4439d48a-0f3c-443c-8249-fd951894e316):
  minimal step screen, thick segmented progress bar at top, colored inline
  values, one full-width "Next" pill — the cheapest cook-mode upgrade to copy.
- [HelloFresh cook step](https://mobbin.com/screens/a8ce2bb8-cecc-423b-aaaf-53e3e00e333a):
  named per-step timers ("Start 'Cook Pasta' 09:00") — great when a step has
  multiple timed sub-tasks.
- [Blinkit recipes hub](https://mobbin.com/screens/c69b2710-70ee-46d4-b9bc-d221078385f1):
  illustrated category tiles (Breakfast/Lunch/Dinner…) on peach tints; a
  friendly search/browse entry point.
- [Alma](https://mobbin.com/screens/d66c317e-9f44-4fb9-9c9c-011aa60b8bd5):
  color-blocked recipe cards with organic shapes behind photos — an option if
  photography quality is inconsistent.

### 2.5 Non-recipe references (avoid copying any single recipe app)

The boldest "vibrant and happy" executions live outside the recipe category.
Borrowing from these keeps Purecipes from converging on a competitor's look.

#### Flo — closest non-recipe palette match

[Insights feed](https://mobbin.com/screens/309962e3-2a4a-43ec-8f62-d3097f912768):
pink/berry brand on soft cream, with a grid of illustrated content tiles where
each tile sits on its own flat pastel fill (pink, lilac, peach, mint) and
carries a short bold title. Sections are curated rows ("OB-GYN's choice",
"Flo recommends").

**Borrow:** the tinted illustrated tile grid for browse/category entry points
(cuisines, meal types, collections) — same mechanic as Blinkit's hub but
executed with more color confidence; curated "recommended for you" rows with
editorial titles on the search/home screen.

#### Paired — tinted content cards with structure

[Home feed](https://mobbin.com/screens/309da80b-e582-4b54-a327-4487e480e1c0):
deep ink-purple headlines on white, content cards on flat lilac/peach/olive
container tints, each card topped with a small white label chip ("Question",
"Quiz", "Game"), connected by a vertical progress timeline, one full-width
pill CTA at the bottom.

**Borrow:** the label-chip-on-tinted-card pattern (e.g. "Quick dinner",
"Baking", "New" chips on rotating container tints); a very dark plum/ink
`onSurface` (near-black with brand hue) instead of neutral dark grey to make
headlines feel branded; the timeline connector could map to multi-day meal
plans later.

#### Plenty of Fish — boldness without mascots or gradients

[Welcome screen](https://mobbin.com/screens/e3a01116-7637-4d04-b7a6-257703556213):
a single flat warm coral field, an oversized display-serif wordmark, one
hand-drawn line illustration, one black pill button. Maximum personality from
minimum ingredients.

**Borrow:** the recipe for auth/onboarding/empty screens — one saturated
brand-color field, one oversized headline, one simple line illustration, one
pill CTA. No gradients, no mascot. This is the cheapest way to make the
sign-in and empty states feel designed.

#### Tiimo — celebration in a calm register

[Completion screen](https://mobbin.com/screens/325c4ef5-cf3b-4359-9da9-6f90753b9f17):
"You completed 6 tasks 🎉" in an elegant serif, surrounded by gently floating
emoji inside pastel circles, a Share pill, and a Today/This week toggle.
Celebratory but adult — no confetti explosion.

**Borrow:** the "finished cooking" moment. When a user completes the last
step, show a calm celebration screen: bold headline, recipe photo or floating
ingredient emoji on container-tint circles, share action, and a prompt to
rate/favorite the recipe.

#### Duolingo — stat chips, used sparingly

[Session complete](https://mobbin.com/screens/6be60a24-80c6-4411-9918-9a1768aa0275):
outlined stat chips in candy colors (XP / accuracy / time), each with its own
hue, icon, and label, on a flat white background.

**Borrow:** only the outlined tri-color stat chip component — cook time,
servings, difficulty as three colored outlined chips on recipe details, or
total time / steps done on the cook-mode completion screen. The full Duolingo
register (mascot, shouting colors) is too loud for Purecipes.

## 3. Synthesis: recommended direction for Purecipes

Keep Material3; do a *palette-and-expression* pass rather than a redesign.
Do **not** clone any single recipe app: take palette confidence from
Cherrypick/Flo, structure from Paired, restraint from Plenty of Fish, and
celebration tone from Tiimo, applied to the recipe flows benchmarked above.

1. **Boldness first (highest impact, lowest risk).** Bundle real Cabin Bold and
   SemiBold (or switch display/headline styles to a rounder display face such
   as Nunito/Baloo) and map `FontWeight.Bold` to the actual bold file in
   `Type.kt`. Increase weight on `headline*` and `title*`. Nothing else changes
   until this is fixed — synthetic bold is why the app feels flat.
2. **Re-seed the palette.** Regenerate the M3 tonal palette from a
   higher-chroma berry seed (Cherrypick direction). Keep the warm pink surfaces
   — they already match the target mood — but push `primary` and the container
   tones up in saturation. Update `Color.kt`/`Theme.kt` wholesale via the
   Material Theme Builder export, as done today.
3. **Signature components** (in `shared/ui/component`):
   - Metadata pill chips (time, difficulty, dietary) — Cherrypick.
   - Tinted feature cards rotating container colors, topped with a small label
     chip ("Quick dinner", "Baking", "New") — Crouton/Cherrypick/Paired.
   - Full-width pill primary button style — Cherrypick.
   - Segmented Ingredients/Method/Nutrition control on details — Cherrypick.
   - Outlined tri-color stat chips (cook time / servings / difficulty) —
     Duolingo, toned down.
   - Illustrated tinted tile grid for browse entry points (cuisines, meal
     types, collections) — Flo/Blinkit.
4. **Cook mode is the flow to differentiate.** Combine: Recime's thick top
   progress bar + Crouton's minimal step layout with swipe/pager + Kitchen
   Stories' tappable inline times/temps and floating timer chip + HelloFresh's
   named timers for multi-part steps. End with a Tiimo-style calm celebration
   screen — bold headline, floating ingredient emoji on container-tint
   circles, share action, and a rate/favorite prompt.
5. **Motion, targeted not global:**
   - Shared-element transition from feed card image → recipe details header.
   - Animated progress bar and step transitions (pager slide) in cook mode.
   - Springy scale-in on the favorite action; checkmark morph when a step or
     timer completes.
   - Staggered fade/slide-in for feed cards on first load.
6. **Empty/loading states and auth screens**: replace bare
   `CircularProgressIndicator` and icon empty states with the Plenty of Fish
   recipe — one flat brand-color field, one oversized headline, one simple
   line illustration, one pill CTA. Same treatment for sign-in/onboarding.
   A mascot à la BitePal/Tolan is *not* recommended — wrong register for a
   recipe utility.

Suggested order of work: 1 → 2 ship together (one visual-refresh release), then
3, then 4+5 as the cook-mode feature slice, 6 opportunistically.
