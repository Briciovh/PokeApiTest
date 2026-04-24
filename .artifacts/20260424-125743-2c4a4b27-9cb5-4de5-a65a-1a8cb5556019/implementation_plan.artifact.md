# Normalize Pokemon Details Schema

Refactor `PokemonEntity` to move `varieties` and `moves` from semicolon-delimited strings to normalized database tables using Room `@Relation`. This improves data integrity, enables better querying, and follows Room best practices.

## User Review Required

> [!IMPORTANT]
> **Database Migration**: This change requires a database version bump. Since the project uses `fallbackToDestructiveMigration()`, the local cache will be cleared upon the next run. This is acceptable per the project's strategy.

- **Schema Normalization**: Varieties and Moves will now be stored in their own tables (`pokemon_varieties` and `pokemon_moves`) with foreign keys back to `pokemon_details`.

## Proposed Changes

### Data Layer: Entities & Database

---

#### [NEW] [PokemonVarietyEntity.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/local/entity/PokemonVarietyEntity.kt)

- Define `PokemonVarietyEntity` with a foreign key to `PokemonEntity`.
- Includes fields: `pokemonId`, `name`, `url`, `isDefault`, `imageUrl`, `officialArtworkUrl`.

#### [NEW] [PokemonMoveEntity.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/local/entity/PokemonMoveEntity.kt)

- Define `PokemonMoveEntity` with a foreign key to `PokemonEntity`.
- Includes fields: `pokemonId`, `name`, `power`, `type`.

#### [NEW] [PokemonWithDetails.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/local/entity/PokemonWithDetails.kt)

- A POJO for Room relations:
```kotlin
data class PokemonWithDetails(
    @Embedded val pokemon: PokemonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemonId"
    )
    val varieties: List<PokemonVarietyEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "pokemonId"
    )
    val moves: List<PokemonMoveEntity>
)
```

#### [PokemonEntity.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/local/entity/PokemonEntity.kt)

- Remove `varieties: String` and `moves: String` fields.

#### [PokemonDatabase.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/local/PokemonDatabase.kt)

- Register `PokemonVarietyEntity` and `PokemonMoveEntity` in the `@Database` entities array.
- Increment `version` from 5 to 6.

---

### Data Layer: DAO & Repository

#### [PokemonDao.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/local/PokemonDao.kt)

- Update `getPokemonByName` and `getPokemonById` to return `PokemonWithDetails?`.
- Add `@Insert(onConflict = OnConflictStrategy.REPLACE)` for `List<PokemonVarietyEntity>` and `List<PokemonMoveEntity>`.
- Add a `@Transaction` method `insertFullPokemonDetail` to insert the main entity and its relations.

#### [PokemonRepositoryImpl.kt](file:///C:/Users/brici/code/PokeApiTest/app/src/main/java/com/example/pokeapitest/data/repository/PokemonRepositoryImpl.kt)

- Update `PokemonDto.toEntity` to return a triple or a wrapper containing `PokemonEntity`, `List<PokemonVarietyEntity>`, and `List<PokemonMoveEntity>`.
- Update `getPokemonDetail` to use the new DAO transaction for insertion.
- Move `toDomain()` logic from `PokemonEntity` to `PokemonWithDetails`, removing the manual string splitting and keeping the "shiny" variety generation logic.

## Verification Plan

### Automated Tests
- Run existing unit tests to ensure no regression in domain model logic:
  `./gradlew test`
- Run `PokemonDaoTest` (if it exists or update it) to verify relations are correctly saved and retrieved:
  `./gradlew connectedAndroidTest --tests "com.example.pokeapitest.data.local.PokemonDaoTest"`

### Manual Verification
- Deploy the app and navigate to a Pokemon detail screen.
- Verify that details, varieties, and moves are still displayed correctly.
- Check Logcat for any Room-related errors during migration/first-run.
- Use Layout Inspector to verify the UI reflects the loaded data.
