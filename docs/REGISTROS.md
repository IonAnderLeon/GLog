# Registros en GLog – Guía paso a paso

Este documento describe cómo está implementada la funcionalidad de **Registros** en la app GLog, siguiendo la misma estructura que **Games** (ViewModel, Hilt, Repository, API).

---

## 1. Resumen de la arquitectura

La pantalla de Registro (pestaña "Registro" dentro de Cluster) sigue una arquitectura en capas:

```
UI (RegisterScreen)
    ↓ eventos / estado
RegisterViewModel
    ↓ llamadas
RegisterRepository (interfaz)
    ↓ implementación
RegisterRepositoryImpl
    ↓ API + mapeo
GLogApiService + RegisterMapper
    ↓
Spring Boot API (backend)
```

---

## 2. Lo que se ha creado o modificado

### 2.1. Capa de dominio (ya existía, se reutiliza)

| Archivo | Descripción |
|--------|-------------|
| `domain/model/Register.kt` | Modelo de datos: `id`, `date`, `playtime`, `gameId`, `gameName`, `gameImageUrl` (URL de la portada del juego), `userId`, `userName`. |
| `domain/repository/RegisterRepository.kt` | Interfaz del repositorio: `getRegisters`, `getRegistersByUser`, `createRegister`, `updateRegister`, `deleteRegister`. |

### 2.2. Capa de datos (nueva o ya existente)

| Archivo | Descripción |
|--------|-------------|
| `domain/repository/RegisterRepositoryImpl.kt` | **Nuevo.** Implementación del repositorio: llama a la API, mapea DTO → `Register` y maneja errores con `Result`. |
| `data/network/dto/RegisterDTO.kt` | DTO para crear/actualizar (campos que envía el cliente). |
| `data/network/dto/RegisterDetailDTO.kt` | DTO de detalle que devuelve la API (incluye `gameName`, `gameImageUrl`, `userName`, `register`). |
| `data/mapper/RegisterMapper.kt` | Convierte `RegisterDetailDTO` → `Register` (incl. `gameImageUrl`) y `Register` → `RegisterDTO`. |
| `data/network/api/GLogApiService.kt` | Ya tenía los endpoints de registros (`getRegisters`, `getRegistersByUser`, etc.). |
| `data/network/routes/K.kt` | Rutas: `REGISTERS`, `REGISTERS_BY_ID`, `REGISTERS_BY_USER`. |

### 2.3. Inyección de dependencias (Hilt)

| Archivo | Cambio |
|--------|--------|
| `di/AppModule.kt` | Se añaden `provideRegisterMapper()` y `provideRegisterRepository()`. Así, cuando el ViewModel pide `RegisterRepository`, Hilt inyecta `RegisterRepositoryImpl` con `GLogApiService` y `RegisterMapper`. |

### 2.4. UI: estado y eventos

| Archivo | Descripción |
|--------|-------------|
| `ui/state/RegisterState.kt` | **Nuevo.** Estado de la pantalla: `registers`, `isLoading`, `error`, `searchQuery`. Eventos: `LoadRegisters`, `SearchRegisters(query)`. |

### 2.5. ViewModel

| Archivo | Descripción |
|--------|-------------|
| `ui/viewmodels/RegisterViewModel.kt` | **Nuevo.** `@HiltViewModel`, recibe `RegisterRepository`. Expone `state` (StateFlow) y `onEvent()`. Al recibir `LoadRegisters` o `SearchRegisters`, llama al repositorio y actualiza el estado. |

### 2.6. Pantalla

| Archivo | Descripción |
|--------|-------------|
| `ui/screens/RegisterScreen.kt` | **Modificado.** Usa `RegisterViewModel` con `hiltViewModel()`, recoge el estado con `collectAsStateWithLifecycle`, dispara `LoadRegisters` al entrar. Cada tarjeta (`RegisterCard`) muestra la **imagen del juego** con `AsyncImage(register.gameImageUrl)`; si la API no envía URL, se usa `placeholder`. |

---

## 3. Flujo paso a paso

### 3.1. Al abrir la pestaña "Registro"

1. Se muestra `RegisterScreen`.
2. `RegisterScreen` obtiene `RegisterViewModel` con `hiltViewModel()`.
3. En un `LaunchedEffect(Unit)` se lanza `viewModel.onEvent(RegisterEvent.LoadRegisters)`.
4. El ViewModel pone `isLoading = true` y llama a `registerRepository.getRegisters(search = null)`.
5. `RegisterRepositoryImpl` llama a `apiService.getRegisters(null)` (GET a la API).
6. La API devuelve una lista de `RegisterDetailDTO`.
7. El repositorio mapea cada DTO a `Register` con `RegisterMapper.toEntity()` y devuelve `Result.success(registers)`.
8. El ViewModel actualiza el estado: `registers = lista`, `isLoading = false`, `error = null`.
9. La UI se recompone: se muestra la lista en un `LazyColumn` de tarjetas (`RegisterCard`).

### 3.2. Si la API falla

1. `RegisterRepositoryImpl` captura la excepción y devuelve `Result.failure(e)`.
2. El ViewModel actualiza `error = e.message`, `isLoading = false`.
3. `RegisterScreen` muestra el mensaje de error en rojo arriba de la lista.

