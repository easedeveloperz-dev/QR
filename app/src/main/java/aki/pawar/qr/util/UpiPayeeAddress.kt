package aki.pawar.qr.util

/**
 * Builds a valid UPI payee address (pa) from UPI ID or mobile number input.
 */
object UpiPayeeAddress {

    enum class InputMode {
        UPI_ID,
        MOBILE_NUMBER,
    }

    /** Common PSP suffixes when paying via registered mobile number. */
    enum class MobileSuffix(val label: String, val handle: String) {
        UPI("Default (@upi)", "upi"),
        YBL("PhonePe (@ybl)", "ybl"),
        PAYTM("Paytm (@paytm)", "paytm"),
        OKAXIS("Google Pay (@okaxis)", "okaxis"),
        AXL("Axis (@axl)", "axl"),
    }

    /**
     * Resolve the payee address used in upi://pay?pa=...
     */
    fun resolve(
        mode: InputMode,
        upiId: String,
        mobileNumber: String,
        mobileSuffix: MobileSuffix = MobileSuffix.UPI,
    ): String? {
        return when (mode) {
            InputMode.UPI_ID -> upiId.trim().takeIf { isValidUpiId(it) }
            InputMode.MOBILE_NUMBER -> normalizeMobileToVpa(mobileNumber, mobileSuffix.handle)
        }
    }

    fun isValidUpiId(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.contains("@") && trimmed.length >= 5
    }

    fun isValidMobileNumber(value: String): Boolean {
        return normalizeIndianMobile(value) != null
    }

    /** Extract 10-digit Indian mobile from user input. */
    fun normalizeIndianMobile(input: String): String? {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length == 10 -> digits
            digits.length == 12 && digits.startsWith("91") -> digits.drop(2)
            else -> null
        }
    }

    fun normalizeMobileToVpa(mobileNumber: String, suffix: String): String? {
        val mobile = normalizeIndianMobile(mobileNumber) ?: return null
        val cleanSuffix = suffix.removePrefix("@")
        return "$mobile@$cleanSuffix"
    }

    /** True when pa looks like mobile@psp rather than name@psp. */
    fun isMobileVpa(payeeAddress: String): Boolean {
        val localPart = payeeAddress.substringBefore("@")
        return localPart.length == 10 && localPart.all { it.isDigit() }
    }

    fun formatMobileForDisplay(payeeAddress: String): String? {
        if (!isMobileVpa(payeeAddress)) return null
        val mobile = payeeAddress.substringBefore("@")
        return "+91 ${mobile.substring(0, 5)} ${mobile.substring(5)}"
    }
}
