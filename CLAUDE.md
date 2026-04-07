# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
./gradlew build                  # Full project build
./gradlew assembleDebug          # Build debug APK
./gradlew installDebug           # Build and install debug APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests
./gradlew test --tests "com.example.pokeapitest.FooTest"  # Run a single test class
```

After any change to `build.gradle.kts` or `libs.versions.toml`, a Gradle sync is required. After any code change, run a full build and unit tests.

## Architecture

Clean Architecture + MVVM with three layers:

- **`data/`** — Repository implementations, Room database (`local/`), Retrofit API + DTOs (`remote/`). The repository reads from local DB first (offline-first), then fetches from API and caches if empty.
- **`domain/`** — Use cases (`GetPokemonListUseCase`, `GetPokemonDetailUseCase`) and domain models. Pure Kotlin, no Android dependencies.
- **`ui/`** — Compose screens and ViewModels. ViewModels expose `StateFlow` for state and `SharedFlow` for one-time events (errors).
- **`di/`** — Single Hilt `AppModule` wiring Retrofit, OkHttp, Room, and repositories.

Data flows through mapper extension functions at each boundary: `DTO → Entity → Domain model`.

Navigation uses a `sealed class Screen` with typed route builders; the nav graph lives in `PokeAPIMainScreen.kt`.

## Key Rules (from GEMINI.md)

- **Never downgrade dependencies.** Always use the most recent compatible version.
- **Always follow MVVM + Clean Architecture + Repository pattern.**
- **Post-change workflow:** Gradle sync (if needed) → full build → run unit tests.

## Tech Stack

| Concern | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| DI | Hilt 2.59.2 |
| Networking | Retrofit 3 + OkHttp + Moshi |
| Local DB | Room 2.8.4 |
| Images | Coil 2.7.0 |
| Async | Kotlin Coroutines + Flow |
| Testing | JUnit 4, MockK, Truth, Turbine |

Versions are managed via `gradle/libs.versions.toml`.

## Testing Approach

Unit tests live in `app/src/test/`. Key patterns:
- MockK for mocking
- Turbine for Flow assertions
- `MainDispatcherRule` (custom) to swap the main dispatcher in tests
- Truth for fluent assertions
