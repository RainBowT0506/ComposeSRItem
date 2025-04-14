package com.rainbowt0506.composesritem.sr

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.zIndex
import com.rainbowt0506.composesritem.swipe.SwipeableState
import org.burnoutcrew.reorderable.ReorderableState
import com.rainbowt0506.composesritem.swipe.detectSwipe


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.SwipeableReorderableItem(
    swipeableState: SwipeableState<*>,
    reorderableState: ReorderableState<*>,
    key: Any?,
    modifier: Modifier = Modifier,
    index: Int? = null,
    orientationLocked: Boolean = true,
    isRevealed: Boolean,
    leftActions: @Composable RowScope.() -> Unit,
    rightActions: @Composable RowScope.() -> Unit,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) = SwipeableReorderableItem(
    swipeableState,
    reorderableState,
    key,
    modifier,
    Modifier.animateItemPlacement(),
    orientationLocked,
    index,
    isRevealed = isRevealed,
    leftActions = leftActions,
    rightActions = rightActions,
    content
)

@Composable
fun SwipeableReorderableItem(
    swipeableState: SwipeableState<*>,
    reorderableState: ReorderableState<*>,
    key: Any?,
    modifier: Modifier = Modifier,
    defaultDraggingModifier: Modifier = Modifier,
    orientationLocked: Boolean = true,
    index: Int? = null,
    isRevealed: Boolean,
    leftActions: @Composable RowScope.() -> Unit,
    rightActions: @Composable RowScope.() -> Unit,
    content: @Composable BoxScope.(isDragging: Boolean) -> Unit
) {

    LaunchedEffect(index) {
        swipeableState.onItemSwiped(index)
    }

    // 反應外部 isRevealed 狀態變化（例如列表中只有一個 item 可展開）
    LaunchedEffect(isRevealed) {
        swipeableState.onRevealStateChanged(isRevealed)
    }

    val isDragging = if (index != null) {
        index == reorderableState.draggingItemIndex
    } else {
        key == reorderableState.draggingItemKey
    }
    val draggingModifier =
        if (isDragging) {
            Modifier
                .zIndex(1f)
                .graphicsLayer {
                    translationX =
                        if (!orientationLocked || !reorderableState.isVerticalScroll) reorderableState.draggingItemLeft else 0f
                    translationY =
                        if (!orientationLocked || reorderableState.isVerticalScroll) reorderableState.draggingItemTop else 0f
                }
        } else {
            val cancel = if (index != null) {
                index == reorderableState.dragCancelledAnimation.position?.index
            } else {
                key == reorderableState.dragCancelledAnimation.position?.key
            }
            if (cancel) {
                Modifier
                    .zIndex(1f)
                    .graphicsLayer {
                        translationX =
                            if (!orientationLocked || !reorderableState.isVerticalScroll) reorderableState.dragCancelledAnimation.offset.x else 0f
                        translationY =
                            if (!orientationLocked || reorderableState.isVerticalScroll) reorderableState.dragCancelledAnimation.offset.y else 0f
                    }
            } else {
                defaultDraggingModifier
            }
        }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .then(draggingModifier)
    ) {
        // 左邊 actions
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .onSizeChanged {
                    swipeableState.updateActionWidths(
                        it.width.toFloat(),
                        swipeableState.rightActionWidth
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            leftActions()
        }

        // 右邊 actions
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .onSizeChanged {
                    swipeableState.updateActionWidths(
                        swipeableState.leftActionWidth,
                        it.width.toFloat()
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            rightActions()
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset { swipeableState.currentOffset }
                .detectSwipe(swipeableState)
        ) {
            content(isDragging)
        }
    }
}
