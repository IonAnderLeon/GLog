package com.example.glog.ui.screens.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class FabPosition(
    val x: Dp,
    val y: Dp
)

@Composable
fun DraggableFAB(
    position: FabPosition,
    onPositionChange: (FabPosition) -> Unit
) {
    val animatedX by animateDpAsState(
        targetValue = position.x,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabX"
    )

    val animatedY by animateDpAsState(
        targetValue = position.y,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "fabY"
    )

    FloatingActionButton(
        onClick = { },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .offset(x = animatedX, y = animatedY)
            .size(56.dp)
            .clip(CircleShape)
            .shadow(8.dp, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val density = this@pointerInput.density
                    val dragAmountXDp = with(density) { dragAmount.x.toDp() }
                    val dragAmountYDp = with(density) { dragAmount.y.toDp() }
                    onPositionChange(
                        FabPosition(
                            x = position.x + dragAmountXDp,
                            y = position.y + dragAmountYDp
                        )
                    )
                }
            }
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Añadir",
            modifier = Modifier.size(24.dp)
        )
    }
}
