# Ϟ PokeApiTest Ϟ

A modern, offline-first Android Pokedex application built to demonstrate best practices in Android development, including Clean Architecture, MVVM, and Jetpack Compose.

## 🚀 Features

- **Dynamic Pokemon List:** Browse Pokemon across multiple generations (Gen 1-9), with specific range loading and filtering.
- **Detailed Insights:** View comprehensive statistics for each Pokemon, including types, height, weight, and high-quality official artwork.
- **Move Sets:** Detailed move list for each Pokemon, including power and type, sorted by power level.
- **Varieties & Shinies:** Explore Pokemon varieties (Mega evolutions, regional forms) and automatically generated shiny previews.
- **Offline-First Experience:** Reliable local caching with Room database. The app fetches from the API and persists data locally for a seamless offline experience.
- **Generation-Based Navigation:** Navigation drawer to switch between different Pokemon generations, with custom display names for regions like Gen 4 (Sinnoh/Shin'ō).
- **Modern UI:** Responsive interface built with Jetpack Compose and Material 3, featuring theme-aware Pokemon type coloring.
- **Robust Error Handling:** Real-time feedback via Snackbars and Loading overlays, managing network failures gracefully.

## 🛠️ Tech Stack

- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern toolkit for building native UI.
- **Architecture:** [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) + [MVVM](https://developer.android.com/topic/libraries/architecture/viewmodel).
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Standard way to incorporate DI in Android.
- **Networking:** [Retrofit 3](https://square.github.io/retrofit/) + [OkHttp](https://square.github.io/okhttp/) - Type-safe HTTP client with Moshi for JSON parsing.
- **Local Storage:** [Room 2.8.4](https://developer.android.com/training/data-storage/room) - Normalized schema with `@Relation` for complex data types (Varieties and Moves).
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/) - Image loading library for Android.
- **Asynchronous Flow:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) - For reactive data handling and parallel API calls.
- **Testing:**
    - [JUnit 4](https://junit.org/junit4/) - Unit testing framework.
    - [MockK](https://mockk.io/) - Mocking library for Kotlin.
    - [Truth](https://truth.dev/) - Fluent assertions.
    - [Turbine](https://github.com/cashapp/turbine) - Testing library for Kotlin Flow.

## 🏗️ Project Structure

The project follows a clean architecture approach across three layers:
- **`data`**: Repository implementations, Room database (`local`), and Retrofit API + DTOs (`remote`). Uses a normalized schema to store Pokemon details, varieties, and moves in separate tables.
- **`domain`**: Pure Kotlin layer containing business logic via Use Cases (`GetPokemonListUseCase`, `GetPokemonDetailUseCase`) and Domain Models.
- **`ui`**: Jetpack Compose screens, ViewModels (StateFlow/SharedFlow), and theme definitions.
- **`di`**: Hilt modules for providing dependencies.

## 🧪 Testing

The project emphasizes reliability with comprehensive tests:
- **Unit Tests**: Coverage for Mappers, Use Cases, ViewModels, and Repositories (located in `app/src/test/`).
- **Instrumented Tests**: DAO integration tests and Screen-level UI tests using Compose Test Rule (located in `app/src/androidTest/`).

Run all tests via:
```bash
./gradlew test                 # Run unit tests
./gradlew connectedAndroidTest # Run instrumented tests
```

---
*Gotta catch 'em all!* 🔴⚪
