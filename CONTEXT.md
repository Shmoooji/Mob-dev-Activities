# Ginansya — Context for the next agent session

Hand-off notes so the next session can pick up without re-reading the whole chat.

## What this project is

- Android app the user is submitting to their teacher. **Student demo, not production.**
- Offline personal finance learning app: compound interest calculator, savings plans, progress tracking, insights.
- **Java**, minSdk 31, targetSdk 36, compileSdk 36, Java 11 source level, AGP 8.11.2.
- Single module (`app`), package `com.example.ginansya`, on Windows 11, bash shell, project root `C:\Users\monsi\AndroidStudioProjects\Ginansya`.

## Scope rules the user set (important)

- **No database.** No Room, no persistence. In-memory only. User explicitly confirmed this.
- **No account / login / online banking APIs.** Fully offline.
- **Visual polish > architectural correctness.** Don't add Hilt/Dagger, MVVM layering, unit test scaffolding, or other production plumbing unless asked.
- **Java, not Kotlin.**
- **No AI-looking code smell** — the user audited for and removed these on 2026-04-24. Do NOT reintroduce:
  - No emojis anywhere.
  - No per-line or per-section banner comments in XML (no `<!-- Hero card -->`, `<!-- action 1 -->`, etc.).
  - No redundant Javadoc or comments that restate what the code does.
  - Comments only when the *why* is non-obvious.

## Current build state (as of 2026-04-24)

Build is green: `./gradlew.bat assembleDebug` succeeds. App runs on an emulator/device.

### Architecture shipped

- Single `MainActivity` hosts a `NavHostFragment` + `BottomNavigationView` with 4 destinations: Home, Plans, Calculator, Insights.
- Material 3 DayNight theme with custom teal (`#0F766E`) primary and amber (`#F59E0B`) secondary. Both `values/` and `values-night/` themes exist. Custom `Widget.Ginansya.*` styles for cards and bottom nav.
- Edge-to-edge enabled; insets handled per-fragment for top padding and on `MainActivity` for bottom nav.
- MPAndroidChart 3.1.0 via JitPack for line / bar / pie charts. (Note: `enableDashedLine` — no `set` prefix.)
- Navigation Component 2.8.4, Lifecycle 2.8.7, RecyclerView 1.4.0, Material 1.13.0. All dependencies in `gradle/libs.versions.toml`.

### Features shipped

| Screen | Status |
|---|---|
| Dashboard (Home) | Teal gradient hero with total saved, "+$X this month" pill, 4 quick-action cards, horizontal plan carousel, projected-vs-actual line chart with dashed projection, amber "insight of the day" card. |
| Plans list | Filterable list (All / Active / Completed chips), rich plan cards with progress bar + status chip, extended FAB (Snackbar stub). |
| Calculator | Live 4-input form (principal, monthly, rate, years), hero result card, dual-line chart (contributions vs with-interest), info card. Recalculates on every keystroke. |
| Insights | Monthly contributions bar chart, allocation donut with side legend, 3 colored tip cards. |

### Data layer

- `data/FinanceStore.java` — singleton repository, `getInstance()`, mutable `ArrayList<Plan>`, seeded on construction. Methods: `getAllPlans`, `getActivePlans`, `getCompletedPlans`, `findById`, `totalSaved`, `gainedThisMonth`, `monthlyContributionTotals`.
- `data/model/` — `Plan`, `Contribution`, `PlanStatus` POJOs.
- `domain/CompoundInterestCalculator.java` — pure Java, returns month-by-month balances.
- `util/CurrencyUtils.java` — `format`, `formatCompact`, `formatSigned`.

Seed data in `FinanceStore` is 4 plans: Emergency Fund (ahead), House Deposit (on track), Dream Car (behind), Japan Trip (completed). All `Contribution` history is realistic-looking to make charts and insights feel lived-in.

## What the user paused before

User said: "*stops here for now I need to rest*" — right before the **logic step**. They previously phrased it as "Before we proceed to its logic" when asking for the AI-footprint audit.

### "Logic" = wire up interactions so the app is usable, not just a visual shell

Likely next tasks (in rough priority order):

