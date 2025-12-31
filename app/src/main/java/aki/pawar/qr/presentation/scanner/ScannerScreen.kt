package aki.pawar.qr.presentation.scanner

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import aki.pawar.qr.domain.model.BarcodeContentType
import aki.pawar.qr.ui.theme.ScannerFrame
import aki.pawar.qr.ui.theme.ScannerOverlay
import androidx.compose.material.icons.filled.QrCodeScanner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

private const val TAG = "ScannerScreen"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val bitmap = android.graphics.ImageDecoder.decodeBitmap(
                    android.graphics.ImageDecoder.createSource(context.contentResolver, it)
                )
                viewModel.onEvent(ScannerEvent.ProcessImage(bitmap))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load image", e)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Flash toggle
                    IconButton(onClick = { viewModel.onEvent(ScannerEvent.ToggleFlash) }) {
                        Icon(
                            imageVector = if (state.isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash"
                        )
                    }
                    // Gallery picker
                    IconButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Icon(Icons.Default.Image, "Pick from Gallery")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (cameraPermissionState.status.isGranted) {
                // Camera Preview
                if (state.isScanning && state.scanResult == null) {
                    CameraPreview(
                        isFlashOn = state.isFlashOn,
                        onBarcodeScanned = { rawValue, format, type ->
                            viewModel.processBarcode(rawValue, format, type)
                        }
                    )
                    
                    // Scanner Overlay
                    ScannerOverlayView()
                }
                
                // Result Card
                AnimatedVisibility(
                    visible = state.scanResult != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    state.scanResult?.let { result ->
                        ScanResultCard(
                            result = result,
                            onOpen = { viewModel.onEvent(ScannerEvent.HandleResult(ResultAction.Open)) },
                            onCopy = { viewModel.onEvent(ScannerEvent.HandleResult(ResultAction.Copy)) },
                            onShare = { viewModel.onEvent(ScannerEvent.HandleResult(ResultAction.Share)) },
                            onAddContact = { viewModel.onEvent(ScannerEvent.HandleResult(ResultAction.AddContact)) },
                            onConnectWifi = { viewModel.onEvent(ScannerEvent.HandleResult(ResultAction.ConnectWifi)) },
                            onScanAgain = { viewModel.onEvent(ScannerEvent.DismissResult) }
                        )
                    }
                }
                
                // Loading overlay
                if (state.isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            } else {
                // Permission request UI
                PermissionRequestView(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                )
            }
            
            // Warning Dialog
            if (state.showWarningDialog) {
                WarningDialog(
                    message = state.warningMessage,
                    onConfirm = { viewModel.onEvent(ScannerEvent.ConfirmAction) },
                    onDismiss = { viewModel.onEvent(ScannerEvent.DismissWarning) }
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(
    isFlashOn: Boolean,
    onBarcodeScanned: (String, Int, Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    // Remember camera reference for flash control
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    
    // Update flash when it changes
    LaunchedEffect(isFlashOn) {
        camera?.cameraControl?.enableTorch(isFlashOn)
    }
    
    DisposableEffect(Unit) {
        onDispose { 
            cameraExecutor.shutdown() 
        }
    }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    
                    // Preview use case
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                    
                    // Barcode scanner
                    val barcodeScanner = BarcodeScanning.getClient()
                    
                    // Image analysis use case
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
                                            barcodes.firstOrNull()?.let { barcode ->
                                                barcode.rawValue?.let { value ->
                                                    onBarcodeScanned(value, barcode.format, barcode.valueType)
                                                }
                                            }
                                        }
                                        .addOnCompleteListener { imageProxy.close() }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }
                    
                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll()
                    
                    // Bind use cases to camera
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                    
                    Log.d(TAG, "Camera bound successfully")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Camera binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ScannerOverlayView() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    
    // Animated scan line
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )
    
    // Pulsing corners
    val cornerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cornerAlpha"
    )
    
    val overlayColor = Color.Black.copy(alpha = 0.6f)
    val scannerSize = 280.dp
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Draw overlay with cutout using 4 rectangles
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            val horizontalPadding = (screenWidth - scannerSize) / 2
            val verticalPadding = (screenHeight - scannerSize) / 2
            
            // Top overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(verticalPadding)
                    .align(Alignment.TopCenter)
                    .background(overlayColor)
            )
            
            // Bottom overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(verticalPadding)
                    .align(Alignment.BottomCenter)
                    .background(overlayColor)
            )
            
            // Left overlay
            Box(
                modifier = Modifier
                    .width(horizontalPadding)
                    .height(scannerSize)
                    .align(Alignment.CenterStart)
                    .background(overlayColor)
            )
            
            // Right overlay
            Box(
                modifier = Modifier
                    .width(horizontalPadding)
                    .height(scannerSize)
                    .align(Alignment.CenterEnd)
                    .background(overlayColor)
            )
        }
        
        // Scanner frame with animated corners
        Box(
            modifier = Modifier.size(scannerSize)
        ) {
            val cornerLength = 40.dp
            val cornerWidth = 4.dp
            val cornerColor = ScannerFrame.copy(alpha = cornerAlpha)
            
            // Draw corners
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cLength = cornerLength.toPx()
                val cWidth = cornerWidth.toPx()
                val radius = 16.dp.toPx()
                
                // Top-left
                drawLine(cornerColor, Offset(0f, radius), Offset(0f, cLength), cWidth, StrokeCap.Round)
                drawLine(cornerColor, Offset(radius, 0f), Offset(cLength, 0f), cWidth, StrokeCap.Round)
                drawArc(cornerColor, 180f, 90f, false, Offset(0f, 0f), Size(radius * 2, radius * 2), style = Stroke(cWidth, cap = StrokeCap.Round))
                
                // Top-right
                drawLine(cornerColor, Offset(size.width, radius), Offset(size.width, cLength), cWidth, StrokeCap.Round)
                drawLine(cornerColor, Offset(size.width - radius, 0f), Offset(size.width - cLength, 0f), cWidth, StrokeCap.Round)
                drawArc(cornerColor, 270f, 90f, false, Offset(size.width - radius * 2, 0f), Size(radius * 2, radius * 2), style = Stroke(cWidth, cap = StrokeCap.Round))
                
                // Bottom-left
                drawLine(cornerColor, Offset(0f, size.height - radius), Offset(0f, size.height - cLength), cWidth, StrokeCap.Round)
                drawLine(cornerColor, Offset(radius, size.height), Offset(cLength, size.height), cWidth, StrokeCap.Round)
                drawArc(cornerColor, 90f, 90f, false, Offset(0f, size.height - radius * 2), Size(radius * 2, radius * 2), style = Stroke(cWidth, cap = StrokeCap.Round))
                
                // Bottom-right
                drawLine(cornerColor, Offset(size.width, size.height - radius), Offset(size.width, size.height - cLength), cWidth, StrokeCap.Round)
                drawLine(cornerColor, Offset(size.width - radius, size.height), Offset(size.width - cLength, size.height), cWidth, StrokeCap.Round)
                drawArc(cornerColor, 0f, 90f, false, Offset(size.width - radius * 2, size.height - radius * 2), Size(radius * 2, radius * 2), style = Stroke(cWidth, cap = StrokeCap.Round))
            }
            
            // Animated scan line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .padding(horizontal = 16.dp)
                    .offset(y = (260.dp * scanLineOffset))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ScannerFrame,
                                ScannerFrame,
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
        
        // Scan hint with background
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "📷 Point camera at QR code",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ScanResultCard(
    result: aki.pawar.qr.domain.model.ScanResult,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onAddContact: () -> Unit,
    onConnectWifi: () -> Unit,
    onScanAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Plain text content at the top - prominently displayed
        Text(
            text = "Scanned Result",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Raw value as plain text - selectable
        SelectionContainer {
            Text(
                text = result.rawValue,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Type and format badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = result.type.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text = result.format.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        
        // UPI Details if applicable
        if (result.isUpi) {
            result.parseUpiDetails()?.let { upi ->
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment Details",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        DetailRow("Payee", upi.payeeName)
                        DetailRow("UPI ID", upi.upiId)
                        if (upi.amount.isNotBlank()) {
                            DetailRow("Amount", "₹${upi.amount}")
                        }
                        if (upi.transactionNote.isNotBlank()) {
                            DetailRow("Note", upi.transactionNote)
                        }
                    }
                }
            }
        }
        
        // Warning for potentially unsafe links
        if (result.isPotentiallyUnsafe) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "This link may be shortened or uses HTTP. Proceed with caution.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action buttons
        Text(
            text = "Actions",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Primary action
        if (result.isUrl || result.isUpi || result.type == BarcodeContentType.PHONE ||
            result.type == BarcodeContentType.SMS || result.type == BarcodeContentType.EMAIL ||
            result.type == BarcodeContentType.GEO) {
            Button(
                onClick = onOpen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.OpenInNew, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when {
                        result.isUpi -> "Open in Payment App"
                        result.isUrl -> "Open URL"
                        result.type == BarcodeContentType.PHONE -> "Call Number"
                        result.type == BarcodeContentType.SMS -> "Send SMS"
                        result.type == BarcodeContentType.EMAIL -> "Send Email"
                        result.type == BarcodeContentType.GEO -> "Open in Maps"
                        else -> "Open"
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Type-specific actions
        if (result.type == BarcodeContentType.CONTACT) {
            FilledTonalButton(
                onClick = onAddContact,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PersonAdd, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Contact")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        if (result.type == BarcodeContentType.WIFI) {
            FilledTonalButton(
                onClick = onConnectWifi,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Wifi, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Open Wi-Fi Settings")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCopy,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy")
            }
            
            OutlinedButton(
                onClick = onShare,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onScanAgain,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan Again")
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PermissionRequestView(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "To scan QR codes and barcodes, please grant camera permission.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}

@Composable
private fun WarningDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Warning") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Proceed")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

