package aki.pawar.qr.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aki.pawar.qr.domain.repository.QRCodeRepository
import aki.pawar.qr.domain.usecase.CopyToClipboardUseCase
import aki.pawar.qr.domain.usecase.ScanImageUseCase
import aki.pawar.qr.presentation.state.Message
import aki.pawar.qr.presentation.state.MessageType
import aki.pawar.qr.presentation.state.QRScannerEvent
import aki.pawar.qr.presentation.state.QRScannerState
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for QR Code and Barcode Scanner screen
 * Handles scanning state and user actions
 */
class QRScannerViewModel(
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val scanImageUseCase: ScanImageUseCase,
    private val repository: QRCodeRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(QRScannerState())
    val state: StateFlow<QRScannerState> = _state.asStateFlow()
    
    /**
     * Handles UI events from the screen
     */
    fun onEvent(event: QRScannerEvent) {
        when (event) {
            is QRScannerEvent.OnBarcodeScanned -> {
                if (_state.value.scannedResult == null) {
                    _state.update { 
                        it.copy(
                            scannedResult = event.result,
                            barcodeType = mapBarcodeFormat(event.format),
                            isScanning = false
                        ) 
                    }
                }
            }
            
            is QRScannerEvent.OnImageSelected -> {
                scanImageFromGallery(event.uri)
            }
            
            is QRScannerEvent.OnCopyClick -> {
                _state.value.scannedResult?.let { result ->
                    copyToClipboardUseCase(result)
                    _state.update { 
                        it.copy(
                            isCopied = true,
                            message = Message(
                                text = "Copied to clipboard!",
                                type = MessageType.SUCCESS
                            )
                        ) 
                    }
                }
            }
            
            is QRScannerEvent.OnScanAgainClick -> {
                _state.update { 
                    QRScannerState(
                        isScanning = true,
                        scannedResult = null
                    )
                }
            }
            
            is QRScannerEvent.OnMessageDismissed -> {
                _state.update { it.copy(message = null) }
            }
        }
    }
    
    private fun scanImageFromGallery(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isProcessingImage = true) }
            
            try {
                val bitmap = repository.loadBitmapFromUri(uri)
                
                if (bitmap != null) {
                    val result = scanImageUseCase(bitmap)
                    
                    _state.update { currentState ->
                        if (result != null) {
                            currentState.copy(
                                isProcessingImage = false,
                                scannedResult = result.content,
                                barcodeType = result.barcodeType.displayName,
                                isScanning = false
                            )
                        } else {
                            currentState.copy(
                                isProcessingImage = false,
                                message = Message(
                                    text = "No QR code or barcode found in image",
                                    type = MessageType.ERROR
                                )
                            )
                        }
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isProcessingImage = false,
                            message = Message(
                                text = "Failed to load image",
                                type = MessageType.ERROR
                            )
                        ) 
                    }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isProcessingImage = false,
                        message = Message(
                            text = "Error processing image",
                            type = MessageType.ERROR
                        )
                    ) 
                }
            }
        }
    }
    
    /**
     * Maps ML Kit barcode format to display name
     */
    private fun mapBarcodeFormat(format: Int): String {
        return when (format) {
            Barcode.FORMAT_QR_CODE -> "QR Code"
            Barcode.FORMAT_CODE_128 -> "Code 128"
            Barcode.FORMAT_CODE_39 -> "Code 39"
            Barcode.FORMAT_CODE_93 -> "Code 93"
            Barcode.FORMAT_CODABAR -> "Codabar"
            Barcode.FORMAT_EAN_13 -> "EAN-13"
            Barcode.FORMAT_EAN_8 -> "EAN-8"
            Barcode.FORMAT_ITF -> "ITF"
            Barcode.FORMAT_UPC_A -> "UPC-A"
            Barcode.FORMAT_UPC_E -> "UPC-E"
            Barcode.FORMAT_PDF417 -> "PDF417"
            Barcode.FORMAT_AZTEC -> "Aztec"
            Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
            else -> "Barcode"
        }
    }
}

