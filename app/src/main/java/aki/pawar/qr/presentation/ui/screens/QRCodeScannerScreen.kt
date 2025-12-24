package aki.pawar.qr.presentation.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import aki.pawar.qr.MyApp
import aki.pawar.qr.di.QRScannerViewModelFactory
import aki.pawar.qr.presentation.state.QRScannerEvent
import aki.pawar.qr.presentation.ui.theme.QRTheme
import aki.pawar.qr.presentation.viewmodel.QRScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private const val TAG = "BarcodeScanner"

/**
 * QR Code and Barcode Scanner Screen
 * Uses CameraX and ML Kit for scanning
 * Supports scanning from gallery images
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRCodeScannerScreen(
    viewModel: QRScannerViewModel = viewModel(
        factory = QRScannerViewModelFactory(
            (LocalContext.current.applicationContext as MyApp).container
        )
    )
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Camera permission
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    // Gallery image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onEvent(QRScannerEvent.OnImageSelected(it)) }
    }
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    // Handle messages
    LaunchedEffect(state.message) {
        state.message?.let { message ->
            Toast.makeText(context, message.text, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(QRScannerEvent.OnMessageDismissed)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        QRTheme.BackgroundLight,
                        QRTheme.BackgroundLightSecondary
                    )
                )
            )
    ) {
        // Show loading overlay when processing image
        if (state.isProcessingImage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = QRTheme.CardBackground)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = QRTheme.AccentOrange)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Scanning image...",
                            color = QRTheme.TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        if (cameraPermissionState.status.isGranted) {
            ScannerContent(
                isScanning = state.isScanning,
                scannedResult = state.scannedResult,
                barcodeType = state.barcodeType,
                onBarcodeScanned = { result, format -> 
                    viewModel.onEvent(QRScannerEvent.OnBarcodeScanned(result, format)) 
                },
                onCopyClick = { viewModel.onEvent(QRScannerEvent.OnCopyClick) },
                onScanAgainClick = { viewModel.onEvent(QRScannerEvent.OnScanAgainClick) },
                onPickFromGallery = { imagePickerLauncher.launch("image/*") }
            )
        } else {
            PermissionRequiredScreen(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() },
                onPickFromGallery = { imagePickerLauncher.launch("image/*") }
            )
        }
    }
}

@Composable
private fun ScannerContent(
    isScanning: Boolean,
    scannedResult: String?,
    barcodeType: String,
    onBarcodeScanned: (String, Int) -> Unit,
    onCopyClick: () -> Unit,
    onScanAgainClick: () -> Unit,
    onPickFromGallery: () -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Scan QR codes & barcodes",
            style = MaterialTheme.typography.bodyMedium,
            color = QRTheme.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        // Supported formats hint
        Text(
            text = "Supports: QR, EAN, UPC, Code 128, Code 39, and more",
            style = MaterialTheme.typography.bodySmall,
            color = QRTheme.TextLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Camera preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    color = QRTheme.SoftBorder,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            if (isScanning) {
                CameraPreview(onBarcodeScanned = onBarcodeScanned)
                ScannerOverlay()
            } else {
                // Static placeholder when not scanning
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(QRTheme.BackgroundLightSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = QRTheme.AccentOrange.copy(alpha = 0.3f),
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pick from gallery button
        OutlinedButton(
            onClick = onPickFromGallery,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = QRTheme.AccentOrange
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = Brush.horizontalGradient(
                    colors = listOf(QRTheme.AccentOrange, QRTheme.AccentOrangeDark)
                )
            )
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Pick from Gallery",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Result card
        AnimatedVisibility(
            visible = scannedResult != null,
            enter = fadeIn() + slideInVertically { it }
        ) {
            scannedResult?.let { result ->
                ResultCard(
                    result = result,
                    barcodeType = barcodeType,
                    onCopyClick = onCopyClick,
                    onOpenClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result))
                        context.startActivity(intent)
                    },
                    onScanAgainClick = onScanAgainClick
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ScannerOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(
                width = 3.dp,
                color = QRTheme.AccentOrange.copy(alpha = animatedAlpha),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        CornerAccents()
    }
}

@Composable
private fun CornerAccents() {
    val cornerSize = 40.dp
    val strokeWidth = 4.dp
    val cornerColor = QRTheme.AccentOrange
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Top left
        Box(modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(strokeWidth)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
            Box(
                modifier = Modifier
                    .width(strokeWidth)
                    .height(cornerSize)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
        }
        
        // Top right
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(strokeWidth)
                    .align(Alignment.TopEnd)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
            Box(
                modifier = Modifier
                    .width(strokeWidth)
                    .height(cornerSize)
                    .align(Alignment.TopEnd)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
        }
        
        // Bottom left
        Box(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(strokeWidth)
                    .align(Alignment.BottomStart)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
            Box(
                modifier = Modifier
                    .width(strokeWidth)
                    .height(cornerSize)
                    .align(Alignment.BottomStart)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
        }
        
        // Bottom right
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(cornerSize)
                    .height(strokeWidth)
                    .align(Alignment.BottomEnd)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
            Box(
                modifier = Modifier
                    .width(strokeWidth)
                    .height(cornerSize)
                    .align(Alignment.BottomEnd)
                    .background(cornerColor, RoundedCornerShape(strokeWidth))
            )
        }
    }
}

@Composable
private fun CameraPreview(onBarcodeScanned: (String, Int) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }
    
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                
                val barcodeScanner = BarcodeScanning.getClient()
                
                @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                
                                barcodeScanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { value ->
                                                Log.d(TAG, "Barcode scanned: $value (format: ${barcode.format})")
                                                onBarcodeScanned(value, barcode.format)
                                            }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Barcode scanning failed", e)
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Camera binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

@Composable
private fun ResultCard(
    result: String,
    barcodeType: String,
    onCopyClick: () -> Unit,
    onOpenClick: () -> Unit,
    onScanAgainClick: () -> Unit
) {
    val isUrl = result.startsWith("http://") || result.startsWith("https://")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = QRTheme.AccentOrange.copy(alpha = 0.1f),
                spotColor = QRTheme.AccentOrange.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = QRTheme.CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barcode type badge
            Box(
                modifier = Modifier
                    .background(
                        QRTheme.AccentOrangeLight,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = barcodeType,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = QRTheme.AccentOrange
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Scanned Result",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = QRTheme.TextPrimary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Result text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(QRTheme.BackgroundLightSecondary)
                    .border(
                        width = 1.dp,
                        color = QRTheme.SoftBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = result,
                    color = QRTheme.TextPrimary,
                    fontSize = 14.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = onCopyClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = QRTheme.AccentOrangeLight,
                        contentColor = QRTheme.AccentOrange
                    )
                ) {
                    Icon(Icons.Default.ContentCopy, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Copy", fontWeight = FontWeight.SemiBold)
                }
                
                if (isUrl) {
                    FilledTonalButton(
                        onClick = onOpenClick,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = QRTheme.AccentOrangeLight,
                            contentColor = QRTheme.AccentOrangeDark
                        )
                    ) {
                        Icon(Icons.Default.OpenInBrowser, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Open", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onScanAgainClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QRTheme.AccentOrange,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Refresh, null, Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text("Scan Again", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun PermissionRequiredScreen(
    onRequestPermission: () -> Unit,
    onPickFromGallery: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(QRTheme.AccentOrangeLight, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = QRTheme.AccentOrange,
                modifier = Modifier.size(64.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = QRTheme.TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "To scan QR codes and barcodes with camera, please grant permission",
            style = MaterialTheme.typography.bodyMedium,
            color = QRTheme.TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.8f).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = QRTheme.AccentOrange,
                contentColor = Color.White
            )
        ) {
            Text("Grant Permission", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "OR",
            color = QRTheme.TextLight,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onPickFromGallery,
            modifier = Modifier.fillMaxWidth(0.8f).height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = QRTheme.AccentOrange
            )
        ) {
            Icon(Icons.Default.Image, null, Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text("Pick from Gallery", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

