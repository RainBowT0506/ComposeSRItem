package com.rainbowt0506.composesritem.swipe
import androidx.compose.ui.geometry.Offset

interface SwipeCancelAnimation {
    suspend fun animateCancel(item: Any?, offset: Offset)
    val offset: Offset
    val item: Any?
}

object NoSwipeCancelAnimation : SwipeCancelAnimation {
    override suspend fun animateCancel(item: Any?, offset: Offset) {}
    override val offset: Offset = Offset.Zero
    override val item: Any? = null
}