package aki.pawar.qr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import aki.pawar.qr.navigation.QrNavGraph
import aki.pawar.qr.ui.theme.QrAppTheme
import aki.pawar.qr.util.InAppReviewManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity - Entry point of the application
 * Annotated with @AndroidEntryPoint for Hilt dependency injection
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var inAppReviewManager: InAppReviewManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Observe choice dialog trigger state
            val shouldShowChoiceDialog by inAppReviewManager.shouldShowChoiceDialog.collectAsState()
            
            QrAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QrNavGraph()
                }
                
                // Show rating choice dialog when triggered
                if (shouldShowChoiceDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            // User dismissed without choosing - treat as NO
                            inAppReviewManager.onUserTappedNo()
                        },
                        title = {
                            Text(
                                text = "Enjoying the App?",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Text(
                                text = "Would you like to rate us on the Play Store? Your feedback helps us improve!"
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    // User tapped YES - open Google Play review
                                    inAppReviewManager.onUserTappedYes(this@MainActivity)
                                }
                            ) {
                                Text(
                                    text = "Yes, Rate Now",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    // User tapped NO - do nothing
                                    inAppReviewManager.onUserTappedNo()
                                }
                            ) {
                                Text(
                                    text = "No, Thanks",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
