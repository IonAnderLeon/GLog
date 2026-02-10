package com.example.glog.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedSwitch(
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val sliderPosition by animateDpAsState(
        targetValue = if (isOn) 28.dp else 4.dp,
        animationSpec = spring(),
        label = "switchSlider"
    )

    val trackColor = if (isOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val thumbColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .width(60.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(trackColor)
            .clickable { onToggle(!isOn) }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = sliderPosition)
                .size(26.dp)
                .clip(CircleShape)
                .background(thumbColor)
                .shadow(2.dp, CircleShape)
        )
    }
}
