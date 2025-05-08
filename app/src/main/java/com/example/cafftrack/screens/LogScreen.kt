package com.example.cafftrack.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.cafftrack.viewmodel.CaffeineViewModel
import java.util.Date
import kotlin.math.abs

@Composable
fun LogScreen(
    modifier: Modifier = Modifier,
    viewModel: CaffeineViewModel = hiltViewModel()
) {
    val entries by viewModel.caffeineEntries.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(entries, key = { it.id }) { entry ->
            var swipeOffset by remember { mutableFloatStateOf(0f) }
            var isDismissed by remember { mutableStateOf(false) }

            if (!isDismissed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateDismissal(isDismissed = isDismissed, containerWidth = 200.dp)
                ) {
                    // Delete background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    // Swipeable content
                    Box(
                        modifier = Modifier
                            .offset(x = swipeOffset.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { change, dragAmount ->
                                    swipeOffset += dragAmount
                                    if (abs(swipeOffset) > size.width * 0.6) {
                                        isDismissed = true
                                        viewModel.delete(entry)
                                    }
                                }
                            }
                    ) {
                        ListItem(
                            headlineContent = { Text(entry.name) },
                            supportingContent = { Text("${entry.caffeineMg}mg @ ${Date(entry.timestamp)}") }
                        )
                    }
                }
            }

            Divider(
                color = MaterialTheme.colorScheme.outlineVariant,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun Modifier.animateDismissal(isDismissed: Boolean, containerWidth: Dp) = composed {
    val density = LocalDensity.current
    val targetValue = if (isDismissed) -with(density) { containerWidth.toPx() } else 0f
    val offset by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 300),
        label = "DismissAnimation"
    )
    offset(x = offset.dp)
}