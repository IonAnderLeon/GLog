# Letra más grande (accesibilidad) en GLog

Este documento describe la implementación de la opción **Letra más grande** en toda la app: preferencia en Ajustes, persistencia con DataStore y aplicación global mediante `LocalDensity` y tipografía escalada.

---

## 1. Resumen

- El usuario puede activar **Letra más grande** desde **Ajustes** (Profile).
- La preferencia se guarda en **DataStore** y se aplica en **toda la app**: tanto los textos que usan `MaterialTheme.typography` como los que usan `fontSize = X.sp` se escalan (factor 1.15).
- Se combinan dos mecanismos: **tipografía escalada** y **fontScale** en `LocalDensity`.

---

## 2. Archivos implicados

### 2.1. Preferencias

| Archivo | Descripción |
|--------|-------------|
| `data/preferences/AppPreferences.kt` | Incluye `useLargeText: Boolean = false`. |
| `data/preferences/AppPreferencesDataSource.kt` | Clave `use_large_text`; método `setLargeText(useLarge: Boolean)`. |

### 2.2. ViewModel y actividad

| Archivo | Descripción |
|--------|-------------|
| `ui/viewmodels/AppPreferencesViewModel.kt` | Expone `preferences` y `setLargeText(useLarge: Boolean)`. |
| `MainActivity.kt` | Pasa `useLargeText = prefs.useLargeText` a `GLogTheme`. |

### 2.3. Tema y tipografía

| Archivo | Descripción |
|--------|-------------|
| `ui/theme/Theme.kt` | `GLogTheme(useLargeText: Boolean, ...)`: si `useLargeText`, usa `scaledTypography(LARGE_TEXT_SCALE)` y envuelve el contenido en `CompositionLocalProvider(LocalDensity)` con `fontScale = LARGE_TEXT_SCALE` (1.15f). Así se escalan todos los `sp` de la app. |
| `ui/theme/Type.kt` | `scaledTypography(scale: Float)`: devuelve una `Typography` con cada estilo (bodyLarge, titleLarge, etc.) con `fontSize` multiplicado por `scale`. Se usa además el `fontScale` de `LocalDensity` para textos con tamaño fijo en `sp`. |

### 2.4. UI de Ajustes

| Archivo | Descripción |
|--------|-------------|
| `ui/screens/SettingsDialog.kt` | Opción **Letra más grande** con `AnimatedSwitch`; callbacks `useLargeText` y `onLargeTextChange`. |
| `ui/screens/UserScreen.kt` | Pasa al diálogo el estado y `appPrefsViewModel.setLargeText(it)`. |

---

## 3. Cómo se aplica en todo el proyecto

1. **Tipografía de Material:** cuando `useLargeText` es true, `MaterialTheme.typography` es el resultado de `scaledTypography(1.15f)`, así que todos los composables que usan `MaterialTheme.typography.bodyLarge`, `titleLarge`, etc., ven tamaños un 15% mayores.
2. **Cualquier otro texto en `sp`:** el `CompositionLocalProvider(LocalDensity provides Density(..., fontScale = 1.15f))` hace que Compose escale **todos** los valores en `sp` (por ejemplo `fontSize = 24.sp`) en toda la jerarquía. Así, pantallas como Collection, Cluster, User, GameInfo, etc., escalan la letra sin tocar cada `Text` a mano.

---

## 4. Flujo

1. Usuario entra en **Profile** → **Ajustes** → activa **Letra más grande**.
2. `appPrefsViewModel.setLargeText(true)` escribe en DataStore.
3. El `StateFlow` de preferencias emite; `MainActivity` recomposición pasa `useLargeText = true` a `GLogTheme`.
4. `GLogTheme` aplica tipografía escalada y `fontScale` 1.15; toda la UI se redibuja con letra más grande.
5. En el siguiente arranque, la preferencia se lee de DataStore y se aplica desde el inicio.

---

## 5. Constante de escala

- En `Theme.kt`: `private const val LARGE_TEXT_SCALE = 1.15f`. Puedes cambiarla para hacer la letra aún más grande o más suave.
