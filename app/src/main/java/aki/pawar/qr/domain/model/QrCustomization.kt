package aki.pawar.qr.domain.model

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Represents the customization options for QR code generation
 */
data class QrCustomization(
    val foregroundColor: Int = Color.BLACK,
    val backgroundColor: Int = Color.WHITE,
    val moduleShape: ModuleShape = ModuleShape.SQUARE,
    val logoBitmap: Bitmap? = null,
    val logoSizePercent: Float = 0.2f,  // 20% default, range 0.1 to 0.3
    val frameStyle: FrameStyle = FrameStyle.NONE
) {
    /**
     * Checks if the contrast between foreground and background is sufficient for scanning
     * Returns true if contrast ratio is >= 40%
     */
    fun hasAdequateContrast(): Boolean {
        val fgLuminance = calculateLuminance(foregroundColor)
        val bgLuminance = calculateLuminance(backgroundColor)
        val contrastRatio = kotlin.math.abs(fgLuminance - bgLuminance)
        return contrastRatio >= 0.4f
    }
    
    private fun calculateLuminance(color: Int): Float {
        val r = Color.red(color) / 255f
        val g = Color.green(color) / 255f
        val b = Color.blue(color) / 255f
        return 0.299f * r + 0.587f * g + 0.114f * b
    }
    
    companion object {
        const val MIN_LOGO_SIZE_PERCENT = 0.1f
        const val MAX_LOGO_SIZE_PERCENT = 0.3f
        const val DEFAULT_LOGO_SIZE_PERCENT = 0.2f
    }
}

/**
 * Shape of individual QR code modules (the black/colored squares)
 */
enum class ModuleShape(val displayName: String) {
    SQUARE("Square"),
    CIRCLE("Circle"),
    ROUNDED("Rounded")
}

/**
 * Frame style around the QR code
 */
enum class FrameStyle(val displayName: String) {
    NONE("None"),
    SIMPLE_BORDER("Simple Border"),
    SCAN_ME("Scan Me")
}

/**
 * Preset colors for easy selection
 */
object QrPresetColors {
    val foregroundColors = listOf(
        Color.BLACK,
        Color.parseColor("#1a1a2e"),  // Dark blue
        Color.parseColor("#16213e"),  // Navy
        Color.parseColor("#0f3460"),  // Deep blue
        Color.parseColor("#533483"),  // Purple
        Color.parseColor("#e94560"),  // Red/Pink
        Color.parseColor("#00695c"),  // Teal
        Color.parseColor("#1b5e20"),  // Dark green
        Color.parseColor("#bf360c"),  // Deep orange
        Color.parseColor("#4a148c")   // Deep purple
    )
    
    val backgroundColors = listOf(
        Color.WHITE,
        Color.parseColor("#f5f5f5"),  // Light gray
        Color.parseColor("#fff8e1"),  // Light amber
        Color.parseColor("#e8f5e9"),  // Light green
        Color.parseColor("#e3f2fd"),  // Light blue
        Color.parseColor("#fce4ec"),  // Light pink
        Color.parseColor("#f3e5f5"),  // Light purple
        Color.parseColor("#e0f7fa"),  // Light cyan
        Color.parseColor("#fff3e0"),  // Light orange
        Color.parseColor("#eceff1")   // Blue gray
    )
}
