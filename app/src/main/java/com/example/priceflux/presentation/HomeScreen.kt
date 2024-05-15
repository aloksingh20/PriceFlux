package com.example.priceflux.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.priceflux.R
import com.example.priceflux.data.remote.amazon.RemoteDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PriceViewModel,
    context:Context
) {
    val state = viewModel.state
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            MyAppBar(
                title = "PriceFlux",
                viewModel = viewModel,
                onCameraClick = {
                    navController.navigate("scanner")
                },
                listState = listState

            )
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else if(state.amazonInfo.isNotEmpty()&&state.flipkartInfo.isNotEmpty()) {
                    // Create a TabRow with two tabs: one for Amazon and one for Flipkart
                    val tabs = listOf("Amazon", "Flipkart")
                    var selectedTabIndex by remember { mutableStateOf(0) }
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        pageCount = { tabs.size }
                    )




                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, bottom = 5.dp)
                    ){
                        TabRow(
                            selectedTabIndex,
                            ) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 }
                            ) {
                                Text(text = "Amazon")
                            }
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 }
                            ) {
                                Text(text = "Flipkart")
                            }
                        }
                        // Display the content based on the selected tab
                        when (selectedTabIndex) {
                            0 -> {
                                // Display Amazon content
                                LazyColumn(state = listState,
                                    modifier = Modifier
                                        .pointerInput(Unit) {
                                            detectHorizontalDragGestures { change, dragAmount ->
                                                if (dragAmount.toDp().toPx() > 0) {
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        selectedTabIndex = 0
                                                        pagerState.scrollToPage(0)
                                                    }
                                                } else {
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        selectedTabIndex = 1
                                                        pagerState.scrollToPage(1)
                                                    }
                                                }
                                            }
                                        }
                                ) {
                                    items(state.amazonInfo.size) { index ->
                                        // Display individual Amazon product information
                                        val product = state.amazonInfo[index]
                                        ItemCard(product = product, modifier =  Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp), context = context,
                                            text = "amazon")
                                    }
                                }
                            }

                            1 -> {
                                // Display Flipkart content
                                LazyColumn(state = listState,
                                    modifier = Modifier
                                        .pointerInput(Unit) {
                                            detectHorizontalDragGestures { change, dragAmount ->
                                                if (dragAmount.toDp().toPx() > 0) {
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        selectedTabIndex = 0
                                                        pagerState.scrollToPage(0)
                                                    }
                                                } else {
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        selectedTabIndex = 1
                                                        pagerState.scrollToPage(1)
                                                    }
                                                }
                                            }
                                        }
                                ) {
                                    items(state.flipkartInfo.size) { index ->
                                        // Display individual Flipkart product information
                                        val product = state.flipkartInfo[index]
                                        ItemCard(product,
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp),
                                            context,
                                            text = "flipkart"
                                        )


                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    title: String,
    viewModel: PriceViewModel,
    onCameraClick: () -> Unit,
    listState: LazyListState

) {
    var isSearchExpanded by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState() // Observe search query directly
    val isSearching by viewModel.isSearching.collectAsState(false) // Observe search state
    var isTopBarVisible by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        if (searchQuery.isNotEmpty()) {
            isSearchExpanded = true
        }
    }

    var previousScrollOffset by remember { mutableStateOf(0) }

    LaunchedEffect(listState.firstVisibleItemScrollOffset) {
        val currentScrollOffset = listState.firstVisibleItemScrollOffset
        if (currentScrollOffset > previousScrollOffset) {
            // Scrolled down
            isTopBarVisible = false
        } else if (currentScrollOffset < previousScrollOffset) {
            // Scrolled up
            isTopBarVisible = true
        }
        previousScrollOffset = currentScrollOffset
    }

    if (isTopBarVisible) {
        TopAppBar(
            title = { Text(text = title) },
            actions = {
                if (isSearchExpanded) {
                    SearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        query = searchQuery,
                        onQueryChange = viewModel::onSearchTextChange,
                        onSearch = viewModel::onSearch,
                        active = isSearching,
                        onActiveChange = {
                            if (!it) {
                                viewModel.onToggleSearch()
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    isSearchExpanded = false
                                }
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    modifier = Modifier.clickable {
                                        viewModel.onSearchTextChange("")
                                    },
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Close"
                                )
                            }
                        }
                    ) {

                    }
                } else {
                    IconButton(onClick = { isSearchExpanded = true }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                    }
                }


                IconButton(
                    onClick = onCameraClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Qr scanner"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

}


@Composable
fun ItemCard(
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
                    .size(200.dp)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = product.productName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.productPrice.split(" ")[0],
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Green
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rating: ${product.productRating}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = {
                    val appUri :Uri
                    val webUri :Uri
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
                } ) {

                    Text(
                        text = if(text.contains("amazon")){
                            "View product on Amazon"
                        } else {
                               "View product on Flipkart"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Blue
                    )

                }
            }
        }
    }

}

