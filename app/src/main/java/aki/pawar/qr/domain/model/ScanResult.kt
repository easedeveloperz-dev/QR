package aki.pawar.qr.domain.model

import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Represents a scanned QR code or barcode result
 */
data class ScanResult(
    val rawValue: String,
    val format: BarcodeFormat,
    val type: BarcodeContentType,
    val displayValue: String = rawValue,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Checks if the content is a URL
     */
    val isUrl: Boolean
        get() = rawValue.startsWith("http://") || rawValue.startsWith("https://")
    
    /**
     * Checks if the content is a UPI payment link
     */
    val isUpi: Boolean
        get() = rawValue.startsWith("upi://")
    
    /**
     * Checks if the content appears to be a payment QR
     */
    val isPayment: Boolean
        get() = isUpi || rawValue.contains("pay", ignoreCase = true)
    
    /**
     * Checks if the URL might be potentially unsafe
     */
    val isPotentiallyUnsafe: Boolean
        get() = isUrl && (
            rawValue.contains("bit.ly") ||
            rawValue.contains("tinyurl") ||
            rawValue.contains("goo.gl") ||
            !rawValue.startsWith("https://")
        )
    
    /**
     * Extracts UPI details if available
     */
    fun parseUpiDetails(): UpiDetails? {
        if (!isUpi) return null
        
        return try {
            val params = rawValue
                .removePrefix("upi://pay?")
                .split("&")
                .associate {
                    val (key, value) = it.split("=", limit = 2)
                    key to java.net.URLDecoder.decode(value, "UTF-8")
                }
            
            UpiDetails(
                upiId = params["pa"] ?: "",
                payeeName = params["pn"] ?: "",
                amount = params["am"] ?: "",
                currency = params["cu"] ?: "INR",
                transactionNote = params["tn"] ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * UPI payment details extracted from QR code
 */
data class UpiDetails(
    val upiId: String,
    val payeeName: String,
    val amount: String,
    val currency: String,
    val transactionNote: String
)

/**
 * Supported barcode formats
 */
enum class BarcodeFormat(val displayName: String) {
    QR_CODE("QR Code"),
    CODE_128("Code 128"),
    CODE_39("Code 39"),
    CODE_93("Code 93"),
    CODABAR("Codabar"),
    EAN_13("EAN-13"),
    EAN_8("EAN-8"),
    ITF("ITF"),
    UPC_A("UPC-A"),
    UPC_E("UPC-E"),
    PDF417("PDF417"),
    AZTEC("Aztec"),
    DATA_MATRIX("Data Matrix"),
    UNKNOWN("Unknown");
    
    companion object {
        fun fromMlKit(format: Int): BarcodeFormat {
            return when (format) {
                Barcode.FORMAT_QR_CODE -> QR_CODE
                Barcode.FORMAT_CODE_128 -> CODE_128
                Barcode.FORMAT_CODE_39 -> CODE_39
                Barcode.FORMAT_CODE_93 -> CODE_93
                Barcode.FORMAT_CODABAR -> CODABAR
                Barcode.FORMAT_EAN_13 -> EAN_13
                Barcode.FORMAT_EAN_8 -> EAN_8
                Barcode.FORMAT_ITF -> ITF
                Barcode.FORMAT_UPC_A -> UPC_A
                Barcode.FORMAT_UPC_E -> UPC_E
                Barcode.FORMAT_PDF417 -> PDF417
                Barcode.FORMAT_AZTEC -> AZTEC
                Barcode.FORMAT_DATA_MATRIX -> DATA_MATRIX
                else -> UNKNOWN
            }
        }
    }
}

/**
 * Content types detected from barcode
 */
enum class BarcodeContentType(val displayName: String) {
    URL("Website URL"),
    WIFI("Wi-Fi Network"),
    CONTACT("Contact"),
    PHONE("Phone Number"),
    SMS("SMS Message"),
    EMAIL("Email"),
    GEO("Location"),
    CALENDAR("Calendar Event"),
    UPI("UPI Payment"),
    TEXT("Plain Text"),
    UNKNOWN("Unknown");
    
    companion object {
        fun fromMlKit(type: Int): BarcodeContentType {
            return when (type) {
                Barcode.TYPE_URL -> URL
                Barcode.TYPE_WIFI -> WIFI
                Barcode.TYPE_CONTACT_INFO -> CONTACT
                Barcode.TYPE_PHONE -> PHONE
                Barcode.TYPE_SMS -> SMS
                Barcode.TYPE_EMAIL -> EMAIL
                Barcode.TYPE_GEO -> GEO
                Barcode.TYPE_CALENDAR_EVENT -> CALENDAR
                Barcode.TYPE_TEXT -> TEXT
                else -> UNKNOWN
            }
        }
        
        fun detect(rawValue: String): BarcodeContentType {
            return when {
                rawValue.startsWith("upi://") -> UPI
                rawValue.startsWith("http://") || rawValue.startsWith("https://") -> URL
                rawValue.startsWith("WIFI:") -> WIFI
                rawValue.startsWith("BEGIN:VCARD") -> CONTACT
                rawValue.startsWith("tel:") -> PHONE
                rawValue.startsWith("smsto:") || rawValue.startsWith("sms:") -> SMS
                rawValue.startsWith("mailto:") -> EMAIL
                rawValue.startsWith("geo:") -> GEO
                rawValue.startsWith("BEGIN:VEVENT") -> CALENDAR
                else -> TEXT
            }
        }
    }
}









