# Tema claro / oscuro en GLog

Este documento describe la implementación del **cambio de tema** (claro u oscuro) en la app, persistido con DataStore y accesible desde el diálogo de Ajustes en Profile.

---

## 1. Resumen

- El usuario puede elegir **tema oscuro** o **tema claro** desde **Ajustes** (icono de engranaje en la pantalla **Profile**).
- La preferencia se guarda en **DataStore** y se aplica en toda la app al arrancar y al cambiar el switch.
- Si no hay preferencia guardada, se usa el tema del sistema (`isSystemInDarkTheme()`).

---

## 2. Archivos implicados

### 2.1. Preferencias y datos

| Archivo | Descripción |
|--------|-------------|
| `data/preferences/AppPreferences.kt` | Modelo: `useDarkTheme: Boolean?` (null = seguir sistema). |
| `data/preferences/AppPreferencesDataSource.kt` | Lee/escribe en DataStore: `preferences` (Flow), `setDarkTheme(useDark: Boolean)`. Clave: `use_dark_theme`. |

### 2.2. Inyección y ViewModel

| Archivo | Descripción |
|--------|-------------|
| `di/AppModule.kt` | `provideAppPreferencesDataSource(@ApplicationContext Context)` y `provideAppPreferencesDataSource` como Singleton. |
| `ui/viewmodels/AppPreferencesViewModel.kt` | Expone `preferences: StateFlow<AppPreferences>` y `setDarkTheme(useDark: Boolean)`. ViewModel compartido a nivel de **Activity**. |

### 2.3. Tema y actividad

| Archivo | Descripción |
|--------|-------------|
| `ui/theme/Theme.kt` | `GLogTheme(darkTheme: Boolean, ...)`: aplica `darkColorScheme` o `lightColorScheme` según `darkTheme`. |
| `MainActivity.kt` | Obtiene `AppPreferencesViewModel` con `hiltViewModel()`, lee `prefs` con `collectAsStateWithLifecycle`, y pasa `darkTheme = prefs.useDarkTheme ?: isSystemInDarkTheme()` a `GLogTheme`. |

### 2.4. UI de Ajustes

| Archivo | Descripción |
|--------|-------------|
| `ui/screens/SettingsDialog.kt` | Diálogo con opción **Tema oscuro** y un switch. |
| `ui/screens/UserScreen.kt` | Icono de ajustes en Profile; abre `SettingsDialog` y pasa `useDarkTheme`, `onDarkThemeChange` (llamada a `appPrefsViewModel.setDarkTheme(it)`). |
| `ui/components/AnimatedSwitch.kt` | Switch animado reutilizable (estilo Material, colores del tema). |

---

## 3. Flujo

1. **Al abrir la app:** `MainActivity` obtiene las preferencias; si `useDarkTheme != null` se usa, si no se usa el tema del sistema.
2. **Al entrar en Profile y pulsar el icono de ajustes:** se abre `SettingsDialog` con el switch de “Tema oscuro” en el estado actual (`prefs.useDarkTheme ?: isSystemInDarkTheme()`).
3. **Al cambiar el switch:** se llama a `appPrefsViewModel.setDarkTheme(checked)`; el DataStore se actualiza y el `StateFlow` emite; `MainActivity` recomposición aplica el nuevo `darkTheme` en `GLogTheme`.
4. **Persistencia:** en el siguiente arranque, `AppPreferencesDataSource.preferences` devuelve el último valor guardado.

---

## 4. Dependencias

- **DataStore:** `androidx.datastore:datastore-preferences:1.0.0` en `app/build.gradle.kts`.
- **Hilt:** el ViewModel de preferencias debe ser el mismo en `MainActivity` y en Profile; en Profile se usa `hiltViewModel(LocalContext.current as Activity)` para obtener el ViewModel a nivel de Activity.
