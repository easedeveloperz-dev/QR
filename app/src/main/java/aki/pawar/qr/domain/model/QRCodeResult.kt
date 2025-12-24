package aki.pawar.qr.domain.model

/**
 * Domain model representing a generated QR code
 */
data class QRCodeResult(
    val bitmap: android.graphics.Bitmap,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Barcode format types
 */
enum class BarcodeType(val displayName: String) {
    QR_CODE("QR Code"),
    BARCODE_128("Code 128"),
    BARCODE_39("Code 39"),
    BARCODE_93("Code 93"),
    CODABAR("Codabar"),
    EAN_13("EAN-13"),
    EAN_8("EAN-8"),
    ITF("ITF"),
    UPC_A("UPC-A"),
    UPC_E("UPC-E"),
    PDF_417("PDF417"),
    AZTEC("Aztec"),
    DATA_MATRIX("Data Matrix"),
    UNKNOWN("Barcode")
}

/**
 * Domain model representing a scanned QR code or barcode result
 */
data class ScanResult(
    val content: String,
    val barcodeType: BarcodeType = BarcodeType.UNKNOWN,
    val isUrl: Boolean = content.startsWith("http://") || content.startsWith("https://"),
    val timestamp: Long = System.currentTimeMillis()
)

