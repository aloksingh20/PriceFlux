package com.example.priceflux.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.priceflux.data.remote.RemoteDto
import com.example.priceflux.presentation.watchlist.WatchlistViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: PriceViewModel,
    context: Context,
    watchlistViewModel: WatchlistViewModel
) {

    val state = viewModel.state
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            MyAppBar(
                title = "PriceFlux",
                viewModel = viewModel,
                onCameraClick = {
                    navController.navigate("qrscanner")
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
                    // Display a circular progress indicator
                    Column (
                        modifier = Modifier.align(Alignment.Center)
                    ){

                        SearchAnimation(Modifier.align(Alignment.CenterHorizontally))
                        Text(text = "Hold on search in progress...")

                    }

                } else if(state.amazonInfo.isNotEmpty()&&state.flipkartInfo.isNotEmpty()) {
                    // Create a TabRow with two tabs: one for Amazon and one for Flipkart
                    val tabs = listOf("Amazon", "Flipkart")
                    var selectedTabIndex by remember { mutableStateOf(0) }
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        pageCount = { tabs.size }
                    )
                    val watchlistState = watchlistViewModel.state

                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, bottom = 5.dp)
                    ){
                        TabRow(
                            selectedTabIndex,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp, bottom = 10.dp),
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
                                    // Inside LazyColumn's items block
                                    items(state.amazonInfo.size) { index ->
                                        val product = state.amazonInfo[index]

                                        val showWatchlistDialog = remember { mutableStateOf(false) }
                                        Log.d("product",product.toString())
                                        Log.d("Watchlist",watchlistState.prodInfo.toString())
                                        watchlistViewModel.searchProduct(product.productName)
                                        val isPresent = watchlistState.prodInfo.filter {
                                            it.productName == product.productName

                                        }

                                        val watchlist = SwipeAction(
                                            icon = { Icon(if(isPresent.isNotEmpty()) {
                                                Icons.Filled.RemoveRedEye
                                            }else{
                                                Icons.Outlined.RemoveRedEye
                                            }, contentDescription = null , modifier = Modifier
                                                .size(52.dp)
                                                .padding(start = 15.dp)) },
                                            background = if(isPresent.isNotEmpty()) {
                                                Color.Yellow
                                            }else{
                                                Color.Green
                                            },
                                            isUndo = true,
                                            onSwipe = {
                                                if(isPresent.isNotEmpty()) {
                                                    Toast.makeText(context,"Already in watchlist",Toast.LENGTH_SHORT).show()
                                                }else {
                                                    showWatchlistDialog.value = true
                                                }
                                            }
                                        )

                                        SwipeableActionsBox (
                                            endActions = listOf(watchlist)
                                        ){
                                            Box {
                                                ListItemCard(
                                                    product = product,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(5.dp),
                                                    context = context,
                                                    text = "amazon"
                                                )
                                            }
                                        }
                                        if(showWatchlistDialog.value){
                                            WatchlistAlertDialog(
                                                onDismissRequest = { showWatchlistDialog.value = false },
                                                onConfirmAdd = {
                                                    watchlistViewModel.addToWatchlist(product)
                                                    showWatchlistDialog.value = false }
                                            )
                                        }

//

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
                                        val showWatchlistDialog = remember { mutableStateOf(false) }
                                        Log.d("product",product.toString())
                                        Log.d("Watchlist",watchlistState.prodInfo.toString())
                                        watchlistViewModel.searchProduct(product.productName)
                                        val isPresent = watchlistState.prodInfo.filter {
                                            it.productName == product.productName

                                        }

                                        val watchlist = SwipeAction(
                                            icon = { Icon(if(isPresent.isNotEmpty()) {
                                                Icons.Filled.RemoveRedEye
                                            }else{
                                                Icons.Outlined.RemoveRedEye
                                            }, contentDescription = null , modifier = Modifier
                                                .size(52.dp)
                                                .padding(start = 15.dp)) },
                                            background = if(isPresent.isNotEmpty()) {
                                                Color.Yellow
                                            }else{
                                                Color.Green
                                            },
                                            isUndo = true,
                                            onSwipe = {
                                                if(isPresent.isNotEmpty()) {
                                                    Toast.makeText(context,"Already in watchlist",Toast.LENGTH_SHORT).show()
                                                }else {
                                                    showWatchlistDialog.value = true
                                                }
                                            }
                                        )

                                        SwipeableActionsBox (
                                            endActions = listOf(watchlist)
                                        ){
                                            Box {
                                                ListItemCard(
                                                    product = product,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(5.dp),
                                                    context = context,
                                                    text = "flipkart"
                                                )
                                            }
                                        }
                                        if(showWatchlistDialog.value){
                                            WatchlistAlertDialog(
                                                onDismissRequest = { showWatchlistDialog.value = false },
                                                onConfirmAdd = {
                                                    watchlistViewModel.addToWatchlist(product)
                                                    showWatchlistDialog.value = false }
                                            )
                                        }

//
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
        val scrollThreshold = 0 // Adjust as needed

        if (currentScrollOffset+scrollThreshold > previousScrollOffset) {
            // Scrolled down
            isTopBarVisible = false

        } else if (currentScrollOffset < previousScrollOffset+scrollThreshold) {
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
fun SearchAnimation(
    modifier: Modifier =Modifier
) {
    // Add your search animation here
    val context= LocalContext.current
    val composition by rememberLottieComposition( spec = LottieCompositionSpec.Asset("loading.json"))

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfo(
    productInfo: RemoteDto,
    sheetState: SheetState,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
){
    ModalBottomSheet(
        onDismissRequest  = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        // Sheet content
        Text(text = "Bottom sheet")

        Button(onClick = onDismissRequest){
            Text(text = "Close")
        }
    }
}

@Composable
fun WatchlistAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmAdd: () -> Unit,
){

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "No")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmAdd()
            }) {
                Text(text = "Yes")
            }
        },
        title = { Text("Watchlist Item?") },
        text = { Text("Are you sure you want to Watchlist this item?") }
    )

}

