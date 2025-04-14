# 🧩 ComposeSRItem

中文：
結合 **Reorderable 拖曳排序** 與 **Swipe 滑動操作列** 的 Jetpack Compose 組件。

英文：
**ComposeSRItem** is a powerful Jetpack Compose utility that brings together **swipe gestures** and **drag-and-drop reordering** in a unified item component, perfectly designed for lists built with `LazyColumn`.

It merges the best of both gesture systems:
- 🔄 **Swipeable** list items with action buttons (like **Pin**, **Copy**, **Delete**)
- ↕️ **Reorderable** drag-and-drop support with built-in animation
- 🎯 Seamless integration using `SwipeableState<T>` and `ReorderableState`

# ✨ Features

- 💡 **Composable** & **declarative** usage pattern with `LazyColumn` and `itemsIndexed`
- 👆 Customizable **left & right swipe actions**
- ⬆️⬇️ Drag handle for intuitive **reordering**
- 🧩 Fully compatible with `rememberSwipeableState` & `rememberReorderableLazyListState`
- 🧱 Suitable for building rich **interactive lists** such as:
  - Contact books
  - To-do apps
  - Email or chat threads
  - Bookmark managers

 ---

參考自：
- [aclassen/ComposeReorderable](https://github.com/aclassen/ComposeReorderable)
- [philipplackner/ComposeSwipeToReveal](https://github.com/philipplackner/ComposeSwipeToReveal)

---

# ✨ 特性

- ✅ 支援 LazyColumn 拖曳排序
- ✅ 支援滑動顯示左右 Action 按鈕
- ✅ 左右滑動可觸發不同邏輯（Pin / Delete / Copy）
- ✅ 拖曳動畫與滑動動畫完美融合
- ✅ State 抽象化，支援 MVVM 管理狀態
- ✅ 可自由組合 `Swipeable`、`Reorderable`、或兩者一起使用

---


---

# 🧱 架構總覽：Reorderable vs Swipeable 對比表

| 模組職責     | Reorderable 模組                                  | Swipeable 模組                                      | 說明 |
|--------------|---------------------------------------------------|-----------------------------------------------------|------|
| **核心邏輯** | `ReorderableState.kt`                             | `SwipeableState.kt`                                 | 管理拖曳/滑動邏輯與 offset 計算，負責控制動畫 |
| **狀態實作** | `ReorderableLazyListState.kt` / `LazyGridState.kt`| `HorizontalSwipeableState.kt`                       | 對應垂直/水平或 LazyList 實作的狀態邏輯 |
| **UI 組件**  | `ReorderableItem.kt`                              | `SwipeableItem.kt`, `SwipeableReorderableItem.kt`   | 可組合元件，負責組裝動畫與觸控邏輯 |
| **手勢處理** | `Reorderable.kt`, `DetectReorder.kt`              | `DetectSwipe.kt`                                    | 封裝手勢偵測行為（拖曳 / 滑動） |
| **手勢輔助** | `DragGesture.kt`                                  | 無（邏輯簡單，不需抽出）                            | 拖曳距離 Slop / LongPress 偵測工具 |
| **動畫模組** | `DragCancelledAnimation.kt`                       | `SwipeCancelAnimation.kt`, `NoSwipeCancelAnimation.kt` | 當動作被取消時的還原動畫策略 |
| **資料模型** | `ItemPosition.kt`                                 | 無（使用 index or item 自身即可）                   | 拖曳排序時記錄 item 位置與 key |

---

# 🧩 結合共通點分析

| 層級           | 共通點                                   | 合併後模組             |
|----------------|------------------------------------------|------------------------|
| 狀態管理       | 都有 `State` 類別持有 offset、控制動畫     | `SwipeableState`、`ReorderableState` 各自管理 |
| UI 元件        | 都使用 `@Composable` 實作項目畫面         | `SwipeableReorderableItem` 將兩者組合 |
| 手勢註冊方式   | 都以 `Modifier` 擴充形式提供               | `Modifier.detectSwipe(...)` 與 `Modifier.reorderable(...)` |
| 動畫處理       | 拖曳與滑動都會觸發動畫回彈或位移         | 各自維持自己的動畫策略，互不衝突 |
| Key 使用需求   | 都依賴 `key` 或 `index` 保持項目唯一性     | 使用 `itemsIndexed(..., key = { _, it -> it.id })` 管理 |

---

# 🧪 使用方式

## 1. 建立狀態

```kotlin
val reorderableState = rememberReorderableLazyListState(onMove = ::onMove)
val swipeState = rememberSwipeableState<Team>(
    onExpandedToLeft = { /* ... */ },
    onExpandedToRight = { /* ... */ },
    onCollapsed = { /* ... */ }
)
```

## 2. 套用在 `LazyColumn` 內

```kotlin
itemsIndexed(teams, key = { _, item -> item.id }) { index, item ->
    SwipeableReorderableItem(
        swipeableState = swipeState,
        reorderableState = reorderableState,
        key = item.id,
        index = index,
        isRevealed = item.isOptionsRevealed,
        leftActions = {
            // ex: PinActionBlock(...)
        },
        rightActions = {
            // ex: DeleteActionBlock(...)
        }
    ) { isDragging ->
        TeamCard(item, isDragging)
    }
}
```

---

# 📦 範例畫面預覽

![image](https://github.com/user-attachments/assets/c3896dac-96e3-44a3-8d0c-566282e84243)

如上圖所示：

- ✅ **滑動操作**  
  - 向左滑：顯示「刪除」按鈕  
  - 向右滑：顯示「釘選」、「複製」等自定義操作按鈕  

- ✅ **拖曳排序**  
  - 每個項目右側都有可拖曳的 handle 圖示  
  - 使用者可透過拖曳重新排序項目  

- ✅ **組合特性**  
  - 支援同時拖曳與滑動  
  - 即使項目被移動過，依然能正確使用滑動操作功能

> ✅ 畫面中可見：`Team 0` 被向右滑動展開操作列，並且已被拖曳到原本的第三筆位置。

---

# 📄 授權 License

```
Apache License 2.0

本專案中部分邏輯參考自：
- ComposeReorderable by @aclassen
- ComposeSwipeToReveal by @philipplackner
```

---

# 💬 聯絡作者

作者：@rainbowt0506  
有任何建議或改進歡迎發 PR 或 issue！

---
