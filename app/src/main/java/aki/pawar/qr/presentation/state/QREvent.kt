package aki.pawar.qr.presentation.state

import android.net.Uri

/**
 * Sealed class representing user actions/events for QR Generator
 */
sealed class QRGeneratorEvent {
    data class OnTextChanged(val text: String) : QRGeneratorEvent()
    data object OnGenerateClick : QRGeneratorEvent()
    data object OnShareClick : QRGeneratorEvent()
    data object OnSaveClick : QRGeneratorEvent()
    data object OnMessageDismissed : QRGeneratorEvent()
}

/**
 * Sealed class representing user actions/events for QR Scanner
 */
sealed class QRScannerEvent {
    data class OnBarcodeScanned(val result: String, val format: Int) : QRScannerEvent()
    data class OnImageSelected(val uri: Uri) : QRScannerEvent()
    data object OnCopyClick : QRScannerEvent()
    data object OnScanAgainClick : QRScannerEvent()
    data object OnMessageDismissed : QRScannerEvent()
}

