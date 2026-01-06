package aki.pawar.qr.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for bitmap operations like saving and sharing
 */
@Singleton
class BitmapUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Saves bitmap to device gallery
     */
    suspend fun saveToGallery(bitmap: Bitmap, filename: String = "QR_${System.currentTimeMillis()}"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val name = "$filename.png"
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QR Codes")
                    }
                    
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    
                    uri?.let {
                        context.contentResolver.openOutputStream(it)?.use { stream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val qrDir = File(picturesDir, "QR Codes")
                    qrDir.mkdirs()
                    
                    val file = File(qrDir, name)
                    FileOutputStream(file).use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                    
                    // Notify media scanner
                    MediaStore.Images.Media.insertImage(
                        context.contentResolver,
                        file.absolutePath,
                        name,
                        "QR Code"
                    )
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    /**
     * Gets a shareable URI for the bitmap
     */
    suspend fun getShareableUri(bitmap: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "qrcode_${System.currentTimeMillis()}.png")
                
                FileOutputStream(file).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    /**
     * Shares bitmap via system share sheet
     */
    suspend fun shareBitmap(bitmap: Bitmap, title: String = "Share QR Code"): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val uri = getShareableUri(bitmap) ?: return@withContext false
                
                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                
                context.startActivity(
                    android.content.Intent.createChooser(shareIntent, title).apply {
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}



