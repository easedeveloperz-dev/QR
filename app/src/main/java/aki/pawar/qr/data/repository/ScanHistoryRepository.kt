package aki.pawar.qr.data.repository

import aki.pawar.qr.data.local.dao.ScanHistoryDao
import aki.pawar.qr.data.local.entity.ScanHistoryEntity
import aki.pawar.qr.domain.model.BarcodeContentType
import aki.pawar.qr.domain.model.BarcodeFormat
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing scan history data
 */
@Singleton
class ScanHistoryRepository @Inject constructor(
    private val scanHistoryDao: ScanHistoryDao
) {
    
    fun getAllScans(): Flow<List<ScanHistoryEntity>> = scanHistoryDao.getAllScans()
    
    fun getFavoriteScans(): Flow<List<ScanHistoryEntity>> = scanHistoryDao.getFavoriteScans()
    
    fun getRecentScans(limit: Int = 10): Flow<List<ScanHistoryEntity>> = 
        scanHistoryDao.getRecentScans(limit)
    
    fun searchScans(query: String): Flow<List<ScanHistoryEntity>> = 
        scanHistoryDao.searchScans(query)
    
    fun getScanCount(): Flow<Int> = scanHistoryDao.getScanCount()
    
    suspend fun getScanById(id: Long): ScanHistoryEntity? = scanHistoryDao.getScanById(id)
    
    suspend fun saveScan(
        rawValue: String,
        displayValue: String,
        format: BarcodeFormat,
        contentType: BarcodeContentType
    ): Long {
        val entity = ScanHistoryEntity.fromScanResult(
            rawValue = rawValue,
            displayValue = displayValue,
            format = format,
            contentType = contentType
        )
        return scanHistoryDao.insertScan(entity)
    }
    
    suspend fun deleteScan(scan: ScanHistoryEntity) = scanHistoryDao.deleteScan(scan)
    
    suspend fun deleteScanById(id: Long) = scanHistoryDao.deleteScanById(id)
    
    suspend fun deleteAllScans() = scanHistoryDao.deleteAllScans()
    
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = 
        scanHistoryDao.updateFavoriteStatus(id, isFavorite)
}

