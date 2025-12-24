package aki.pawar.qr.di

import android.content.Context
import aki.pawar.qr.data.repository.QRCodeRepositoryImpl
import aki.pawar.qr.domain.repository.QRCodeRepository
import aki.pawar.qr.domain.usecase.CopyToClipboardUseCase
import aki.pawar.qr.domain.usecase.GenerateQRCodeUseCase
import aki.pawar.qr.domain.usecase.SaveQRCodeUseCase
import aki.pawar.qr.domain.usecase.ScanImageUseCase
import aki.pawar.qr.domain.usecase.ShareQRCodeUseCase

/**
 * Manual Dependency Injection Container
 * Provides dependencies throughout the application
 * Can be replaced with Hilt/Dagger for larger projects
 */
class AppContainer(context: Context) {
    
    // Repository - Single instance
    val qrCodeRepository: QRCodeRepository = QRCodeRepositoryImpl(context)
    
    // Use Cases - Created fresh each time for stateless operations
    val generateQRCodeUseCase: GenerateQRCodeUseCase
        get() = GenerateQRCodeUseCase(qrCodeRepository)
    
    val saveQRCodeUseCase: SaveQRCodeUseCase
        get() = SaveQRCodeUseCase(qrCodeRepository)
    
    val shareQRCodeUseCase: ShareQRCodeUseCase
        get() = ShareQRCodeUseCase(qrCodeRepository)
    
    val copyToClipboardUseCase: CopyToClipboardUseCase
        get() = CopyToClipboardUseCase(qrCodeRepository)
    
    val scanImageUseCase: ScanImageUseCase
        get() = ScanImageUseCase(qrCodeRepository)
}

