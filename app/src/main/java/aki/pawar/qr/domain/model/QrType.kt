package aki.pawar.qr.domain.model

/**
 * Sealed class representing all supported QR code types
 * Each type contains the data needed to generate a properly formatted QR code
 */
sealed class QrType {

    abstract val displayName: String
    abstract val iconName: String

    /**
     * Website URL QR Code
     */
    data class Url(val url: String) : QrType() {
        override val displayName = "Website URL"
        override val iconName = "link"

        fun toQrString(): String = url.let {
            if (!it.startsWith("http://") && !it.startsWith("https://")) {
                "https://$it"
            } else it
        }
    }

    /**
     * Wi-Fi Network QR Code
     * Format: WIFI:T:{security};S:{ssid};P:{password};H:{hidden};;
     */
    data class WiFi(
        val ssid: String,
        val password: String,
        val securityType: WifiSecurity = WifiSecurity.WPA,
        val isHidden: Boolean = false
    ) : QrType() {
        override val displayName = "Wi-Fi Network"
        override val iconName = "wifi"

        fun toQrString(): String {
            val escapedSsid = escapeSpecialChars(ssid)
            val escapedPassword = escapeSpecialChars(password)
            return "WIFI:T:${securityType.name};S:$escapedSsid;P:$escapedPassword;H:${isHidden};;"
        }

        private fun escapeSpecialChars(input: String): String {
            return input
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace(":", "\\:")
                .replace("\"", "\\\"")
        }
    }

    /**
     * Contact vCard QR Code
     */
    data class Contact(
        val firstName: String,
        val lastName: String = "",
        val phone: String = "",
        val email: String = "",
        val organization: String = "",
        val title: String = "",
        val address: String = "",
        val website: String = "",
        val note: String = ""
    ) : QrType() {
        override val displayName = "Contact"
        override val iconName = "person"

        fun toQrString(): String {
            return buildString {
                appendLine("BEGIN:VCARD")
                appendLine("VERSION:3.0")
                appendLine("N:$lastName;$firstName;;;")
                appendLine("FN:$firstName $lastName".trim())
                if (phone.isNotBlank()) appendLine("TEL:$phone")
                if (email.isNotBlank()) appendLine("EMAIL:$email")
                if (organization.isNotBlank()) appendLine("ORG:$organization")
                if (title.isNotBlank()) appendLine("TITLE:$title")
                if (address.isNotBlank()) appendLine("ADR:;;$address;;;;")
                if (website.isNotBlank()) appendLine("URL:$website")
                if (note.isNotBlank()) appendLine("NOTE:$note")
                appendLine("END:VCARD")
            }
        }
    }

    /**
     * Phone Call QR Code
     */
    data class Phone(val phoneNumber: String) : QrType() {
        override val displayName = "Phone Call"
        override val iconName = "phone"

        fun toQrString(): String = "tel:${phoneNumber.filter { it.isDigit() || it == '+' }}"
    }

    /**
     * SMS QR Code
     */
    data class Sms(
        val phoneNumber: String,
        val message: String = ""
    ) : QrType() {
        override val displayName = "SMS Message"
        override val iconName = "sms"

        fun toQrString(): String {
            val number = phoneNumber.filter { it.isDigit() || it == '+' }
            return if (message.isNotBlank()) {
                "smsto:$number:$message"
            } else {
                "smsto:$number"
            }
        }
    }

    /**
     * Email QR Code
     */
    data class Email(
        val emailAddress: String,
        val subject: String = "",
        val body: String = ""
    ) : QrType() {
        override val displayName = "Email"
        override val iconName = "email"

        fun toQrString(): String {
            return buildString {
                append("mailto:$emailAddress")
                val params = mutableListOf<String>()
                if (subject.isNotBlank()) params.add("subject=${subject.encodeUrl()}")
                if (body.isNotBlank()) params.add("body=${body.encodeUrl()}")
                if (params.isNotEmpty()) {
                    append("?${params.joinToString("&")}")
                }
            }
        }

        private fun String.encodeUrl(): String {
            return java.net.URLEncoder.encode(this, "UTF-8")
        }
    }

    /**
     * Location/Maps QR Code
     */
    data class Location(
        val latitude: Double,
        val longitude: Double,
        val label: String = ""
    ) : QrType() {
        override val displayName = "Location"
        override val iconName = "location_on"

        fun toQrString(): String {
            return if (label.isNotBlank()) {
                "geo:$latitude,$longitude?q=$latitude,$longitude(${label.encodeUrl()})"
            } else {
                "geo:$latitude,$longitude"
            }
        }

        private fun String.encodeUrl(): String {
            return java.net.URLEncoder.encode(this, "UTF-8")
        }
    }

