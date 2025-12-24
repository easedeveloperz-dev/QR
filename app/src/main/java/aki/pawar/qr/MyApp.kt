package aki.pawar.qr

import android.app.Application
import android.util.Log
import aki.pawar.qr.di.AppContainer

class MyApp : Application() {
    
    // Dependency injection container - accessible throughout the app
    lateinit var container: AppContainer
        private set
    
    // Initialize the application and configure dependencies
    override fun onCreate() {
        super.onCreate()
        
        // Initialize DI container
        container = AppContainer(applicationContext)
        
        Log.i(TAG, "QR Code Pro App initialized")
    }
    
    companion object {
        private const val TAG = "MyApp"
    }
}

