# Chip agregar / quitar de favoritos en GLog

Este documento describe la implementación del **chip de favoritos** en la pantalla de detalle de un juego (GameInfo): agregar a la colección de favoritos (id 1) o quitarlo, con icono y texto que reflejan el estado.

---

## 1. Resumen

- En **GameInfoScreen** (detalle de un juego) hay un **chip** debajo de la línea **GENRE**.
- **Si el juego no está en favoritos:** texto "Agregar a favoritos", icono `FavoriteBorder`; al pulsar se añade a la colección con **id 1** (favoritos).
- **Si ya está en favoritos:** texto "Quitar de favoritos", icono `Favorite` (relleno); al pulsar se elimina de esa colección.
- El estado se comprueba al cargar el juego (consultando la colección 1) y se actualiza tras cada acción.

---

## 2. Archivos implicados

### 2.1. Capa de datos y API

| Archivo | Descripción |
|--------|-------------|
| `data/network/routes/K.kt` | `COLLECTION_ADD_GAME`, `COLLECTION_REMOVE_GAME = "collections/{collectionId}/games/{gameId}"`. |
| `data/network/api/GLogApiService.kt` | `addGameToCollection(collectionId, body)`, `removeGameFromCollection(collectionId, gameId)` con `@DELETE`. |
| `domain/repository/CollectionRepository.kt` | `addGameToCollection(collectionId, gameId)`, `removeGameFromCollection(collectionId, gameId)`. |
| `domain/repository/CollectionRepositoryImpl.kt` | Implementación de ambos; para remove se usa `deleteById(GameCollectionId(...))` en el backend. |

### 2.2. ViewModel y estado

| Archivo | Descripción |
|--------|-------------|
| `ui/state/GameInfoUiState.kt` | Campos `isInFavorites: Boolean`, `isUpdatingFavorites: Boolean`. |
| `ui/viewmodels/GameInfoViewModel.kt` | Inyecta `CollectionRepository`. Al cargar el juego, obtiene la colección 1 y pone `isInFavorites = games.any { it.id == game.id }`. Método **`toggleFavorites()`**: si está en favoritos llama a `removeGameFromCollection(1, game.id)`, si no a `addGameToCollection(1, game.id)`; actualiza `isInFavorites` e `isUpdatingFavorites`. |

### 2.3. UI

| Archivo | Descripción |
|--------|-------------|
| `ui/screens/GameInfoScreen.kt` | **TopGameInfo** recibe `isInFavorites`, `isUpdatingFavorites`, `onToggleFavorites`. Debajo de "GENRE · ..." se muestra **FavoritesChip** (AssistChip) con texto "Agregar a favoritos" / "Quitar de favoritos", icono `Icons.Outlined.FavoriteBorder` o `Icons.Filled.Favorite`. Al pulsar se llama a `onToggleFavorites` salvo si `isUpdatingFavorites`. |

### 2.4. Dependencia

| Archivo | Descripción |
|--------|-------------|
| `app/build.gradle.kts` | `implementation("androidx.compose.material:material-icons-extended")` para usar `Icons.Outlined.FavoriteBorder`. |

---

## 3. Flujo

1. **Al abrir el detalle de un juego:** `GameInfoViewModel.loadGame(id)` carga el juego y, con `getCollectionById(1)`, comprueba si el juego está en la lista de juegos de la colección 1; asigna `isInFavorites`.
2. **Al pulsar el chip cuando no está en favoritos:** `toggleFavorites()` → `addGameToCollection(1, game.id)` → backend `POST /api/collections/1/games`; tras éxito, `isInFavorites = true`.
3. **Al pulsar el chip cuando ya está en favoritos:** `toggleFavorites()` → `removeGameFromCollection(1, game.id)` → backend `DELETE /api/collections/1/games/{gameId}`; tras éxito, `isInFavorites = false`.
4. Mientras se ejecuta la petición, `isUpdatingFavorites = true` y el chip se deshabilita para evitar doble pulsación.

---

## 4. Backend (GLogPanel)

Para que **quitar de favoritos** funcione, el backend debe exponer:

- **DELETE** `/api/collections/{collectionId}/games/{gameId}`

En GLogPanel:

- **CollectionApiController:** método `removeGameFromCollection(collectionId, gameId)` con `@DeleteMapping("/{collectionId}/games/{gameId}")`.
- **GameCollectionService:** método `removeGameFromCollection(idCollection, idGame)` que usa `GameCollectionId(idCollection, idGame)` y `existsById` + `deleteById`.

La colección con **id 1** se considera la de favoritos (misma que usa la sección "Favoritos" en Profile).
