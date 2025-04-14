/*
 * Copyright 2022 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rainbowt0506.composesritem.screen.srlist

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.rainbowt0506.composesritem.R
import com.rainbowt0506.composesritem.sr.SwipeableReorderableItem
import com.rainbowt0506.composesritem.swipe.CopyActionBlock
import com.rainbowt0506.composesritem.swipe.DeleteActionBlock
import com.rainbowt0506.composesritem.swipe.PinActionBlock
import com.rainbowt0506.composesritem.swipe.rememberSwipeableState
import com.rainbowt0506.composesritem.ui.theme.N600
import com.rainbowt0506.composesritem.ui.theme.N900
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun SRList(
    modifier: Modifier = Modifier,
    vm: SRListViewModel = viewModel(),
) {
    val context = LocalContext.current

    val reorderableState =
        rememberReorderableLazyListState(onMove = vm::onMove, canDragOver = vm::canDragOver)

    LazyColumn(
        state = reorderableState.listState,
        modifier = modifier
            .then(Modifier.reorderable(reorderableState))
    ) {
        item {
            HeaderFooter(stringResource(R.string.header_title), vm.headerImage)
        }
        itemsIndexed(vm.teams, key = { _, team -> team.id }) { index, team ->
            val swipeableState = rememberSwipeableState<Team>(
                onExpandedToLeft = {
                    vm.updateTeamById(team.id, team.copy(isOptionsRevealed = true))
                },
                onExpandedToRight = {
                    vm.updateTeamById(team.id, team.copy(isOptionsRevealed = true))
                },
                onCollapsed = {
                    vm.updateTeamById(team.id, team.copy(isOptionsRevealed = false))
                }
            )

            SwipeableReorderableItem(
                swipeableState = swipeableState,
                reorderableState = reorderableState,
                key = team.id,
                isRevealed = team.isOptionsRevealed,
                leftActions = {
                    PinActionBlock(isPin = team.isPin) {
                        Toast.makeText(context, "Pinned ${team.name}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    CopyActionBlock {
                        Toast.makeText(context, "Copy ${team.name}", Toast.LENGTH_SHORT).show()
                    }
                },
                rightActions = {
                    DeleteActionBlock {
                        Toast.makeText(context, "Deleted ${team.name}", Toast.LENGTH_SHORT)
                            .show()
                        vm.removeTeam(team)
                    }
                }
            ) { isDragging ->
                val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)
                Column(
                    modifier = Modifier
                        .shadow(elevation.value)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)

                ) {
                    TeamItem(reorderableState, team)

                    Divider()
                }
            }
        }
        item {
            HeaderFooter(stringResource(R.string.footer_title), vm.footerImage)
        }
    }
}

@Composable
private fun TeamItem(
    state: ReorderableLazyListState,
    team: Team
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(team.url),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = team.name,
                fontSize = 16.sp,
                color = N900,
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_drag),
            contentDescription = "Drag",
            modifier = Modifier
                .detectReorder(state)
                .size(20.dp),
            tint = N600
        )
    }
}


@Composable
private fun HeaderFooter(title: String, url: String) {
    Box(
        modifier = Modifier
            .height(128.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = rememberAsyncImagePainter(url),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/**
 * 資料類別，用來表示單一 Team 項目的 UI 狀態。
 *
 * @param id Team 的唯一識別編號
 * @param painter Team 的大頭貼
 * @param name Team 名稱
 * @param isPin 此 Team 是否已釘選
 * @param isOptionsRevealed 此 Team 是否已展開操作選項（例如：Pin、Unpin、Delete...）
 */
data class Team(
    val id: Int,
    val url: String,
    val name: String,
    val isPin: Boolean,
    val isOptionsRevealed: Boolean
)