package aki.pawar.qr

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for QR Code Pro
 * Annotated with @HiltAndroidApp for Hilt dependency injection
 */
@HiltAndroidApp
class QrApp : Application(){
    override fun onCreate() {
        super.onCreate()

    }
}







