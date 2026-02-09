# Colecciones en GLog – Guía paso a paso

Este documento describe cómo está implementada la funcionalidad de **Colecciones** en la app GLog, siguiendo la misma estructura que **Games** y **Registros** (ViewModel, Hilt, Repository, API).

---

## 1. Resumen de la arquitectura

La pantalla de Collections (pestaña "Collections" dentro de Cluster) sigue una arquitectura en capas:

```
UI (Collection + CollectionInfoScreen)
    ↓ eventos / estado
CollectionViewModel
    ↓ llamadas
CollectionRepository (interfaz)
    ↓ implementación
CollectionRepositoryImpl
    ↓ API + mapeo
GLogApiService + CollectionMapper (+ GameMapper para juegos dentro de colecciones)
    ↓
Spring Boot API (backend)
```

Hay **dos vistas**: lista de colecciones en grid y, al tocar una, pantalla de detalle (nombre, descripción, juegos de la colección).

---

## 2. Lo que se ha creado o modificado

### 2.1. Capa de dominio (ya existía, se reutiliza)

| Archivo | Descripción |
|--------|-------------|
| `domain/model/Collection.kt` | Modelo de datos: `id`, `name`, `description`, `gameIds`, `games` (lista de `Game`). |
| `domain/repository/CollectionRepository.kt` | Interfaz: `getCollections`, `getCollectionById`, `createCollection`, `updateCollection(id, collection)`, `deleteCollection`. |

### 2.2. Capa de datos (nueva o ya existente)

| Archivo | Descripción |
|--------|-------------|
| `domain/repository/CollectionRepositoryImpl.kt` | **Nuevo.** Implementación: llama a la API, mapea DTOs a `Collection` y maneja errores con `Result`. |
| `data/network/dto/CollectionDTO.kt` | DTO para crear/actualizar: `idCollection`, `name`, `description`. |
| `data/network/dto/CollectionGamesDTO.kt` | DTO que devuelve la API: `collection` (CollectionDTO) + `games` (lista de `GameDTO`). |
| `data/mapper/CollectionMapper.kt` | **Implementado.** Convierte `CollectionGamesDTO` → `Collection` (usando `GameMapper` para cada juego) y `Collection` → `CollectionDTO`. |
| `data/mapper/GameMapper.kt` | **Modificado.** Se añade `toEntity(GameDTO)` para mapear juegos dentro de colecciones (la API usa `GameDTO` en `CollectionGamesDTO`, no `GameDetailDTO`). |
| `data/network/api/GLogApiService.kt` | Ya tenía los endpoints de colecciones (`getCollections`, `getCollectionById`, `createCollection`, etc.). |
| `data/network/routes/K.kt` | Rutas: `COLLECTIONS`, `COLLECTION_BY_ID`. |

### 2.3. Inyección de dependencias (Hilt)

| Archivo | Cambio |
|--------|--------|
| `di/AppModule.kt` | Se añaden `provideCollectionMapper(GameMapper)` y `provideCollectionRepository()`. El ViewModel recibe `CollectionRepository` inyectado. |

### 2.4. UI: estado y eventos

| Archivo | Descripción |
|--------|-------------|
| `ui/state/CollectionState.kt` | **Nuevo.** Estado: `collections`, `selectedCollection`, `isLoading`, `error`, `searchQuery`. Eventos: `LoadCollections`, `SearchCollections(query)`, `SelectCollection(collection?)`, `UpdateCollection(collection)`. |

### 2.5. ViewModel

| Archivo | Descripción |
|--------|-------------|
| `ui/viewmodels/CollectionViewModel.kt` | **Nuevo.** `@HiltViewModel`, recibe `CollectionRepository`. Expone `state` y `onEvent()`. Carga lista, selecciona colección, **actualiza colección** (`UpdateCollection` → `updateCollection(id, collection)` en el repo) y opcionalmente `loadCollectionDetail(id)`. |

