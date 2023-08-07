package codes.beleap.wearos_pt_info.components

import android.widget.Toast
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import kotlinx.coroutines.launch

data class CardInfo(
    val title: String,
    val content: @Composable ColumnScope.() -> Unit,
)

@Composable
fun CardList(
    listState: ScalingLazyListState,
    heading: String,
    cards: List<CardInfo>?,
    isDebugMode: Boolean,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    val itemSpacing = 6.dp
    val scrollOffset = with(LocalDensity.current) {
        -(itemSpacing / 2).roundToPx()
    }

    ScalingLazyColumn(
        contentPadding = PaddingValues(top = 40.dp),
        state = listState,
        modifier = Modifier
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    listState.animateScrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable()
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        item {
            ListHeader {
                Text(
                    text = heading,
                    textAlign = TextAlign.Center
                )
            }
        }

        cards?.let { cards ->
            items(cards.size) { idx ->
                val cardInfo = cards[idx]

                TitleCard(
                    onClick = {
                        if (isDebugMode) {
                            val toast =
                                Toast.makeText(context, cardInfo.toString(), Toast.LENGTH_SHORT)
                            toast.show()
                        }
                        coroutineScope.launch {
                            listState.animateScrollToItem(idx + 1, scrollOffset)
                        }
                    },
                    title = {
                        Text(
                            text = cardInfo.title,
                            softWrap = true,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    contentColor = MaterialTheme.colors.onSurface,
                    titleColor = MaterialTheme.colors.onSurface,
                    content = cardInfo.content,
                )
            }
        } ?: item { CircularProgressIndicator() }
    }
}