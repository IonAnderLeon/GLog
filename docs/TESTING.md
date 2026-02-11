# Testing en GLog

Documentación del testing unitario del proyecto GLog: dependencias, estructura, convenciones y cómo ejecutar los tests.

---

## 1. Dependencias de test

En `app/build.gradle.kts`:

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| `junit` | (libs.junit) | JUnit 4 para tests |
| `io.mockk:mockk` | 1.13.13 | Mocks en Kotlin |
| `kotlinx-coroutines-test` | 1.8.1 | `runTest`, `TestDispatcher` para coroutines |
| `androidx.arch.core:core-testing` | 2.2.0 | Utilidades para tests de arquitectura |
| `okhttp:mockwebserver` | 4.12.0 | Servidor HTTP fake (tests de API/red si se usa) |
| `moshi` / `moshi-kotlin` | 1.15.0 | Serialización JSON en tests (si aplica) |

No se usa el matcher `any()` de MockK (puede dar problemas de resolución). En su lugar se usan **argumentos concretos** o DTOs construidos con los mappers en los `coEvery`.

---

## 2. Estructura de tests

```
app/src/test/java/com/example/glog/
├── MainCoroutineRule.kt              # Regla para Dispatchers.Main en tests
├── ExampleUnitTest.kt                # Test de ejemplo (opcional)
├── data/
│   ├── mapper/
│   │   ├── CollectionMapperTest.kt
│   │   ├── GameMapperTest.kt
│   │   ├── RegisterMapperTest.kt
│   │   ├── UserMapperTest.kt
│   │   └── constants/
│   │       ├── GenreConstantsTest.kt
│   │       ├── PlatformConstantsTest.kt
│   │       └── UtilsTest.kt
│   └── network/
│       ├── api/
│       │   ├── GLogApiServiceTest.kt
│       │   └── GLogApiServiceRealTest.kt   # (si existe)
│       ├── dto/
│       │   └── DataDtosTest.kt
│       └── routes/
│           └── KTest.kt
└── ui/
    ├── navigation/
    │   └── DestinationTest.kt
    ├── usecase/
    │   └── GetFavoriteGamesUseCaseTest.kt
    └── viewmodels/
        ├── GameInfoViewModelTest.kt
        ├── GameSearchViewModelTest.kt
        ├── HomeViewModelTest.kt
        ├── RegisterViewModelTest.kt
        └── UserViewModelTest.kt
```

---

## 3. Qué se testea

### 3.1 Regla compartida: `MainCoroutineRule`

- **Ubicación:** `MainCoroutineRule.kt`
- **Función:** Asigna `Dispatchers.Main` a un `UnconfinedTestDispatcher` durante el test y lo restaura al final.
- **Uso:** Todos los tests de ViewModels que usan `viewModelScope` deben usar esta regla con `@get:Rule val mainCoroutineRule = MainCoroutineRule()` para que las coroutines se ejecuten de forma determinista.

### 3.2 Capa `data`

#### Mappers

- **GameMapperTest:** `toEntity(GameDTO)`, `toEntity(GameDetailDTO)` con datos completos, nulos, valores por defecto e imagen/descripción en blanco.
- **CollectionMapperTest:** `toEntity(CollectionGamesDTO)`, `toDto(Collection)`, colección/games nulos, lista de games con nulls (`mapNotNull`).
- **RegisterMapperTest:** `toEntity(RegisterDetailDTO)`, `toDto(Register)`, register nulo, `gameImageUrl` en blanco.
- **UserMapperTest:** `toEntity(UserDTO)`, nickname nulo, id nulo, `UserDTO.forUpdate`.

#### Constantes y utilidades

- **GenreConstantsTest:** IDs de género conocidos (1–11), IDs desconocidos (0, 99, -1).
- **PlatformConstantsTest:** IDs de plataforma conocidos (1–8, 17), IDs desconocidos.
- **UtilsTest:** Funciones `internal` de `Utils`: `formatEmpty`, `formatGenre`, `formatPlatform`, `formatGenres`, `formatPlatforms` (con `with(Utils) { ... }`).

#### Red y DTOs

- **GLogApiServiceTest:** Contrato de la API ejercitado a través de los repositorios. Se mockea `GLogApiService` y se comprueba que cada repositorio (Game, User, Register, Collection) llama a la API correcta y mapea bien. Se usan **argumentos concretos** en los `coEvery` (null, DTOs construidos con los mappers, etc.), sin `any()`.
- **DataDtosTest:** DTOs y `AppPreferences`: constructores por defecto, con valores, `copy()`, `equals`/`hashCode`/`toString` donde aplica (AddGameToCollectionDTO, AppPreferences, CollectionDTO, CollectionGamesDTO, GameDTO, GameDetailDTO, RegisterDTO, RegisterDetailDTO, UserDTO).
- **KTest:** Constantes de rutas del objeto `K` (BASE_URL, QUERY_SEARCH, GAMES, USERS, REGISTERS, COLLECTIONS, etc.).

