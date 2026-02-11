package com.example.glog.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp,
    color: Color = Color.White
) {
    var textWidthPx by remember { mutableIntStateOf(0) }
    var boxWidthPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val gapPx = with(density) { 32.dp.roundToPx() }
    val needMarquee = textWidthPx > 0 && boxWidthPx > 0 && textWidthPx > boxWidthPx

    val infiniteTransition = rememberInfiniteTransition(label = "marquee")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -(textWidthPx + gapPx).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing, delayMillis = 0),
            repeatMode = RepeatMode.Restart
        ),
        label = "marqueeOffset"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { boxWidthPx = it.width }
            .clipToBounds()
    ) {
        when {
            textWidthPx == 0 -> {
                UnboundedMeasureLayout(onMeasuredWidth = { textWidthPx = it }) {
                    Text(text = text, fontSize = fontSize, color = color, maxLines = 1)
                }
            }
            needMarquee -> {
                MarqueeScrollLayout(scrollOffsetPx = offset.roundToInt()) {
                    Row {
                        Text(text = text, fontSize = fontSize, color = color, maxLines = 1)
                        Spacer(modifier = Modifier.width(32.dp))
                        Text(text = text, fontSize = fontSize, color = color, maxLines = 1)
                    }
                }
            }
            else -> {
                Text(
                    text = text,
                    fontSize = fontSize,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MarqueeScrollLayout(
    scrollOffsetPx: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(modifier = modifier.fillMaxWidth(), content = content) { measurables, constraints ->
        val unboundedConstraints = Constraints(
            minWidth = 0,
            maxWidth = Int.MAX_VALUE,
            minHeight = 0,
            maxHeight = constraints.maxHeight
        )
        val placeable = measurables.first().measure(unboundedConstraints)
        layout(constraints.maxWidth, placeable.height) {
            placeable.place(scrollOffsetPx, 0)
        }
    }
}

@Composable
fun UnboundedMeasureLayout(
    onMeasuredWidth: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            Box(Modifier.onSizeChanged { onMeasuredWidth(it.width) }) {
                content()
            }
        }
    ) { measurables, constraints ->
        val unboundedConstraints = Constraints(
            minWidth = 0,
            maxWidth = Int.MAX_VALUE,
            minHeight = 0,
            maxHeight = constraints.maxHeight
        )
        val placeable = measurables.first().measure(unboundedConstraints)
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}
