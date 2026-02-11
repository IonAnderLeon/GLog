# Animación infinita del icono de Ajustes

## Qué se hizo

Se añadió una **rotación infinita y lenta** al icono de ajustes (engranaje) en la pantalla de usuario (Profile), para dar un detalle de movimiento sin ser intrusivo.

## Cómo está implementado

### 1. APIs de Compose usadas

- **`rememberInfiniteTransition(label)`**  
  Crea una transición que se repite indefinidamente. El `label` sirve para depuración y para que Compose identifique la animación en el árbol.

- **`animateFloat`**  
  Anima un `Float` (en este caso el ángulo de rotación de 0° a 360°) con una especificación de animación.

- **`infiniteRepeatable`**  
  Hace que la animación se repita sin parar.

- **`RepeatMode.Restart`**  
  Cada ciclo vuelve a empezar desde el valor inicial (0°) en lugar de ir y volver.

- **`tween(durationMillis, easing)`**  
  Define la duración de cada vuelta y el easing (aquí `LinearEasing` para velocidad constante).

- **`Modifier.rotate(degrees)`**  
  Aplica la rotación en grados al composable (el `Icon`).

### 2. Dónde está el código

Todo está en **`UserScreen.kt`**, en la composable privada **`UserHeader`**:

1. Se crea la transición infinita con `rememberInfiniteTransition`.
2. Se anima un `Float` de `0f` a `360f` con `infiniteRepeatable` + `tween(4000, LinearEasing)` y `RepeatMode.Restart`.
3. El `Icon` de `Icons.Default.Settings` usa `modifier = Modifier.rotate(rotation)`.

No se usa ningún archivo de recursos extra (drawable, XML, etc.); todo es código en Compose.

### 3. Parámetros actuales

| Parámetro        | Valor   | Significado                          |
|------------------|--------|--------------------------------------|
| Duración por vuelta | 4000 ms | Una rotación completa cada 4 segundos |
| Easing           | LinearEasing | Velocidad angular constante        |
| RepeatMode       | Restart | Cada vuelta empieza de 0° a 360°   |

Para que gire más lento: subir `durationMillis` (por ejemplo 6000). Para más rápido: bajarlo (por ejemplo 3000).

### 4. Imports necesarios

```kotlin
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.rotate
```

## Resumen

- **Qué**: rotación continua del icono de ajustes en Profile.  
- **Dónde**: `UserScreen.kt` → `UserHeader` → `Icon` de Settings.  
- **Cómo**: `rememberInfiniteTransition` + `animateFloat` (0°→360°) + `Modifier.rotate(rotation)`.  
- **Sin archivos extra**: solo código Compose con la API de animación.
