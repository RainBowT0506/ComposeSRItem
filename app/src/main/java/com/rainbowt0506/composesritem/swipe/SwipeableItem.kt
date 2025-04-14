package com.rainbowt0506.composesritem.swipe
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 一個可以左右滑動的可組合項目，滑動後會顯示出一組操作按鈕。
 *
 * 常見應用像是：在列表中對某個項目滑動，顯示「刪除」、「分享」、「編輯」等按鈕。
 *
 * @param modifier 外部可套用的 Modifier
 * @param isRevealed 控制是否展開操作列
 * @param leftActions 操作列中的按鈕內容（Composable RowScope）
 * @param rightActions 操作列中的按鈕內容（Composable RowScope）
 * @param onExpandedToLeft 當滑動超過一半、展開時要執行的動作（如更新狀態）
 * @param onCollapsed 當滑動不足一半、收合時要執行的動作
 * @param content 主內容區塊，例如聯絡人名稱、清單項目等
 */
@Composable
fun <T> SwipeableItem(
    modifier: Modifier = Modifier,
    index: Int? = null,
    state: SwipeableState<T>,
    isRevealed: Boolean,
    leftActions: @Composable RowScope.() -> Unit,
    rightActions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit
) {

    LaunchedEffect(index) {
        state.onItemSwiped(index)
    }


    // 反應外部 isRevealed 狀態變化（例如列表中只有一個 item 可展開）
    LaunchedEffect(isRevealed) {
        state.onRevealStateChanged(isRevealed)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // 左邊 actions
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .onSizeChanged {
                    state.updateActionWidths(
                        it.width.toFloat(),
                        state.rightActionWidth
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
                    state.updateActionWidths(
                        state.leftActionWidth,
                        it.width.toFloat()
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            rightActions()
        }

        // 上層可滑動內容
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset { state.currentOffset }
                .detectSwipe(state)
        ) {
            content()
        }
    }
}
