package aki.pawar.qr.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import aki.pawar.qr.data.local.dao.GeneratedQrDao
import aki.pawar.qr.data.local.dao.ScanHistoryDao
import aki.pawar.qr.data.local.entity.GeneratedQrEntity
import aki.pawar.qr.data.local.entity.ScanHistoryEntity

/**
 * Room database for QR app
 * Contains scan history and generated QR code history
 */
@Database(
    entities = [
        ScanHistoryEntity::class,
        GeneratedQrEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class QrDatabase : RoomDatabase() {
    
    abstract fun scanHistoryDao(): ScanHistoryDao
    
    abstract fun generatedQrDao(): GeneratedQrDao
    
    companion object {
        const val DATABASE_NAME = "qr_database"
    }
}






