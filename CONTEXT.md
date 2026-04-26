# Ginansya — Context for the next agent session

Hand-off notes so the next session can pick up without re-reading the whole chat.

## What this project is

- Android app the user is submitting to their teacher. **Student demo, not production.**
- Offline compound-interest learning tool: a Home dashboard summarizing the last projection and a live Calculator.
- **Java**, minSdk 31, targetSdk 36, compileSdk 36, Java 11 source level, AGP 8.11.2.
- Single module (`app`), package `com.example.ginansya`, on Windows 11, bash shell, project root `C:\Users\monsi\AndroidStudioProjects\Ginansya`.

## Scope rules the user set (important)

- **Local persistence is OK** via `SharedPreferences` (the user approved this on 2026-04-26 after originally saying "in-memory only"). No Room, no online services.
- **No login / online banking APIs.** Fully offline.
- **Visual polish > architectural correctness.** Don't add Hilt/Dagger, MVVM layering, unit-test scaffolding, or production plumbing unless asked.
- **Java, not Kotlin.**
- **No AI-looking code smell.** No emojis. No banner comments in XML. No redundant Javadoc. Comments only when the *why* is non-obvious.
- **No hardcoding the formula.** All four compound-interest variables (P, r, n, t) plus PMT are user inputs. Visual styling constants (chart line widths, animation durations) are exempt — the rule is about formula correctness.
- **Currency: PHP (₱).** Locale `en-PH` with the symbol overridden to ₱.

## Current build state (2026-04-26)

Build is green: `./gradlew.bat assembleDebug` succeeds. App runs on emulator/device.

### Architecture shipped

- Single `MainActivity` hosting a `NavHostFragment`. **No toolbar, no bottom nav.** Status-bar inset is applied to the nav host (top), nav-bar inset to the nav host (bottom).
- `nav_graph.xml` has two destinations: `dashboardFragment` (start) and `calculatorFragment`. Calculator has an in-content back arrow at the top that calls `popBackStack()`; system back also works.
- Material 3 DayNight theme. Brand colors: teal `#0F766E` primary, amber `#F59E0B` secondary.
- MPAndroidChart 3.1.0 via JitPack for the Calculator's line chart.

### Features shipped

| Screen | What's there |
|---|---|
| **Home** (`DashboardFragment`) | Title "Check your Ginansya!" + subtitle. Teal gradient hero with future-value. Breakdown card (You contribute / Interest earned). Amber-toned **Insight card** that derives text from current state and fades + slides in on every resume. CTA card "Try your own numbers" → Calculator. |
| **Calculator** (`CalculatorFragment`) | Back arrow. Title + subtitle. Input card: Starting amount, Deposit per period, Annual rate, Years (decimals OK), **Compounding frequency dropdown** (Annually/Semi-annually/Quarterly/Monthly/Daily). Result hero (future value + contributed/interest split). Live dual-line chart (contributions dashed, total filled). Info card. |

### Data layer

- **`data/CalculatorState`** — static API over SharedPreferences (file `calculator_state`). `update(...)` writes the four inputs + n, returns the freshly computed `Result`. `getResult(ctx)` reads the four inputs and re-runs the formula. Calculator writes on every keystroke; Home reads in `onResume`.
- **`domain/CompoundInterestCalculator`** — pure function. Iterates period-by-period for a non-closed-form result that also produces the running balance series for the chart.

### Formula

`project(principal, periodicContribution, annualRatePct, years, compoundsPerYear)` does:

- `n = max(1, compoundsPerYear)`
- `i = annualRatePct / 100 / n`
- `N = max(1, round(years × n))`
- iterates `balance = balance × (1 + i) + periodicContribution` for `N` periods

Equivalent algebraically to `A = P(1+i)^N + PMT × [((1+i)^N − 1)/i]`. Compounding choices live in `res/values/arrays.xml` as parallel `compound_freq_labels` / `compound_freq_values` (1, 2, 4, 12, 365). Default 12.

### Persistence schema (`calculator_state` SharedPreferences file)

| Key | Type | Notes |
|---|---|---|
| `principal` | String | parsed as double |
| `monthly` | String | **legacy key name** — semantics now "deposit per compounding period". Java constant is `KEY_DEPOSIT`. |
| `rate` | String | annual rate, percent |
| `years` | String | decimals OK; legacy int values are migrated via `ClassCastException` catch |
| `compounds_per_year` | int | default 12 |

## File map

```
C:\Users\monsi\AndroidStudioProjects\Ginansya\
├── CONTEXT.md                             ← this file
├── Ginansya Docs.pdf                      ← user-facing short docs
├── app\build.gradle.kts                   ← stripped of unused deps + viewBinding
├── gradle\libs.versions.toml
├── settings.gradle.kts                    ← JitPack repo for MPAndroidChart
└── app\src\main\
    ├── AndroidManifest.xml
    ├── java\com\example\ginansya\
    │   ├── MainActivity.java              ← inflate + insets only
    │   ├── data\CalculatorState.java      ← SharedPreferences-backed state
    │   ├── domain\CompoundInterestCalculator.java  ← pure formula
    │   ├── util\CurrencyUtils.java        ← PHP / ₱ formatter
    │   └── ui\
    │       ├── dashboard\DashboardFragment.java
    │       └── calculator\CalculatorFragment.java
    └── res\
        ├── values\{colors, themes, strings, dimens, arrays}.xml
        ├── values-night\themes.xml
        ├── drawable\          ← 9 vectors + 3 backgrounds (post-audit)
        ├── layout\{activity_main, fragment_dashboard, fragment_calculator}.xml
        └── navigation\nav_graph.xml
```

## Build + run

```bash
cd "C:/Users/monsi/AndroidStudioProjects/Ginansya"
./gradlew.bat assembleDebug
```

Open in Android Studio, run on a device or emulator. First build pulls MPAndroidChart from JitPack (~2 min).

## What was just done (2026-04-26 cleanup pass)

- Removed Profile/Plans/Insights screens (earlier in the session) and the seeded plan data layer.
- Switched currency to PHP/₱.
- Added local persistence via SharedPreferences with user approval.
- Exposed compounding frequency `n` as a dropdown so all four formula variables come from inputs.
- Audit removed: 3 unused drawables, 10 unused colors, 3 theme styles, 7 dimens, 4 dependencies (`lifecycle-viewmodel`, `lifecycle-livedata`, `recyclerview`, `navigation-ui`), `viewBinding = true`, and the Android Studio default test scaffolding (`testInstrumentationRunner` + `testImplementation`/`androidTestImplementation` lines and their version-catalog entries).
- Promoted `#D1FAE5` (4 occurrences) → `@color/hero_subtitle`. Promoted repeated `14dp` corner radii → `@dimen/input_corner_radius`. Three runtime English labels in `fragment_calculator.xml` moved to string resources.

## What's likely next

- Chart label colors are still hard-bound to the `light_*` palette in `CalculatorFragment.configureChart()`. Dark-mode polish would swap to `?colorOnSurface` / `?colorOutlineVariant` via `TypedValue`.
- A "Reset" button in Calculator if persistence stickiness confuses a teacher running the app fresh.
- Decide what the chart should show when `years=0` or `rate=0` (currently flat line at 0).

## Memory (Claude-side)

The main agent's memory already records:
- Project scope (student demo, visual polish first).
- AI-footprint preference.

Those memories persist across sessions. The "no DB, in-memory only" rule has been relaxed in practice — local SharedPreferences is now in use.
