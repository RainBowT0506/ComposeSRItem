package com.rainbowt0506.composesritem.swipe
import kotlinx.coroutines.CoroutineScope

class HorizontalSwipeableState<T>(
    scope: CoroutineScope,
    onExpandedToLeft: (Int?) -> Unit = {},
    onExpandedToRight: (Int?) -> Unit = {},
    onCollapsed: () -> Unit = {},
    cancelAnimation: SwipeCancelAnimation = NoSwipeCancelAnimation
) : SwipeableState<T>(
    scope = scope,
    onExpandedToLeft = onExpandedToLeft,
    onExpandedToRight = onExpandedToRight,
    onCollapsed = onCollapsed,
    cancelAnimation = cancelAnimation
)