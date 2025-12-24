package aki.pawar.qr.domain.usecase

import android.graphics.Bitmap
import aki.pawar.qr.domain.repository.QRCodeRepository

/**
 * Use case for sharing QR codes
 */
class ShareQRCodeUseCase(
    private val repository: QRCodeRepository
) {
    /**
     * Shares the QR code bitmap via system share sheet
     * @param bitmap The bitmap to share
     * @return True if share was initiated successfully
     */
    suspend operator fun invoke(bitmap: Bitmap): Boolean {
        return repository.shareQRCode(bitmap)
    }
}

