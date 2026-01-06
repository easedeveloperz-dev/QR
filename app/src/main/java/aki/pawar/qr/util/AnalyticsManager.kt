package aki.pawar.qr.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analytics Manager for tracking app events with Firebase Analytics
 * Provides easy-to-use methods for logging various QR app events
 */
@Singleton
class AnalyticsManager @Inject constructor() {
    
    private val firebaseAnalytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }
    
    // ==========================================
    // SCREEN TRACKING
    // ==========================================
    
    /**
     * Log screen view event
     */
    fun logScreenView(screenName: String, screenClass: String? = null) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        })
    }
    
    // ==========================================
    // QR SCANNER EVENTS
    // ==========================================
    
    /**
     * Log when QR/barcode scanning starts
     */
    fun logScanStart() {
        firebaseAnalytics.logEvent(EVENT_SCAN_START, null)
    }
    
    /**
     * Log successful QR/barcode scan
     */
    fun logScanSuccess(format: String, contentType: String) {
        firebaseAnalytics.logEvent(EVENT_SCAN_SUCCESS, Bundle().apply {
            putString(PARAM_BARCODE_FORMAT, format)
            putString(PARAM_CONTENT_TYPE, contentType)
        })
    }
    
    /**
     * Log scan from gallery image
     */
    fun logGalleryScan(success: Boolean) {
        firebaseAnalytics.logEvent(EVENT_GALLERY_SCAN, Bundle().apply {
            putBoolean(PARAM_SUCCESS, success)
        })
    }
    
    /**
     * Log scan failure/error
     */
    fun logScanError(errorMessage: String) {
        firebaseAnalytics.logEvent(EVENT_SCAN_ERROR, Bundle().apply {
            putString(PARAM_ERROR_MESSAGE, errorMessage)
        })
    }
    
    /**
     * Log flash toggle action
     */
    fun logFlashToggle(isOn: Boolean) {
        firebaseAnalytics.logEvent(EVENT_FLASH_TOGGLE, Bundle().apply {
            putBoolean(PARAM_FLASH_ON, isOn)
        })
    }
    
    // ==========================================
    // QR GENERATOR EVENTS
    // ==========================================
    
    /**
     * Log QR code type selection
     */
    fun logQrTypeSelected(qrType: String) {
        firebaseAnalytics.logEvent(EVENT_QR_TYPE_SELECTED, Bundle().apply {
            putString(PARAM_QR_TYPE, qrType)
        })
    }
    
    /**
     * Log QR code generation
     */
    fun logQrGenerated(qrType: String) {
        firebaseAnalytics.logEvent(EVENT_QR_GENERATED, Bundle().apply {
            putString(PARAM_QR_TYPE, qrType)
        })
    }
    
    /**
     * Log QR code save to gallery
     */
    fun logQrSaved(qrType: String) {
        firebaseAnalytics.logEvent(EVENT_QR_SAVED, Bundle().apply {
            putString(PARAM_QR_TYPE, qrType)
        })
    }
    
    /**
     * Log QR code share
     */
    fun logQrShared(qrType: String) {
        firebaseAnalytics.logEvent(EVENT_QR_SHARED, Bundle().apply {
            putString(PARAM_QR_TYPE, qrType)
        })
    }
    
    // ==========================================
    // SCAN RESULT ACTIONS
    // ==========================================
    
    /**
     * Log action taken on scan result
     */
    fun logResultAction(action: String, contentType: String) {
        firebaseAnalytics.logEvent(EVENT_RESULT_ACTION, Bundle().apply {
            putString(PARAM_ACTION, action)
            putString(PARAM_CONTENT_TYPE, contentType)
        })
    }
    
    /**
     * Log URL opened from scan result
     */
    fun logUrlOpened(isSecure: Boolean) {
        firebaseAnalytics.logEvent(EVENT_URL_OPENED, Bundle().apply {
            putBoolean(PARAM_IS_SECURE, isSecure)
        })
    }
    
    /**
     * Log UPI payment initiated
     */
    fun logUpiPaymentInitiated() {
        firebaseAnalytics.logEvent(EVENT_UPI_PAYMENT_INITIATED, null)
    }
    
    /**
     * Log content copied to clipboard
     */
    fun logContentCopied(contentType: String) {
        firebaseAnalytics.logEvent(EVENT_CONTENT_COPIED, Bundle().apply {
            putString(PARAM_CONTENT_TYPE, contentType)
        })
    }
    
    /**
     * Log content shared
     */
    fun logContentShared(contentType: String) {
        firebaseAnalytics.logEvent(EVENT_CONTENT_SHARED, Bundle().apply {
            putString(PARAM_CONTENT_TYPE, contentType)
        })
    }
    
    // ==========================================
    // HISTORY EVENTS
    // ==========================================
    
    /**
     * Log history screen viewed
     */
    fun logHistoryViewed() {
        firebaseAnalytics.logEvent(EVENT_HISTORY_VIEWED, null)
    }
    
    /**
     * Log history item clicked
     */
    fun logHistoryItemClicked(itemType: String) {
        firebaseAnalytics.logEvent(EVENT_HISTORY_ITEM_CLICKED, Bundle().apply {
            putString(PARAM_ITEM_TYPE, itemType)
        })
    }
    
    /**
     * Log history item deleted
     */
    fun logHistoryItemDeleted(itemType: String) {
        firebaseAnalytics.logEvent(EVENT_HISTORY_ITEM_DELETED, Bundle().apply {
            putString(PARAM_ITEM_TYPE, itemType)
        })
    }
    
    // ==========================================
    // APP EVENTS
    // ==========================================
    
    /**
     * Log app opened
     */
    fun logAppOpen() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }
    
    /**
     * Log camera permission granted/denied
     */
    fun logCameraPermission(granted: Boolean) {
        firebaseAnalytics.logEvent(EVENT_CAMERA_PERMISSION, Bundle().apply {
            putBoolean(PARAM_GRANTED, granted)
        })
    }
    
    /**
     * Set user property for analytics segmentation
     */
    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }
    
    companion object {
        // Event names
        private const val EVENT_SCAN_START = "scan_start"
        private const val EVENT_SCAN_SUCCESS = "scan_success"
        private const val EVENT_SCAN_ERROR = "scan_error"
        private const val EVENT_GALLERY_SCAN = "gallery_scan"
        private const val EVENT_FLASH_TOGGLE = "flash_toggle"
        private const val EVENT_QR_TYPE_SELECTED = "qr_type_selected"
        private const val EVENT_QR_GENERATED = "qr_generated"
        private const val EVENT_QR_SAVED = "qr_saved"
        private const val EVENT_QR_SHARED = "qr_shared"
        private const val EVENT_RESULT_ACTION = "result_action"
        private const val EVENT_URL_OPENED = "url_opened"
        private const val EVENT_UPI_PAYMENT_INITIATED = "upi_payment_initiated"
        private const val EVENT_CONTENT_COPIED = "content_copied"
        private const val EVENT_CONTENT_SHARED = "content_shared"
        private const val EVENT_HISTORY_VIEWED = "history_viewed"
        private const val EVENT_HISTORY_ITEM_CLICKED = "history_item_clicked"
        private const val EVENT_HISTORY_ITEM_DELETED = "history_item_deleted"
        private const val EVENT_CAMERA_PERMISSION = "camera_permission"
        
        // Parameter names
        private const val PARAM_BARCODE_FORMAT = "barcode_format"
        private const val PARAM_CONTENT_TYPE = "content_type"
        private const val PARAM_QR_TYPE = "qr_type"
        private const val PARAM_ACTION = "action"
        private const val PARAM_SUCCESS = "success"
        private const val PARAM_ERROR_MESSAGE = "error_message"
        private const val PARAM_FLASH_ON = "flash_on"
        private const val PARAM_IS_SECURE = "is_secure"
        private const val PARAM_ITEM_TYPE = "item_type"
        private const val PARAM_GRANTED = "granted"
    }
}


