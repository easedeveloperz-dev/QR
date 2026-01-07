package aki.pawar.qr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import aki.pawar.qr.domain.model.BarcodeContentType
import aki.pawar.qr.domain.model.BarcodeFormat

/**
 * Room entity for storing scan history
 */
@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawValue: String,
    val displayValue: String,
    val format: String,
    val contentType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    companion object {
        fun fromScanResult(
            rawValue: String,
            displayValue: String,
            format: BarcodeFormat,
            contentType: BarcodeContentType
        ): ScanHistoryEntity {
            return ScanHistoryEntity(
                rawValue = rawValue,
                displayValue = displayValue,
                format = format.name,
                contentType = contentType.name
            )
        }
    }
    
    fun toBarcodeFormat(): BarcodeFormat {
        return try {
            BarcodeFormat.valueOf(format)
        } catch (e: Exception) {
            BarcodeFormat.UNKNOWN
        }
    }
    
    fun toContentType(): BarcodeContentType {
        return try {
            BarcodeContentType.valueOf(contentType)
        } catch (e: Exception) {
            BarcodeContentType.UNKNOWN
        }
    }
}




