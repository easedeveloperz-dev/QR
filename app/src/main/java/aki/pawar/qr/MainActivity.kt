package aki.pawar.qr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import aki.pawar.qr.navigation.QrNavGraph
import aki.pawar.qr.ui.theme.QrAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity - Entry point of the application
 * Annotated with @AndroidEntryPoint for Hilt dependency injection
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
