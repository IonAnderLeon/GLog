# GLog

Aplicación Android para registrar y organizar tu biblioteca de videojuegos: catálogo de juegos, colecciones, favoritos, registros de tiempo jugado y perfil de usuario. Conecta con el backend **GLogPanel** (API REST) para sincronizar datos.

**Temática:** App tipo “log de gamer”: gestionar juegos, colecciones (incluida una de favoritos), horas jugadas y perfil, con una interfaz orientada a jugadores (tema claro/oscuro, paleta cyan/naranja/verde, tipografía escalable).

---

## Requisitos

- **Android Studio** (recomendado Ladybug o superior)
- **JDK 11**
- **minSdk 24** / **targetSdk 36**
- Backend **GLogPanel** en ejecución (o URL de API configurada)

---

## Cómo ejecutar

1. Clona el repositorio y abre el proyecto en Android Studio.
2. Configura la URL base de la API en el proyecto (por defecto apunta al backend GLogPanel, por ejemplo `http://10.0.2.2:8080` en emulador para localhost).
3. Sincroniza Gradle y ejecuta en emulador o dispositivo:
   - **Run** (▶) o `Shift + F10`.
4. La app arranca en la pantalla **Home**; desde la barra inferior puedes ir a **Home**, **Cluster** (colecciones y registros) y **Profile**.

---

## Ejemplo de flujo de datos

Flujo típico: **cargar lista de juegos en Home y mostrarlos en pantalla**.

```
┌─────────────────────────────────────────────────────────────────────────┐
│  UI (Compose)                                                            │
│  HomeScreen → collectAsStateWithLifecycle(uiState)                       │
│  Muestra: recentGames, popularGames, pcGames, adventureGames, error      │
└─────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │  HomeUiState (StateFlow)
                                    │
┌─────────────────────────────────────────────────────────────────────────┐
│  ViewModel                                                               │
│  HomeViewModel.loadGames()                                               │
│  → gameRepository.getAllGames()                                          │
│  → Result → copy(recentGames = ..., popularGames = ..., isLoading = false)│
└─────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │  GameRepository (interfaz de dominio)
                                    │
┌─────────────────────────────────────────────────────────────────────────┐
│  Capa de datos                                                           │
│  GameRepositoryImpl                                                       │
│  → apiService.getAllGames()  (Retrofit, suspend)                         │
│  → dtos.map { gameMapper.toEntity(it) }  → List<Game>                    │
│  → Result.success(games)                                                 │
└─────────────────────────────────────────────────────────────────────────┘
                                    ▲
                                    │  HTTP GET /api/games
                                    │
┌─────────────────────────────────────────────────────────────────────────┐
│  Backend (GLogPanel)                                                     │
│  API REST → MySQL                                                        │
└─────────────────────────────────────────────────────────────────────────┘
```

**Resumen:** La pantalla observa un `StateFlow` del ViewModel. El ViewModel llama al repositorio; el repositorio usa Retrofit para llamar a la API, mapea DTOs a modelos de dominio (`Game`) y devuelve un `Result`. El ViewModel actualiza el estado (listas, loading, error) y la UI se recompone con Compose.

---

## Librerías principales

| Categoría        | Librería | Uso |
|------------------|----------|-----|
| **UI**           | Jetpack Compose (Material3, BOM) | Pantallas, componentes, tema |
| **Navegación**   | Navigation Compose | NavHost, rutas, bottom bar |
| **Inyección**    | Hilt (Dagger) | ViewModels, repositorios, API, DataStore |
| **Red**          | Retrofit + OkHttp + Gson / Moshi | API REST, JSON |
| **Imágenes**     | Coil (Compose) | Carga de imágenes desde URL |
| **Estado**       | StateFlow + Lifecycle (ViewModel) | Estado de pantallas |
| **Preferencias** | DataStore Preferences | Tema oscuro, letra grande |
| **Testing**      | JUnit, MockK, Kotlinx Coroutines Test, MockWebServer | Unit tests |

Versiones gestionadas en `build.gradle.kts` y `gradle/libs.versions.toml`.

---

## Estructura del proyecto (resumen)

```
app/src/main/java/com/example/glog/
├── data/           # API (Retrofit), DTOs, mappers, preferencias (DataStore)
├── di/             # Módulos Hilt (AppModule)
├── domain/         # Modelos (Game, User, Collection, Register…) y repositorios (interfaces)
├── ui/
│   ├── components/ # Componentes reutilizables (MarqueeText, AnimatedSwitch)
│   ├── navigation/ # AppNavHost, BottomNavigationBar, Destination
│   ├── screens/   # Pantallas Compose (Home, Cluster, Profile, GameInfo, etc.)
│   ├── state/     # UiState y estados por pantalla
│   ├── theme/     # GLogTheme, colores, tipografía
│   └── viewmodels/
├── GLogApp.kt
└── MainActivity.kt
```

---

## Componentes interesantes

### Tema (gamer)

- **Tema claro y oscuro** con paleta propia: cyan primario, naranja secundario, verde terciario, rojo para errores.
- **Tipografía escalable**: opción “letra más grande” (DataStore) que escala la tipografía del tema.
- Definido en `ui/theme/` (Theme.kt, Color.kt, Type.kt).

### MarqueeText

- Texto que se desplaza horizontalmente cuando no cabe en el espacio (p. ej. títulos de juegos en cards).
- Animación infinita con `rememberInfiniteTransition`; solo activa el marquesina si el ancho del texto supera el del contenedor.
- Ubicación: `ui/components/MarqueeText.kt`.

### DraggableFAB

- FAB que se puede **arrastrar** por la pantalla y pulsar para crear “Nueva colección” o “Nuevo registro” según la pestaña actual (Cluster).
- Usa `detectDragGestures` y animación con `spring` para suavizar el movimiento.
- Ubicación: `ui/screens/components/DraggableFAB.kt`.

### AnimatedSwitch

- Switch personalizado con animación de desplazamiento (spring) para opciones de ajustes (tema oscuro, letra más grande).
- Ubicación: `ui/components/AnimatedSwitch.kt`.

### Navegación

- **Bottom bar** con tres destinos: Home, Cluster, Profile.
- **Rutas:** `home`, `cluster`, `profile`, `gameInfo/{id}`.
- Definido en `ui/navigation/` (Destination, AppNavHost, BottomNavigationBar).

### Pantallas principales

- **Home:** listas de juegos (recientes, populares, PC, aventura) y búsqueda.
- **Cluster:** pestañas Colecciones y Registros; FAB arrastrable para crear.
- **Profile:** usuario, favoritos, estadísticas (horas, juegos distintos, plataforma favorita), ajustes (tema, letra, nickname).
- **GameInfo:** detalle de juego, valoración, favoritos, juegos similares, descripción expandible.

---

## Relación con GLogPanel

GLog consume la API REST del proyecto **GLogPanel** (Spring Boot): juegos, usuarios, colecciones, registros. La URL base de la API debe coincidir con el servidor donde esté desplegado GLogPanel (en desarrollo, normalmente la IP de tu máquina o `10.0.2.2` en emulador Android).
