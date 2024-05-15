// Import required Compose libraries

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.priceflux.presentation.BarcodeAnalyzer
import com.example.priceflux.presentation.PriceViewModel
import kotlinx.coroutines.delay


// Modify the CameraPreview composable to accept a NavController parameter
@Composable
fun CameraPreview(
    viewModel: PriceViewModel,
    navController: NavController
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )


    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    var isMovingDownward by remember { mutableStateOf(true) }

    var currentOffset by remember {
        mutableStateOf(700.dp.value)
    }
    // Launch the animation when the composable is first composed
    LaunchedEffect(Unit) {
        while (true) {
           if(isMovingDownward){
               currentOffset += 20.dp.value // Adjust speed by changing this value
               delay(20) // Adjust duration by changing this value
               if(currentOffset >= 1500.dp.value){
                   isMovingDownward = false
               }
           }else{
               currentOffset -= 20.dp.value // Adjust speed by changing this value
               delay(20) // Adjust duration by changing this value
               if(currentOffset <= 700.dp.value){
                   isMovingDownward = true
               }
           }
        }
    }



    Box(
        modifier = Modifier
            .width(600.dp)
            .height(500.dp)
            .padding(60.dp),
        contentAlignment = Alignment.Center
    ) {

        if (hasCamPermission) {
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(
                                Size(
                                    previewView.width,
                                    previewView.height
                                )
                            )
                            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            BarcodeAnalyzer { result ->
                                for (i in result) {
                                    if (i.displayValue.toString().isNotEmpty()) {
                                        viewModel.onSearchTextChange(i.displayValue.toString())
                                        Toast.makeText(
                                            context,
                                            i.displayValue.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.popBackStack()
                                        navController.navigate("home")
                                        break
                                    }

                                }
                            }
                        )
                        try {
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        previewView
                    }
                ){

                }


            Canvas(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)) {
                val rectWidth = size.width
                val rectHeight = size.height / 2
                val centerX = size.width / 2
                val centerY = size.height / 4 // Adjust the vertical position here

//                // Draw semi-transparent rectangle
//                drawRect(
//                    color = Color(0x770000FF), // Semi-transparent blue color
//                    topLeft = Offset(centerX - rectWidth / 2, centerY - rectHeight / 2),
//                    size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight)
//                )

                val lineWidth = 5.dp.toPx() // Adjust line width
                val lineColor = Color.Blue
                drawLine(
                    color = lineColor,
                    start = Offset(x = 100.dp.value, y = currentOffset),
                    end = Offset(x = size.width-100.dp.value , y = currentOffset),
                    strokeWidth = lineWidth
                )

            }



        }

    }
}