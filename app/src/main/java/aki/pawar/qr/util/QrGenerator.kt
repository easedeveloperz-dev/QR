package aki.pawar.qr.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for generating QR code bitmaps using ZXing
 */
@Singleton
class QrGenerator @Inject constructor() {
    
    /**
     * Generates a QR code bitmap from the given content
     * 
     * @param content The content to encode in the QR code
     * @param size The size of the generated bitmap in pixels
     * @param foregroundColor The color of the QR code modules (default: black)
     * @param backgroundColor The background color (default: white)
     * @param margin The quiet zone margin (default: 1)
     * @return Generated bitmap or null if generation fails
     */
    suspend fun generate(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        margin: Int = 1
    ): Bitmap? = withContext(Dispatchers.Default) {
        try {
            if (content.isBlank()) return@withContext null
            
            val hints = hashMapOf<EncodeHintType, Any>(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to margin
            )
            
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (bitMatrix[x, y]) foregroundColor else backgroundColor
                }
            }
            
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Generates a high-quality QR code with custom styling
     */
    suspend fun generateStyled(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        cornerRadius: Float = 0f
    ): Bitmap? = withContext(Dispatchers.Default) {
        // For now, delegate to standard generation
        // Can be extended to add rounded corners, logos, etc.
        generate(content, size, foregroundColor, backgroundColor)
    }
}



