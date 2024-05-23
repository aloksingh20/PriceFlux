package com.example.priceflux.presentation.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.priceflux.data.local.notification.NotificationEntity
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.time.format.DateTimeFormatter

@Composable
fun NotificationListItem(
    modifier: Modifier = Modifier,
    notification: NotificationEntity,
    onItemClick: (NotificationEntity) -> Unit,
    onItemSwiped: (NotificationEntity) -> Unit
){
    val delete = SwipeAction(
        icon = { Icon(Icons.Default.Delete, contentDescription = null , modifier = Modifier
            .size(52.dp)
            .padding(start = 15.dp)) },
        background = MaterialTheme.colorScheme.error,
        isUndo = true,
        onSwipe = {
           onItemSwiped(notification)
        }
    )
    SwipeableActionsBox (endActions = listOf(delete), modifier = Modifier.clickable {
        onItemClick(notification)
    }) {

        Card(modifier = modifier) {
            Box(modifier = Modifier.padding(16.dp)) {

                Column (modifier = Modifier.align(Alignment.TopStart)){
                    Row (verticalAlignment = Alignment.CenterVertically, ){
                        AsyncImage(model = notification.imageUrl, contentDescription = null, modifier = Modifier.size(100.dp).clip(
                            RoundedCornerShape(16.dp)
                        ))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = notification.title)

                    }
                    Text(text = notification.body)
                }
                Text(text = notification.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 8.dp))

            }
        }
    }

}