package ch.timeon.cafftrack.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun swipeToDeleteItem(
    onDelete: () -> Unit,
    threshold: Dp = 100.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val screenPx = with(density) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    val thresholdPx = with(density) { threshold.toPx() }
    val offsetX = remember { Animatable(0f) }
    val scope   = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .height(IntrinsicSize.Min)
    ) {
        Icon(
            imageVector    = Icons.Outlined.Delete,
            contentDescription = "Delete",
            tint           = MaterialTheme.colorScheme.onErrorContainer,
            modifier       = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )

        Box(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val new = (offsetX.value + dragAmount.x)
                                .coerceIn(-screenPx, 0f)
                            scope.launch {
                                offsetX.snapTo(new)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                if (abs(offsetX.value) > thresholdPx) {
                                    offsetX.animateTo(
                                        targetValue     = -screenPx,
                                        animationSpec   = tween(300)
                                    )
                                    onDelete()
                                } else {
                                    offsetX.animateTo(
                                        targetValue     = 0f,
                                        animationSpec   = spring(stiffness = Spring.StiffnessMedium)
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(0f, spring())
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
