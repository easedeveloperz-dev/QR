package aki.pawar.qr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            // Observe review trigger state
            val shouldShowReview by inAppReviewManager.shouldShowReview.collectAsState()
            
            // Launch review flow when triggered
            LaunchedEffect(shouldShowReview) {
                if (shouldShowReview) {
                    inAppReviewManager.launchReviewFlow(this@MainActivity)
                }
            }
            
            QrAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QrNavGraph()
                }
            }
        }
    }
}
