package com.example.priceflux.presentation.watchlist

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.priceflux.ContextProvider.context
import com.example.priceflux.data.local.product.ProductEntity
import com.example.priceflux.presentation.SearchAnimation
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel
) {
    val viewModel = hiltViewModel<WatchlistViewModel>()

    val state = viewModel.state
    val searchQuery by viewModel.searchQuery.collectAsState() // Observe search query directly
    val isSearching by viewModel.isSearching.collectAsState(false) // Observe search state

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Watchlist") },
                actions = {
                    SearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        query = searchQuery,
                        onQueryChange = {
                            viewModel.onSearchTextChange(it)
                        },
                        onSearch = viewModel::onSearch,
                        active = isSearching,
                        onActiveChange = {
                            if(!it){
                                viewModel.onToggleSearch()
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,

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
                }
            )
        }
    ){ paddingValues ->
        Surface(
            Modifier
                .padding(paddingValues)
                .padding(start = 10.dp, end = 10.dp)
                .fillMaxSize()
        ){
            if(state.isLoading){
                SearchAnimation()
            }else if(state.error.isNotEmpty()){

            }else{
                val searchResults = state.prodInfo
                Log.d("local db search",searchResults.toString())
                if(searchResults.isEmpty()){

                }else{
                    Column {
                        Text(
                            text = "Watchlist:",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        var result by remember { mutableStateOf(searchResults) } // Assuming your initial list is stored here

                        LazyColumn {
                            items(result.size) { index ->

                                val product = searchResults[index]
                                val showDeleteDialog = remember { mutableStateOf(false) }

                                val delete = SwipeAction(
                                    icon = { Icon(Icons.Default.Delete, contentDescription = null , modifier = Modifier.size(52.dp).padding(start = 15.dp)) },
                                    background = MaterialTheme.colorScheme.error,
                                    isUndo = true,
                                    onSwipe = {
                                        showDeleteDialog.value = true
                                    }
                                )

                                SwipeableActionsBox (
                                    endActions = listOf(delete)
                                ){
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                    ){
                                        WatchListItem(
                                            product = product,
                                            modifier = Modifier
                                                .fillMaxSize()
                                        )
                                    }
                                }

                                if (showDeleteDialog.value) {
                                    DeleteAlertDialog(
                                        onDismissRequest = { showDeleteDialog.value = false },
                                        onConfirmDelete = {
                                            result = result.toMutableList().apply { removeAt(index) }

                                            viewModel.deleteProduct(product)
                                            showDeleteDialog.value = false

                                        }
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

@Composable
fun DeleteAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
){

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = {
                onConfirmDelete()
            }) {
                Text(text = "Yes")
            }
        }, dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "No")
            }
        },
        title = { Text("Delete Item?") },
        text = { Text("Are you sure you want to delete this item?") }
    )

}
