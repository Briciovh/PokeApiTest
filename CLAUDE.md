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

Navigation uses a `sealed class Screen` with typed route builders; the nav graph lives in `PokeAPIMainScreen.kt`. Generation list and navigation are driven by `GenerationInfo(id, name, altName, startId, endId)` — defined alongside `Generations` (a 9-entry `val`) in the same file. `displayName` is a computed property that returns `"$name/$altName"` when `altName` is present, or just `name`. All UI (drawer items, top app bar title) uses `displayName`; never `name` directly.

## Key Rules

- **Never downgrade dependencies.** Always use the most recent compatible version.
- **Always follow MVVM + Clean Architecture + Repository pattern.**
- **Post-change workflow:** Gradle sync (if needed) → full build → run unit tests.
- **CLAUDE.md / GEMINI.md sync:** These two files must always have identical content. A `PostToolUse` hook in `.claude/settings.json` enforces this automatically — any edit to one file is immediately copied to the other.

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

## Naming Conventions

| Layer | Pattern | Example |
|---|---|---|
| Remote DTO | `XxxDto` | `PokemonDto`, `TypeSlotDto` |
| Room entity | `XxxEntity` | `PokemonEntity`, `PokemonListItemEntity` |
| Domain model | no suffix | `PokemonDetail`, `PokemonListItem` |
| Use case | `GetXxxUseCase` | `GetPokemonDetailUseCase` |
| Repository | `XxxRepository` / `XxxRepositoryImpl` | — |
| Mappers | `toEntity()` / `toDomain()` | extension functions on the source type |

## Conventions and Non-Obvious Decisions

**Error handling:** ViewModels expose `SharedFlow<String> errorChannel` (replay=1). Compose screens collect it via `LaunchedEffect` and show a Snackbar. Do not introduce sealed error state wrappers — keep this pattern.

**Room migrations:** `fallbackToDestructiveMigration()` is intentional. To change the schema, just bump the version number in `PokemonDatabase.kt`. Do not add migration scripts unless deliberately changing this strategy.

**Varieties storage:** `PokemonEntity.varieties` is a pipe/semicolon-delimited string blob (`"name|url|isDefault|spriteUrl;..."`), not a normalized relation. This is an intentional simplification.

**Per-generation caching:** The repository fetches only the pokemon in the selected generation's range: `api.getPokemonList(limit = endId - startId + 1, offset = startId - 1)`. Cache completeness is checked via `dao.getPokemonInRange(startId, endId)` — if `cached.size < expectedCount`, a fetch is triggered. Generations accumulate in the DB (no `clearPokemonList()` between switches); `OnConflictStrategy.REPLACE` handles re-insertion safely.

**Dual-name regions:** `GenerationInfo.altName` holds the Japanese romanization for Gen 4 (`Shin'ō`) and Gen 5 (`Isshu`). `displayName` is the single source of truth for displayed names — never read `.name` directly in UI code.

**Sprite URLs:** Images come from GitHub raw content, not PokeAPI:
- Official artwork (list): `.../sprites/pokemon/other/official-artwork/{id}.png`
- Variety sprites (detail): `.../sprites/pokemon/{id}.png`
