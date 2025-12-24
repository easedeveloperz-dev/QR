package aki.pawar.qr.domain.usecase

import aki.pawar.qr.domain.model.QRCodeResult
import aki.pawar.qr.domain.repository.QRCodeRepository

/**
 * Use case for generating QR codes
 * Encapsulates the business logic for QR code generation
 */
class GenerateQRCodeUseCase(
    private val repository: QRCodeRepository
) {
    /**
     * Generates a QR code from the given text
     * @param text The content to encode
     * @param size The size of the QR code in pixels
     * @return QRCodeResult if successful, null otherwise
     */
    suspend operator fun invoke(text: String, size: Int = 256): QRCodeResult? {
        if (text.isBlank()) return null
        
        val bitmap = repository.generateQRCode(text, size)
        return bitmap?.let {
            QRCodeResult(
                bitmap = it,
                content = text
            )
        }
    }
}

