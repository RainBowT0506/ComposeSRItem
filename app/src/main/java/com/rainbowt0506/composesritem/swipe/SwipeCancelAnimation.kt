package com.rainbowt0506.composesritem.swipe
import androidx.compose.ui.geometry.Offset

interface SwipeCancelAnimation {
    suspend fun animateCancel(item: Int?, offset: Offset)
    val offset: Offset
    val itemIndex: Int?
}

object NoSwipeCancelAnimation : SwipeCancelAnimation {
    override suspend fun animateCancel(index: Int?, offset: Offset) {}
    override val offset: Offset = Offset.Zero
    override val itemIndex: Int? = null
}