package aki.pawar.qr.domain.repository

import android.graphics.Bitmap
import android.net.Uri

/**
 * Repository interface for QR code operations
 * Defines the contract for data layer implementation
 */
interface QRCodeRepository {
    
    /**
     * Generates a QR code bitmap from the given text
     * @param text The content to encode in the QR code
     * @param size The size of the generated bitmap in pixels
     * @return Generated bitmap or null if generation fails
     */
    suspend fun generateQRCode(text: String, size: Int = 256): Bitmap?
    
    /**
     * Saves the QR code bitmap to the device gallery
     * @param bitmap The bitmap to save
     * @param filename Optional custom filename
     * @return True if save was successful, false otherwise
     */
    suspend fun saveToGallery(bitmap: Bitmap, filename: String? = null): Boolean
    
    /**
     * Shares the QR code bitmap via system share sheet
     * @param bitmap The bitmap to share
     * @return True if share intent was launched successfully
     */
    suspend fun shareQRCode(bitmap: Bitmap): Boolean
    
    /**
     * Copies text to system clipboard
     * @param text The text to copy
     */
    fun copyToClipboard(text: String)
    
    /**
     * Scans a QR code or barcode from an image bitmap
     * @param bitmap The image to scan
     * @return Pair of content and barcode format, or null if not found
     */
    suspend fun scanBarcodeFromImage(bitmap: Bitmap): Pair<String, Int>?
    
    /**
     * Loads a bitmap from a content URI
     * @param uri The content URI of the image
     * @return The loaded bitmap or null if failed
     */
    suspend fun loadBitmapFromUri(uri: Uri): Bitmap?
}

