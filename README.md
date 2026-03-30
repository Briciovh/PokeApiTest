# Ϟ PokeApiTest Ϟ

A modern, offline-first Android Pokedex application built to demonstrate best practices in Android development, including Clean Architecture, MVVM, and Jetpack Compose.

## 🚀 Features

- **Dynamic Pokemon List:** Browse a comprehensive list of Pokemon fetched directly from the [PokeAPI](https://pokeapi.co/).
- **Detailed Insights:** View detailed statistics for each Pokemon, including types, height, weight, and high-quality sprites.
- **Offline-First Experience:** Seamlessly browse previously viewed Pokemon even without an internet connection, thanks to local caching with Room.
- **Modern UI:** A beautiful, responsive interface built entirely with Jetpack Compose and Material 3.
- **Robust Error Handling:** Real-time feedback via Snackbars and Loading overlays for a smooth user experience.

## 🛠️ Tech Stack

- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern toolkit for building native UI.
- **Architecture:** [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) + [MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel).
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Standard way to incorporate DI in Android.
- **Networking:** [Retrofit](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) - Type-safe HTTP client.
- **Local Storage:** [Room](https://developer.android.com/training/data-storage/room) - SQLite object mapping library for offline caching.
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/) - Image loading library for Android backed by Kotlin Coroutines.
- **Asynchronous Flow:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) - For reactive data handling.
- **Testing:**
    - [JUnit 4](https://junit.org/junit4/) - Unit testing framework.
    - [MockK](https://mockk.io/) - Mocking library for Kotlin.
    - [Truth](https://truth.dev/) - Fluent assertions for Java and Android.
    - [Turbine](https://github.com/cashapp/turbine) - A small testing library for Kotlin Flow.

## 🏗️ Project Structure

The project follows a modularized clean architecture approach:
- **`data`**: Implements repositories, local Room database, and remote API definitions (DTOs).
- **`domain`**: Contains business logic via Use Cases (repository interfaces are also defined here in a strict Clean Architecture setup, though currently co-located for simplicity).
- **`ui`**: Jetpack Compose screens, ViewModels, and theme definitions.
- **`util`**: Helper functions and extensions.

## 🧪 Testing

The project emphasizes reliability with comprehensive unit tests for:
- Repositories (data logic & caching strategy).
- ViewModels (UI state management).
- Use Cases (business logic).
- Utility functions.

Run tests via:
```bash
./gradlew test
```

---
*Gotta catch 'em all!* 🔴⚪
