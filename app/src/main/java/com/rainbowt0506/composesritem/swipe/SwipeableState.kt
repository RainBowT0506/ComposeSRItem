package com.rainbowt0506.composesritem.swipe
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

abstract class SwipeableState<T>(
    protected val scope: CoroutineScope,
    private val onExpandedToLeft: (T?) -> Unit = {},
    private val onExpandedToRight: (T?) -> Unit = {},
    private val onCollapsed: () -> Unit = {},
    val cancelAnimation: SwipeCancelAnimation = NoSwipeCancelAnimation
) {
    private var isReadyToAnimate by mutableStateOf(false)
    private val offset = Animatable(0f)

    var currentItem: T? by mutableStateOf(null)
        private set

    var leftActionWidth by mutableFloatStateOf(0f)
    var rightActionWidth by mutableFloatStateOf(0f)

    val currentOffset: IntOffset
        get() = IntOffset(offset.value.roundToInt(), 0)

    fun onItemSwiped(item: T?) {
        currentItem = item
    }

    fun updateActionWidths(left: Float, right: Float) {
        leftActionWidth = left
        rightActionWidth = right
        if (left > 0f || right > 0f) {
            isReadyToAnimate = true
        }
    }

    fun onRevealStateChanged(isRevealed: Boolean) {
        if (!isReadyToAnimate) return
        scope.launch {
            offset.animateTo(
                when {
                    offset.value < 0 -> -rightActionWidth
                    offset.value > 0 -> leftActionWidth
                    else -> 0f
                }
            )
        }
    }

    fun onDrag(dragAmount: Float) {
        scope.launch {
            val newOffset = (offset.value + dragAmount)
                .coerceIn(-rightActionWidth, leftActionWidth)
            offset.snapTo(newOffset)
        }
    }

    fun onDragEnd() {
        scope.launch {
            val current = offset.value
            val item = currentItem
            when {
                current >= leftActionWidth / 2f -> {
                    offset.animateTo(leftActionWidth)
                    onExpandedToLeft(item)
                }

                current <= -rightActionWidth / 2f -> {
                    offset.animateTo(-rightActionWidth)
                    onExpandedToRight(item)
                }

                else -> {
                    cancelAnimation.animateCancel(item, Offset(current, 0f))
                    offset.animateTo(0f)
                    onCollapsed()
                }
            }
        }
    }

    fun collapse() {
        scope.launch {
            val current = offset.value
            val item = currentItem
            cancelAnimation.animateCancel(item, Offset(current, 0f))
            offset.animateTo(0f)
            onCollapsed()
        }
    }
}