### 2.6. Pantallas

| Archivo | Descripción |
|--------|-------------|
| `ui/screens/CollectionScreen.kt` | **Implementado.** Composable `Collection()`: si no hay `selectedCollection`, muestra **grid de 2 columnas** de cards. Si hay `selectedCollection`, muestra **CollectionInfoScreen** (nombre con **icono lápiz** para editar, descripción, grid de juegos, botón atrás). En modo edición: campo de texto + **icono tick** para guardar; al pulsar tick se dispara `UpdateCollection(collection)` y se llama al API de update. |

---

## 3. Flujo paso a paso

### 3.1. Al abrir la pestaña "Collections"

1. Se muestra el composable `Collection()` (dentro de la primera página del pager de Cluster).
2. Se obtiene `CollectionViewModel` con `hiltViewModel()`.
3. En un `LaunchedEffect(Unit)` se lanza `viewModel.onEvent(CollectionEvent.LoadCollections)`.
4. El ViewModel pone `isLoading = true` y llama a `collectionRepository.getCollections(search = null)`.
5. `CollectionRepositoryImpl` llama a `apiService.getCollections(null)` (GET a la API).
6. La API devuelve una lista de `CollectionGamesDTO` (cada uno con `collection` + `games`).
7. El repositorio mapea cada DTO a `Collection` con `CollectionMapper.toEntity()` (que a su vez usa `GameMapper.toEntity(GameDTO)` para cada juego) y devuelve `Result.success(collections)`.
8. El ViewModel actualiza el estado: `collections = lista`, `isLoading = false`, `error = null`.
9. La UI se recompone: se muestra el grid de cards (`CollectionCard`): imagen (primer juego o placeholder), nombre, estilo tipo “gamer” (borde, esquinas redondeadas).

### 3.2. Al tocar una colección

1. Se llama a `viewModel.onEvent(CollectionEvent.SelectCollection(collection))`.
2. El ViewModel actualiza `selectedCollection = collection` (la misma que ya venía en la lista, con sus `games`).
3. La UI recompone y deja de mostrar el grid; muestra **CollectionInfoScreen**: barra con botón atrás y nombre, descripción (si existe), y grid de juegos de la colección.

### 3.3. Volver al listado

1. El usuario toca el botón atrás en `CollectionInfoScreen`.
2. Se llama a `viewModel.onEvent(CollectionEvent.SelectCollection(null))`.
3. El ViewModel pone `selectedCollection = null`.
4. La UI vuelve a mostrar el grid de colecciones.

### 3.4. Si la API falla

1. `CollectionRepositoryImpl` captura la excepción y devuelve `Result.failure(e)`.
2. El ViewModel actualiza `error = e.message`, `isLoading = false`.
3. La pantalla muestra el mensaje de error en rojo arriba del contenido.

### 3.5. Editar nombre de la colección (update)

1. El usuario está en **CollectionInfoScreen** (detalle de una colección). A la derecha del nombre hay un **icono de lápiz**.
2. Al tocar el lápiz, la fila pasa a **modo edición**: el nombre se muestra en un **OutlinedTextField** y el icono cambia a un **tick** (check / “ok”).
3. El usuario modifica el texto y toca el **tick**.
4. Se llama a `viewModel.onEvent(CollectionEvent.UpdateCollection(collection.copy(name = editedName)))` (el nombre se trimea; si queda en blanco se mantiene el anterior).
5. El ViewModel pone `isLoading = true` y llama a `collectionRepository.updateCollection(collection.id, collection)`.
6. `CollectionRepositoryImpl` hace **PUT** a la API con `CollectionMapper.toDto(collection)` (id, name, description).
7. Si la API responde bien, el ViewModel actualiza `selectedCollection` con la colección actualizada y la lista `collections` (sustituye la colección con el mismo id). Si falla, se actualiza `error` y se muestra en pantalla.
8. La UI sale del modo edición (vuelve a mostrar el nombre en texto y el icono de lápiz).

