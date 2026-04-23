# Add Hamburger Menu and Settings Screen

This plan outlines the steps to add a hamburger menu (drawer) to the main screen, a Settings screen, and persistent settings for Theme (Light/Dark/System) and Image Preference (Official Artwork vs. Pixel Art).

## User Review Required

> [!NOTE]
> I will be using `androidx.datastore:datastore-preferences` for persisting settings. This is the modern replacement for SharedPreferences in Android.

## Proposed Changes

### Dependencies

#### [libs.versions.toml](file:///C:/Users/brici/code/PokeApiTest/gradle/libs.versions.toml)
- Add `datastore = "1.2.1"`
- Add `androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }`

#### [build.gradle.kts](file:///C:/Users/brici/code/PokeApiTest/app/build.gradle.kts)
- Add `implementation(libs.androidx.datastore.preferences)`

---

### Domain Models

#### [NEW] [AppPreferences.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/domain/model/AppPreferences.kt)
- Define `ThemePreference` (LIGHT, DARK, SYSTEM) and `ImagePreference` (OFFICIAL, PIXEL) enums.
- Define `AppPreferences` data class.

---

### Data Layer

#### [NEW] [SettingsRepository.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/repository/SettingsRepository.kt)
- Implement `SettingsRepository` using DataStore.
- Methods: `getPreferences(): Flow<AppPreferences>`, `updateTheme(ThemePreference)`, `updateImagePreference(ImagePreference)`.

#### [AppModule.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/di/AppModule.kt)
- Provide `DataStore<Preferences>`.
- Provide `SettingsRepository`.

---

### UI Layer - Settings

#### [NEW] [SettingsViewModel.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/ui/settings/SettingsViewModel.kt)
- Expose `AppPreferences` as `StateFlow`.
- Methods to update preferences.

#### [NEW] [SettingsScreen.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/ui/settings/SettingsScreen.kt)
- UI for choosing Theme and Image preference.

---

### UI Layer - Navigation & Main Screen

#### [PokeAPIMainScreen.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/ui/PokeAPIMainScreen.kt)
- Add `Screen.Settings` route.
- Update `PokeAPINavHost` to:
    - Add `ModalNavigationDrawer` with a `DrawerContent` showing the "Settings" menu item.
    - Add a menu icon to the `TopAppBar` (only when on the main list screen).
    - Collect `AppPreferences` from a shared `SettingsViewModel` and pass the theme to `PokeApiTestTheme`.
    - Provide `ImagePreference` via `CompositionLocalProvider`.

#### [PokemonItem.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/ui/PokemonItem.kt)
- Use `LocalImagePreference` to decide which sprite URL to show.

#### [PokemonDetailScreen.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/ui/PokemonDetailScreen.kt)
- Use `LocalImagePreference` to decide which sprite URL to show for the hero image and varieties.

---

### UI Layer - Theme

#### [Theme.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/ui/theme/Theme.kt)
- Update `PokeApiTestTheme` to accept `ThemePreference`.
- Define `LocalImagePreference` CompositionLocal.

## Verification Plan

### Automated Tests
- Run `./gradlew test` to ensure existing tests still pass.

### Manual Verification
1. **Drawer**: Open the hamburger menu from the main screen.
2. **Settings Navigation**: Click "Settings" in the drawer and verify navigation to the Settings screen.
3. **Theme Setting**:
    - Change to "Dark Mode" and verify UI changes.
    - Change to "Light Mode" and verify UI changes.
    - Change to "System Default" and verify it follows system theme.
4. **Image Preference Setting**:
    - Change to "Pixel Art" and verify that both the list and detail screens show pixel art.
    - Change to "Official Art" and verify they show official artwork.
5. **Persistence**: Close and reopen the app to verify settings are saved.
