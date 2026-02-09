# Marquesina en las cards del Home (GLog)

Este documento describe cómo está implementado el **texto en marquesina** en las cards de juego del Home: cuando el título es más largo que el ancho de la card, el texto se desplaza para poder leerlo completo.

---

## 1. Resumen del comportamiento

- **Si el título cabe** en el ancho de la card: se muestra en una sola línea; si en algún caso no cupiera, se usaría `TextOverflow.Ellipsis`.
- **Si el título no cabe**: se activa la marquesina: el texto se desplaza hacia la izquierda en bucle (animación infinita) para que se pueda leer hasta el final (por ejemplo *"The Legend of Zelda: Breath of the Wild"*).

Todo esto ocurre dentro de la franja negra del título en cada `GameCard` del `HomeScreen`.

---

## 2. Dónde está el código

Todo está en el mismo archivo:

| Archivo | Contenido |
|--------|-----------|
| `ui/screens/HomeScreen.kt` | `MarqueeText`, `MarqueeScrollLayout`, `UnboundedMeasureLayout`, y el uso en `GameCard`. |

No hay ViewModel ni estado global: solo estado local dentro de `MarqueeText`.

---

## 3. Componentes

### 3.1. `MarqueeText` (composable principal)

- **Entrada:** `text`, `modifier`, `fontSize`, `color`.
- **Estado local:**
  - `textWidthPx`: ancho real del texto en una sola línea (en píxeles).
  - `boxWidthPx`: ancho del contenedor (la franja del título en la card).
- **Lógica:**
  - `needMarquee = textWidthPx > boxWidthPx` (solo si el texto es más ancho que la card).
  - Animación infinita: `offset` va de `0` a `-(textWidthPx + gapPx)` en 6 segundos y se reinicia (`RepeatMode.Restart`).
- **Tres ramas:**
  1. **`textWidthPx == 0`** → fase de medición: se usa `UnboundedMeasureLayout` para obtener el ancho real del texto sin límite de ancho.
  2. **`needMarquee == true`** → se dibuja el contenido que se mueve con `MarqueeScrollLayout` (Row con el texto dos veces y un hueco).
  3. **Resto** → texto estático con `TextOverflow.Ellipsis` si hiciera falta.

El contenedor es un `Box` con `fillMaxWidth()`, `onSizeChanged { boxWidthPx = it.width }` y `clipToBounds()` para que solo se vea la parte que corresponde al ancho de la card.

### 3.2. `UnboundedMeasureLayout`

- **Objetivo:** Medir el ancho real del texto **sin** limitarlo al ancho de la card.
- **Problema que resuelve:** Si el `Text` se mide dentro de un `Box` con `fillMaxWidth()`, Compose le da `maxWidth = ancho de la card`, el texto se recorta/ellipsiza y nunca detectamos que “no cabe”.
- **Cómo:** Es un `Layout` que mide a su único hijo con **restricciones de ancho ilimitado** (`maxWidth = Int.MAX_VALUE`). Así el texto se mide en una sola línea y se obtiene su ancho real.
- El hijo va envuelto en un `Box` con `onSizeChanged` para reportar ese ancho (evitando escribir estado dentro del bloque de measure).
- Se usa **solo en la fase de medición** (`textWidthPx == 0`).

### 3.3. `MarqueeScrollLayout`

- **Objetivo:** Dibujar el Row de la marquesina con su **ancho real** (texto + hueco + texto otra vez) y colocarlo con el `scrollOffsetPx` para que al desplazarse se vea todo, incluido el final del texto.
- **Problema que resuelve:** Si el Row está dentro de un `Box` con `fillMaxWidth()`, Compose limita el ancho del Row al de la card, los dos `Text` se comprimen y la parte final del título nunca llega a dibujarse.
- **Cómo:** Es un `Layout` que:
  - Mide a su único hijo (el `Row` con los dos textos y el `Spacer`) con **ancho ilimitado** (`maxWidth = Int.MAX_VALUE`).
  - Se coloca en pantalla con el ancho del padre (`constraints.maxWidth`), es decir, el ancho de la franja del título.
  - Coloca el hijo en **`(scrollOffsetPx, 0)`**. El Row puede ser más ancho que la card; solo es visible la parte que cae dentro del padre, que tiene `clipToBounds()`.

Así el texto completo (repetido dos veces con un hueco) existe y se desplaza; el usuario ve pasar todo el título hasta el final antes de que se repita el bucle.

---

## 4. Flujo paso a paso

### 4.1. Primera vez que se muestra la card

1. Se pinta `MarqueeText` con el título del juego.
2. `textWidthPx == 0`, así que se entra en la rama de medición.
3. Se usa `UnboundedMeasureLayout` con un `Text` (mismo texto, mismo estilo). El Layout mide ese Text con ancho ilimitado y lo coloca; el `Box` interno recibe `onSizeChanged` con el ancho medido.
4. Se actualiza `textWidthPx` con ese ancho. También se tiene `boxWidthPx` por el `onSizeChanged` del `Box` exterior (ancho de la franja del título).
5. Recomposición: ya no se cumple `textWidthPx == 0`. Se evalúa `needMarquee = textWidthPx > boxWidthPx`.

### 4.2. Si el título no cabe (marquesina activada)

1. Se pinta la rama `needMarquee`: `MarqueeScrollLayout(scrollOffsetPx = offset.roundToInt())` con un `Row` que contiene: `Text` + `Spacer(32.dp)` + `Text` (mismo texto).
2. `MarqueeScrollLayout` mide ese Row con ancho ilimitado, así el Row tiene ancho real ≈ `2 * textWidthPx + gapPx`.
3. El Layout se dibuja con ancho = ancho de la card y coloca el Row en `(offset, 0)`. Como el padre tiene `clipToBounds()`, solo se ve el trozo del Row que cae dentro de la card.
4. La animación va moviendo `offset` de `0` a `-(textWidthPx + gapPx)`. Así el Row se desplaza hacia la izquierda y el usuario ve pasar todo el título (incluido el final); al llegar al final se reinicia y se ve de nuevo el inicio (el segundo `Text` del Row está en la misma posición visual que el primero al inicio).

### 4.3. Si el título cabe

1. Se pinta la rama `else`: un solo `Text` con `maxLines = 1` y `overflow = TextOverflow.Ellipsis`.
2. No hay animación ni layouts extra.

---

## 5. Parámetros que puedes tocar

- **Duración del ciclo:** En `MarqueeText`, en `tween(durationMillis = 6000)` (6 segundos). Aumentar para más lento, reducir para más rápido.
- **Hueco entre repeticiones:** `Spacer(Modifier.width(32.dp))` y la variable `gapPx` (debe coincidir con ese 32.dp para que el bucle sea coherente).
- **Estilo del texto:** Los parámetros `fontSize` y `color` de `MarqueeText`; en `GameCard` se usa el mismo estilo que antes (p. ej. 12.sp, blanco).

---

## 6. Resumen

- **MarqueeText:** Decide si hace falta marquesina, anima el `offset` y elige entre medición, marquesina o texto estático.
- **UnboundedMeasureLayout:** Mide el ancho real del texto sin limitar por el ancho de la card (para detectar títulos largos).
- **MarqueeScrollLayout:** Dibuja el Row de la marquesina con ancho real y lo coloca con el offset para que al desplazarse se vea todo el texto hasta el final.

Con esto, los títulos largos en las cards del Home se leen completos gracias a la marquesina, y los cortos se muestran estáticos.