    /**
     * Social Media Profile QR Code
     */
    data class SocialMedia(
        val platform: SocialPlatform,
        val username: String
    ) : QrType() {
        override val displayName = "Social Media"
        override val iconName = "share"

        fun toQrString(): String = platform.getProfileUrl(username)
    }

    /**
     * App Download (Play Store) QR Code
     */
    data class AppDownload(val packageName: String) : QrType() {
        override val displayName = "App Download"
        override val iconName = "download"

        fun toQrString(): String = "https://play.google.com/store/apps/details?id=$packageName"
    }

    /**
     * UPI Payment QR Code
     * Standard UPI format: upi://pay?pa={upiId}&pn={name}&am={amount}&cu=INR&tn={note}
     */
    data class Upi(
        val upiId: String,
        val payeeName: String,
        val amount: String = "",
        val transactionNote: String = "",
        val merchantCode: String = ""
    ) : QrType() {
        override val displayName = "UPI Payment"
        override val iconName = "payment"

        fun toQrString(): String {
            return buildString {
                append("upi://pay?pa=${upiId.encodeUrl()}")
                append("&pn=${payeeName.encodeUrl()}")
                if (amount.isNotBlank()) append("&am=$amount")
                append("&cu=INR")
                if (transactionNote.isNotBlank()) append("&tn=${transactionNote.encodeUrl()}")
                if (merchantCode.isNotBlank()) append("&mc=$merchantCode")
            }
        }

        private fun String.encodeUrl(): String {
            return java.net.URLEncoder.encode(this, "UTF-8")
        }
    }

    /**
     * Plain Text QR Code
     */
    data class Text(val content: String) : QrType() {
        override val displayName = "Plain Text"
        override val iconName = "text_fields"

        fun toQrString(): String = content
    }

    companion object {
        /**
         * Returns all available QR types for selection
         */
        fun getAllTypes(): List<QrTypeOption> = listOf(
            QrTypeOption.URL,
//            QrTypeOption.WIFI,
//            QrTypeOption.CONTACT,
//            QrTypeOption.PHONE,
            QrTypeOption.SMS,
            QrTypeOption.EMAIL,
            QrTypeOption.LOCATION,
            QrTypeOption.SOCIAL_MEDIA,
            QrTypeOption.APP_DOWNLOAD,
            QrTypeOption.UPI,
            QrTypeOption.TEXT
        )
    }
}

/**
 * Wi-Fi security types
 */
enum class WifiSecurity(val displayName: String) {
    WPA("WPA/WPA2"),
    WEP("WEP"),
    NONE("None")
}

/**
 * Supported social media platforms
 */
enum class SocialPlatform(val displayName: String) {
    FACEBOOK("Facebook") {
        override fun getProfileUrl(username: String) = "https://facebook.com/$username"
    },
    INSTAGRAM("Instagram") {
        override fun getProfileUrl(username: String) = "https://instagram.com/$username"
    },
    TWITTER("Twitter/X") {
        override fun getProfileUrl(username: String) = "https://x.com/$username"
    },
    LINKEDIN("LinkedIn") {
        override fun getProfileUrl(username: String) = "https://linkedin.com/in/$username"
    },
    YOUTUBE("YouTube") {
        override fun getProfileUrl(username: String) = "https://youtube.com/@$username"
    },
    TIKTOK("TikTok") {
        override fun getProfileUrl(username: String) = "https://tiktok.com/@$username"
    },
    GITHUB("GitHub") {
        override fun getProfileUrl(username: String) = "https://github.com/$username"
    },
    TELEGRAM("Telegram") {
        override fun getProfileUrl(username: String) = "https://t.me/$username"
    },
    WHATSAPP("WhatsApp") {
        override fun getProfileUrl(username: String) = "https://wa.me/$username"
    };

    abstract fun getProfileUrl(username: String): String
}

/**
 * QR Type selection options for UI
 */
enum class QrTypeOption(
    val displayName: String,
    val description: String,
    val iconName: String
) {
    TEXT("Plain Text", "Any text content", "text_fields"),
    EMAIL("Email", "Pre-filled email composition", "email"),
    URL("Website URL", "Create QR for any website link", "link"),
//    WIFI("Wi-Fi", "Share Wi-Fi network credentials", "wifi"),
//    CONTACT("Contact", "Create digital business card (vCard)", "person"),
//    PHONE("Phone Call", "Quick dial phone number", "phone"),
    SMS("SMS Message", "Pre-filled text message", "sms"),
    LOCATION("Location", "Share GPS coordinates", "location_on"),
    SOCIAL_MEDIA("Social Media", "Share social profile link", "share"),
    APP_DOWNLOAD("App Download", "Play Store app link", "download"),
    UPI("UPI Payment", "Generate payment QR code", "payment"),
}









