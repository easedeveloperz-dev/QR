package aki.pawar.qr.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import aki.pawar.qr.presentation.viewmodel.QRGeneratorViewModel
import aki.pawar.qr.presentation.viewmodel.QRScannerViewModel

/**
 * Factory for creating QRGeneratorViewModel with dependencies
 */
class QRGeneratorViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QRGeneratorViewModel::class.java)) {
            return QRGeneratorViewModel(
                generateQRCodeUseCase = container.generateQRCodeUseCase,
                saveQRCodeUseCase = container.saveQRCodeUseCase,
                shareQRCodeUseCase = container.shareQRCodeUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

/**
 * Factory for creating QRScannerViewModel with dependencies
 */
class QRScannerViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QRScannerViewModel::class.java)) {
            return QRScannerViewModel(
                copyToClipboardUseCase = container.copyToClipboardUseCase,
                scanImageUseCase = container.scanImageUseCase,
                repository = container.qrCodeRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

