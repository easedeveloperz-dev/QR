package aki.pawar.qr.domain.usecase

import android.graphics.Bitmap
import aki.pawar.qr.domain.repository.QRCodeRepository

/**
 * Use case for saving QR codes to gallery
 */
class SaveQRCodeUseCase(
    private val repository: QRCodeRepository
) {
    /**
     * Saves the QR code bitmap to device gallery
     * @param bitmap The bitmap to save
     * @return True if save was successful
     */
    suspend operator fun invoke(bitmap: Bitmap): Boolean {
        return repository.saveToGallery(bitmap)
    }
}

