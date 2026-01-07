package aki.pawar.qr.presentation.scanner

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aki.pawar.qr.data.repository.ScanHistoryRepository
import aki.pawar.qr.domain.model.BarcodeContentType
import aki.pawar.qr.domain.model.BarcodeFormat
import aki.pawar.qr.domain.model.ScanResult
import aki.pawar.qr.util.InAppReviewManager
import aki.pawar.qr.util.IntentHandler
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * UI State for Scanner Screen
 */
data class ScannerState(
    val isScanning: Boolean = true,
    val isFlashOn: Boolean = false,
    val scanResult: ScanResult? = null,
    val isProcessing: Boolean = false,
    val showWarningDialog: Boolean = false,
    val warningMessage: String = "",
    val error: String? = null
)

/**
 * Events from Scanner Screen
 */
sealed class ScannerEvent {
    data object StartScanning : ScannerEvent()
    data object StopScanning : ScannerEvent()
    data object ToggleFlash : ScannerEvent()
    data class ProcessImage(val bitmap: Bitmap) : ScannerEvent()
    data object DismissResult : ScannerEvent()
    data object DismissWarning : ScannerEvent()
    data object ConfirmAction : ScannerEvent()
    data class HandleResult(val action: ResultAction) : ScannerEvent()
    data object ClearError : ScannerEvent()
}

/**
 * Actions for scan result
 */
sealed class ResultAction {
    data object Open : ResultAction()
    data object Copy : ResultAction()
    data object Share : ResultAction()
    data object AddContact : ResultAction()
    data object ConnectWifi : ResultAction()
}

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val barcodeScanner: BarcodeScanner,
    private val scanHistoryRepository: ScanHistoryRepository,
    private val intentHandler: IntentHandler,
    private val inAppReviewManager: InAppReviewManager
) : ViewModel() {
    
    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state.asStateFlow()
    
    private var pendingAction: (() -> Unit)? = null
    
    fun onEvent(event: ScannerEvent) {
        when (event) {
            is ScannerEvent.StartScanning -> startScanning()
            is ScannerEvent.StopScanning -> stopScanning()
            is ScannerEvent.ToggleFlash -> toggleFlash()
            is ScannerEvent.ProcessImage -> processImage(event.bitmap)
            is ScannerEvent.DismissResult -> dismissResult()
            is ScannerEvent.DismissWarning -> dismissWarning()
            is ScannerEvent.ConfirmAction -> confirmAction()
            is ScannerEvent.HandleResult -> handleResultAction(event.action)
            is ScannerEvent.ClearError -> clearError()
        }
    }
    
    /**
     * Process barcode from camera frame
     */
    fun processBarcode(rawValue: String, format: Int, type: Int) {
        if (_state.value.scanResult != null) return // Already have a result
        
        viewModelScope.launch {
            val barcodeFormat = BarcodeFormat.fromMlKit(format)
            val contentType = BarcodeContentType.detect(rawValue)
            
            val result = ScanResult(
                rawValue = rawValue,
                format = barcodeFormat,
                type = contentType,
                displayValue = rawValue
            )
            
            // Save to history
            scanHistoryRepository.saveScan(
                rawValue = rawValue,
                displayValue = rawValue,
                format = barcodeFormat,
                contentType = contentType
            )
            
            // Record successful action for In-App Review
            inAppReviewManager.recordSuccessfulAction()
            
            _state.update { it.copy(scanResult = result, isScanning = false) }
        }
    }
    
    private fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                val barcodes = barcodeScanner.process(inputImage).await()
                
                val barcode = barcodes.firstOrNull()
                if (barcode != null && barcode.rawValue != null) {
                    processBarcode(
                        barcode.rawValue!!,
                        barcode.format,
                        barcode.valueType
                    )
                } else {
                    _state.update { it.copy(error = "No QR code or barcode found in image") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to process image: ${e.message}") }
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }
    
    private fun handleResultAction(action: ResultAction) {
        val result = _state.value.scanResult ?: return
        
        when (action) {
            is ResultAction.Open -> {
                // Check for potential security risks
                when {
                    result.isUpi -> {
                        val upiDetails = result.parseUpiDetails()
                        showWarning(
                            "Payment QR Code Detected!\n\n" +
                            "Payee: ${upiDetails?.payeeName ?: "Unknown"}\n" +
                            "UPI ID: ${upiDetails?.upiId ?: "Unknown"}\n" +
                            "Amount: ${upiDetails?.amount?.ifBlank { "Not specified" }}\n\n" +
                            "Do you want to proceed to payment app?"
                        ) {
                            intentHandler.openUpiPayment(result.rawValue)
                        }
                    }
                    result.isPotentiallyUnsafe -> {
                        showWarning(
                            "⚠️ Potentially Unsafe Link\n\n" +
                            "This URL may be shortened or uses HTTP instead of HTTPS.\n\n" +
                            "${result.rawValue}\n\n" +
                            "Do you want to open it anyway?"
                        ) {
                            intentHandler.openUrl(result.rawValue)
                        }
                    }
                    result.isUrl -> {
                        showWarning(
                            "Open URL?\n\n${result.rawValue}"
                        ) {
                            intentHandler.openUrl(result.rawValue)
                        }
                    }
                    else -> {
                        intentHandler.handleScanResult(result.rawValue)
                    }
                }
            }
            is ResultAction.Copy -> {
                intentHandler.copyToClipboard(result.rawValue)
            }
            is ResultAction.Share -> {
                intentHandler.shareText(result.rawValue)
            }
            is ResultAction.AddContact -> {
                if (result.type == BarcodeContentType.CONTACT) {
                    // Parse vCard and add contact
                    parseAndAddContact(result.rawValue)
                }
            }
            is ResultAction.ConnectWifi -> {
                if (result.type == BarcodeContentType.WIFI) {
                    intentHandler.openWifiSettings()
                }
            }
        }
    }
    
    private fun parseAndAddContact(vCard: String) {
        // Simple vCard parsing
        val lines = vCard.lines()
        var name = ""
        var phone = ""
        var email = ""
        var org = ""
        
        for (line in lines) {
            when {
                line.startsWith("FN:") -> name = line.removePrefix("FN:")
                line.startsWith("TEL:") || line.startsWith("TEL;") -> {
                    phone = line.substringAfter(":").trim()
                }
                line.startsWith("EMAIL:") || line.startsWith("EMAIL;") -> {
                    email = line.substringAfter(":").trim()
                }
                line.startsWith("ORG:") -> org = line.removePrefix("ORG:")
            }
        }
        
        intentHandler.addContact(name, phone, email, org)
    }
    
    private fun showWarning(message: String, action: () -> Unit) {
        pendingAction = action
        _state.update { it.copy(showWarningDialog = true, warningMessage = message) }
    }
    
    private fun confirmAction() {
        pendingAction?.invoke()
        pendingAction = null
        dismissWarning()
    }
    
    private fun dismissWarning() {
        pendingAction = null
        _state.update { it.copy(showWarningDialog = false, warningMessage = "") }
    }
    
    private fun startScanning() {
        _state.update { it.copy(isScanning = true, scanResult = null) }
    }
    
    private fun stopScanning() {
        _state.update { it.copy(isScanning = false) }
    }
    
    private fun toggleFlash() {
        val newFlashState = !_state.value.isFlashOn
        _state.update { it.copy(isFlashOn = newFlashState) }
    }
    
    private fun dismissResult() {
        _state.update { it.copy(scanResult = null, isScanning = true) }
    }
    
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

