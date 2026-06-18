package aki.pawar.qr.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Google Play In-App Review
 * Shows a choice dialog first, then launches Play review only if user taps YES
 * 
 * Conditions to show:
 * 1. User has performed at least 3 successful actions (scans + generates)
 * 2. At least 2 days have passed since first app open
 * 3. Review prompt has not been shown before
 */
@Singleton
class InAppReviewManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Flow to trigger the choice dialog in UI layer
    private val _shouldShowChoiceDialog = MutableStateFlow(false)
    val shouldShowChoiceDialog: StateFlow<Boolean> = _shouldShowChoiceDialog.asStateFlow()
    
    init {
        // Record first open time if not already set
        recordFirstOpenIfNeeded()
    }
    
    /**
     * Record the first time app was opened
     */
    private fun recordFirstOpenIfNeeded() {
        if (prefs.getLong(KEY_FIRST_OPEN_TIME, 0L) == 0L) {
            prefs.edit().putLong(KEY_FIRST_OPEN_TIME, System.currentTimeMillis()).apply()
        }
    }
    
    /**
     * Get the number of days since first app open
     */
    private fun getDaysSinceFirstOpen(): Long {
        val firstOpenTime = prefs.getLong(KEY_FIRST_OPEN_TIME, System.currentTimeMillis())
        val timeDiff = System.currentTimeMillis() - firstOpenTime
        return TimeUnit.MILLISECONDS.toDays(timeDiff)
    }
    
    /**
     * Check if all conditions are met to show the review prompt
     */
    private fun shouldTriggerReview(): Boolean {
        val totalValueActions = getActionCount()
        val daysSinceFirstOpen = getDaysSinceFirstOpen()
        val reviewPromptShown = hasShownReview()
        
        return (
            totalValueActions >= ACTION_THRESHOLD &&
            daysSinceFirstOpen >= DAYS_THRESHOLD &&
            !reviewPromptShown
        )
    }
    
    /**
     * Record a successful action (scan or generate)
     * Shows choice dialog when all conditions are met
     */
    fun recordSuccessfulAction() {
        // Don't count if already shown
        if (hasShownReview()) return
        
        val currentCount = getActionCount()
        val newCount = currentCount + 1
        saveActionCount(newCount)
        
        // Check if all conditions are met to show choice dialog
        if (shouldTriggerReview()) {
            _shouldShowChoiceDialog.value = true
        }
    }
    
    /**
     * Called when user taps YES on the choice dialog
     * Opens Google Play in-app review
     */
    fun onUserTappedYes(activity: Activity) {
        _shouldShowChoiceDialog.value = false
        launchPlayReview(activity)
    }
    
    /**
     * Called when user taps NO on the choice dialog
     * Does nothing, just dismisses the dialog
     */
    fun onUserTappedNo() {
        _shouldShowChoiceDialog.value = false
        // Mark as shown so we don't ask again
        markReviewShown()
    }
    
    /**
     * Launch the Google Play In-App Review flow
     * Called internally when user taps YES
     */
    private fun launchPlayReview(activity: Activity) {
        val reviewManager = ReviewManagerFactory.create(context)
        val requestFlow = reviewManager.requestReviewFlow()
        
        requestFlow.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                
                flow.addOnCompleteListener {
                    // Mark as shown regardless of whether user reviewed
                    // Google doesn't tell us if they actually reviewed
                    markReviewShown()
                }
            }
        }
    }
    
    /**
     * Reset the choice dialog trigger
     */
    fun resetChoiceDialog() {
        _shouldShowChoiceDialog.value = false
    }
    
    /**
     * Get the current action count
     */
    fun getActionCount(): Int {
        return prefs.getInt(KEY_ACTION_COUNT, 0)
    }
    
    /**
     * Check if review has already been shown
     */
    fun hasShownReview(): Boolean {
        return prefs.getBoolean(KEY_REVIEW_SHOWN, false)
    }
    
    private fun saveActionCount(count: Int) {
        prefs.edit().putInt(KEY_ACTION_COUNT, count).apply()
    }
    
    private fun markReviewShown() {
        prefs.edit().putBoolean(KEY_REVIEW_SHOWN, true).apply()
    }
    
    companion object {
        private const val PREFS_NAME = "in_app_review_prefs"
        private const val KEY_ACTION_COUNT = "action_count"
        private const val KEY_REVIEW_SHOWN = "review_shown"
        private const val KEY_FIRST_OPEN_TIME = "first_open_time"
        
        // Show review after 3 successful actions
        const val ACTION_THRESHOLD = 3
        
        // Show review after 2 days since first open
        const val DAYS_THRESHOLD = 2L
    }
}




