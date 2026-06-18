package aki.pawar.qr.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import aki.pawar.qr.domain.model.FrameStyle
import aki.pawar.qr.domain.model.ModuleShape
import aki.pawar.qr.domain.model.QrCustomization
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for generating QR code bitmaps using ZXing with customization support
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
     * Generates a customized QR code with shapes, logo, and frame support
     * 
     * @param content The content to encode in the QR code
     * @param size The size of the generated bitmap in pixels
     * @param customization The customization options
     * @return Generated bitmap or null if generation fails
     */
    suspend fun generateCustomized(
        content: String,
        size: Int = 512,
        customization: QrCustomization
    ): Bitmap? = withContext(Dispatchers.Default) {
        try {
            if (content.isBlank()) return@withContext null
            
            // Calculate sizes based on frame style
            val frameMargin = when (customization.frameStyle) {
                FrameStyle.NONE -> 0
                FrameStyle.SIMPLE_BORDER -> 20
                FrameStyle.SCAN_ME -> 60
            }
            val qrSize = size - (frameMargin * 2)
            
            val hints = hashMapOf<EncodeHintType, Any>(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 1
            )
            
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, qrSize, qrSize, hints)
            
            // Create the final bitmap with frame space
            val finalBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(finalBitmap)
            
            // Draw background
            val bgPaint = Paint().apply {
                color = customization.backgroundColor
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), bgPaint)
            
            // Draw frame if needed
            drawFrame(canvas, size, customization)
            
            // Draw QR code modules with custom shapes
            drawQrModules(
                canvas = canvas,
                bitMatrix = bitMatrix,
                offsetX = frameMargin,
                offsetY = frameMargin,
                foregroundColor = customization.foregroundColor,
                moduleShape = customization.moduleShape
            )
            
            // Overlay logo if provided
            customization.logoBitmap?.let { logo ->
                drawLogo(canvas, logo, size, customization.logoSizePercent)
            }
            
            finalBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Draws QR code modules with the specified shape
     */
    private fun drawQrModules(
        canvas: Canvas,
        bitMatrix: com.google.zxing.common.BitMatrix,
        offsetX: Int,
        offsetY: Int,
        foregroundColor: Int,
        moduleShape: ModuleShape
    ) {
        val width = bitMatrix.width
        val height = bitMatrix.height
        
        // Calculate module size
        val moduleWidth = 1f
        val moduleHeight = 1f
        
        val paint = Paint().apply {
            color = foregroundColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (bitMatrix[x, y]) {
                    val left = offsetX + x * moduleWidth
                    val top = offsetY + y * moduleHeight
                    val right = left + moduleWidth
                    val bottom = top + moduleHeight
                    
                    when (moduleShape) {
                        ModuleShape.SQUARE -> {
                            canvas.drawRect(left, top, right, bottom, paint)
                        }
                        ModuleShape.CIRCLE -> {
                            val centerX = left + moduleWidth / 2
                            val centerY = top + moduleHeight / 2
                            val radius = minOf(moduleWidth, moduleHeight) / 2 * 0.85f
                            canvas.drawCircle(centerX, centerY, radius, paint)
                        }
                        ModuleShape.ROUNDED -> {
                            val cornerRadius = minOf(moduleWidth, moduleHeight) * 0.3f
                            canvas.drawRoundRect(
                                RectF(left, top, right, bottom),
                                cornerRadius,
                                cornerRadius,
                                paint
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Draws the frame around the QR code based on the frame style
     */
    private fun drawFrame(canvas: Canvas, size: Int, customization: QrCustomization) {
        when (customization.frameStyle) {
            FrameStyle.NONE -> { /* No frame */ }
            
            FrameStyle.SIMPLE_BORDER -> {
                val borderPaint = Paint().apply {
                    color = customization.foregroundColor
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    isAntiAlias = true
                }
                val margin = 10f
                canvas.drawRoundRect(
                    RectF(margin, margin, size - margin, size - margin),
                    16f, 16f,
                    borderPaint
                )
            }
            
            FrameStyle.SCAN_ME -> {
                // Draw rounded rectangle frame
                val borderPaint = Paint().apply {
                    color = customization.foregroundColor
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    isAntiAlias = true
                }
                val margin = 10f
                canvas.drawRoundRect(
                    RectF(margin, margin + 40, size - margin, size - margin),
                    16f, 16f,
                    borderPaint
                )
                
                // Draw "SCAN ME" text at top
                val textPaint = Paint().apply {
                    color = customization.foregroundColor
                    textSize = 28f
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                canvas.drawText("SCAN ME", size / 2f, 32f, textPaint)
            }
        }
    }
    
    /**
     * Draws the logo at the center of the QR code
     * The logo is placed in a white circle/rounded rect background to ensure visibility
     */
    private fun drawLogo(
        canvas: Canvas,
        logo: Bitmap,
        qrSize: Int,
        logoSizePercent: Float
    ) {
        val logoSize = (qrSize * logoSizePercent).toInt()
        val logoLeft = (qrSize - logoSize) / 2
        val logoTop = (qrSize - logoSize) / 2
        
        // Draw white background circle for logo
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        
        val centerX = qrSize / 2f
        val centerY = qrSize / 2f
        val bgRadius = logoSize / 2f + 8f  // Add padding
        canvas.drawCircle(centerX, centerY, bgRadius, bgPaint)
        
        // Scale and draw the logo
        val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
        
        // Create circular clip for logo
        val logoBitmap = Bitmap.createBitmap(logoSize, logoSize, Bitmap.Config.ARGB_8888)
        val logoCanvas = Canvas(logoBitmap)
        
        val clipPaint = Paint().apply {
            isAntiAlias = true
        }
        logoCanvas.drawCircle(logoSize / 2f, logoSize / 2f, logoSize / 2f, clipPaint)
        
        clipPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        logoCanvas.drawBitmap(scaledLogo, 0f, 0f, clipPaint)
        
        // Draw the clipped logo onto the main canvas
        canvas.drawBitmap(logoBitmap, logoLeft.toFloat(), logoTop.toFloat(), null)
        
        // Clean up
        if (scaledLogo != logo) {
            scaledLogo.recycle()
        }
        logoBitmap.recycle()
    }
    
    /**
     * Generates a high-quality QR code with custom styling (legacy method)
     */
    suspend fun generateStyled(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        cornerRadius: Float = 0f
    ): Bitmap? = withContext(Dispatchers.Default) {
        val customization = QrCustomization(
            foregroundColor = foregroundColor,
            backgroundColor = backgroundColor,
            moduleShape = if (cornerRadius > 0) ModuleShape.ROUNDED else ModuleShape.SQUARE
        )
        generateCustomized(content, size, customization)
    }
}
