package com.example.priceflux.presentation.watchlist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.priceflux.ContextProvider
import com.example.priceflux.data.local.product.ProductEntity

@Composable
fun WatchListItem(
    modifier: Modifier = Modifier,
    product:ProductEntity,

){
    Card (  modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.elevatedShape
    ){
        Row(modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 8.dp,end = 8.dp)) {
            AsyncImage(
                model = product.productImage,
                contentDescription = "image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .height(150.dp)
                    .width(130.dp)
                    .padding(2.dp)
                    .align(Alignment.CenterVertically)
            )
            Column(modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)) {
                Text(
                    text = product.productName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if(product.prodDescription.isNotEmpty()){
                    Text(
                        text = product.prodDescription,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = product.productPrice.split(" ")[0],
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Green
                )

                TextButton(onClick = {
                    val webUri  = Uri.parse(product.productUrl)

                    // If the Amazon app is not installed, open the Amazon website in a browser
                    val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                    ContextProvider.context.startActivity(webIntent)
                } ,
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {

                    Text(
                        text = "View Product",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )


                }
            }
        }
    }
}