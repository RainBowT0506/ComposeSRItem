package com.rainbowt0506.composesritem.swipe
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun <T> rememberSwipeableState(
    onExpandedToLeft: (T?) -> Unit = {},
    onExpandedToRight: (T?) -> Unit = {},
    onCollapsed: () -> Unit = {},
    cancelAnimation: SwipeCancelAnimation = NoSwipeCancelAnimation
): HorizontalSwipeableState<T> {
    val scope = rememberCoroutineScope()
    return remember {
        HorizontalSwipeableState(
            scope = scope,
            onExpandedToLeft = onExpandedToLeft,
            onExpandedToRight = onExpandedToRight,
            onCollapsed = onCollapsed,
            cancelAnimation = cancelAnimation
        )
    }
}