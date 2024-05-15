package com.example.priceflux.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.priceflux.data.remote.amazon.RemoteDto

@Composable
fun ListItemCard(
    product: RemoteDto,
    modifier: Modifier = Modifier,
    context: Context,
    text:String
) {
    Card (  modifier = modifier,
        shape = CardDefaults.elevatedShape
    ){
        Row(modifier = modifier.padding(top = 5.dp, bottom = 5.dp, start = 8.dp,end = 8.dp)) {
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
                if(product.productRating.isNotEmpty()){
                    Text(
                        text = "Rating: ${product.productRating}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
                TextButton(onClick = {
                    val appUri : Uri
                    val webUri : Uri
                    if(text.contains("amazon")) {
                        val amazonAppUri = Uri.parse("amzn://www.amazon.com${product.productUrl}")
                        val amazonWebUri = Uri.parse("https://www.amazon.com${product.productUrl}")
                        appUri = amazonAppUri
                        webUri = amazonWebUri
                    }else{
                        val flipkartAppUri = Uri.parse("flipkart://www.flipkart.com${product.productUrl}")
                        val flipkartWebUri = Uri.parse("https://www.flipkart.com${product.productUrl}") // Fallback URL in case the Flipkart app is not installed
                        appUri = flipkartAppUri
                        webUri = flipkartWebUri
                    }
// Check if the Amazon app is installed
                    val appIntent = Intent(Intent.ACTION_VIEW, appUri)
                    appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                    if (appIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(appIntent)
                    } else {
                        // If the Amazon app is not installed, open the Amazon website in a browser
                        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                        context.startActivity(webIntent)
                    }
                } ,
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {

                    Text(
                        text = if(text.contains("amazon")){
                            "View product on Amazon"
                        } else {
                            "View product on Flipkart"
                        },
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