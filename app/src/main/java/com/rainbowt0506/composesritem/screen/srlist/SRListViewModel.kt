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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.burnoutcrew.reorderable.ItemPosition
import kotlin.random.Random


class SRListViewModel : ViewModel() {

    // 頁首與頁尾圖片隨機產生（用於 UI 上方與下方裝飾）
    val headerImage = "https://picsum.photos/seed/compose${Random.nextInt(Int.MAX_VALUE)}/400/200"
    val footerImage = "https://picsum.photos/seed/compose${Random.nextInt(Int.MAX_VALUE)}/400/200"

    // 初始化 20 組 Team 資料，每組資料包含 id、圖片 URL、名稱等
    var teams by mutableStateOf(
        List(20) {
            Team(
                id = it,
                url = "https://picsum.photos/seed/team$it/200/200",
                name = "Team $it",
                isPin = false,
                isOptionsRevealed = false
            )
        }
    )

    /**
     * 拖曳排序的邏輯處理，會將某一項目移動到指定位置
     */
    fun onMove(from: ItemPosition, to: ItemPosition) {
        // 比對 Team.id（Int）與傳入的 key（必須一致為 Int）
        val fromIndex = teams.indexOfFirst { it.id == from.key }
        val toIndex = teams.indexOfFirst { it.id == to.key }

        // 如果找不到對應索引則直接 return，避免錯誤
        if (fromIndex == -1 || toIndex == -1) return

        // 交換順序：先移除 fromIndex，再插入到 toIndex
        teams = teams.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
    }


    fun canDragOver(draggedOver: ItemPosition, dragging: ItemPosition) =
        teams.any { it.id == draggedOver.key }

    fun updateTeamById(id: Int, newValue: Team) {
        teams = teams.map { if (it.id == id) newValue else it }
    }


    fun removeTeam(team: Team) {
        teams = teams.toMutableList().apply { remove(team) }
    }

}