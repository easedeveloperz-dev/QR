package aki.pawar.qr.domain.usecase

import aki.pawar.qr.domain.repository.QRCodeRepository

/**
 * Use case for copying text to clipboard
 */
class CopyToClipboardUseCase(
    private val repository: QRCodeRepository
) {
    /**
     * Copies the given text to system clipboard
     * @param text The text to copy
     */
    operator fun invoke(text: String) {
        repository.copyToClipboard(text)
    }
}