### 3.3 Capa `ui`

#### Navegación

- **DestinationTest:** Rutas de `Destination` (Home, Cluster, Profile, GameDetails), `GameDetails.createRoute`, `bottomNavItems`.

#### Casos de uso

- **GetFavoriteGamesUseCaseTest:** `invoke()` con colección con juegos, colección vacía y fallo del repositorio (mock de `CollectionRepository`).

#### ViewModels

- **HomeViewModelTest:** `loadGames` (éxito con secciones, límite 10, fallo), `onSearchTextChange`, `onToggleSearch`. Mock de `GameRepository`.
- **GameInfoViewModelTest:** `loadGame` (éxito con similares, juego en favoritos, fallo), `clearToastMessage`. Mocks de `GameRepository` y `CollectionRepository`.
- **UserViewModelTest:** `loadUserData` (éxito con user y favoritos, fallo de usuario, fallo solo de favoritos). Mocks de `UserRepository`, `RegisterRepository`, `GetFavoriteGamesUseCase`.
- **RegisterViewModelTest:** `LoadRegisters` (éxito/fallo), `SearchRegisters`. Mock de `RegisterRepository`.
- **GameSearchViewModelTest:** `searchGames` con query en blanco, `clearSearch`. Mock de `GameRepository`.

---

## 4. Cómo ejecutar los tests

### Desde Android Studio

- Clic derecho en `app/src/test` → **Run 'Tests in test'**.
- O en una clase de test → **Run** (ej. `GLogApiServiceTest`).
- Para cobertura: **Run 'Tests in test' with Coverage** (o Run with Coverage en la clase deseada).

### Desde terminal

```bash
# Todos los tests unitarios del módulo app
./gradlew :app:testDebugUnitTest

# Con informe HTML (en app/build/reports/tests/...)
./gradlew :app:testDebugUnitTest --info
```

Los reportes se generan en `app/build/reports/tests/testDebugUnitTest/` y los resultados en `app/build/test-results/testDebugUnitTest/`.

---

## 5. Convenciones y buenas prácticas

1. **No usar `any()` de MockK**  
   Evitar `coEvery { api.foo(any()) }` si el compilador no resuelve `any`. Usar en su lugar el valor exacto que se pasa en el test (por ejemplo `null`, un DTO construido con el mapper, o un valor literal).

2. **ViewModels y coroutines**  
   Usar `MainCoroutineRule` y `runTest { ... }` para tests que llaman a métodos del ViewModel que lanzan coroutines con `viewModelScope`.

3. **Use cases y repositorios**  
   Mockear las dependencias (repositorios, API) con `coEvery { ... } returns ...` o `returns Result.success(...)` / `Result.failure(...)` según el caso.

4. **Funciones `internal` (Utils)**  
   Se testean desde el mismo módulo usando `with(Utils) { ... }` para llamar a las extensiones.

5. **DTOs y cobertura**  
   Para subir cobertura de data classes: constructores vacíos, constructores con distintos campos, `copy()` y, si aporta, `equals`/`hashCode`/`toString`.

6. **API (GLogApiService)**  
   La interfaz no tiene lógica; se “cubre” indirectamente testeando los repositorios que la usan, con la API mockeada y argumentos concretos en los `coEvery`.

---

## 6. Resumen rápido

| Área | Archivos de test | Qué se valida |
|------|------------------|----------------|
| Mappers | GameMapper, CollectionMapper, RegisterMapper, UserMapper | DTO → dominio y dominio → DTO, nulos y defaults |
| Constantes | GenreConstants, PlatformConstants, Utils | Valores por ID y formateo |
| Red | GLogApiServiceTest, KTest | Uso del API vía repos, rutas K |
| DTOs | DataDtosTest | Creación, copy, equals de DTOs y AppPreferences |
| Navegación | DestinationTest | Rutas y bottomNavItems |
| Use case | GetFavoriteGamesUseCaseTest | Favoritos desde colección 1 |
| ViewModels | Home, GameInfo, User, Register, GameSearch | Estados de UI y llamadas a repos/use cases |

Si añades nuevos ViewModels, repositorios o mappers, conviene seguir el mismo patrón (mock de dependencias, argumentos concretos, `MainCoroutineRule` cuando haya coroutines en el ViewModel).
