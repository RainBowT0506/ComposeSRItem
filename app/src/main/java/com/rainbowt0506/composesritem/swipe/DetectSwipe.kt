package com.rainbowt0506.composesritem.swipe
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun <T> Modifier.detectSwipe(state: SwipeableState<T>) = this.then(
    Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onHorizontalDrag = { _, dragAmount ->
                state.onDrag(dragAmount)
            },
            onDragEnd = {
                state.onDragEnd()
            }
        )
    }
)