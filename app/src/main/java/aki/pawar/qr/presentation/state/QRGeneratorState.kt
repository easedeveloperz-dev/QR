package aki.pawar.qr.presentation.state

import android.graphics.Bitmap

/**
 * UI State for QR Code Generator screen
 * Represents all possible states of the generator
 */
data class QRGeneratorState(
    val inputText: String = "",
    val generatedBitmap: Bitmap? = null,
    val isGenerating: Boolean = false,
    val isSaving: Boolean = false,
    val isSharing: Boolean = false,
    val showQRCode: Boolean = false,
    val message: Message? = null
)

/**
 * UI State for QR Code Scanner screen
 */
data class QRScannerState(
    val isScanning: Boolean = true,
    val scannedResult: String? = null,
    val barcodeType: String = "QR Code",
    val isCopied: Boolean = false,
    val isProcessingImage: Boolean = false,
    val message: Message? = null
)

/**
 * Represents a user-facing message (toast/snackbar)
 */
data class Message(
    val text: String,
    val type: MessageType = MessageType.INFO
)

/**
 * Types of messages for styling
 */
enum class MessageType {
    SUCCESS,
    ERROR,
    INFO
}

