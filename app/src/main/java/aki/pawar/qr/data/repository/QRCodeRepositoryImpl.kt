package aki.pawar.qr.data.repository

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import aki.pawar.qr.domain.repository.QRCodeRepository
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

/**
 * Implementation of QRCodeRepository
 * Handles all QR code related data operations
 */
class QRCodeRepositoryImpl(
    private val context: Context
) : QRCodeRepository {
    
    override suspend fun generateQRCode(text: String, size: Int): Bitmap? {
        return withContext(Dispatchers.Default) {
            try {
                val hints = hashMapOf<EncodeHintType, Any>()
                hints[EncodeHintType.MARGIN] = 2
                hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
                
                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
                
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(
                            x, y,
                            if (bitMatrix[x, y]) android.graphics.Color.BLACK
                            else android.graphics.Color.WHITE
                        )
                    }
                }
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    
    override suspend fun saveToGallery(bitmap: Bitmap, filename: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val name = filename ?: "QRCode_${System.currentTimeMillis()}.png"
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRCodes")
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
                    val qrDir = File(picturesDir, "QRCodes")
                    qrDir.mkdirs()
                    
                    val file = File(qrDir, name)
                    FileOutputStream(file).use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    override suspend fun shareQRCode(bitmap: Bitmap): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "qrcode_${System.currentTimeMillis()}.png")
                
                FileOutputStream(file).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                
                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, "Check out this QR Code!")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                
                context.startActivity(
                    Intent.createChooser(shareIntent, "Share QR Code via")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    override fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QR Code Result", text)
        clipboard.setPrimaryClip(clip)
    }
    
    override suspend fun scanBarcodeFromImage(bitmap: Bitmap): Pair<String, Int>? {
        return suspendCancellableCoroutine { continuation ->
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val scanner = BarcodeScanning.getClient()
                
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        val barcode = barcodes.firstOrNull()
                        val result = barcode?.rawValue?.let { value ->
                            Pair(value, barcode.format)
                        }
                        continuation.resume(result)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                continuation.resume(null)
            }
        }
    }
    
    override suspend fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