1. **Log deposit quick-action** → bottom sheet: plan picker, amount, note, date. Appends a `Contribution` to the chosen Plan via `FinanceStore`. Update carousel + charts after.
2. **New plan FAB + quick-action** → form: name, target amount, principal, rate, monthly contribution, icon picker, color. Calls `FinanceStore.addPlan(...)`.
3. **Plan Detail screen** → tap a plan card (from carousel or list). Dual-line chart, contributions list, "Log contribution" button, edit, delete.
4. **What-If screen** (4th quick action) → sliders adjusting a plan's rate / monthly contribution / term, side-by-side comparison chart.
5. **Calculator "Save as plan"** button — optional, low priority.

### Things `FinanceStore` needs for the logic step

Currently read-only public API. To support mutations, add:

```java
public void addPlan(Plan p)
public void addContribution(String planId, Contribution c)
public void removePlan(String planId)
```

Plans are `final` fields right now — `addContribution` will need to either rebuild the Plan (current approach would require making it mutable) or change `Plan.contributions` to a mutable `ArrayList` (it already is internally via the ctor). Current `Plan` is effectively immutable at the field level but holds a mutable list, so appending is safe.

For the UI to refresh after a mutation, the simplest option without pulling in LiveData is a listener pattern on `FinanceStore` (`addOnChangeListener(Runnable)`), with fragments registering in `onStart` / unregistering in `onStop`. LiveData is also fine — `lifecycle-livedata` is already on the classpath.

## File map

```
C:\Users\monsi\AndroidStudioProjects\Ginansya\
├── CONTEXT.md                              ← this file
├── app\build.gradle.kts                    ← viewBinding + all deps
├── gradle\libs.versions.toml               ← version catalog
├── settings.gradle.kts                     ← JitPack repo added for MPAndroidChart
└── app\src\main\
    ├── AndroidManifest.xml
    ├── java\com\example\ginansya\
    │   ├── MainActivity.java
    │   ├── data\
    │   │   ├── FinanceStore.java           ← singleton repo
    │   │   └── model\{Plan, Contribution, PlanStatus}.java
    │   ├── domain\CompoundInterestCalculator.java
    │   ├── util\CurrencyUtils.java
    │   └── ui\
    │       ├── dashboard\{DashboardFragment, PlanCarouselAdapter}.java
    │       ├── plans\{PlansFragment, PlansAdapter}.java
    │       ├── calculator\CalculatorFragment.java
    │       └── insights\InsightsFragment.java
    └── res\
        ├── values\{colors, themes, strings, dimens}.xml
        ├── values-night\themes.xml
        ├── color\bottom_nav_item_color.xml
        ├── drawable\           ← 15 vector icons + gradients + chip bgs
        ├── layout\             ← activity + 4 fragments + 3 item layouts
        ├── menu\bottom_nav_menu.xml
        └── navigation\nav_graph.xml
```

## Build + run

```bash
cd "C:/Users/monsi/AndroidStudioProjects/Ginansya"
./gradlew.bat assembleDebug           # verify compile
```

Open in Android Studio, run on a device/emulator. First build takes ~2 min (JitPack pulls MPAndroidChart).

## Gotchas

- `chart.enableDashedLine(...)` on MPAndroidChart 3.1.0 — no `set` prefix.
- In `item_plan_carousel.xml`, pushing the status chip to the right uses a weighted spacer `View` — don't use `layout_marginStart="auto"` (not a valid value, build fails).
- The progress ring currently uses a `LinearProgressIndicator`; if switching to a `CircularProgressIndicator`, `setProgressCompat(pct, true)` still works.
- `Plan.currentBalance()` applies compound growth once over `monthsElapsed` months against `principal + sum(contributions)` — deliberately simplified for the demo. Tighter math (month-by-month with each contribution compounded separately) would be a small rewrite if the teacher asks.
- Chart label colors are currently hard-bound to the light palette (`R.color.light_on_surface` etc.), so in dark mode axis labels will look off. Not a blocker; swap to `?colorOnSurface` via `TypedValue` if polishing.

## Memory (Claude-side)

The main agent's memory already records:
- Project scope = student demo, no DB, visual polish first.
- AI-footprint preference.

Those memories persist across sessions.