El estado de “estoy editando” y el texto temporal (`editedName`) son **estado local** en `CollectionInfoScreen` (`isEditingName`, `editedName` con `remember(collection.id)` y `LaunchedEffect(collection)` para sincronizar con el nombre actual).

### 3.6. Búsqueda (preparado para el futuro)

- El estado tiene `searchQuery` y el evento `SearchCollections(query)`.
- En el ViewModel, `loadCollections(search)` puede recibir el query y pasarlo a `getCollections(search)`.
- Para activarlo basta conectar un campo de búsqueda que llame a `onEvent(CollectionEvent.SearchCollections(text))`.

---

## 4. Cómo encaja con la API (Spring Boot)

- **GET lista:** `GET /api/collections?busqueda=opcional` → lista de objetos con estructura tipo `CollectionGamesDTO` (`collection` + `games`).
- **GET por id:** `GET /api/collections/{id}` → un `CollectionGamesDTO` (detalle de una colección con sus juegos).
- **POST/PUT/DELETE:** ya definidos en `GLogApiService`; el repositorio los usa en `createCollection`, `updateCollection(id, body)`, `deleteCollection(id)`.

Los juegos dentro de una colección vienen como `GameDTO` (id, name, image, year, rating, genreId, platformId). El `GameMapper.toEntity(GameDTO)` usa `formatGenre` y `formatPlatform` para obtener los nombres de género y plataforma a partir de los IDs.

---

## 5. Dónde está cada cosa (rutas en el proyecto)

```
app/src/main/java/com/example/glog/
├── data/
│   ├── mapper/
│   │   ├── CollectionMapper.kt    ← implementado (depende de GameMapper)
│   │   └── GameMapper.kt          ← añadido toEntity(GameDTO)
│   └── network/
│       ├── api/
│       │   └── GLogApiService.kt
│       ├── dto/
│       │   ├── CollectionDTO.kt
│       │   └── CollectionGamesDTO.kt
│       └── routes/
│           └── K.kt
├── di/
│   └── AppModule.kt               ← provideCollectionMapper, provideCollectionRepository
├── domain/
│   ├── model/
│   │   └── Collection.kt
│   └── repository/
│       ├── CollectionRepository.kt
│       └── CollectionRepositoryImpl.kt   ← nuevo
├── ui/
│   ├── screens/
│   │   └── CollectionScreen.kt    ← Collection() + CollectionInfoScreen
│   ├── state/
│   │   └── CollectionState.kt     ← nuevo
│   └── viewmodels/
│       └── CollectionViewModel.kt ← nuevo
```

---

## 6. Resumen

- **Repository:** implementación que habla con la API y mapea `CollectionGamesDTO` / `CollectionDTO` a `Collection`.
- **CollectionMapper:** depende de `GameMapper` para convertir la lista de `GameDTO` dentro de cada colección en `List<Game>`.
- **Hilt:** inyecta `CollectionMapper` (con `GameMapper`) y `CollectionRepository` en el ViewModel.
- **ViewModel:** reacciona a eventos (cargar lista, buscar, seleccionar colección), mantiene un único estado (`CollectionState`) y opcionalmente permite cargar detalle por id con `loadCollectionDetail(id)`.
- **Pantallas:** lista en grid de 2 columnas con cards “gamer”; al tocar una se muestra el detalle (nombre, descripción, grid de juegos) con botón atrás. En el detalle, **editar nombre**: icono lápiz → campo de texto + icono tick → al pulsar tick se envía `UpdateCollection` y se hace PUT a la API; el estado se actualiza con la colección modificada.

Con esto, la pestaña Collections queda alineada con la estructura de Games y Registros, incluye **edición del nombre** (update) desde el detalle, y está lista para ampliar con búsqueda, crear/borrar colecciones o enlaces a ficha de juego.
