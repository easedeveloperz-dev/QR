package aki.pawar.qr.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for handling various intents from scanned QR codes
 */
@Singleton
class IntentHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Opens a URL in browser
     */
    fun openUrl(url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showToast("Cannot open URL")
            false
        }
    }
    
    /**
     * Initiates a phone call
     */
    fun dialPhone(phoneNumber: String): Boolean {
        return try {
            val cleanNumber = phoneNumber.filter { it.isDigit() || it == '+' }
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showToast("Cannot dial number")
            false
        }
    }
    
    /**
     * Opens SMS app with pre-filled message
     */
    fun sendSms(phoneNumber: String, message: String = ""): Boolean {
        return try {
            val cleanNumber = phoneNumber.filter { it.isDigit() || it == '+' }
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$cleanNumber")
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showToast("Cannot open SMS")
            false
        }
    }
    
    /**
     * Opens email app with pre-filled fields
     */
    fun sendEmail(
        emailAddress: String,
        subject: String = "",
        body: String = ""
    ): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                if (subject.isNotBlank()) putExtra(Intent.EXTRA_SUBJECT, subject)
                if (body.isNotBlank()) putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showToast("Cannot open email")
            false
        }
    }
    
    /**
     * Opens location in maps app
     */
    fun openLocation(latitude: Double, longitude: Double, label: String = ""): Boolean {
        return try {
            val uri = if (label.isNotBlank()) {
                Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
            } else {
                Uri.parse("geo:$latitude,$longitude")
            }
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showToast("Cannot open maps")
            false
        }
    }
    
    /**
     * Opens UPI payment app
     */
    fun openUpiPayment(upiString: String): Boolean {
        return try {
            val uri = Uri.parse(upiString)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Try to start activity directly with a chooser
            val chooser = Intent.createChooser(intent, "Pay with").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            // Check if any app can handle this intent
            if (chooser.resolveActivity(context.packageManager) != null || 
                intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
                true
            } else {
                // Fallback: try to start directly without checking
                try {
                    context.startActivity(intent)
                    true
                } catch (e: Exception) {
                    showToast("No UPI app installed")
                    false
                }
            }
        } catch (e: Exception) {
            showToast("Cannot open UPI payment: ${e.message}")
            false
        }
    }
    
    /**
     * Opens Wi-Fi settings
     */
    fun openWifiSettings(): Boolean {
        return try {
            val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showToast("Cannot open Wi-Fi settings")
            false
        }
    }
    
    /**
     * Adds contact to contacts app
     */
    fun addContact(
        name: String,
        phone: String = "",
        email: String = "",
        organization: String = ""
    ): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                type = ContactsContract.Contacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.NAME, name)
                if (phone.isNotBlank()) {
                    putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                }
                if (email.isNotBlank()) {
                    putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                }
                if (organization.isNotBlank()) {
                    putExtra(ContactsContract.Intents.Insert.COMPANY, organization)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            showToast("Cannot add contact")
            false
        }
    }
    
    /**
     * Opens Play Store app page
     */
    fun openPlayStore(packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to web browser
            openUrl("https://play.google.com/store/apps/details?id=$packageName")
        }
    }
    
    /**
     * Shares text content
     */
    fun shareText(text: String, title: String = "Share"): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            true
        } catch (e: Exception) {
            showToast("Cannot share")
            false
        }
    }
    
    /**
     * Copies text to clipboard
     */
    fun copyToClipboard(text: String, label: String = "QR Content"): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) 
                as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            showToast("Copied to clipboard")
            true
        } catch (e: Exception) {
            showToast("Failed to copy")
            false
        }
    }
    
    /**
     * Handles scanned content based on its type
     */
    fun handleScanResult(rawValue: String): Boolean {
        return when {
            rawValue.startsWith("http://") || rawValue.startsWith("https://") -> openUrl(rawValue)
            rawValue.startsWith("tel:") -> dialPhone(rawValue.removePrefix("tel:"))
            rawValue.startsWith("smsto:") -> {
                val parts = rawValue.removePrefix("smsto:").split(":", limit = 2)
                sendSms(parts[0], parts.getOrElse(1) { "" })
            }
            rawValue.startsWith("mailto:") -> {
                // Parse mailto URI manually since it's not hierarchical
                val mailtoContent = rawValue.removePrefix("mailto:")
                val emailPart = mailtoContent.substringBefore("?")
                val queryPart = if (mailtoContent.contains("?")) {
                    mailtoContent.substringAfter("?")
                } else ""
                
                // Parse query parameters manually
                val params = queryPart.split("&")
                    .filter { it.contains("=") }
                    .associate { 
                        val (key, value) = it.split("=", limit = 2)
                        key.lowercase() to Uri.decode(value)
                    }
                
                sendEmail(
                    emailPart,
                    params["subject"] ?: "",
                    params["body"] ?: ""
                )
            }
            rawValue.startsWith("geo:") -> {
                val coords = rawValue.removePrefix("geo:").substringBefore("?").split(",")
                if (coords.size >= 2) {
                    openLocation(coords[0].toDoubleOrNull() ?: 0.0, coords[1].toDoubleOrNull() ?: 0.0)
                } else false
            }
            rawValue.startsWith("upi://") -> openUpiPayment(rawValue)
            else -> {
                copyToClipboard(rawValue)
                true
            }
        }
    }
    
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