### 3.3. Búsqueda (preparado para el futuro)

- El estado tiene `searchQuery` y el evento `SearchRegisters(query)`.
- En el ViewModel, `loadRegisters(search)` usa ese parámetro y podría pasarlo a `getRegisters(search)`.
- Para activarlo solo hay que conectar en la UI un campo de búsqueda que llame a `onEvent(RegisterEvent.SearchRegisters(text))`.

---

## 4. Cómo encaja con la API (Spring Boot)

- **GET registros:** `GET /api/registers?busqueda=opcional` → lista de objetos con estructura tipo `RegisterDetailDTO` (p. ej. `register`, `gameName`, `userName`).
- **GET por usuario:** `GET /api/registers/user/{userId}`.
- **POST/PUT/DELETE:** ya definidos en `GLogApiService`; el repositorio los usa en `createRegister`, `updateRegister` y `deleteRegister`.

Si tu backend devuelve otros nombres de campo (p. ej. snake_case), hay que alinear los DTOs (o usar anotaciones `@SerializedName` de Gson) para que el mapeo siga siendo correcto.

### 4.1. Imagen del juego en la card

Cada registro hace referencia a un juego. En la tarjeta se muestra la **portada del juego** a la izquierda:

- **En la app (GLog):** Se usa **`AsyncImage`** (Coil) con `model = register.gameImageUrl`. Si `gameImageUrl` viene rellenado desde la API, se carga esa imagen; si es `null` o vacío, se muestra el **placeholder** (`R.drawable.placeholder`). También se usa placeholder como `error` si la URL falla al cargar.
- **En el modelo:** `Register` tiene el campo **`gameImageUrl: String? = null`**.
- **En la API (Android):** El DTO `RegisterDetailDTO` tiene **`gameImageUrl`** con `@SerialName("gameImageUrl")`. Si tu backend devuelve el campo con otro nombre (p. ej. `gameImage`, `image`), cambia el `@SerialName` en `RegisterDetailDTO.kt` para que coincida.
- **URL de la imagen:** La app usa el valor que envía la API **tal cual** (p. ej. una URL completa tipo `https://raw.githubusercontent.com/...` o la que guardes en el backend). No se monta ninguna URL extra en la app.

#### Si sale el placeholder: el backend no enviaba la imagen (GLogPanel)

El problema era que **GLogPanel** (Spring Boot) no incluía la imagen del juego en la respuesta de registros. Se ha solucionado así:

1. **`RegisterWithDetailsDto.kt` (GLogPanel):** Se añade el campo **`gameImageUrl: String? = null`**. Jackson lo serializa en el JSON como `"gameImageUrl"`.
2. **`RegisterRepository.kt` (GLogPanel):** En las cuatro consultas (`findAllWithUserAndGame`, `findAllWithUserAndGameBySearch`, `findByIdWithUserAndGame`, `findByUserIdWithUserAndGame`) se añade **`g.image`** al `SELECT` (además de `r`, `u.nickname`, `g.name`), para que cada fila devuelva también la imagen del juego.
3. **`RegisterService.kt` (GLogPanel):** Al mapear cada resultado a `RegisterWithDetailsDto`, se toma **`result[3]`** como `gameImageUrl` (el valor de `g.image`) y se pasa al DTO.

Tras reiniciar GLogPanel y volver a cargar la pestaña Registro en la app, la respuesta de la API incluye `gameImageUrl`. La app muestra esa URL directamente en `AsyncImage` (p. ej. si en base de datos guardas una URL completa como `https://raw.githubusercontent.com/...`, se usa tal cual).

---

## 5. Dónde está cada cosa (rutas en el proyecto)

```
app/src/main/java/com/example/glog/
├── data/
│   ├── mapper/
│   │   └── RegisterMapper.kt
│   └── network/
│       ├── api/
│       │   └── GLogApiService.kt
│       ├── dto/
│       │   ├── RegisterDTO.kt
│       │   └── RegisterDetailDTO.kt
│       └── routes/
│           └── K.kt
├── di/
│   └── AppModule.kt
├── domain/
│   ├── model/
│   │   └── Register.kt
│   └── repository/
│       ├── RegisterRepository.kt
│       └── RegisterRepositoryImpl.kt   ← nuevo
├── ui/
│   ├── screens/
│   │   └── RegisterScreen.kt
│   ├── state/
│   │   └── RegisterState.kt          ← nuevo
│   └── viewmodels/
│       └── RegisterViewModel.kt      ← nuevo
```

---

## 6. Resumen

- **Repository:** implementación que habla con la API y mapea DTOs a `Register`.
- **Hilt:** inyecta `RegisterMapper` y `RegisterRepository` en el ViewModel.
- **ViewModel:** reacciona a eventos, llama al repositorio y actualiza un único estado (`RegisterState`).
- **Pantalla:** observa el estado, muestra lista, loading y error, y dispara `LoadRegisters` al entrar. Cada tarjeta muestra la imagen del juego con **AsyncImage** usando `register.gameImageUrl` (o placeholder si no viene en la API).

Con esto, la pestaña Registro queda alineada con la estructura de Games (ViewModel, Hilt, Repository), muestra la portada del juego cuando la API la envía, y está lista para ampliar con búsqueda, crear/editar/borrar desde la UI o filtrar por usuario.
