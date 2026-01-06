package aki.pawar.qr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing generated QR code history
 */
@Entity(tableName = "generated_qr")
data class GeneratedQrEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val qrType: String,
    val qrContent: String,
    val displayLabel: String,
    val metadata: String = "", // JSON string for type-specific data
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)



